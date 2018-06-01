package com.ni3.ag.navigator.client.domain;

import java.awt.Color;


public class ChartAttributeDescriptor{
	private int valueIndex;
	private Color color;
	private String label;
	private int minVal;
	private int maxVal;
	private Attribute attribute;

	public ChartAttributeDescriptor(DynamicChartAttribute attribute, int valueIndex){
		setValueIndex(valueIndex);
		setColor(attribute.getColor());
		setLabel(attribute.getAttribute().label);
		setAttribute(attribute.getAttribute());
	}

	public ChartAttributeDescriptor(Attribute attribute, Color color, int valueIndex){
		setAttribute(attribute);
		setValueIndex(valueIndex);
		setColor(color);
		setLabel(attribute.label);
	}

	public ChartAttributeDescriptor(int attributeId, String label, Color color, int valueIndex){
		Attribute attr = new Attribute();
		attr.ID = attributeId;
		attr.label = label;
		setAttribute(attr);
		setValueIndex(valueIndex);
		setColor(color);
		setLabel(label);
	}

	public Attribute getAttribute(){
		return attribute;
	}

	public void setAttribute(Attribute attribute){
		this.attribute = attribute;
	}

	public int getValueIndex(){
		return valueIndex;
	}

	public void setValueIndex(int valueIndex){
		this.valueIndex = valueIndex;
	}

	public Color getColor(){
		return color;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public int getMinVal(){
		return minVal;
	}

	public void setMinVal(int minVal){
		this.minVal = minVal;
	}

	public int getMaxVal(){
		return maxVal;
	}

	public void setMaxVal(int maxVal){
		this.maxVal = maxVal;
	}
}