package com.openhab.core.event.admin;

import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;

public class AdminEventManager {

	private static AdminEventManager instance	=	null;
	private static Object local	=	new Object();
	
	private static AsyncEventBus	asyncEventBus	=	null;
	
	public static AdminEventManager getInstance(){
		
		if(instance==null){
			synchronized (local) {
				instance	=	new AdminEventManager();
				init();
			}
		}
	return	instance;	
	}
	
	
	private static void init(){
		asyncEventBus	=	new AsyncEventBus(Executors.newCachedThreadPool());
	}
	
	
	public AsyncEventBus getAsyncEventBus(){
		return asyncEventBus;
	}
	
}
