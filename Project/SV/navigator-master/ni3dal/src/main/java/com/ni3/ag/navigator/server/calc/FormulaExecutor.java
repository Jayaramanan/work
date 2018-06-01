/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.calc;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.FormulaHolder;
import com.ni3.ag.adminconsole.server.script.impl.JRubyScriptEngineImpl;
import com.ni3.ag.adminconsole.shared.script.ScriptDataAdapter;
import com.ni3.ag.adminconsole.shared.script.ScriptEngine;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.navigator.server.dictionary.DBObject;
import com.ni3.ag.navigator.server.domain.Attribute;
import org.apache.log4j.Logger;

public class FormulaExecutor{
	private static final Logger log = Logger.getLogger(FormulaExecutor.class);
	private static ScriptEngine scriptEngine;
	static{
		log.debug("script engine create");
		scriptEngine = new JRubyScriptEngineImpl();
	}

	public ScriptEngine getScriptEngine(){
		return scriptEngine;
	}

	public void setScriptEngine(final ScriptEngine scriptEngine){
		FormulaExecutor.scriptEngine = scriptEngine;
	}

	public static void recalcObjectFields(final DBObject obj){
		final Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<Attribute, String> attributeToValueMap = obj.getAttributeToValueMap();
		for (Attribute a : attributeToValueMap.keySet()){
			dataMap.put(a.getName(), makeValue(attributeToValueMap.get(a), a));
		}
		// we should go through all formula attributes - not only passed for update
		// couse even not editable/visible formula attributes can be affected by edited fields
		for (Attribute a : obj.ent.getAttributes()){
			if (!a.isFormula()){
				continue;
			}
			if (a.getFormula() == null){
				log.warn("Attribute `" + a.getName() + "` has predefined==formula, but formula == null");
				continue;
			}
			final FormulaHolder formula = new FormulaHolderImpl(a.getId(), a.getFormula());
			final ScriptDataAdapter sda = new ObjectDataAdapter(formula, dataMap);
			try{
				final long startTime = System.currentTimeMillis();
				final Object value = scriptEngine.calculateValue(sda);

				if (log.isDebugEnabled()){
					log.debug("Formula: " + a.getFormula());
					log.debug("Calculated value: " + value);
					log.debug("Formula execution time: " + (System.currentTimeMillis() - startTime));
				}
				final String convertedValue = convertValue(value);
				obj.setAttributeValue(a, convertedValue);
			} catch (final ACException e){
				log.error("Error execute formula (script) for Attribute: " + a.getName());
			} finally{
				scriptEngine.dispose();
			}
		}
	}

	static Object makeValue(final String string, final Attribute a){
		try{
			switch (a.getDatabaseDatatype()){
				default:
				case TEXT:
					return string;
				case DECIMAL:
					return string != null && !string.isEmpty() ? Double.valueOf(string) : null;
				case INT:
					return string != null && !string.isEmpty() ? Integer.valueOf(string) : null;
			}
		} catch (final Exception ex){
			return string;
		}
	}

	static String convertValue(final Object value){
		if (value == null){
			return "";
		}
		if (value instanceof String){
			return (String) value;
		}
		return value.toString();
	}

	public static void init(){
		// to initialize script engine,
		// we just force it to execute simple script of datetime
		// getting and formating to human readable state
		log.debug("-------------Initializing Script engine------------------");
		try{
			final Object o = scriptEngine.calculateValue(createInitialScriptDataAdapter());
			if (log.isDebugEnabled()){
				log.debug("simple init script returned value: " + o);
			}
		} catch (final ACException e){
			log.error("Problem initializing script engine", e);
		}
		log.debug("***************init done*********************************");
	}

	private static ScriptDataAdapter createInitialScriptDataAdapter(){

		final FormulaHolder formula = new FormulaHolderImpl(0, "Time.now.strftime(\"%Y-%m-%d %H:%M:%S\")");
		return new ObjectDataAdapter(formula, new HashMap<String, Object>());
	}

}
