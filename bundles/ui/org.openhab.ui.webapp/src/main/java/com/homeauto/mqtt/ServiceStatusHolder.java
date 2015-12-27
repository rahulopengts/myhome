package com.homeauto.mqtt;

public class ServiceStatusHolder {

	private boolean mqttPubRunning	=	false;
	
	public boolean isMqttPubRunning() {
		return mqttPubRunning;
	}

	public void setMqttPubRunning(boolean mqttPubRunning) {
		this.mqttPubRunning = mqttPubRunning;
	}

	public boolean isMqttSubRunning() {
		return mqttSubRunning;
	}

	public void setMqttSubRunning(boolean mqttSubRunning) {
		this.mqttSubRunning = mqttSubRunning;
	}

	public boolean isUsbSerialReaderRunning() {
		return usbSerialReaderRunning;
	}

	public void setUsbSerialReaderRunning(boolean usbSerialReaderRunning) {
		this.usbSerialReaderRunning = usbSerialReaderRunning;
	}

	public boolean isUsbSerialWriterRunning() {
		return usbSerialWriterRunning;
	}

	public void setUsbSerialWriterRunning(boolean usbSerialWriterRunning) {
		this.usbSerialWriterRunning = usbSerialWriterRunning;
	}

	private boolean mqttSubRunning	=	false;

	private boolean	usbSerialReaderRunning	=	false;
	private boolean usbSerialWriterRunning	=	false;

	private static ServiceStatusHolder	instance	=	null;
	private static Object	lock	=	new Object();
	
	
	public static ServiceStatusHolder	getInstance(){
		if(instance==null){
			synchronized(lock){
				instance	=	new ServiceStatusHolder();
			}
		}
		
		return instance;
	}
	
}
