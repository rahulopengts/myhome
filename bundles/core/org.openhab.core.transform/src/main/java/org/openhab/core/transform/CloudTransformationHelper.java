package org.openhab.core.transform;

import org.openhab.core.transform.internal.service.JAVATransformationService;
import org.osgi.framework.BundleContext;

public class CloudTransformationHelper extends TransformationHelper {

	
	static public TransformationService getTransformationService(String transformationType) {
		TransformationService	transformationService	=	null;
		if(transformationType!=null && transformationType.equals("JAVA")){
			transformationService	=	new JAVATransformationService();
		}
		
		return transformationService;
	}
}
