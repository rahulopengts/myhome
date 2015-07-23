/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.ui.webappprofile.internal.servlet;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhab.core.events.EventPublisher;
import org.openhab.core.internal.ItemDataHolder;
//import org.openhab.core.internal.ItemDataHolder;
import org.openhab.ui.webappprofile.internal.common.HubUtility;
import org.openhab.ui.webappprofile.internal.servlet.evthandler.AdminEventHandler;
import org.openhab.ui.webappprofile.internal.xml.XMLDocument;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet receives events from the web app and sends these as
 * commands to the bus.
 * 
 * @author Kai Kreuzer
 *
 */
public class CmdServlet extends BaseServlet {

	private static final Logger logger = LoggerFactory.getLogger(CmdServlet.class);

	public static final String SERVLET_NAME = "/event";

	private EventPublisher eventPublisher;	


	private XMLDocument xmlDocument	=	null;
	
	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void unsetEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = null;
	}
	

	protected void activate() {
		try {
			HubUtility.printDebugMessage(this.toString(), "Activating CmdServletExt "+ WEBAPP_ALIAS + SERVLET_NAME);
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
		
		HubUtility.printDebugMessage(this.toString(), "Got message in hub Command ");
		
		
		
		AdminEventHandler.handleProfileCreateMode((HttpServletRequest)req,(HttpServletResponse)res,itemRegistry,eventPublisher);

	}
	

}
