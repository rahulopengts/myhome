package com.openhab.core.event.handler;

import org.openhab.model.rule.internal.engine.RuleEngine;
import org.openhab.model.script.internal.engine.ScriptEngineImpl;

import com.openhab.core.event.dto.EventObject;

import org.openhab.core.scriptengine.Script;
import org.openhab.core.scriptengine.ScriptEngine;

public class RuleHandler extends AbstractEventHandler {

	@Override
	public void handleRule(EventObject eventObject, String topic,
			String messageContent) {
		// TODO Auto-generated method stub
		RuleEngine ruleEngine	=	new RuleEngine();
		ruleEngine.setItemRegistry(eventObject.getItemRegistry());
		ScriptEngine	scriptEngine	=	new ScriptEngineImpl();
		ruleEngine.setScriptEngine(scriptEngine);
		ruleEngine.setModelRepository(eventObject.getModelRepository());
		
		
	}

}
