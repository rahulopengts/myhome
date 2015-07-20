package org.openhab.ui.webappprofile.internal.common;




public class HubUtility {
	public static boolean DEBUG	=	true;
	public static String HUB_BASE_URI	=	"hub/profile";
	public static String CREATE			=	"create";
	public static String SUBMIT			=	"submit";
	
	public static String APP_MODE		=	"APP_MODE";
	public static String CREATE_PROFILE			=	"creatprofile";
	
	public static String HUB_ACTION_PARAM			=	"action";
	public static String CURRENT_XML_DOC_IN_SESSION	=	"CURRENT_XML_DOC_IN_SESSION";
	
	public static void printDebugMessage(String className,String message){
		if(DEBUG){
			System.out.println(className+ " : "+message);
		}
	}
}
