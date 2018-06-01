/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.script.impl;

import java.util.Map;

import com.ni3.ag.adminconsole.domain.FormulaHolder;
import com.ni3.ag.adminconsole.shared.script.ScriptDataAdapter;

public class NodeDataAdapter implements ScriptDataAdapter{

	private FormulaHolder holder;
	private Map<?, ?> data;

	public NodeDataAdapter(FormulaHolder holder, Map<?, ?> data){
		this.holder = holder;
		this.data = data;
	}

	@Override
	public FormulaHolder getFormula(){
		return holder;
	}

	@Override
	public Map<?, ?> getData(){
		return data;
	}

}
