package com.openhab.core.event.admin;

import java.util.concurrent.Executors;

import org.openhab.core.items.ItemRegistry;

import com.google.common.eventbus.AsyncEventBus;
import com.openhab.core.event.dto.EventObject;
import com.openhab.core.event.handler.DataStoreHandler;
import com.openhab.core.event.handler.IEventHandler;

public class AdminEventImpl implements IAdminEvent {
	AsyncEventBus	asyncEventBus	=	null;
	
	@Override
	public void dispatchEvent(EventObject eventObject,IEventHandler handler) {
		// TODO Auto-generated method stub
		try{
			System.out.println("\nAdminEventImpl->dispatchEvent-00");
			AdminEventManager.getInstance().getAsyncEventBus().register(handler);
			System.out.println("\nAdminEventImpl->dispatchEvent-1");
			AdminEventManager.getInstance().getAsyncEventBus().post(eventObject);
			System.out.println("\nAdminEventImpl->dispatchEvent-2");
		} catch (Throwable e){
			e.printStackTrace();
		}
	}

	
//	public void testNonAsyncEventSubscriber() throws Exception {
//        asyncEventBus = new AsyncEventBus(Executors.newCachedThreadPool());
//        doneSignal = new CountDownLatch(numberLongEvents);
//        longProcessSubscriber = new LongProcessSubscriber(asyncEventBus, doneSignal);
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < numberLongEvents; i++) {
//            asyncEventBus.post(new CashPurchaseEvent(1000l, "Stuff"));
//        }
//        doneSignal.await();
//        long elapsed = start - System.currentTimeMillis();
//        assertTrue(elapsed <= 3000l && elapsed < 3500l);
//    }
	
	public static void main(String [] arg){
		AdminEventImpl	admin	=	new AdminEventImpl();
		EventObject	eventObject	=	new EventObject();
		IEventHandler	eventHandler	=	new DataStoreHandler();
		admin.dispatchEvent(eventObject, eventHandler);
		
	}
}
