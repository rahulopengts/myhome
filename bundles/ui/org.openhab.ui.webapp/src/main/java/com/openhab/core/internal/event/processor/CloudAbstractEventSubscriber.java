package com.openhab.core.internal.event.processor;

import static org.openhab.core.events.EventConstants.TOPIC_PREFIX;
import static org.openhab.core.events.EventConstants.TOPIC_SEPERATOR;

import org.openhab.core.events.EventSubscriber;
import org.openhab.core.types.Command;
import org.openhab.core.types.EventType;
import org.openhab.core.types.State;

import com.openhab.core.event.dto.EventObject;
import com.openhab.core.internal.event.dto.CloudEvent;

public abstract class CloudAbstractEventSubscriber implements EventSubscriber,ICloudEventProcessor{

	
	/**
	 * {@inheritDoc}
	 */
	
	public void handleEvent(CloudEvent event) {  
		
		ThreadLocal<String> t	=	new ThreadLocal<>();
		String s	=	t.get();
		System.out.println("\nCloudAbstractEventSubscriber->handleEvent->Thread Value:"+s);
		
		String itemName = (String) event.getProperty("item");
		
		String topic = event.getTopic();
		String[] topicParts = topic.split(TOPIC_SEPERATOR);
		
		if(!(topicParts.length > 2) || !topicParts[0].equals(TOPIC_PREFIX)) {
			return; // we have received an event with an invalid topic
		}
		String operation = topicParts[1];
		
		if(operation.equals(EventType.UPDATE.toString())) {
			State newState = (State) event.getProperty("state");
			System.out.println("\nCloudAbstractEventSubscriber->handleEvent->operation==UPDATE :"+this);
			if(newState!=null) receiveUpdate(itemName, newState);
		}
		if(operation.equals(EventType.COMMAND.toString())) {
			Command command = (Command) event.getProperty("command");
			System.out.println("\nCloudAbstractEventSubscriber->handleEvent->operation==COMMAND :"+this);
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
