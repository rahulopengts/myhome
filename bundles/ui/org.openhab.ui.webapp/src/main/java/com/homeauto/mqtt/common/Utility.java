package com.homeauto.mqtt.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openhab.core.event.messaging.mqtt.MessagePublisher;

//O12345678900011
//I139990000AAAAA

/*
 * Sample States for Inbound Messages
 *	L1N1L2N1S		I11219[1/0][1/0][1/0][1/0][A][A][A][A][A]	
 *	L1N1L2N1S		I11219[1/0][1/0][1/0][1/0][A][A][A][A][A]	
 *	L1N1L2N2S		I11229[1/0][1/0][1/0][1/0][A][A][A][A][A]	
 *	L1N1L2N2S		I11229[1/0][1/0][1/0][1/0][A][A][A][A][A]	
 *	L1N2L2N3S		I12239[1/0][1/0][1/0][1/0][A][A][A][A][A]	
 *	L1N2L2N3S		I12239[1/0][1/0][1/0][1/0][A][A][A][A][A]	
 *	L1N3L2N4S		I13249[1/0][1/0][1/0][1/0][A][A][A][A][A]	
*/

public class Utility {

	final static Logger logger = LoggerFactory.getLogger(Utility.class);
	public final static boolean isRXTXMode	=	true;
	public final static String STOP	=	"STOP";
	
	public static String OUTBOUND_CHAR	=	"O";
	public static String INBOUND_CHAR	=	"I";
	static MessagePublisher	pub	=	null;	
	public static boolean is_RF_Serial	=	true;
	public static String SERIAL_MSG_PREFIX	=	"~";
	public static String SERIAL_MSG_SUFFIX	=	"#";
	
	/**
	 * 	This is OFFSET OF SWITCH STATE AFTER INSERTING L/S/N into inbound message.for S0. So SWITCH STATE WILL BE DETERMINED AS
	 *  SWITCH At S0 - POSITION IN INBOUND ARRAY WILL 10
	 *  SWITCH At S1 - POSITION IN INBOUND ARRAY WILL 11
	 *  SWITCH At S2 - POSITION IN INBOUND ARRAY WILL 12
	 *  SWITCH At S3 - POSITION IN INBOUND ARRAY WILL 13
	 *  Check the method processInboundMessage
	 */
	public static int SWITCHSTATE_OFFSET	=	10;
	
	static int LG_LIGHT_ON_OFF_BIT	=	7;
	
	public static String fomatToMQTT(String receivedFromNode){
		if(receivedFromNode.charAt(LG_LIGHT_ON_OFF_BIT)=='1'){
			return "ON";	
		} else if(receivedFromNode.charAt(LG_LIGHT_ON_OFF_BIT)=='0') {
			return "OFF";
		}
		
		return "OFF";
	}


	public static String getDateTime(){
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		// Get the date today using Calendar object.
		Date today = Calendar.getInstance().getTime();        
		// Using DateFormat format method we can create a string 
		// representation of a date with the defined format.
		String reportDate = df.format(today);
		return reportDate;
	}
	public static String fomatToUSB(String receivedFromNode){
		String nodeId	=	receivedFromNode.substring(0, 3);
    	if(logger.isDebugEnabled()){
    		logger.debug("\n SUBS "+nodeId);
    	}
		AppPropertiesMap	n	=	new AppPropertiesMap();
		Integer nodePipeAddress	=	n.getAddressPipeMap().get(nodeId);
		if(nodePipeAddress!=null){
			receivedFromNode	=	nodePipeAddress.toString()+receivedFromNode;
			if(logger.isDebugEnabled()){
				logger.debug("\n RETURNED FORMATTED VALUE IS "+receivedFromNode);
			}
		} else {
			receivedFromNode	=	"9";
		}
		return receivedFromNode;
	}

	
	public static String TestFomatMQTTMessage(boolean isOn){
		if(isOn){
			return "ON";
		} else {
			return "OFF";
		}
	}
	
	public static String toOutboundFormat(String messageFromOpenHAB){
		return removeCharsForOutBound(messageFromOpenHAB);
		
	}
	
	public static String removeCharsForOutBound(String messageFromOpenHAB) {
        StringBuffer buf = new StringBuffer(messageFromOpenHAB.length());
        buf.setLength(messageFromOpenHAB.length());
        int current = 0;
        for (int i=0; i<messageFromOpenHAB.length(); i++){
            char cur = messageFromOpenHAB.charAt(i);
            if( (cur == 'L') || (cur == 'S') || (cur == 'N') ){
            	
            } else {
            	buf.setCharAt(current++, cur);
            }
        }
        return buf.toString();
    }
	//#I11219010 0 A A A #
    //0123456789
	public static void publishMessage(String componentId,String state){
    	if(logger.isDebugEnabled()){
    		logger.debug("Data received on the Serial port is "+state+" for Node Id : "+componentId);
    	}        	

//		if(nodeId.equals("000")){
//			nodeId	=	"001";
//		}
		pub.publishMessaeg(state,"/"+componentId);			
		
	}
	public static void main(String arg[]){
		
		String	m	=	"I1121 S 1001 AAAAA";
		
	}
}
