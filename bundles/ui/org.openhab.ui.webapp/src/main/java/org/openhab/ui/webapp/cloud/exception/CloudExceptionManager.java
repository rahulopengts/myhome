package org.openhab.ui.webapp.cloud.exception;

public class CloudExceptionManager {

	public static CloudException throwException(String messageId, Exception exception,String param) throws CloudException {
	
		CloudException	cloudException	=	new CloudException();
		cloudException.setMessageId(messageId);
		
		cloudException.setMessage(exception.getMessage());
		throw cloudException;
	}
}
