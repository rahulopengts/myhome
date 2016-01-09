package com.openhab.core.internal.event.processor;

import com.openhab.core.internal.event.dto.CloudEvent;

public interface ICloudEventProcessor {

	public void handleEvent(CloudEvent event);
	
}
