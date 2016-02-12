/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mqtt.internal;

import org.apache.commons.lang.StringUtils;
import org.openhab.core.transform.CloudTransformationHelper;
import org.openhab.core.transform.TransformationHelper;
import org.openhab.core.transform.TransformationService;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.io.transport.mqtt.MqttMessageProducer;
import org.openhab.io.transport.mqtt.MqttSenderChannel;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message publisher configuration for items which send outbound MQTT messages.
 * 
 * @author Davy Vanherbergen
 * @since 1.3.0
 */
public class MqttMessagePublisher extends AbstractMqttMessagePubSub implements
		MqttMessageProducer {

	private static final Logger logger = LoggerFactory.getLogger(MqttMessagePublisher.class);

	private MqttSenderChannel senderChannel;

	private String trigger;

	/**
	 * Create new MqttMessagePublisher from config string.
	 * 
	 * @param configuration
	 *            config string
	 * @throws BindingConfigParseException
	 *             if the config string is invalid
	 */
	public MqttMessagePublisher(String configuration) throws BindingConfigParseException {

		//System.out.println("\n MqttMessagePublisher : "+configuration+":instance:"+this);
//		String s	=	null;
//		s.toString();
		String[] config = splitConfigurationString(configuration);
		try {

			if (config.length != 5) {
				throw new BindingConfigParseException(
						"Configuration requires 5 parameters separated by ':'");
			}

			if (StringUtils.isEmpty(config[0])) {
				throw new BindingConfigParseException("Missing broker name.");
			} else {
				setBroker(config[0].trim());
			}

			if (StringUtils.isEmpty(config[1]) || config[1].indexOf('+') != -1
					|| config[1].indexOf('#') != -1) {
				throw new BindingConfigParseException("Invalid topic.");
			} else {
				setTopic(config[1].trim());
			}

			if (StringUtils.isEmpty(config[2])) {
				throw new BindingConfigParseException("Missing type.");
			} else {
				try {
					MessageType t = MessageType.valueOf(config[2].trim().toUpperCase());
					
					//System.out.println("\n MqttMessagePublisher : MessageType"+t.toString());
					setMessageType(t);
				} catch (IllegalArgumentException e) {
					throw new BindingConfigParseException("Invalid type.");
				}
			}

			if (StringUtils.isEmpty(config[3])) {
				throw new BindingConfigParseException("Missing trigger.");
			} else {
				trigger = config[3].trim();
				//System.out.println("\n MqttMessagePublisher : trigger"+trigger);
			}

			if (StringUtils.isEmpty(config[4])) {
				throw new BindingConfigParseException("Missing transformation configuration.");
			} else {
				//System.out.println("\n MqttMessagePublisher : setTransformationRule"+config[4].trim());
				setTransformationRule(config[4].trim());
				initTransformService();
			}
			
			System.out.println("\n MqttMessagePublisher : "+configuration+":trigger:"+trigger+":MessageType:"+getMessageType());

		} catch (BindingConfigParseException e) {
			throw new BindingConfigParseException("Configuration '"
					+ configuration
					+ "' is not a valid outbound configuration: "
					+ e.getMessage());
		}
	}

	/**
	 * Check if this configuration supports processing of the given State.
	 * 
	 * @param state
	 *            for which to check if we can process.
	 * @return true if processing is supported.
	 */
	public boolean supportsState(State state) {
		if (getMessageType().equals(MessageType.COMMAND)) {
			return false;
		}
		if (getTrigger().equals("*")) {
			return true;
		}

		return trigger.equalsIgnoreCase(state.toString());
	}

	/**
	 * Check if this configuration supports processing of the given Command.
	 * 
	 * @param command
	 *            for which to check if we can process.
	 * @return true if processing is supported.
	 */
	public boolean supportsCommand(Command command) {
		if (getMessageType().equals(MessageType.STATE)) {
			return false;
		}
		if (getTrigger().equals("*")) {
			return true;
		}

		return trigger.equalsIgnoreCase(command.toString());
	}

	/**
	 * Compose the message to be sent. When a transformation is defined, this
	 * will be applied to the message content. After the transformation is
	 * performed, the default parameters are replaced in the content string.
	 * 
	 * @param value
	 *            command or state in string representation.
	 * @return message content.
	 * @throws Exception
	 */
	private byte[] createMessage(String value) throws Exception {

		//System.out.println("\n MqttMessagePublisher : createMessage "+new String(value) );
		if (getTransformationServiceName() != null
				&& getTransformationService() == null) {
			logger.debug("Sending message before transformation service '{}' was initialized.");
			initTransformService();
		}

		String content = value;
		System.out.println("\n MqttMessagePublisher : createMessage -0 value :->"+value) ;
		String s	=	getTransformationServiceParam();
		System.out.println("\n MqttMessagePublisher : createMessage -0 transformationParam:->"+s) ;
		if (getTransformationService() != null) {
			System.out.println("\n MqttMessagePublisher : createMessage -1-getTransformationServiceParam()->") ;
			content = getTransformationService().transform(getTransformationServiceParam(), value);
			System.out.println("\n MqttMessagePublisher : createMessage content "+content) ;
		} else if (getTransformationRule() != null && !getTransformationRule().equalsIgnoreCase("default")) {
			
			content = getTransformationRule();
			System.out.println("\n MqttMessagePublisher : createMessage -2 content "+content) ;
		}

		if (getMessageType().equals(MessageType.STATE)) {
			
			content = StringUtils.replace(content, "${state}", value);
			System.out.println("\n MqttMessagePublisher : createMessage -3 state"+content) ;
		} else {
			content = StringUtils.replace(content, "${command}", value);
			System.out.println("\n MqttMessagePublisher : createMessage -4 state"+content) ;
		}
		content = StringUtils.replace(content, "${itemName}", getItemName());
		System.out.println("\n MqttMessagePublisher : createMessage -Final "+content) ;
		return content.getBytes();
	}

	/**
	 * Publish a messge to the given topic.
	 * 
	 * @param topic
	 * @param message
	 */
	public void publish(String topic, byte[] message) {
		if (senderChannel == null) {
			System.out.println("\n MqttMessagePublisher : publish message : senderChannel NULL");
			return;
		}
			
		try {
			String m	=new String(createMessage(new String(message)));
			//System.out.println("\n MqttMessagePublisher : publish message : "+topic+" message byte is"+new String(message) +" : Message is ::"+m+":this:"+this);
//			if(!m.equals("OFF")){
//				String s	=	null;
//				s.toString();
//			}
//			String s	=	null;
//			s.toString();
			//senderChannel	=	null;
			senderChannel.publish(topic, createMessage(new String(message)));
		} catch (Exception e) {
			logger.error("Error publishing...", e);
		}
	}

	@Override
	public void setSenderChannel(MqttSenderChannel channel) {
		senderChannel = channel;
	}

	/**
	 * @return string representation of state or command which triggers the
	 *         sending of a message.
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * @return true if this publisher has been activated by the
	 *         MqttBrokerConnection.
	 */
	public boolean isActivated() {
		return senderChannel != null;
	}

	/**
	 * Get the topic and replace ${item} in the topic with the actual name.
	 */
	public String getTopic(String itemName) {
		return StringUtils.replace(getTopic(), "${item}", itemName);
	}

	@Override
	protected void initTransformService() {
		// TODO Auto-generated method stub
		
		if (getTransformationService() != null || StringUtils.isBlank(getTransformationServiceName())) {
			return;
		}

		String transformationServiceType	=	getTransformationServiceName();
		System.out.println("\nMqttMessagePublisher->initTransformService->"+getTransformationServiceName());
		
		
		if(transformationServiceType!=null && transformationServiceType.equals("JAVA")){
			TransformationService	transformationService	=	CloudTransformationHelper.getTransformationService(transformationServiceType);
			setTransformationService(transformationService);
			System.out.println("\nMqttMessagePublisher->initTransformService->done for cloud");
			//TransformationService	transformationService	=	new 	
		} else {
			super.initTransformService();			
		}

	}
}
