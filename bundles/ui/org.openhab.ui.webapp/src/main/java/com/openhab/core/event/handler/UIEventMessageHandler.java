package com.openhab.core.event.handler;

import org.openhab.core.items.ItemRegistry;

import com.google.common.eventbus.Subscribe;
import com.openhab.core.event.messaging.mqtt.MessagePublisher;

public final class UIEventMessageHandler extends AbstractEventHandler {

	@Override
	@Subscribe
	public void handleEvent(com.openhab.core.event.dto.UIEventObject eventObject) {
		System.out.println("\nUIEventMessageHandler->handleEbent->UIEventObject "+this);
		ItemRegistry	itemRegistry	=	eventObject.getItemRegistry();
		System.out.println("\nUIEventMessageHandler->handleEbent->eventObject->topic->"+eventObject.getTopicName());
		System.out.println("\nUIEventMessageHandler->handleEbent->eventObject->messageContent->"+eventObject.getMessageContent());
		MessagePublisher	messagePublisher	=	MessagePublisher.getMessagePublisher();
		messagePublisher.publishMessaeg(eventObject.getMessageContent(), eventObject.getTopicName());
	}

	@Override
	@Subscribe
	public void handleEvent(com.openhab.core.event.dto.DBEventObject eventObject) {
		System.out.println("\nUIEventMessageHandler->handleEbent->DBEventObject "+this);
		ItemRegistry	itemRegistry	=	eventObject.getItemRegistry();
		System.out.println("\nUIEventMessageHandler->handleEbent->eventObject->topic->"+eventObject.getTopicName());
		System.out.println("\nUIEventMessageHandler->handleEbent->eventObject->messageContent->"+eventObject.getMessageContent());
		MessagePublisher	messagePublisher	=	MessagePublisher.getMessagePublisher();
		messagePublisher.publishMessaeg(eventObject.getMessageContent(), eventObject.getTopicName());
	}

}
