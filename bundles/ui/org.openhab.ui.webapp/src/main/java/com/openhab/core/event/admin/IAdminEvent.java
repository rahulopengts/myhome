package com.openhab.core.event.admin;

import org.openhab.core.items.ItemRegistry;

import com.openhab.core.event.dto.EventObject;
import com.openhab.core.event.handler.IEventHandler;

public interface IAdminEvent {

	
	
	public void dispatchEvent(EventObject eventObject,IEventHandler handler);
}
