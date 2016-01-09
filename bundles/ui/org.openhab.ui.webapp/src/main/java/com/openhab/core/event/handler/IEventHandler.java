package com.openhab.core.event.handler;

import com.openhab.core.event.dto.DBEventObject;
import com.openhab.core.event.dto.EventObject;
import com.openhab.core.event.dto.UIEventObject;

public interface IEventHandler {

	public void handleEvent(UIEventObject eventObject);

	public void handleEvent(DBEventObject eventObject);
	
	public void publishOutBoundMessage(EventObject eventObject, String topic,String messageContent);
	
	
	public void handleRule(EventObject eventObject, String topic,String messageContent);
	
	
}
