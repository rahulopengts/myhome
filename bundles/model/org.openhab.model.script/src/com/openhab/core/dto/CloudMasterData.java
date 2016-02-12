package com.openhab.core.dto;

import java.util.EventObject;

import org.openhab.core.items.ItemRegistry;
import org.openhab.core.types.Command;
import org.openhab.model.core.ModelRepository;

public class CloudMasterData implements ICloudMasterData{
	private ItemRegistry	itemRegistry	=	null;
	public ItemRegistry getItemRegistry() {
		return itemRegistry;
	}
	public void setItemRegistry(ItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	public ModelRepository getModelRepository() {
		return modelRepository;
	}
	public void setModelRepository(ModelRepository modelRepository) {
		this.modelRepository = modelRepository;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public Command getCommand() {
		return command;
	}
	public void setCommand(Command command) {
		this.command = command;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public EventObject getEventObject() {
		return eventObject;
	}
	public void setEventObject(EventObject eventObject) {
		this.eventObject = eventObject;
	}
	private ModelRepository	modelRepository	=	null;
	private String topicName		=	null;
	private Command command		=	null;
	private String itemName		=	null;
	private EventObject	eventObject	=	null;
	
}
