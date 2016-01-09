/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.webapp.internal.servlet;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.equinox.internal.event.EventComponent;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.internal.events.EventPublisherImpl;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.persistence.internal.PersistenceManager;
import org.openhab.core.types.Command;
import org.openhab.core.types.TypeParser;
import org.openhab.model.core.ModelRepository;
import org.openhab.model.core.internal.folder.FolderObserver;
import org.openhab.model.rule.internal.engine.RuleEngine;
import org.openhab.ui.webapp.cloud.exception.CloudException;
import org.openhab.ui.webapp.cloud.exception.CloudExceptionManager;
import org.openhab.ui.webapp.cloud.exception.CloudMessageConstants;
import org.openhab.ui.webapp.cloud.session.CloudSessionManager;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openhab.core.constant.CloudHomeAutoConstants;
import com.openhab.core.event.dto.EventObject;
import com.openhab.core.event.handler.EventManager;
import com.openhab.core.internal.event.processor.CloudAutoUpdateBinding;
import com.openhab.core.threadstore.CloudThreadLocalStorage;

/**
 * This servlet receives events from the web app and sends these as
 * commands to the bus.
 * 
 * @author Kai Kreuzer
 *
 */
public class CmdServlet extends BaseServlet {

	private static final Logger logger = LoggerFactory.getLogger(CmdServlet.class);

	public static final String SERVLET_NAME = "CMD";

	private EventPublisher eventPublisher;	

	
	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void unsetEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = null;
	}
	

	protected void activate() {
		try {
			logger.debug("Starting up CMD servlet at " + WEBAPP_ALIAS + SERVLET_NAME);

			Hashtable<String, String> props = new Hashtable<String, String>();
			httpService.registerServlet(WEBAPP_ALIAS + SERVLET_NAME, this, props, createHttpContext());
			
		} catch (NamespaceException e) {
			logger.error("Error during servlet startup", e);
		} catch (ServletException e) {
			logger.error("Error during servlet startup", e);
		}
	}

	protected void deactivate() {
		httpService.unregister(WEBAPP_ALIAS + SERVLET_NAME);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		//System.out.println("\nCmdServlet->service->eventPublisher->"+eventPublisher.getClass().getCanonicalName());
		ModelRepository	cloudModelRepository	=	null;
		ItemRegistry	cloudItemRegistry		=	null;
		PersistenceManager persistenceManager	=	null;
		RuleEngine ruleEngine	=	null;
		EventObject	eventObject	=	null;
		
		if(FolderObserver.CLOUD_MODE){
			try{
				
				persistenceManager	=	(PersistenceManager)CloudSessionManager.getAttribute(CloudSessionManager.getSession((HttpServletRequest)req, (HttpServletResponse)res), CloudSessionManager.PERSISTENCEMANAGER);
				ruleEngine	=	(RuleEngine)CloudSessionManager.getAttribute(CloudSessionManager.getSession((HttpServletRequest)req, (HttpServletResponse)res), CloudSessionManager.RULEENGINE);
				cloudItemRegistry	=	validateAndGetItemRegistry((HttpServletRequest)req,(HttpServletResponse)res,cloudModelRepository);
				cloudModelRepository=	validateAndGetModelRepository((HttpServletRequest)req,(HttpServletResponse)res);
				
				
				//Create EventObject
				eventObject	=	new EventObject();
				eventObject.setItemRegistry(cloudItemRegistry);
				eventObject.setModelRepository(cloudModelRepository);
				eventObject.setRuleEngine(ruleEngine);
				eventObject.setPersistanceManager(persistenceManager);

				CloudThreadLocalStorage.setLocalEventObject(eventObject);
				CloudThreadLocalStorage.setLocalHomeId("RR");
				System.out.println("\nCmdServlet->eventObject->"+Thread.currentThread().getId()+":EVENTOBJECT:"+eventObject);
				
				eventPublisher	=	(EventPublisher)CloudSessionManager.getAttribute(CloudSessionManager.getSession((HttpServletRequest)req, (HttpServletResponse)res), CloudSessionManager.EVENTPUBLISHER);
				//EventPublisher eventPublisher	=	new EventPublisherImpl();
				EventAdmin	eventAdmin	=	new EventComponent();
				((EventPublisherImpl)eventPublisher).setEventAdmin(eventAdmin);
			
			} catch (Throwable e){
				e.printStackTrace();
				throw new ServletException();
			}
		}
		for(Object key : req.getParameterMap().keySet()) {
			String itemName = key.toString();
			
			if(!itemName.startsWith("__")) { // all additional webapp params start with "__" and should be ignored
				String commandName = req.getParameter(itemName);
				try {
					System.out.println("\n CmdServlet->service-> DOES NOT START WITH->_ And Submitted Command Name Is "+commandName);
					Item item =	null; 
					if(CloudHomeAutoConstants.CLOUD_MODE){
						item	=	cloudItemRegistry.getItem(itemName);
					} else {
						item	=	itemRegistry.getItem(itemName);
					}
							
					
					// we need a special treatment for the "TOGGLE" command of switches;
					// this is no command officially supported and must be translated 
					// into real commands by the webapp.
					if ((item instanceof SwitchItem || item instanceof GroupItem) && commandName.equals("TOGGLE")) {
						commandName = OnOffType.ON.equals(item.getStateAs(OnOffType.class)) ? "OFF" : "ON";
					}
					
					
					
					Command command = TypeParser.parseCommand(item.getAcceptedCommandTypes(), commandName);
					
					
					System.out.println("\n CmdServlet->service-> Command:"+command);
					if(command!=null) {
						System.out.println("\n CmdServlet->service-> Command is Before :"+commandName+" :\n Command Type :"+command.getClass().getSimpleName());
						
						if(FolderObserver.CLOUD_MODE){
							
							EventManager	eventManager	=	new EventManager();
							
							eventManager.publishData(itemName, command, cloudItemRegistry,cloudModelRepository,persistenceManager,ruleEngine,eventObject);
						} else {
							eventPublisher.sendCommand(itemName, command);
							System.out.println("\n CmdServlet->service-> Command is "+commandName+" :\n Command Type :"+command.getClass().getSimpleName());
						}
					} else {
						logger.warn("Received unknown command '{}' for item '{}'", commandName, itemName);						
					}
				} catch (ItemNotFoundException e) {
					logger.warn("Received command '{}' for item '{}', but the item does not exist in the registry", commandName, itemName);
				}
			}
		}
	}

	private ItemRegistry validateAndGetItemRegistry(HttpServletRequest req,HttpServletResponse res,ModelRepository cloudModelRepository) throws ServletException{
		//EventPublisherImpl
		ItemRegistry cloudItemRegistry	=	null;
		try{
			HttpSession	session	=	CloudSessionManager.getSession(req, res);
			
			cloudItemRegistry	=	(ItemRegistry)CloudSessionManager.getAttribute(session, CloudSessionManager.ITEMREGISTRY);
			if(cloudItemRegistry==null){
				CloudExceptionManager.throwException(CloudMessageConstants.ATTRIBUTE_NULL, null, CloudSessionManager.ITEMREGISTRY+"is null");
			}
			
			cloudModelRepository	=	(ModelRepository)CloudSessionManager.getAttribute(session, CloudSessionManager.MODELREPO);
			if(cloudModelRepository==null){
				CloudExceptionManager.throwException(CloudMessageConstants.ATTRIBUTE_NULL, null, CloudSessionManager.MODELREPO+"is null");
			}
			
		} catch (CloudException excp){
			excp.printStackTrace();
			throw new ServletException(CloudSessionManager.ITEMREGISTRY+"is null");
		}
		//itemRegistry		
		return cloudItemRegistry;
//		EventPublisher eventPublisher	=	new EventPublisherImpl();
//		EventAdmin	eventAdmin	=	new EventComponent();
//		((EventPublisherImpl)eventPublisher).setEventAdmin(eventAdmin);
	}

	private ModelRepository validateAndGetModelRepository(HttpServletRequest req,HttpServletResponse res) throws ServletException{
		//EventPublisherImpl
		ModelRepository cloudModelRepository	=	null;
		
		try{
			HttpSession	session	=	CloudSessionManager.getSession(req, res);
			cloudModelRepository	=	(ModelRepository)CloudSessionManager.getAttribute(session, CloudSessionManager.MODELREPO);
			if(cloudModelRepository==null){
				CloudExceptionManager.throwException(CloudMessageConstants.ATTRIBUTE_NULL, null, CloudSessionManager.MODELREPO+"is null");
			}
			
		} catch (CloudException excp){
			excp.printStackTrace();
			throw new ServletException(CloudSessionManager.ITEMREGISTRY+"is null");
		}
		//itemRegistry		
		return cloudModelRepository;
//		EventPublisher eventPublisher	=	new EventPublisherImpl();
//		EventAdmin	eventAdmin	=	new EventComponent();
//		((EventPublisherImpl)eventPublisher).setEventAdmin(eventAdmin);
	}
	
//	at org.openhab.persistence.rrd4j.internal.RRD4jService.store(RRD4jService.java:154)
//	at org.openhab.core.persistence.internal.PersistenceManager.handleStateEvent(PersistenceManager.java:243)
//	at org.openhab.core.persistence.internal.PersistenceManager.stateChanged(PersistenceManager.java:222)
//	at org.openhab.core.items.GenericItem.notifyListeners(GenericItem.java:112)
//	at org.openhab.core.items.GenericItem.setState(GenericItem.java:100)
//	at org.openhab.core.items.GroupItem.stateUpdated(GroupItem.java:220)
//	at org.openhab.core.items.GenericItem.notifyListeners(GenericItem.java:108)
//	at org.openhab.core.items.GenericItem.setState(GenericItem.java:100)
//	at org.openhab.core.items.GroupItem.stateUpdated(GroupItem.java:220)
//	at org.openhab.core.items.GenericItem.notifyListeners(GenericItem.java:108)
//	at org.openhab.core.items.GenericItem.setState(GenericItem.java:100)
//	at org.openhab.core.autoupdate.internal.AutoUpdateBinding.postUpdate(AutoUpdateBinding.java:110)
//	at org.openhab.core.autoupdate.internal.AutoUpdateBinding.receiveCommand(AutoUpdateBinding.java:81)
//	at org.openhab.core.events.AbstractEventSubscriber.handleEvent(AbstractEventSubscriber.java:42)
//	at org.eclipse.equinox.internal.event.EventHandlerWrapper.handleEvent(EventHandlerWrapper.java:197)
//	at org.eclipse.equinox.internal.event.EventHandlerTracker.dispatchEvent(EventHandlerTracker.java:197)
//	at org.eclipse.equinox.internal.event.EventHandlerTracker.dispatchEvent(EventHandlerTracker.java:1)
//	at org.eclipse.osgi.framework.eventmgr.EventManager.dispatchEvent(EventManager.java:230)
//	at org.eclipse.osgi.framework.eventmgr.ListenerQueue.dispatchEventSynchronous(ListenerQueue.java:148)
//	at org.eclipse.equinox.internal.event.EventAdminImpl.dispatchEvent(EventAdminImpl.java:135)
//	at org.eclipse.equinox.internal.event.EventAdminImpl.sendEvent(EventAdminImpl.java:78)
//	at org.eclipse.equinox.internal.event.EventComponent.sendEvent(EventComponent.java:39)
//	at org.openhab.core.internal.events.EventPublisherImpl.sendCommand(EventPublisherImpl.java:61)
//	at org.openhab.ui.webapp.internal.servlet.CmdServlet.service(CmdServlet.java:129)
//	at org.eclipse.equinox.http.servlet.internal.ServletRegistration.service(ServletRegistration.java:61)
//	at org.eclipse.equinox.http.servlet.internal.ProxyServlet.processAlias(ProxyServlet.java:128)
//	at org.eclipse.equinox.http.servlet.internal.ProxyServlet.service(ProxyServlet.java:60)
//	at javax.servlet.http.HttpServlet.service(HttpServlet.java:848)
}


//		at org.openhab.binding.mqtt.internal.MqttMessagePublisher.publish(MqttMessagePublisher.java:191) ~[org.openhab.binding.mqtt/:na]
//		at org.openhab.binding.mqtt.internal.MqttEventBusBinding.receiveCommand(MqttEventBusBinding.java:135) [org.openhab.binding.mqtt/:na]
//		at org.openhab.core.events.AbstractEventSubscriber.handleEvent(AbstractEventSubscriber.java:42) [org.openhab.core/:na]
//		at org.eclipse.equinox.internal.event.EventHandlerWrapper.handleEvent(EventHandlerWrapper.java:197) [org.eclipse.equinox.event_1.2.200.v20120522-2049.jar:na]
//		at org.eclipse.equinox.internal.event.EventHandlerTracker.dispatchEvent(EventHandlerTracker.java:197) [org.eclipse.equinox.event_1.2.200.v20120522-2049.jar:na]
//		at org.eclipse.equinox.internal.event.EventHandlerTracker.dispatchEvent(EventHandlerTracker.java:1) [org.eclipse.equinox.event_1.2.200.v20120522-2049.jar:na]
//		at org.eclipse.osgi.framework.eventmgr.EventManager.dispatchEvent(EventManager.java:230) [org.eclipse.osgi_3.8.2.v20130124-134944.jar:na]
//		at org.eclipse.osgi.framework.eventmgr.ListenerQueue.dispatchEventSynchronous(ListenerQueue.java:148) [org.eclipse.osgi_3.8.2.v20130124-134944.jar:na]
//		at org.eclipse.equinox.internal.event.EventAdminImpl.dispatchEvent(EventAdminImpl.java:135) [org.eclipse.equinox.event_1.2.200.v20120522-2049.jar:na]
//		at org.eclipse.equinox.internal.event.EventAdminImpl.sendEvent(EventAdminImpl.java:78) [org.eclipse.equinox.event_1.2.200.v20120522-2049.jar:na]
//		at org.eclipse.equinox.internal.event.EventComponent.sendEvent(EventComponent.java:39) [org.eclipse.equinox.event_1.2.200.v20120522-2049.jar:na]
//		at org.openhab.core.internal.events.EventPublisherImpl.sendCommand(EventPublisherImpl.java:61) [org.openhab.core/:na]
//		at org.openhab.ui.webapp.internal.servlet.CmdServlet.service(CmdServlet.java:129) [org.openhab.ui.webapp/:na]
//		at org.eclipse.equinox.http.servlet.internal.ServletRegistration.service(ServletRegistration.java:61) [org.eclipse.equinox.http.servlet_1.1.300.v20120912-130548.jar:na]
//		at org.eclipse.equinox.http.servlet.internal.ProxyServlet.processAlias(ProxyServlet.java:128) [org.eclipse.equinox.http.servlet_1.1.300.v20120912-130548.jar:na]
//		at org.eclipse.equinox.http.servlet.internal.ProxyServlet.service(ProxyServlet.java:60) [org.eclipse.equinox.http.servlet_1.1.300.v20120912-130548.jar:na]
//		at javax.servlet.http.HttpServlet.service(HttpServlet.java:848) [javax.servlet_3.0.0.v201112011016.jar:na]
//		at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:598) [org.eclipse.jetty.servlet_8.1.3.v20120522.jar:8.1.3.v20120522]
//		at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:486) [org.eclipse.jetty.servlet_8.1.3.v20120522.jar:8.1.3.v20120522]
//		at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:231) [org.eclipse.jetty.server_8.1.3.v20120522.jar:8.1.3.v20120522]
//		at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1065) [org.eclipse.jetty.server_8.1.3.v20120522.jar:8.1.3.v20120522]
//		at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:413) [org.eclipse.jetty.servlet_8.1.3.v20120522.jar:8.1.3.v20120522]