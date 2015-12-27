package com.homeauto.mqtt;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.homeauto.mqtt.common.AppPropertiesMap;

public class MessagePublisher {

	final static Logger logger = LoggerFactory.getLogger(MessagePublisher.class);
	
	private static MessagePublisher	messagePublisher	=	null;
	private static final Object lock 					= new Object(); //Lock Object
    int qos             = 2;

    
	private MqttClient sampleClient = null;
	private MqttConnectOptions connOpts = null;
	private boolean isInitilized	=	false;
	
	public static MessagePublisher getMessagePublisher(){
		synchronized (lock) {
		if(messagePublisher==null){
			messagePublisher	=	new MessagePublisher();
			messagePublisher.init();
			if(logger.isInfoEnabled()){
				logger.info("Successfully inittialized MQTT Publisher");
			}
		}
		}
		return messagePublisher;
	}
	
	

	public boolean spot(){
		try{
			sampleClient.disconnect();
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		
		if(logger.isInfoEnabled()){
			logger.info("Successfully Disconnected MQTT Publisher");
		}		
		return true;
	}

	
    
	public boolean init(){
        int qos             = 2;
        String broker       = "tcp://"+AppPropertiesMap.MQTT_SERVER_HOST_IP+":"+AppPropertiesMap.MQTT_SERVER_PORT;
        //String broker       = "tcp://localhost:1883";
        String clientId     = "pubClient";//AppPropertiesMap.MQTT_CLIENT_ID;
        
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
			if(logger.isInfoEnabled()){
				logger.info("Connecting to broker:"+broker);	
			}				
                        
            sampleClient.connect(connOpts);
			if(logger.isInfoEnabled()){
				logger.info("Connected Successfully...");	
			}				
            

        } catch(MqttException me) {
            logger.error("reason "+me.getReasonCode());
            logger.error("msg "+me.getMessage());
            logger.error("loc "+me.getLocalizedMessage());
            logger.error("cause "+me.getCause());
            logger.error("excep "+me);
            me.printStackTrace();
        }
        isInitilized	=	true;
        
        return isInitilized;
        
	}
	

	public void publishMessaeg(String content,String topic) {
		if(!isInitilized){
			init();
		}

		try{

	        MqttMessage message = new MqttMessage(content.getBytes());
	        message.setQos(qos);
	        sampleClient.publish(topic, message);
			if(logger.isDebugEnabled()){
				logger.debug("Successfully Published MQTT Message on topic "+topic+" with message "+content);
			}			
	        
		} catch (MqttPersistenceException e){
			//e.printStackTrace();
			
		} catch (Exception e){
			e.printStackTrace();
		}
    }
}