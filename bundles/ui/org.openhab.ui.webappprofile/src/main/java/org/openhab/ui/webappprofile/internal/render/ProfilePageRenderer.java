package org.openhab.ui.webappprofile.internal.render;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.openhab.model.sitemap.Widget;
import org.openhab.ui.webappprofile.internal.common.HubUtility;
import org.openhab.ui.webappprofile.internal.xml.XMLDocumentDomImpl;
import org.openhab.ui.webappprofile.render.RenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilePageRenderer extends AbstractWidgetRenderer implements HubPagerRendererInterface{
	private final static Logger logger = LoggerFactory.getLogger(ProfilePageRenderer.class);

	@Override
	public boolean canRender(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EList<Widget> renderWidget(Widget w, StringBuilder sb)
			throws RenderException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder processProfileMainPage(String id,String pageId, String sitemap,
			String label,int evalId) throws RenderException {
		String snippet = null;
		if(evalId==2){
			//Regirtation form
			snippet = getSnippet(false ? "layer" : HubUtility.ARRAY_PAGENAME[evalId]);
			HubUtility.printDebugMessage(this.toString(), "procrssPageCreaterofile : snippet : "+snippet);
			snippet = snippet.replaceAll("%id%", id);
	
			if(label.contains("[") && label.endsWith("]")) {
				label = label.replace("[", "").replace("]", "");
			}
			snippet = StringUtils.replace(snippet, "%label%", label);
			snippet = StringUtils.replace(snippet, "%servletname%", "hub/profile");
			snippet = StringUtils.replace(snippet, "%sitemap%", sitemap);
		} else {
			//EDIT FORM FOR PROFILE
			snippet = getSnippet(false ? "layer" : HubUtility.ARRAY_PAGENAME[evalId]);
			String children	=	getChildren();
			snippet	=	StringUtils.replace(snippet, "%children%", children);
			
		}
		return new StringBuilder(snippet);

		
	}

	private String getChildren(){
		StringBuilder returnSnippet	=	new StringBuilder();
		String childSnippet	=	null;
		try{
		File fileList[] =	XMLDocumentDomImpl.getProfileList();
		if(fileList!=null && fileList.length>0){
			childSnippet	=	getSnippet(false ? "layer" : "editprofilerow");
			for(int fileCount=0;fileCount<fileList.length;fileCount++){
				String fileName	=	fileList[fileCount].getName();
				HubUtility.printDebugMessage(this.toString(), "fileName : "+fileName);
				String childHTML	=	childSnippet;
				
				//<tr><td>%profileName%</td><td>%profileId%</td><td>%profileTime%</td><td>%profileTime%</td></tr>
				childHTML	=	StringUtils.replace(childHTML, "%profileId%", fileName.substring(0,fileName.indexOf('.')));
				childHTML	=	StringUtils.replace(childHTML, "%profileName%", "ProfileId-1");
				childHTML	=	StringUtils.replace(childHTML, "%profileTime%", "ProfileTime-1");
				returnSnippet.append(childHTML);
			}
		}
		} catch (RenderException e){
			e.printStackTrace();
			returnSnippet	=	new StringBuilder("");
		} catch (Exception e){
			e.printStackTrace();
			returnSnippet	=	new StringBuilder("");
		}
		
		return returnSnippet.toString();
	}
}
