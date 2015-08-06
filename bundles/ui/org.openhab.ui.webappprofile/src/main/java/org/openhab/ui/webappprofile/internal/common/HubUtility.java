package org.openhab.ui.webappprofile.internal.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openhab.core.internal.ItemDataHolder;
import org.openhab.core.items.ItemRegistry;
import org.openhab.model.sitemap.Widget;




public class HubUtility {
	//UI Page ID
	public static int MAINPROFILEPAGE	=	1;
	public static int CREATEPROFILE		=	2;
	public static int MAIN				=	0;
	
	public static int ARRAY_PAGEID[]	=	{MAIN,MAINPROFILEPAGE,CREATEPROFILE};
	public static String ARRAY_PAGENAME[]	=	{"main","mainprofilepage","createprofile"};
	
	
	public static boolean DEBUG	=	true;
	public static String HUB_BASE_URI	=	"hub/profile";
	public static String CREATE			=	"create";
	public static String SUBMIT			=	"submit";
	
	public static String APP_MODE		=	"APP_MODE";
	public static String CREATE_PROFILE			=	"creatprofile";
	
	public static String LIST_PROFILE			=	"listprofile";
	
	public static String EDIT_PROFILE			=	"editprofile";
	
	public static String HUB_ACTION_PARAM			=	"action";
	public static String CURRENT_XML_DOC_IN_SESSION	=	"CURRENT_XML_DOC_IN_SESSION";
	
	public static String HUB_CMD_EVENT_URI	= "../hub/event";
	
	public static String SAVE_PROFILE	=	"saveprofile";

	
	public static String PROFILEDIR	=	"profiles";
	
	public static void printDebugMessage(String className,String message){
		if(DEBUG){
			System.out.println(className+ " : "+message);
		}
	}
	
	public static StringBuilder modifyPostChildren(HttpServletRequest request,StringBuilder post_children){
		String appMode	=	(String)request.getSession().getAttribute(HubUtility.APP_MODE);
		
		if(appMode!=null && appMode.equals(HubUtility.CREATE_PROFILE)){
			String formSubmit	=	"'"+HUB_CMD_EVENT_URI+"?action="+SAVE_PROFILE+"'";
			StringBuffer saveButton	=	new StringBuffer("<div style=\"padding-left: 10px; padding-right: 10px; padding-top: 10px; padding-bottom: 10px;\"><button type=\"submit\" onclick=\"OH.saveProfile("+formSubmit+")\">Save Profile</button></div>");			
			//StringBuffer saveButton	=	new StringBuffer("<div class=\"button\"><button type=\"submit\" onclick=\"OH.saveProfile("+formSubmit+")\">Save Profile</button></div>");
			int indexOfdivTag	=	post_children.indexOf("</div>");
			post_children.insert(indexOfdivTag+6, saveButton);
		} else if(appMode!=null && appMode.equals(HubUtility.EDIT_PROFILE)) {
			String formSubmit	=	"'"+HUB_CMD_EVENT_URI+"?action="+EDIT_PROFILE+"'";
			StringBuffer saveButton	=	new StringBuffer("<div style=\"padding-left: 10px; padding-right: 10px; padding-top: 10px; padding-bottom: 10px;\"><button type=\"submit\" onclick=\"OH.saveProfile("+formSubmit+")\">Update Profile</button></div>");
			int indexOfdivTag	=	post_children.indexOf("</div>");
			post_children.insert(indexOfdivTag+6, saveButton);
		}
		return post_children;
	}
	
	public static String getIcon(Widget w) {
		String widgetTypeName = w.eClass().getInstanceTypeName().substring(w.eClass().getInstanceTypeName().lastIndexOf(".")+1);
		return widgetTypeName;
	}
	
	public static void updateHttpSessionForNewProfile(HttpServletRequest req,String applicationMode,String profileId){
		req.getSession().setAttribute(HubUtility.APP_MODE, applicationMode);
		req.getSession().setAttribute("ProfileId", profileId);
	}
	
	public static void cleanHttpSessionForNewProfile(HttpServletRequest req){
		req.getSession().removeAttribute(HubUtility.APP_MODE);
		req.getSession().removeAttribute("ProfileId");
		req.getSession().removeAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
		ItemDataHolder.getItemDataHolder().setProfileDataMap(null);
	}
	
	public static String getItemStatus(HttpServletRequest request,HttpServletResponse response,ItemRegistry itemRegistry,String nodeId){
		String applicationMode	=	(String)request.getSession().getAttribute(HubUtility.APP_MODE);
		String nodeStatus	=	null;
		if(applicationMode!=null && (applicationMode.equals(HubUtility.CREATE_PROFILE) || applicationMode.equals(HubUtility.EDIT_PROFILE) )){
			String nodeBidning	=	ItemDataHolder.getItemDataHolder().getProfileDataMap().get(nodeId);
			printDebugMessage("HubUtil : getItemStatus : Last Status :", nodeBidning);
			if(nodeBidning!=null && (nodeBidning.contains("ON") || nodeBidning.contains("on"))) {
				nodeStatus	=	"OFF";
			} else if(nodeBidning!=null && (nodeBidning.contains("OFF") || nodeBidning.contains("off"))) {
				nodeStatus	=	"ON";	
			}
			
		}
				
		return nodeStatus;
	}
}
