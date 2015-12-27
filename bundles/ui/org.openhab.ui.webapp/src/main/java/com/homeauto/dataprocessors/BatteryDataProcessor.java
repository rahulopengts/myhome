package com.homeauto.dataprocessors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatteryDataProcessor extends AnalogueDataProcessor {

	final static Logger logger = LoggerFactory.getLogger(BatteryDataProcessor.class);
	
	int WARNING_MARK			=	3500;
	int NORMAL_MARK_LOW			=	3500;
	int REPLACE_MARK_LOW		=	2900;
	
	String STR_WARNING_MARK			=	"4";
	String STR_NORMAL_MARK_LOW			=	"5";
	String STR_REPLACE_MARK_LOW		=	"3";
	
	
	@Override
	public String processInboundData(String processMessage,String node) {
		if(logger.isDebugEnabled()){
			logger.debug("Received value for processing "+processMessage);
		}	
		
		String analogueValue	=	null;
		int intAnalogueVal		=	0;
		String retValue	=	"100";
		
		if(processMessage.charAt(ANALOGUE_VAL_INDICATOR_POS)==ANALOGUE_LIGHT_BATTERY_CHAR){
			analogueValue	=	processMessage.substring(ANALOGUE_VAL_INDICATOR_POS+1, ANALOGUE_VAL_INDICATOR_POS+1+ANALOGUE_VAL_INDICATOR_LEN);
			intAnalogueVal	=	Integer.valueOf(analogueValue).intValue();
			
		}
		
		if(logger.isErrorEnabled()){
			logger.error("Received intensity level is "+analogueValue);
		}
		
		   if(intAnalogueVal>=NORMAL_MARK_LOW){
			   if(logger.isDebugEnabled()){
				   logger.debug("Battery at Normal Level "+intAnalogueVal+" for node "+node);
			   }
			   
			   retValue	=	 STR_NORMAL_MARK_LOW;
		   } else if(intAnalogueVal<WARNING_MARK){
			   if(logger.isDebugEnabled()){
				   logger.debug("Battery at Warning level "+intAnalogueVal+" for node "+node);
			   }
			   retValue	=	 STR_WARNING_MARK;   
		   } else if(intAnalogueVal<REPLACE_MARK_LOW) {
			   if(logger.isDebugEnabled()){
				   logger.debug("Battery to be replaced "+intAnalogueVal+" for node "+node);
			   }
			   retValue	=	 STR_REPLACE_MARK_LOW;
		   }
		   //retValue	=	intAnalogueVal+"";
		
		return retValue;
	}

}
