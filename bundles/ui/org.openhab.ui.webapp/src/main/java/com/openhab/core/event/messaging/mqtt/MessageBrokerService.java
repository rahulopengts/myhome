package com.openhab.core.event.messaging.mqtt;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBrokerService {

	final static Logger logger = LoggerFactory.getLogger(MessageBrokerService.class);
	
	private static MessageBrokerService	instance	=	null;
	private static Object lock	=	new Object();
	private Thread	messageSubscriberThread	=	null;
	
	public static MessageBrokerService getInstance(){
		synchronized (lock) {
			if(instance==null){
				instance	=	new MessageBrokerService();
			}
		}
		return instance;
	}

	
	public boolean stopMQTTService(){
		try{
		/** Start MQTT Subscriber	******************************************/
			if(logger.isInfoEnabled()){
				logger.info("Stopping MQTT Service...");	
			}
			
			MessageSubscriber	subscriber	=	MessageSubscriber.getInstance();
			if(subscriber.stop()){
				if(logger.isInfoEnabled()){
					logger.info("Stopped MQTT Service Successfully...");	
				}				
			} else {
				if(logger.isInfoEnabled()){
					logger.info("Stop Unsuccessful for MQTT Service...");	
				}				

			}
			messageSubscriberThread.interrupt();			
			MessagePublisher	pub	=	MessagePublisher.getMessagePublisher();
			pub.spot();
			
		} catch (Throwable th){
			th.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean startMQTTService(){
		try{
		/** Start MQTT Subscriber	******************************************/
			
			if(logger.isDebugEnabled()){
				logger.debug("Starting MQTT Listener Service...");				
			}
			

			MessageSubscriber	subscriber	=	MessageSubscriber.getInstance();
			MessageSubscriberRunnable run	=	new MessageSubscriberRunnable();
			run.init(subscriber);

			messageSubscriberThread	=	new Thread(run);
			messageSubscriberThread.start();
		} catch (Throwable th){
			th.printStackTrace();
			return false;
		}
		if(logger.isInfoEnabled()){
			logger.info("Started MQTT Listener Service...");				
		}

		return true;
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()){
			logger.debug("Starting Message Service Broker");	
		}
		
		
		MessageBrokerService	ins	=	MessageBrokerService.getInstance();
		ins.startMQTTService();
		
		boolean	isRunning	=	false;
		
		
		
		while (isRunning){
			try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s = bufferRead.readLine();
		    
		    if(s!=null && s.equals("s")){
		    	ins.stopMQTTService();
		    	isRunning	=	false;		    	
		    	//System.exit(0);
		    } else if(s!=null && s.equals("u")){
		    }
		    
		    System.out.println(s);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		System.out.println("\n Exiting....");
		//-Djava.library.path=D:\Home_Auto\raspberry\Latest\java_rasp\lib
		
	}

}
//O11991100000000
//O1121110000000000
//O1121110000000000
//O1121110000000000