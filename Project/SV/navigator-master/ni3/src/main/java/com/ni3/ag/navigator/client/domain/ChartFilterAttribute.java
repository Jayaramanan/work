/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

public class ChartFilterAttribute implements Cloneable{
	private int attributeId;
	private double minChartAttrVal;
	private double maxChartAttrVal;
	private boolean excluded;

	public ChartFilterAttribute(int attributeId){
		this(attributeId, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public ChartFilterAttribute(int attributeId, double minChartAttrVal, double maxChartAttrVal){
		this(attributeId, minChartAttrVal, maxChartAttrVal, false);
	}

	public ChartFilterAttribute(int attributeId, double minChartAttrVal, double maxChartAttrVal, boolean excluded){
		this.attributeId = attributeId;
		this.minChartAttrVal = minChartAttrVal;
		this.maxChartAttrVal = maxChartAttrVal;
		this.excluded = excluded;
	}

	public int getAttributeId(){
		return attributeId;
	}

	public void setAttributeId(int attributeId){
		this.attributeId = attributeId;
	}

	public double getMinChartAttrVal(){
		return minChartAttrVal;
	}

	public void setMinChartAttrVal(double minChartAttrVal){
		this.minChartAttrVal = minChartAttrVal;
	}

	public double getMaxChartAttrVal(){
		return maxChartAttrVal;
	}

	public void setMaxChartAttrVal(double maxChartAttrVal){
		this.maxChartAttrVal = maxChartAttrVal;
	}

	public boolean isExcluded(){
		return excluded;
	}

	public void setExcluded(boolean excluded){
		this.excluded = excluded;
	}

	@Override
	protected ChartFilterAttribute clone() throws CloneNotSupportedException{
		return (ChartFilterAttribute) super.clone();
	}
}
