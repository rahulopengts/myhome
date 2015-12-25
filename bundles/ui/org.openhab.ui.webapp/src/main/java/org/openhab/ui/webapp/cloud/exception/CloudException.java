package org.openhab.ui.webapp.cloud.exception;

public class CloudException extends Throwable {

	private String messageId	=	null;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private String message	=	null;
	
}
