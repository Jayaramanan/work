/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Formula implements Serializable, Cloneable{

	private static final long serialVersionUID = 7942591642031335348L;

	public static final Integer NOT_PREDEFINED = 0;
	public static final Integer PREDEFINED = 1;
	public static final Integer FORMULA_BASED = 2;
	public static final Integer FORMULA_PREDEFINED = 3;

	private Integer id;
	private ObjectAttribute attribute;
	private String formula;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public void setAttribute(ObjectAttribute attribute){
		this.attribute = attribute;
	}

	public ObjectAttribute getAttribute(){
		return attribute;
	}

	public void setFormula(String formula){
		this.formula = formula;
	}

	public String getFormula(){
		return formula;
	}

	public static List<Integer> getPredefinedTypes(){
		List<Integer> list = new ArrayList<Integer>();
		list.add(NOT_PREDEFINED);
		list.add(PREDEFINED);
		list.add(FORMULA_BASED);
		list.add(FORMULA_PREDEFINED);
		return list;
	}

	public Formula clone() throws CloneNotSupportedException{
		return (Formula) super.clone();
	}

	public Formula clone(Integer id, ObjectAttribute oa) throws CloneNotSupportedException{
		Formula clone = clone();
		clone.setId(id);
		clone.setAttribute(oa);
		return clone;
	}

	public static boolean isFormula(Integer value){
		return FORMULA_BASED.equals(value) || FORMULA_PREDEFINED.equals(value);
	}

	public static boolean isPredefined(Integer value){
		return PREDEFINED.equals(value) || FORMULA_PREDEFINED.equals(value);
	}
}
