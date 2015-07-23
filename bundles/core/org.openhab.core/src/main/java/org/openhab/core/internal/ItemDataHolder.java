package org.openhab.core.internal;

import java.util.HashMap;

public class ItemDataHolder {

	public static ItemDataHolder itemDataHolder	=	null;
	public static Object object	=	new Object();
	private HashMap<String, String> dataMap	=	null;
	
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
