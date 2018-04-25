package com.ni3.ag.navigator.server.domain;

public class Formula{

	private Integer id;
	private Attribute attribute;
	private String formula;

	public Attribute getAttribute(){
		return attribute;
	}

	public void setAttribute(Attribute attribute){
		this.attribute = attribute;
	}

	public String getFormula(){
		return formula;
	}

	public Integer getId(){
		return id;
	}

	public void setFormula(final String formula){
		this.formula = formula;
	}

	public void setId(final Integer id){
		this.id = id;
	}

}
