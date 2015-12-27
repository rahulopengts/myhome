package com.homeauto.mqtt;

import java.util.HashMap;

public class AppProperty {

	private HashMap<String, String>	propertyKeyVal	=	null;
	
	private AppProperty	property	=	null;
	
	private AppProperty(){
		propertyKeyVal	=	new HashMap<String,String>();
	}
	
	public AppProperty getInstance(){
		
		if(property==null){
			property	=	new AppProperty();
			load();
		}
		
		return property;
	}
	
	private void load(){
		
	}
	
}
