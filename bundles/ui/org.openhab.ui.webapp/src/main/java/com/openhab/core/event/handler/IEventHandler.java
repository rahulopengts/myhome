package com.openhab.core.event.handler;

import com.openhab.core.event.dto.EventObject;

public interface IEventHandler {

	public void handleEvent(EventObject eventObject);
	
}
