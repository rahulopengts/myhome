package org.openhab.ui.webappprofile.internal.render;

import org.openhab.ui.webappprofile.render.RenderException;

public interface HubPagerRendererInterface {
	
	

	public StringBuilder processProfileMainPage(String id,String pageId, String sitemap, String label,int evalId) throws RenderException;
}
