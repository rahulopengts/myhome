package org.openhab.ui.webapp.cloud.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openhab.ui.webapp.cloud.exception.CloudException;
import org.openhab.ui.webapp.cloud.exception.CloudExceptionManager;
import org.openhab.ui.webapp.cloud.exception.CloudMessageConstants;

public class CloudSessionManager {

	public static final String PAGERRENDERER	=	"PAGERRENDERER";
	public static final String MODELREPO		=	"MODELREPO";
	public static final String ITEMREGISTRY		=	"ITEMREGISTRY";
	public static final String EVENTPUBLISHER		=	"EVENTPUBLISHER";
	
	public static HttpSession getSession(HttpServletRequest	request,HttpServletResponse response) throws  CloudException{
		HttpSession session	=	null;
		if(request==null){
			
			CloudExceptionManager.throwException(CloudMessageConstants.HTTPREQUEST_NULL, null,"");
		} else {
			session	=	request.getSession(false);
			if(session==null){
				session	=	request.getSession(true);
			} else {
				
			}
		}
		return session;
	}
	
	
	public static Object getAttribute(HttpSession session,String attributeKey) throws CloudException {
		Object attributeValue	=	null;
		if(session==null){
			CloudExceptionManager.throwException(CloudMessageConstants.SESSION_NULL, null,"");
		} else {
			attributeValue	=	session.getAttribute(attributeKey);
//			if(attributeValue==null){
//				CloudExceptionManager.throwException(CloudMessageConstants.ATTRIBUTE_NULL, null,attributeKey+"is null");
//			}
		}
		return attributeValue;
	}
	
	public static void setAttribute(HttpSession session,String attributeKey,Object attributeValue) throws CloudException {
		
		if(session==null){
			CloudExceptionManager.throwException(CloudMessageConstants.SESSION_NULL, null,"");
		} else {
			session.setAttribute(attributeKey,attributeValue);
		}
	}

}
