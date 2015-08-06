package org.openhab.ui.webappprofile.internal.servlet.evthandler;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhab.core.events.EventPublisher;
import org.openhab.core.internal.ItemDataHolder;
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
			//if(actionId!=null && actionId.equals(HubUtility.CREATE_PROFILE)){
				//
				String profileId	=	request.getParameter("profileID");
				String profileName	=	request.getParameter("profileName");
				String schTime		=	request.getParameter("schTime");
				HubUtility.cleanHttpSessionForNewProfile(request);
				XMLDocument xmlDocument	=	(XMLDocument)request.getSession().getAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
				if(xmlDocument==null){
					xmlDocument	=	new XMLDocumentDomImpl();
					xmlDocument.initDocument();
					xmlDocument.createDocument(profileId,profileName);
					request.getSession().setAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION, xmlDocument);
					request.getSession().setAttribute(HubUtility.APP_MODE, HubUtility.CREATE_PROFILE);
				}
//			} else {
//				//First check if the profile id is existing
//				Enumeration<String> paramEnum	=	request.getParameterNames();
//				if(paramEnum!=null){
//					while(paramEnum.hasMoreElements()){
//						String parameName	=	paramEnum.nextElement();
//						String paramValue	=	request.getParameter(parameName);
//						HubUtility.printDebugMessage("AdminEvent", " Req Param Name : "+parameName+ ": Req Param Value : "+paramValue);
//					}
//				}
//				
//			}
			
			HubUtility.updateHttpSessionForNewProfile(request,HubUtility.CREATE_PROFILE,profileId);
		} catch (Exception e){
			logger.error("Error In handling evenet "+e);
		}
	}
	
	public static void handleProfileCreateMode(HttpServletRequest req,HttpServletResponse res,ItemRegistry itemRegistry,EventPublisher eventPublisher){

		try{
			for(Object key : req.getParameterMap().keySet()) {
				String itemName = key.toString();
				
				HubUtility.printDebugMessage("AdminEvent", "Got message Key In Command "+itemName);
				
				if(!itemName.startsWith("__")) { // all additional webapp params start with "__" and should be ignored
					String commandName = req.getParameter(itemName);
					try {
						
						Item item = itemRegistry.getItem(itemName);
						
						String nodeId	=	item.getName();
						String nodeBinding	=	ItemDataHolder.getItemDataHolder().getData(nodeId);
						XMLDocument	xmlDocument	=	(XMLDocumentDomImpl)req.getSession().getAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
						HubUtility.printDebugMessage("AdminEvent-DOM OBJECT ",""+xmlDocument);						
						//xmlDocument.updateDocumentObject(nodeId, nodeBinding);
						
						
						HubUtility.printDebugMessage("AdminEvent State ", item.getState().toString());
						
						HubUtility.printDebugMessage("AdminEvent", "Got message Item Detials "+item.getName() +" : State : "+item.getState()+" : "+item.getGroupNames()+" : "+item.toString());
						
						
//*******************************************************************************************************************						
						String bindingConfig	=	ItemDataHolder.getItemDataHolder().getData(item.getName());
						String[] configurationStrings = bindingConfig.split("],");
						HubUtility.printDebugMessage("AdminEvent-0", configurationStrings[0]);
						HubUtility.printDebugMessage("AdminEvent-1", configurationStrings[1]);
						
						
//*******************************************************************************************************************						
						// we need a special treatment for the "TOGGLE" command of switches;
						// this is no command officially supported and must be translated 
						// into real commands by the webapp.
						if ((item instanceof SwitchItem || item instanceof GroupItem) && commandName.equals("TOGGLE")) {
							commandName = OnOffType.ON.equals(item.getStateAs(OnOffType.class)) ? "OFF" : "ON";
						}
						
						Command command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), commandName);
						if(command!=null) {
							//HubUtility.printDebugMessage("AdminEvent", "ItemName And Command Are : "+itemName+" :: "+commandName);
//							String statusFromItemDataProfileMap	=	HubUtility.getItemStatus(req, res, itemRegistry, nodeId);
//							
//							HubUtility.printDebugMessage("Button Status for Item    ",nodeId);
//							HubUtility.printDebugMessage("Button Status ItemRegistry",command.toString());
//							HubUtility.printDebugMessage("Button Status HubUtil     ",statusFromItemDataProfileMap);
//							
//							
//							if(statusFromItemDataProfileMap==null){
								xmlDocument.updateDocumentObject(nodeId,nodeBinding,command.toString(),"type",item);	
//							} else {
//								xmlDocument.updateDocumentObject(nodeId,nodeBinding,statusFromItemDataProfileMap,"type",item);
//							}
							
							
							HubUtility.printDebugMessage("AdminEvent Command: ", command.toString());
							//eventPublisher.sendCommand(itemName, command);

						} else {
							logger.warn("Received unknown command '{}' for item '{}'", commandName, itemName);						
						}
					} catch (ItemNotFoundException e) {
						logger.warn("Received command '{}' for item '{}', but the item does not exist in the registry", commandName, itemName);
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			logger.error("Error In handling evenet "+e);
		}
	}

	public static void handleProfileEditMode(HttpServletRequest req,HttpServletResponse res,ItemRegistry itemRegistry,EventPublisher eventPublisher){

		try{
			for(Object key : req.getParameterMap().keySet()) {
				String itemName = key.toString();
				
				HubUtility.printDebugMessage("AdminEvent", "Got message Key In Command "+itemName);
				
				if(!itemName.startsWith("__")) { // all additional webapp params start with "__" and should be ignored
					String commandName = req.getParameter(itemName);
					try {
						
						Item item = itemRegistry.getItem(itemName);
						
						String nodeId	=	item.getName();
						String nodeBinding	=	ItemDataHolder.getItemDataHolder().getProfileDataMap().get(nodeId);
						XMLDocument	xmlDocument	=	(XMLDocumentDomImpl)req.getSession().getAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
						HubUtility.printDebugMessage("AdminEvent-DOM OBJECT ",""+xmlDocument);						
						//xmlDocument.updateDocumentObject(nodeId, nodeBinding);
						
						
						HubUtility.printDebugMessage("AdminEvent State ", item.getState().toString());
						
						HubUtility.printDebugMessage("AdminEvent", "Got message Item Detials "+item.getName() +" : State : "+item.getState()+" : "+item.getGroupNames()+" : "+item.toString());
						
						
//*******************************************************************************************************************						
						String bindingConfig	=	ItemDataHolder.getItemDataHolder().getData(item.getName());
						String[] configurationStrings = bindingConfig.split("],");
						HubUtility.printDebugMessage("AdminEvent-0", configurationStrings[0]);
						HubUtility.printDebugMessage("AdminEvent-1", configurationStrings[1]);
						
						
//*******************************************************************************************************************						
						// we need a special treatment for the "TOGGLE" command of switches;
						// this is no command officially supported and must be translated 
						// into real commands by the webapp.
						if ((item instanceof SwitchItem || item instanceof GroupItem) && commandName.equals("TOGGLE")) {
							commandName = OnOffType.ON.equals(item.getStateAs(OnOffType.class)) ? "OFF" : "ON";
						}
						
						Command command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), commandName);
						if(command!=null) {
							//HubUtility.printDebugMessage("AdminEvent", "ItemName And Command Are : "+itemName+" :: "+commandName);
							String statusFromItemDataProfileMap	=	HubUtility.getItemStatus(req, res, itemRegistry, nodeId);
							
							HubUtility.printDebugMessage("Button Status for Item    ",nodeId);
							HubUtility.printDebugMessage("Button Status ItemRegistry",command.toString());
							HubUtility.printDebugMessage("Button Status HubUtil     ",statusFromItemDataProfileMap);
							
							
							if(statusFromItemDataProfileMap==null){
								xmlDocument.updateDocumentObject(nodeId,bindingConfig,command.toString(),"type",item);	
							} else {
								xmlDocument.updateDocumentObject(nodeId,bindingConfig,statusFromItemDataProfileMap,"type",item);
							}
							
							
							HubUtility.printDebugMessage("AdminEvent Command: ", command.toString());
							//eventPublisher.sendCommand(itemName, command);

						} else {
							logger.warn("Received unknown command '{}' for item '{}'", commandName, itemName);						
						}
					} catch (ItemNotFoundException e) {
						logger.warn("Received command '{}' for item '{}', but the item does not exist in the registry", commandName, itemName);
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			logger.error("Error In handling evenet "+e);
		}
	}
	
	public static void saveProfile(HttpServletRequest req){
		XMLDocument	xmlDocument	=	(XMLDocumentDomImpl)req.getSession().getAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
		xmlDocument.writeToFile();
	}
	
	public static void updateProfile(HttpServletRequest req){
		XMLDocument	xmlDocument	=	(XMLDocumentDomImpl)req.getSession().getAttribute(HubUtility.CURRENT_XML_DOC_IN_SESSION);
		xmlDocument.updateToFile();
	}
	
}
