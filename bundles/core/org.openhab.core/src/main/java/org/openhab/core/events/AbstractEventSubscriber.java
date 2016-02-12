/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.core.events;

import static org.openhab.core.events.EventConstants.TOPIC_PREFIX;
import static org.openhab.core.events.EventConstants.TOPIC_SEPERATOR;

import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.EventType;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

abstract public class AbstractEventSubscriber implements EventSubscriber, EventHandler {
	
	/**
	 * {@inheritDoc}
	 */
	public void handleEvent(Event event) {  
		String itemName = (String) event.getProperty("item");
		
		String topic = event.getTopic();
		String[] topicParts = topic.split(TOPIC_SEPERATOR);
		
		if(!(topicParts.length > 2) || !topicParts[0].equals(TOPIC_PREFIX)) {
			return; // we have received an event with an invalid topic
		}
		String operation = topicParts[1];
		
		//System.out.println("\nAbstractEventSubscriber->handleEvent->==:Thread "+Thread.currentThread().getId()+":ThreadName:"+Thread.currentThread().getName()+":->topicParts->"+topicParts[1]);
		
		if(operation.equals(EventType.UPDATE.toString())) {
			State newState = (State) event.getProperty("state");
			System.out.println("\nAbstractEventSubscriber->handleEvent->operation==UPDATE :"+this);
			if(newState!=null) receiveUpdate(itemName, newState);
		}
		if(operation.equals(EventType.COMMAND.toString())) {
			Object recObject	=	event.getProperty("command");
			Command command = (Command) event.getProperty("command");
			//System.out.println("\nAbstractEventSubscriber->handleEvent->operation==COMMAND :"+this);
			System.out.println("\nAbstractEventSubscriber->handleEvent->operation==COMMAND ItemName :"+itemName+"->Command->"+command+"->object binding->"+this+"->Received Command Type->"+recObject.getClass().getCanonicalName());
			if(command!=null) receiveCommand(itemName, command);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void receiveCommand(String itemName, Command command) {
		// default implementation: do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public void receiveUpdate(String itemName, State newState) {
		// default implementation: do nothing
	}

}
