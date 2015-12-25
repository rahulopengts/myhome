package com.openhab.core.event.handler;

import java.io.Serializable;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.openhab.core.event.dto.EventObject;

public class DataStoreHandler implements IEventHandler {

	int i	=	0;
	//@Override
	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(EventObject eventObject) {
		// TODO Auto-generated method stub
		i++;
		System.out.println("\n handleEvent :"+i);
	}
	
}
