/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.webappprofile.internal.render;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.openhab.core.internal.ItemDataHolder;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;
import org.openhab.model.sitemap.Mapping;
import org.openhab.model.sitemap.Switch;
import org.openhab.model.sitemap.Widget;
import org.openhab.ui.webappprofile.internal.common.HubUtility;
import org.openhab.ui.webappprofile.render.RenderException;
import org.openhab.ui.webappprofile.render.WidgetRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an implementation of the {@link WidgetRenderer} interface, which
 * can produce HTML code for Switch widgets.
 * 
 * @author Kai Kreuzer
 * @since 0.6.0
 *
 */
public class SwitchRenderer extends AbstractWidgetRenderer {

	private static final Logger logger = LoggerFactory.getLogger(SwitchRenderer.class);
	
	/**
	 * {@inheritDoc}
	 */
	public boolean canRender(Widget w) {
		return w instanceof Switch;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EList<Widget> renderWidget(Widget w, StringBuilder sb) throws RenderException {
		Switch s = (Switch) w;
		
		String snippetName = null;
		Item item;
		try {
			item = itemUIRegistry.getItem(w.getItem());
			if(s.getMappings().size()==0) {
				if(item instanceof RollershutterItem) {
					snippetName = "rollerblind";
				} else if (item instanceof GroupItem && ((GroupItem) item).getBaseItem() instanceof RollershutterItem) {
					snippetName = "rollerblind";
				} else {
					snippetName = "switch";
				}
			} else {
				snippetName = "buttons";
			}
		} catch (ItemNotFoundException e) {
			logger.warn("Cannot determine item type of '{}'", w.getItem(), e);
			snippetName = "switch";
		}

		String snippet = getSnippet(snippetName);

		State stateIs	=	itemUIRegistry.getState(w);
		HubUtility.printDebugMessage(this.toString(), "State of widget s "+stateIs.toString());
		
		snippet = StringUtils.replace(snippet, "%id%", itemUIRegistry.getWidgetId(w));
		HubUtility.printDebugMessage(this.toString(), "URL PATH : "+escapeURLPath(itemUIRegistry.getIcon(w)));
		snippet = StringUtils.replace(snippet, "%icon%", escapeURLPath(itemUIRegistry.getIcon(w)));
		snippet = StringUtils.replace(snippet, "%item%", w.getItem());
		snippet = StringUtils.replace(snippet, "%label%", getLabel(w));
		snippet = StringUtils.replace(snippet, "%servletname%", "hub/profile");
	
		
		State state = itemUIRegistry.getState(w);
		
		if(s.getMappings().size()==0) {
			if(state instanceof PercentType) {
				state = ((PercentType) state).intValue() > 0 ? OnOffType.ON : OnOffType.OFF;
			}
			if(state.equals(OnOffType.ON)) {
				snippet = snippet.replaceAll("%checked%", "checked=true");
			} else {
				snippet = snippet.replaceAll("%checked%", "");
			}
		} else {
			StringBuilder buttons = new StringBuilder();
			for(Mapping mapping : s.getMappings()) {
				String button = getSnippet("button");
				button = StringUtils.replace(button, "%item%",w.getItem());
				button = StringUtils.replace(button, "%cmd%", mapping.getCmd());
				button = StringUtils.replace(button, "%label%", mapping.getLabel());
				if(s.getMappings().size()>1 && state.toString().equals(mapping.getCmd())) {
					button = StringUtils.replace(button, "%type%", "Warn"); // button with red color
				} else {
					button = StringUtils.replace(button, "%type%", "Action"); // button with blue color
				}
				buttons.insert(0, button);
			}
			snippet = StringUtils.replace(snippet, "%buttons%", buttons.toString());
		}
		
		// Process the color tags
		snippet = processColor(w, snippet);

		sb.append(snippet);
		HubUtility.printDebugMessage(this.toString(), "Snippet is : "+sb.toString());
		return null;
	}
	

	@Override
	public EList<Widget> renderWidget(Widget w, StringBuilder sb,
			String applicationMode,String profileId,HttpServletRequest req) throws RenderException {
		// TODO Auto-generated method stub
		
		applicationMode	=	(String)req.getSession().getAttribute(HubUtility.APP_MODE);
		profileId	=	(String)req.getSession().getAttribute("ProfileId");

		HubUtility.printDebugMessage(this.toString(), "Profile Id to be processed is "+profileId);
		if(applicationMode!=null && applicationMode.equals(HubUtility.EDIT_PROFILE)){
			
			HashMap<String, String> profileDataMap	=	ItemDataHolder.getItemDataHolder().getProfileDataMap();
			HubUtility.printDebugMessage(this.toString(), "profileDataMap	=	"+profileDataMap);
			Switch s = (Switch) w;
			
			String snippetName = null;
			Item item;
			try {
				item = itemUIRegistry.getItem(w.getItem());
				if(s.getMappings().size()==0) {
					if(item instanceof RollershutterItem) {
						snippetName = "rollerblind";
					} else if (item instanceof GroupItem && ((GroupItem) item).getBaseItem() instanceof RollershutterItem) {
						snippetName = "rollerblind";
					} else {
						snippetName = "switch";
					}
				} else {
					snippetName = "buttons";
				}
			} catch (ItemNotFoundException e) {
				logger.warn("Cannot determine item type of '{}'", w.getItem(), e);
				snippetName = "switch";
			}
	
			String snippet = getSnippet(snippetName);
	
			State stateIs	=	itemUIRegistry.getState(w);
			HubUtility.printDebugMessage(this.toString(), "State of widget s "+stateIs.toString());
			HubUtility.printDebugMessage(this.toString(), "Id of Item "+w.getItem());
			String itemProfileBinding	=	(String)profileDataMap.get(w.getItem());
			HubUtility.printDebugMessage(this.toString(), "Value from Profile of widget  "+itemProfileBinding);
			
			snippet = StringUtils.replace(snippet, "%id%", itemUIRegistry.getWidgetId(w));
			//itemUIRegistry.getIcon(w, applicationMode, profileId, itemProfileBinding);
			HubUtility.printDebugMessage(this.toString(), "URL PATH : "+itemUIRegistry.getIcon(w));
			HubUtility.printDebugMessage(this.toString(), "State to be shown for node : "+w.getItem()+" is "+escapeURLPath(itemUIRegistry.getIcon(w, applicationMode, profileId, itemProfileBinding)));
			snippet = StringUtils.replace(snippet, "%icon%", escapeURLPath(itemUIRegistry.getIcon(w, applicationMode, profileId, itemProfileBinding)));
			snippet = StringUtils.replace(snippet, "%item%", w.getItem());
			
			snippet = StringUtils.replace(snippet, "%label%", getLabel(w));
			snippet = StringUtils.replace(snippet, "%servletname%", "hub/profile");
		
			
			State state = itemUIRegistry.getState(w);
			
			if(s.getMappings().size()==0) {
				if(state instanceof PercentType) {
					state = ((PercentType) state).intValue() > 0 ? OnOffType.ON : OnOffType.OFF;
				}
				if(applicationMode!=null && applicationMode.equals(HubUtility.EDIT_PROFILE)){
					if(itemProfileBinding!=null && (itemProfileBinding.contains("ON") || itemProfileBinding.contains("on"))) {
						snippet = snippet.replaceAll("%checked%", "checked=true");
					} else {//if(itemProfileBinding!=null && (itemProfileBinding.contains("OFF") || itemProfileBinding.contains("off"))) {
						snippet = snippet.replaceAll("%checked%", "");	
					}
				}
				if(state.equals(OnOffType.ON)) {
					snippet = snippet.replaceAll("%checked%", "checked=true");
				} else {
					snippet = snippet.replaceAll("%checked%", "");
				}
			} else {
				StringBuilder buttons = new StringBuilder();
				for(Mapping mapping : s.getMappings()) {
					String button = getSnippet("button");
					button = StringUtils.replace(button, "%item%",w.getItem());
					button = StringUtils.replace(button, "%cmd%", mapping.getCmd());
					button = StringUtils.replace(button, "%label%", mapping.getLabel());
					if(s.getMappings().size()>1 && state.toString().equals(mapping.getCmd())) {
						button = StringUtils.replace(button, "%type%", "Warn"); // button with red color
					} else {
						button = StringUtils.replace(button, "%type%", "Action"); // button with blue color
					}
					buttons.insert(0, button);
				}
				snippet = StringUtils.replace(snippet, "%buttons%", buttons.toString());
			}
			
			// Process the color tags
			snippet = processColor(w, snippet);
	
			sb.append(snippet);
			HubUtility.printDebugMessage(this.toString(), "Snippet is : "+sb.toString());
			return null;
		} else {
			return renderWidget(w, sb);
		}
		
	}
}
