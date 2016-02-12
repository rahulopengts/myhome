package com.openhab.core.event.handler;

import java.util.HashMap;
import java.util.Map;

import org.openhab.core.items.ItemRegistry;
import org.openhab.core.persistence.internal.PersistenceManager;
import org.openhab.model.rule.internal.engine.RuleEngine;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.openhab.core.event.dto.EventObject;
import com.openhab.core.internal.event.dto.CloudEvent;
import com.openhab.core.internal.event.dto.CloudEventProperty;
import com.openhab.core.internal.event.processor.CloudAutoUpdateBinding;
import com.openhab.core.internal.event.processor.CloudEventPublisherImpl;
import com.openhab.core.internal.event.processor.ext.ExtAutoUpdateBinding;

public class DataStoreHandler extends AbstractEventHandler {

	int i	=	0;
	//@Override
	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(EventObject eventObject) {
		// TODO Auto-generated method stub
		i++;
		System.out.println("\n DataStoreHandler->handleEvent->itemName"+eventObject.getItemName()+"->command->"+eventObject.getCommand());
		ItemRegistry	itemRegistrty	=	eventObject.getItemRegistry();

		PersistenceManager	persistanceManager	=	eventObject.getPersistanceManager();
		RuleEngine	ruleEngine	=	eventObject.getRuleEngine();
		
		CloudEventPublisherImpl	cloudEventPublisherImpl	=	new CloudEventPublisherImpl();
		cloudEventPublisherImpl.setItemRegistry(itemRegistrty);
		cloudEventPublisherImpl.setModelRepository(eventObject.getModelRepository());
		cloudEventPublisherImpl.setPersistenceManager(persistanceManager);
		cloudEventPublisherImpl.setRuleEngine(ruleEngine);
		
		cloudEventPublisherImpl.sendCommand(eventObject.getItemName(), eventObject.getCommand());
		
		
		
		System.out.println("\n DataStoreHandler->handleEvent->1");
		
//		CloudAutoUpdateBinding	cloudAutoUpdateBinding	=	new CloudAutoUpdateBinding();
//		CloudEvent	cloudEvent	=	null;//new CloudEvent("",new Properties() );
//		
//		Map	property	=	new HashMap<String, Object>();
//		property.put("topic","");
//		property.put("state",eventObject.getNewState());
//		CloudEventProperty	cloudProperty	=	new CloudEventProperty(property);
//		CloudEvent	event	=	new CloudEvent(eventObject.getTopicName(), cloudProperty);
//		cloudAutoUpdateBinding.handleEvent(cloudEvent);

	}
	
}
