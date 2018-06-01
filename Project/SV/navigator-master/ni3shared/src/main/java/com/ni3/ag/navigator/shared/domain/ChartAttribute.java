package com.ni3.ag.navigator.shared.domain;

public class ChartAttribute{
	private int id;
	private int objectChartId;
	private int attributeId;
	private String rgb;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getRgb(){
		return rgb;
	}

	public void setRgb(String rgb){
		this.rgb = rgb;
	}

	public int getObjectChartId(){
		return objectChartId;
	}

	public void setObjectChartId(int objectChartId){
		this.objectChartId = objectChartId;
	}

	public int getAttributeId(){
		return attributeId;
	}

	public void setAttributeId(int attributeId){
		this.attributeId = attributeId;
	}

}
