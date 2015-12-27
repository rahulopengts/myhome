package com.homeauto.mqtt.common;
import java.util.HashMap;
import java.util.Map;

public class AppPropertiesMap {

	private Map<String, Integer> addressPipeMap	=	new HashMap();
	
	public Map<String, Integer> getAddressPipeMap() {
		return addressPipeMap;
	}

	public void setAddressPipeMap(Map<String, Integer> addressPipeMap) {
		this.addressPipeMap = addressPipeMap;
	}

	public AppPropertiesMap(){
		addressPipeMap.put("001", new Integer(1));
		addressPipeMap.put("002", new Integer(2));
		addressPipeMap.put("003", new Integer(0));
		addressPipeMap.put("004", new Integer(4));
		addressPipeMap.put("005", new Integer(5));

		
		addressPipeMap.put("009", new Integer(9));
	}
	
	public static String MQTT_SERVER_HOST_IP	=	"localhost";
	public static String MQTT_SERVER_PORT		=	"1883";
	public static String MQTT_SUBS_TOPIC		=	"/raspberry";
	public static String MQTT_CLIENT_ID			=	"JavaSample";
	
	
	public static String USB_PORT1				=	"COM15";//"/dev/ttyUSB0";//COM14
	

}
