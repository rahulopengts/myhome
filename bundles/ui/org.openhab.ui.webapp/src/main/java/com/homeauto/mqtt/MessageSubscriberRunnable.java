package com.homeauto.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSubscriberRunnable implements Runnable {
	
	final static Logger logger = LoggerFactory.getLogger(MessageSubscriberRunnable.class);
	
	private MessageSubscriber	subscriber	=	null;
	
	public void init(MessageSubscriber subscriber){
		this.subscriber	=	MessageSubscriber.getInstance();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{

			if(logger.isInfoEnabled()){
				logger.info("Starting MQTT Subscriber...");	
			}							
			subscriber.runClient();
		} catch (Throwable	e){
			e.printStackTrace();
		}
	}

}
