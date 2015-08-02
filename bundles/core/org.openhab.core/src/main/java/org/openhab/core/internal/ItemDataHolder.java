package org.openhab.core.internal;

import java.util.HashMap;

public class ItemDataHolder {

	public static ItemDataHolder itemDataHolder	=	null;
	public static Object object	=	new Object();
	
	//This holds the data of the current state of the Nodes.
	private HashMap<String, String> dataMap	=	null;
	
	//This hold the data for the profiles created for users
	private HashMap<String, String> profileDataMap	=	null;
	
	public HashMap<String, String> getProfileDataMap() {
		return profileDataMap;
	}

	public void setProfileDataMap(HashMap<String, String> profileDataMap) {
		this.profileDataMap = profileDataMap;
	}

	private ItemDataHolder(){
		dataMap	=	new HashMap<String, String>();
	}

	public static ItemDataHolder getItemDataHolder(){
		if(itemDataHolder==null){
			synchronized (object) {
				itemDataHolder	=	new ItemDataHolder();
				return itemDataHolder;
			}
		}
		return itemDataHolder;
	}
	
	
	public void addDataMap(String key,String value){
		dataMap.put(key, value);
	}
	
	public String getData(String key){
		String	data	=	(String)dataMap.get(key);
		return data;
	}
	
}
