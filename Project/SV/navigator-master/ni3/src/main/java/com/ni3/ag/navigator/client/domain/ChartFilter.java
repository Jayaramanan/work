/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

import java.util.ArrayList;
import java.util.List;

public class ChartFilter{
	private double minChartVal;
	private double maxChartVal;
	private List<ChartFilterAttribute> attributes;

	public ChartFilter(){
		this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
	}

	public ChartFilter(final double minChartVal, final double maxChartVal, final List<ChartFilterAttribute> attributes){
		this.minChartVal = minChartVal;
		this.maxChartVal = maxChartVal;
		if (attributes == null){
			this.attributes = new ArrayList<ChartFilterAttribute>();
		} else{
			this.attributes = attributes;
		}
	}

	public double getMinChartVal(){
		return minChartVal;
	}

	public void setMinChartVal(double minChartVal){
		this.minChartVal = minChartVal;
	}

	public double getMaxChartVal(){
		return maxChartVal;
	}

	public void setMaxChartVal(double maxChartVal){
		this.maxChartVal = maxChartVal;
	}

	public List<ChartFilterAttribute> getAttributes(){
		return attributes;
	}

	public ChartFilterAttribute getAttribute(int index){
		final int size = attributes.size();
		if (size <= index){
			for (int i = size; i <= index; i++){
				addAttribute(-1);
			}
		}
		return attributes.get(index);
	}

	public void setAttributes(List<ChartFilterAttribute> attributes){
		this.attributes = attributes;
	}

	public void addAttribute(int attributeId){
		attributes.add(new ChartFilterAttribute(attributeId));
	}

	public double getMinChartAttrVal(int index){
		double result = getAttribute(index).getMinChartAttrVal();
		return result;
	}

	public void setMinChartAttrVal(int index, double minChartAttrVal){
		getAttribute(index).setMinChartAttrVal(minChartAttrVal);
	}

	public double getMaxChartAttrVal(int index){
		double result = getAttribute(index).getMaxChartAttrVal();
		return result;
	}

	public void setMaxChartAttrVal(int index, double maxChartAttrVal){
		getAttribute(index).setMaxChartAttrVal(maxChartAttrVal);
	}

	public boolean isExcluded(int index){
		boolean result = getAttribute(index).isExcluded();
		return result;
	}

	public void setExcluded(int index, boolean excluded){
		getAttribute(index).setExcluded(excluded);
	}

	public void clearAttributes(){
		attributes.clear();
	}

	public List<ChartFilterAttribute> copyChartFilterAttributes(){
		List<ChartFilterAttribute> attrs = new ArrayList<ChartFilterAttribute>();
		for (ChartFilterAttribute attr : attributes){
			ChartFilterAttribute copy = new ChartFilterAttribute(attr.getAttributeId(), attr.getMinChartAttrVal(),
			        attr.getMaxChartAttrVal(), attr.isExcluded());
			attrs.add(copy);
		}
		return attrs;
	}

}
