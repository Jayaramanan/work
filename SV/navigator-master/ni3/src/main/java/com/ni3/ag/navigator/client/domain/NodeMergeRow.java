/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

public class NodeMergeRow{

	private Attribute attribute;
	private Object fromValue;
	private Object toValue;
	private boolean selected;

	public NodeMergeRow(Attribute attribute, Object fromValue, Object toValue, boolean selected){
		super();
		this.attribute = attribute;
		this.fromValue = fromValue;
		this.toValue = toValue;
		this.selected = selected;
	}

	public Attribute getAttribute(){
		return attribute;
	}

	public void setAttribute(Attribute attribute){
		this.attribute = attribute;
	}

	public Object getFromValue(){
		return fromValue;
	}

	public void setFromValue(Object fromValue){
		this.fromValue = fromValue;
	}

	public Object getToValue(){
		return toValue;
	}

	public void setToValue(Object toValue){
		this.toValue = toValue;
	}

	public boolean isSelected(){
		return selected;
	}

	public void setSelected(boolean selected){
		this.selected = selected;
	}

}
