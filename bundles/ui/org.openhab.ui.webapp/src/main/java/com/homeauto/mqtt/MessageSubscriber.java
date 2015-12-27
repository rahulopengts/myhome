package com.homeauto.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.homeauto.mqtt.common.AppPropertiesMap;
import com.homeauto.mqtt.common.Utility;

public class MessageSubscriber implements MqttCallback {

	final static Logger logger = LoggerFactory.getLogger(MessageSubscriber.class);

	private static MessageSubscriber	instance	=	null;
	
	private static Object lock	=	new Object();

	private MessageSubscriber(){
		
	}
	
	public static MessageSubscriber	getInstance(){
		if(instance==null){
			synchronized(lock){
				instance	=	new MessageSubscriber();
				
				if(logger.isInfoEnabled()){
					logger.info("Successfully initialized MQTT Subscriber");
				}			
				
			}
		}
		
		return instance;
	}
	
	MqttClient myClient;
	MqttConnectOptions connOpt;

	//static final String BROKER_URL =	"tcp://"+AppPropertiesMap.MQTT_SERVER_HOST_IP+":"+AppPropertiesMap.MQTT_SERVER_PORT; 
	static final String BROKER_URL =	"tcp://localhost:1883";
	
	
	// the following two flags control whether this example is a publisher, a subscriber or both
	static final Boolean subscriber = true;
	static final Boolean publisher = false;
	boolean isRunning	=	false;
	
	public boolean stop(){
		try{
			isRunning	=	false;
			
			myClient.disconnect();
			ServiceStatusHolder.getInstance().setMqttSubRunning(false);
			
			
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("Successfully Stopped MQTT Subscriber");
		}			
		
		return true;
	}
	
	/**
	 * 
	 * connectionLost
	 * This callback is invoked upon losing the MQTT connection.
	 * 
	 */
	@Override
	public void connectionLost(Throwable t) {
		
		if(logger.isErrorEnabled()){
			logger.error("MQTT Subscriber connection lost");
		}			
		
		// code to reconnect to the broker would go here if desired
	}



	/**
	 * 
	 * MAIN
	 * 
	 */
	public static void main(String[] args) {
		MessageSubscriber smc = new MessageSubscriber();
		smc.runClient();
		
	}
	
	/**
	 * 
	 * runClient
	 * The main functionality of this simple example.
	 * Create a MQTT client, connect to broker, pub/sub, disconnect.
	 * 
	 */
	public void runClient() {
		// setup MQTT Client
		String clientID = AppPropertiesMap.MQTT_CLIENT_ID;
		connOpt = new MqttConnectOptions();
		
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
		
		// Connect to Broker
		try {
			isRunning	=	true;
			MemoryPersistence persistence = new MemoryPersistence();
			
			myClient = new MqttClient(BROKER_URL, clientID,persistence);
			myClient.setCallback(this);
			myClient.connect(connOpt);
			
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
			isRunning	=	false;
		}
		ServiceStatusHolder.getInstance().setMqttSubRunning(true);

		if(logger.isInfoEnabled()){
			logger.info("Successfully initialized MQTT Subscriber and connected to broker "+BROKER_URL);
		}			


		String myTopic = "/raspberry";

		// subscribe to topic if subscriber
		while (subscriber && isRunning) {
			try {
				int subQoS = 0;
				myClient.subscribe(myTopic, subQoS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		try{
			String receivedPayload	=	new String(arg1.getPayload());
			
			if(logger.isDebugEnabled()){
				logger.debug("Message arrived as payload "+receivedPayload);
			}			
			
			if(receivedPayload!=null && receivedPayload.equalsIgnoreCase(Utility.STOP)){
				System.out.println("\n**** Stopping Command Received");
				
				System.exit(0);
				
			} else {
				//Call USB WRITER HERE
			}
			} catch (Throwable th){
			if(th!=null){
				logger.error(th.toString());	
				th.printStackTrace();
			}			
			
		}
	}
}