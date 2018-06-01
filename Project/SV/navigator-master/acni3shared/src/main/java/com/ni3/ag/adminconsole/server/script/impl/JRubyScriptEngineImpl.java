/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.script.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jruby.embed.ScriptingContainer;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.script.ScriptDataAdapter;
import com.ni3.ag.adminconsole.shared.script.ScriptEngine;
import com.ni3.ag.adminconsole.validation.ACException;

public class JRubyScriptEngineImpl implements ScriptEngine{
	private static final Logger log = Logger.getLogger(JRubyScriptEngineImpl.class);
	private static ScriptingContainer container = null;

	private ScriptingContainer getContainer(){
		if (container == null){
			container = new ScriptingContainer();
		}
		return container;
	}

	@Override
	public Object calculateSliceValueForNode(ScriptDataAdapter sda) throws ACException{
		ScriptingContainer container = getContainer();
		Map<?, ?> dataMap = sda.getData();
		if (dataMap != null){
			for (Object key : dataMap.keySet()){
				Object value = dataMap.get(key);
				if (value instanceof BigInteger){
					value = ((BigInteger) value).intValue();
				} else if (value instanceof BigDecimal){
					value = new Float(String.valueOf(value));
				}

				String k = "@" + ((String) key);
				container.put(k, value);
			}
		}
		String formula = sda.getFormula().getFormula();

		Object result = null;
		try{
			result = container.runScriptlet(formula);
			container.getVarMap().clear();
		} catch (Throwable th){
			throw new ACException(TextID.MsgErrorExecutingScript, new String[] { th.getMessage() });
		}
		log.debug("Result: " + result);

		return result;
	}

	@Override
	public void dispose(){
		container = null;
	}

	@Override
	public Object calculateValue(ScriptDataAdapter sda) throws ACException{
		ScriptingContainer container = getContainer();
		Map<?, ?> dataMap = sda.getData();
		if (dataMap != null){
			for (Object key : dataMap.keySet()){
				Object value = dataMap.get(key);
				String k = "@" + ((String) key);
				container.put(k, value);
			}
		}
		String formula = sda.getFormula().getFormula();

		Object result = null;
		try{
			result = container.runScriptlet(formula);
			container.getVarMap().clear();
		} catch (Throwable th){
			throw new ACException(TextID.MsgErrorExecutingScript, new String[] { th.getMessage() });
		}
		log.debug("Result: " + result);

		return result;
	}
}
