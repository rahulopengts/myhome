package com.openhab.core.event.dto;

import org.openhab.core.items.ItemRegistry;

public class EventObject {

	private ItemRegistry	itemRegistry	=	null;

	public ItemRegistry getItemRegistry() {
		return itemRegistry;
	}

	public void setItemRegistry(ItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
}
