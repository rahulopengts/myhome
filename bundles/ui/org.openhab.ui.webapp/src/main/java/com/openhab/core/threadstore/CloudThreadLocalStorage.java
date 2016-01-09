package com.openhab.core.threadstore;

import com.openhab.core.event.dto.EventObject;

public class CloudThreadLocalStorage {

	private static ThreadLocal<EventObject> localEventObject = new ThreadLocal<EventObject>();
    private static ThreadLocal<String> localHomeId = new ThreadLocal<String>();

    public static EventObject getLocalEventObject() {
        return localEventObject.get();
    }

    public static void setLocalEventObject(EventObject eventObj) {
    	localEventObject.set(eventObj);
    }

    public static String getLocalHomeId() {
        return localHomeId.get();
    }

    public static void setLocalHomeId(String homeId) {
    	localHomeId.set(homeId);
    }

    public static void cleanup() {
    	localEventObject.remove();
        localHomeId.remove();
    }
    
}
