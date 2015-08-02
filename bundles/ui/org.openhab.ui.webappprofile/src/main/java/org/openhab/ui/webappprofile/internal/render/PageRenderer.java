package org.openhab.ui.webappprofile.internal.render;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.openhab.model.sitemap.Frame;
import org.openhab.model.sitemap.Sitemap;
import org.openhab.model.sitemap.Widget;
import org.openhab.ui.webappprofile.internal.common.HubUtility;
import org.openhab.ui.webappprofile.render.RenderException;
import org.openhab.ui.webappprofile.render.WidgetRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageRenderer extends AbstractWidgetRenderer {
	private final static Logger logger = LoggerFactory.getLogger(PageRenderer.class);

	List<WidgetRenderer> widgetRenderers = new ArrayList<WidgetRenderer>();

	public void addWidgetRenderer(WidgetRenderer widgetRenderer) {
		widgetRenderers.add(widgetRenderer);
	}

	public void removeWidgetRenderer(WidgetRenderer widgetRenderer) {
		widgetRenderers.remove(widgetRenderer);
	}

	/**
	 * This is the main method, which is called to produce the HTML code for a servlet request.
	 * 
	 * @param id the id of the parent widget whose children are about to appear on this page
	 * @param sitemap the sitemap to use
	 * @param label the title of this page
	 * @param children a list of widgets that should appear on this page
	 * @param async true, if this is an asynchronous request. This will use a different HTML skeleton
	 * @return a string builder with the produced HTML code
	 * @throws RenderException if an error occurs during the processing
	 */
	public StringBuilder processPage(String id, String sitemap, String label, EList<Widget> children, boolean async,String appMode,String profileId,HttpServletRequest req) throws RenderException {
		
		String snippet = getSnippet(async ? "layer" : "main");
		snippet = snippet.replaceAll("%id%", id);

		// if the label contains a value span, we remove this span as
		// the title of a page/layer cannot deal with this
		// Note: we can have a span here, if the parent widget had a label
		// with some value defined (e.g. "Windows [%d]"), which getLabel()
		// will convert into a "Windows <span>5</span>".
		if(label.contains("[") && label.endsWith("]")) {
			label = label.replace("[", "").replace("]", "");
		}
		snippet = StringUtils.replace(snippet, "%label%", label);
		snippet = StringUtils.replace(snippet, "%servletname%", "hub/profile");
		snippet = StringUtils.replace(snippet, "%sitemap%", sitemap);

		String[] parts = snippet.split("%children%");

		StringBuilder pre_children = new StringBuilder(parts[0]);
		StringBuilder post_children = new StringBuilder(parts[1]);
		
		if(parts.length==2) {
			processChildren(pre_children, post_children, children,appMode,profileId,req);
		} else if(parts.length > 2){
			logger.error("Snippet '{}' contains multiple %children% sections, but only one is allowed!", async ? "layer" : "main");
		}
		
		//Adding create Button if required
		post_children	=	HubUtility.modifyPostChildren(post_children);
		return pre_children.append(post_children);

	}

	private StringBuffer addSaveButton(){
		String buttonSnippet	=	null;
		try{
			buttonSnippet	=	getSnippet("button");
			
			HubUtility.printDebugMessage(this.toString(), "Button Snippet Is "+buttonSnippet);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		if(buttonSnippet!=null){
			return new StringBuffer(buttonSnippet);
		}
		return new StringBuffer("");
	}
	private void processChildren(StringBuilder sb_pre, StringBuilder sb_post,
			EList<Widget> children,String appMode,String profileId,HttpServletRequest req) throws RenderException {
		
		// put a single frame around all children widgets, if there are no explicit frames 
		if(!children.isEmpty()) {
			EObject firstChild = children.get(0);
			EObject parent = firstChild.eContainer();
			if(!(firstChild instanceof Frame || parent instanceof Frame || parent instanceof Sitemap || parent instanceof List)) {
				String frameSnippet = getSnippet("frame");
				frameSnippet = StringUtils.replace(frameSnippet, "%label%", "");
				
				String[] parts = frameSnippet.split("%children%");
				if(parts.length>1) {
					sb_pre.append(parts[0]);
				}
				if(parts.length>2) {
					sb_post.insert(0, parts[1]);
				} 
				if(parts.length > 2){
					logger.error("Snippet 'frame' contains multiple %children% sections, but only one is allowed!");
				}
			}
		}

		for(Widget w : children) {
			StringBuilder new_pre = new StringBuilder();
			StringBuilder new_post = new StringBuilder();
			StringBuilder widgetSB = new StringBuilder();
			EList<Widget> nextChildren = renderWidget(w, widgetSB,appMode,profileId,req);
			if(nextChildren!=null) {
				String[] parts = widgetSB.toString().split("%children%");
				// no %children% placeholder found or at the end
				if(parts.length==1) {
					new_pre.append(widgetSB);
					
				}
				// %children% section found 
				if(parts.length>1) {
					new_pre.append(parts[0]);
					new_post.insert(0, parts[1]);
				} 
				// multiple %children% sections found -> log an error and ignore all code starting from the second occurance
				if(parts.length > 2){
					String widgetType = w.eClass().getInstanceTypeName().substring(w.eClass().getInstanceTypeName().lastIndexOf(".")+1);
					logger.error("Snippet for widget '{}' contains multiple %children% sections, but only one is allowed!", widgetType);
				}
				processChildren(new_pre, new_post, nextChildren,appMode,profileId,req);
				sb_pre.append(new_pre);
				sb_pre.append(new_post);
				
//				System.out.println("\n PageRenderer : ProcessChildren Pre : "+sb_pre);
//				System.out.println("\n PageRenderer : ProcessChildren Post: "+sb_post);
			} else {
//				System.out.println("\n PageRenderer : ProcessChildren sb_pre: "+sb_pre);
				sb_pre.append(widgetSB);
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public EList<Widget> renderWidget(Widget w, StringBuilder sb) throws RenderException {
		// Check if this widget is visible
		if(itemUIRegistry.getVisiblity(w) == false)
			return null;

		for(WidgetRenderer renderer : widgetRenderers) {
			if(renderer.canRender(w)) {
				//String ren =	renderer.renderWidget(w, sb)
				//System.out.println("\n Render : "+w + " SB : "+sb.toString());
				printContent(w);
				//return renderer.renderWidget(w, sb);
				return renderer.renderWidget(w, sb);
			}
		}
		return null;
	}

	@Override
	public EList<Widget> renderWidget(Widget w, StringBuilder sb,
			String applicationMode, String profileId,HttpServletRequest req) throws RenderException {
		// TODO Auto-generated method stub
		if(itemUIRegistry.getVisiblity(w) == false)
			return null;

		for(WidgetRenderer renderer : widgetRenderers) {
			if(renderer.canRender(w)) {
				//String ren =	renderer.renderWidget(w, sb)
				//System.out.println("\n Render : "+w + " SB : "+sb.toString());
				printContent(w);
				//return renderer.renderWidget(w, sb);
				return renderer.renderWidget(w, sb,applicationMode,profileId,req);
			}
		}
		return null;
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public boolean canRender(Widget w) {
		return false;		
	}
	
	public void printContent(Widget w){
		TreeIterator t	=	 w.eAllContents();
		while(t.hasNext()){
			Object o	=	t.next();
			//System.out.println(o);
		}
		EList elist	=	w.eContents();
		//System.out.println("\n Elist : "+elist.toString());
	}

	public StringBuilder processProfileMainPage(String id, String sitemap, String label, EList<Widget> children, boolean async) throws RenderException {
		
		String snippet = getSnippet(async ? "layer" : "createprofile");
		HubUtility.printDebugMessage(this.toString(), "procrssPageCreaterofile : snippet : "+snippet);
		snippet = snippet.replaceAll("%id%", id);

		// if the label contains a value span, we remove this span as
		// the title of a page/layer cannot deal with this
		// Note: we can have a span here, if the parent widget had a label
		// with some value defined (e.g. "Windows [%d]"), which getLabel()
		// will convert into a "Windows <span>5</span>".
		if(label.contains("[") && label.endsWith("]")) {
			label = label.replace("[", "").replace("]", "");
		}
		snippet = StringUtils.replace(snippet, "%label%", label);
		snippet = StringUtils.replace(snippet, "%servletname%", "hub/profile");
		snippet = StringUtils.replace(snippet, "%sitemap%", sitemap);
		
//		IOUtils.toString(entry.openStream());
		
		return new StringBuilder(snippet);
	}
	
}
