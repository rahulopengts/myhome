package com.openhab.core.threadstore;




import com.openhab.core.dto.CloudMasterData;



public class CloudThreadLocalStorage {

	

		private static ThreadLocal<CloudMasterData> localCloudMasterData = new ThreadLocal<CloudMasterData>();
	    private static ThreadLocal<String> localHomeId = new ThreadLocal<String>();

//		private static ThreadLocal<EventObject> localEventObject = new ThreadLocal<EventObject>();
//
//	    public static EventObject getEventObject() {
//	        return localEventObject.get();
//	    }
//
//	    public static void setEventObject(EventObject eventObj) {
//	    	localEventObject.set(eventObj);
//	    }
		
	    public static CloudMasterData getCloudMasterData() {
	        return localCloudMasterData.get();
	    }

	    public static void setCloudMasterData(CloudMasterData eventObj) {
	    	localCloudMasterData.set(eventObj);
	    }

	    public static String getLocalHomeId() {
	        return localHomeId.get();
	    }

	    public static void setLocalHomeId(String homeId) {
	    	localHomeId.set(homeId);
	    }

	    public static void cleanup() {
	    	localCloudMasterData.remove();
	        localHomeId.remove();
	    }
	    
	}
