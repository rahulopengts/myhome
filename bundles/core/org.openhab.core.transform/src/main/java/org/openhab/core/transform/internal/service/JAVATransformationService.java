package org.openhab.core.transform.internal.service;

import org.openhab.core.transform.TransformationException;
import org.openhab.core.transform.TransformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAVATransformationService  implements TransformationService {

	static final Logger logger = 
		LoggerFactory.getLogger(JAVATransformationService.class);

	@Override
	public String transform(String function, String source)
			throws TransformationException {
		// TODO Auto-generated method stub
		System.out.println("\nJAVATransformationService-transform->function->"+function+"-source->"+source);
		return null;
	}

}
