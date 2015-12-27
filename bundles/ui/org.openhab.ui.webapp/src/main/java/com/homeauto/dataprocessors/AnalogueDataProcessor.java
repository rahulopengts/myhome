package com.homeauto.dataprocessors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AnalogueDataProcessor implements IDataProcessors {

	public static char ANALOGUE_VAL_INDICATOR_CHAR		=	'A';
	
	public static char ANALOGUE_LIGHT_INTENSITY_CHAR	=	'I';
	
	public static char ANALOGUE_LIGHT_BATTERY_CHAR		=	'B';
	
	
	public static int ANALOGUE_VAL_INDICATOR_POS	=	11;
	
	public static int ANALOGUE_VAL_INDICATOR_LEN	=	4;
	
	public static int BATTER_COMPONENT_ID	=	6;

	final static Logger logger = LoggerFactory.getLogger(AnalogueDataProcessor.class);


	public static IDataProcessors getProcessorClass(String processMessage){
		
		IDataProcessors dataProcessor	=	null;
		char analogueDataType	=	processMessage.charAt(AnalogueDataProcessor.ANALOGUE_VAL_INDICATOR_POS);
		if(logger.isDebugEnabled()){
			logger.debug("Analogue char is "+analogueDataType);
		}
		
		if(analogueDataType==ANALOGUE_LIGHT_INTENSITY_CHAR){
			dataProcessor	=	new IntensityDataProcessor();
		}
		
		if(analogueDataType==ANALOGUE_LIGHT_BATTERY_CHAR){
			dataProcessor	=	new BatteryDataProcessor();
		}
	
		return dataProcessor;
	}
}
