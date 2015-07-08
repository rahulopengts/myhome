package org.openhab.binding.testbinding.common;

import org.openhab.binding.testbinding.internal.servlet.WebAppServletTest;

public class HubUtility {
	public static boolean DEBUG	=	true;
	public static String HUB_BASE_URI	=	"hub/profile";
	public static String CREATE			=	"create";
	public static String SUBMIT			=	"submit";
	
	public static String HUB_ACTION_PARAM			=	"action";
	
	public static void printDebugMessage(String className,String message){
		if(DEBUG){
			System.out.println(className+ " : "+message);
		}
	}
}
