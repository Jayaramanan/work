/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.calc;

import java.util.Map;

import com.ni3.ag.adminconsole.domain.FormulaHolder;
import com.ni3.ag.adminconsole.shared.script.ScriptDataAdapter;

public class ObjectDataAdapter implements ScriptDataAdapter{

	private FormulaHolder formula;
	private Map<String, Object> dataMap;

	public ObjectDataAdapter(FormulaHolder formula, Map<String, Object> dataMap){
		this.formula = formula;
		this.dataMap = dataMap;
	}

	@Override
	public FormulaHolder getFormula(){
		return formula;
	}

	@Override
	public Map<?, ?> getData(){
		return dataMap;
	}

}
