/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ChartAttribute implements Serializable, Cloneable{
	private static final long serialVersionUID = 5634658085515184521L;

	private Integer id;
	private String rgb;
	private ObjectChart objectChart;
	private ObjectAttribute attribute;

	public ChartAttribute(){
	}

	public ChartAttribute(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return this.id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getRgb(){
		return this.rgb;
	}

	public void setRgb(String rgb){
		this.rgb = rgb;
	}

	public ObjectChart getObjectChart(){
		return objectChart;
	}

	public void setObjectChart(ObjectChart objectChart){
		this.objectChart = objectChart;
	}

	public ObjectAttribute getAttribute(){
		return attribute;
	}

	public void setAttribute(ObjectAttribute attribute){
		this.attribute = attribute;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (getId() == null || !(obj instanceof ChartAttribute)){
			return false;
		}

		return getId().equals(((ChartAttribute) obj).getId());
	}

	public ChartAttribute clone() throws CloneNotSupportedException{
		return (ChartAttribute) super.clone();
	}

	public ChartAttribute clone(Integer id, ObjectChart objectChart, ObjectAttribute attribute)
	        throws CloneNotSupportedException{
		ChartAttribute cca = clone();
		cca.setId(id);
		cca.setObjectChart(objectChart);
		cca.setAttribute(attribute);
		return cca;
	}

}
