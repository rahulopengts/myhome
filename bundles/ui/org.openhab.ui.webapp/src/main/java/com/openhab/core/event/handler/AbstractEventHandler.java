package com.openhab.core.event.handler;

import com.openhab.core.event.dto.DBEventObject;
import com.openhab.core.event.dto.EventObject;
import com.openhab.core.event.dto.UIEventObject;
import com.openhab.core.event.messaging.mqtt.MessagePublisher;

public abstract class AbstractEventHandler implements IEventHandler {

	@Override
	public void handleEvent(UIEventObject eventObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(DBEventObject eventObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publishOutBoundMessage(EventObject eventObject, String topic,String messageContent) {
		// TODO Auto-generated method stub
		MessagePublisher	messageBrokerService	=	MessagePublisher.getMessagePublisher();
		messageBrokerService.publishMessaeg("messageContent", topic);
		
	}
	
	@Override
	public void handleRule(EventObject eventObject, String topic,
			String messageContent){
		
	}
}
