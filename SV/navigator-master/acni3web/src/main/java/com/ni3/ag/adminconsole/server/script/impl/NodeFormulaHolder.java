/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.script.impl;

import com.ni3.ag.adminconsole.domain.FormulaHolder;

public class NodeFormulaHolder implements FormulaHolder{

	private String formula;
	private Integer chartAttributeId;

	public NodeFormulaHolder(String formula, Integer chartAttributeId){
		this.formula = formula;
		this.chartAttributeId = chartAttributeId;
	}

	@Override
	public Integer getAttributeId(){
		return chartAttributeId;
	}

	@Override
	public String getFormula(){
		return formula;
	}

	@Override
	public void setFormula(String formula){
		this.formula = formula;
	}

}
