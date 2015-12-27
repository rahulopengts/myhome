package com.homeauto.mqtt;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MessageSubscriber	subscriber	=	MessageSubscriber.getInstance();
		
		MessageSubscriberRunnable run	=	new MessageSubscriberRunnable();
		run.init(subscriber);
		Thread th	=	new Thread(run);
		
		th.start();
		
		//run.init();
		
	}

}
