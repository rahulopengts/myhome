package com.openhab.core.event.dto;

import org.openhab.binding.mqtt.internal.MqttMessagePublisher;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.persistence.internal.PersistenceManager;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.model.core.ModelRepository;
import org.openhab.model.rule.internal.engine.RuleEngine;

public class EventObject {

	private ItemRegistry	itemRegistry	=	null;
	
	private ModelRepository	modelRepository	=	null;

	private PersistenceManager	persistanceManager	=	null;
	
	
	private MqttMessagePublisher	mqttMessageToBePublished	=	null;
	
	public MqttMessagePublisher getMqttMessageToBePublished() {
		return mqttMessageToBePublished;
	}

	public void setMqttMessageToBePublished(
			MqttMessagePublisher mqttMessageToBePublished) {
		this.mqttMessageToBePublished = mqttMessageToBePublished;
	}

	public PersistenceManager getPersistanceManager() {
		return persistanceManager;
	}

	public void setPersistanceManager(PersistenceManager persistanceManager) {
		this.persistanceManager = persistanceManager;
	}

	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}

	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	private RuleEngine	ruleEngine	=	null;
	
	
	
	public ModelRepository getModelRepository() {
		return modelRepository;
	}

	public void setModelRepository(ModelRepository modelRepository) {
		this.modelRepository = modelRepository;
	}

	private String topicName		=	null;
	
	private Command command		=	null;
	
	private String itemName		=	null;
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public State getNewState() {
		return newState;
	}

	public void setNewState(State newState) {
		this.newState = newState;
	}

	private State newState		=	null;
	
	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	private String messageContent	=	null;
	public ItemRegistry getItemRegistry() {
		return itemRegistry;
	}

	public void setItemRegistry(ItemRegistry itemRegistry) {
		this.itemRegistry = itemRegistry;
	}
	
}
