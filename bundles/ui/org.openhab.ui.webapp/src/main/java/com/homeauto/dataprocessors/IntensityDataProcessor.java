package com.homeauto.dataprocessors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.homeauto.mqtt.MessageBrokerService;

public class IntensityDataProcessor extends AnalogueDataProcessor {

	static int baseDayIntensity    =  982;
	static int nightCutOff         =  725;
	static int eveningCutOffStart  =  800;
	static int eveningCutOffEnd    =  725;
	
	static String DAY_MAP_VALUE		=	"30";
	static String NIGHT_MAP_VALUE	=	"20";
	static String EVENING_MAP_VALUE	=	"10";
	
	final static Logger logger = LoggerFactory.getLogger(IntensityDataProcessor.class);
	
	@Override
	public String processInboundData(String processMessage,String node) {
		// TODO Auto-generated method stub
		
		if(logger.isDebugEnabled()){
			logger.debug("Received value for processing "+processMessage);
		}
		
		String analogueValue	=	null;
		int intAnalogueVal		=	0;
		String retValue	=	"100";
		
		if(processMessage.charAt(ANALOGUE_VAL_INDICATOR_POS)==ANALOGUE_LIGHT_INTENSITY_CHAR){
			analogueValue	=	processMessage.substring(ANALOGUE_VAL_INDICATOR_POS+1, ANALOGUE_VAL_INDICATOR_POS+1+ANALOGUE_VAL_INDICATOR_LEN);
			intAnalogueVal	=	Integer.valueOf(analogueValue).intValue();
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Received intensity level is "+analogueValue);
		}
		
		   if(intAnalogueVal>=eveningCutOffStart){
			   if(logger.isDebugEnabled()){
				   logger.debug("Its Day with intensity level "+intAnalogueVal+" mapped to "+DAY_MAP_VALUE);
			   }
			   retValue	=	DAY_MAP_VALUE;
		   } else if(intAnalogueVal<eveningCutOffStart && intAnalogueVal>eveningCutOffEnd){
			   if(logger.isDebugEnabled()){
				   logger.debug("Its Evening with intensity level "+intAnalogueVal+" mapped to "+EVENING_MAP_VALUE);
			   }
			   retValue	=	EVENING_MAP_VALUE;
		   } else if(intAnalogueVal<eveningCutOffEnd) {
			   if(logger.isDebugEnabled()){
				   logger.debug("Its Night with intensity level "+intAnalogueVal+" mapped to "+NIGHT_MAP_VALUE);
			   }
			   retValue	=	NIGHT_MAP_VALUE;
		   }
		
		
		return retValue;
	}	
	
}
