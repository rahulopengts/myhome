package org.openhab.ui.webappprofile.internal.servlet.evthandler;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.TypeParser;
import org.openhab.ui.webappprofile.internal.common.HubUtility;
import org.openhab.ui.webappprofile.internal.xml.XMLDocument;
import org.openhab.ui.webappprofile.internal.xml.XMLDocumentDomImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminEventHandler {

	private static final Logger logger = LoggerFactory.getLogger(AdminEventHandler.class);
	
	public static void intitializeProfileCreateMode(HttpServletRequest request,HttpServletResponse response){
		String actionId	=	(String)request.getParameter(HubUtility.CREATE_PROFILE);
		try{
			if(actionId!=null && actionId.equals(HubUtility.CREATE_PROFILE)){
				//
				String profileId	=	request.getParameter("profileId");
				String profileName	=	request.getParameter("profileName");
				String schTime		=	request.getParameter("schTime");
				XMLDocument xmlDocument	=	(XMLDocument)request.getSession().getAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
				if(xmlDocument==null){
					xmlDocument	=	new XMLDocumentDomImpl();
					xmlDocument.initDocument();
					xmlDocument.createDocument(profileId,profileName);
					request.getSession().setAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION, xmlDocument);
					request.getSession().setAttribute(HubUtility.APP_MODE, HubUtility.CREATE_PROFILE);
				}
			} else {
				//First check if the profile id is existing
				Enumeration<String> paramEnum	=	request.getParameterNames();
				if(paramEnum!=null){
					while(paramEnum.hasMoreElements()){
						String parameName	=	paramEnum.nextElement();
						String paramValue	=	request.getParameter(parameName);
						HubUtility.printDebugMessage("AdminEvent", " Req Param Name : "+parameName+ ": Req Param Value : "+paramValue);
					}
				}
				
			}
		} catch (Exception e){
			logger.error("Error In handling evenet "+e);
		}
	}
	
	public static void handleProfileCreateMode(HttpServletRequest req,HttpServletResponse res,ItemRegistry itemRegistry){

		try{
			for(Object key : req.getParameterMap().keySet()) {
				String itemName = key.toString();
				
				HubUtility.printDebugMessage("AdminEvent", "Got message Key In Command "+itemName);
				
				if(!itemName.startsWith("__")) { // all additional webapp params start with "__" and should be ignored
					String commandName = req.getParameter(itemName);
					try {
						Item item = itemRegistry.getItem(itemName);
						
						HubUtility.printDebugMessage("AdminEvent", "Got message Item Detials "+item.getName() +" : State : "+item.getState()+" : "+item.getGroupNames()+" : "+item.toString());
						
						// we need a special treatment for the "TOGGLE" command of switches;
						// this is no command officially supported and must be translated 
						// into real commands by the webapp.
						if ((item instanceof SwitchItem || item instanceof GroupItem) && commandName.equals("TOGGLE")) {
							commandName = OnOffType.ON.equals(item.getStateAs(OnOffType.class)) ? "OFF" : "ON";
						}
						
						Command command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), commandName);
						if(command!=null) {
							HubUtility.printDebugMessage("AdminEvent", "ItemName And Command Are : "+itemName+" :: "+commandName);

						} else {
							logger.warn("Received unknown command '{}' for item '{}'", commandName, itemName);						
						}
					} catch (ItemNotFoundException e) {
						logger.warn("Received command '{}' for item '{}', but the item does not exist in the registry", commandName, itemName);
					}
				}
			}
		} catch (Exception e){
			logger.error("Error In handling evenet "+e);
		}
	}
	
}
