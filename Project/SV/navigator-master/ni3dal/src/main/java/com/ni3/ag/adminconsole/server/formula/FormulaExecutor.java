package com.ni3.ag.adminconsole.server.formula;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.FormulaHolder;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.script.impl.JRubyScriptEngineImpl;
import com.ni3.ag.adminconsole.shared.script.ScriptDataAdapter;
import com.ni3.ag.adminconsole.shared.script.ScriptEngine;
import com.ni3.ag.adminconsole.validation.ACException;

public class FormulaExecutor{
	private static final Logger log = Logger.getLogger(FormulaExecutor.class);
	private static ScriptEngine scriptEngine;

	static{
		log.debug("script engine create");
		scriptEngine = new JRubyScriptEngineImpl();
	}

	public static void recalcObjectFields(ObjectDefinition objectDefinition,
			final Map<ObjectAttribute, Object> attributeToValueMap){
		final Map<String, Object> dataMap = new HashMap<String, Object>();
		for (ObjectAttribute a : attributeToValueMap.keySet()){
			dataMap.put(a.getName(), attributeToValueMap.get(a));
		}
		// we should go through all formula attributes - not only passed for update
		// couse even not editable/visible formula attributes can be affected by edited fields
		for (ObjectAttribute a : objectDefinition.getObjectAttributes()){
			if (!a.isFormulaAttribute()){
				continue;
			}
			if (a.getFormula() == null){
				log.warn("Attribute `" + a.getName() + "` has predefined==formula, but formula == null");
				continue;
			}
			try{
				Object value = calculateValue(dataMap, a);
				attributeToValueMap.put(a, value);
			} catch (ACException e){
				log.error("Error execute formula (script) for Attribute: " + a.getName());
			}
		}
	}

	private static Object calculateValue(final Map<String, Object> dataMap, ObjectAttribute a) throws ACException{
		final FormulaHolder formula = new FormulaHolderImpl(a.getId(), a.getFormula().getFormula());
		final ScriptDataAdapter sda = new ObjectDataAdapter(formula, dataMap);
		try{
			final long startTime = System.currentTimeMillis();
			final Object value = scriptEngine.calculateValue(sda);

			if (log.isDebugEnabled()){
				log.debug("Calculated value: " + value);
				log.debug("Formula execution time: " + (System.currentTimeMillis() - startTime));
			}
			return value;
		} finally{
			scriptEngine.dispose();
		}
	}

	public static Object recalculateFormulaValues(ObjectAttribute attribute, final Map<String, Object> dataMap)
			throws ACException{
		return calculateValue(dataMap, attribute);
	}

}
