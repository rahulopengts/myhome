package org.openhab.ui.webappprofile.internal.common;

import org.apache.commons.lang.StringUtils;
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
	
	public static StringBuilder modifyPostChildren(StringBuilder post_children){
		
		String formSubmit	=	"'"+HUB_CMD_EVENT_URI+"?action="+SAVE_PROFILE+"'";
		
		StringBuffer saveButton	=	new StringBuffer("<div class=\"button\"><button type=\"submit\" onclick=\"OH.saveProfile("+formSubmit+")\">Create</button></div>");
		
		int indexOfdivTag	=	post_children.indexOf("</div>");
		post_children.insert(indexOfdivTag+6, saveButton);
		return post_children;
	}
	
	public static String getIcon(Widget w) {
		String widgetTypeName = w.eClass().getInstanceTypeName().substring(w.eClass().getInstanceTypeName().lastIndexOf(".")+1);
		return widgetTypeName;
	}
}
