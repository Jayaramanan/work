package com.ni3.ag.navigator.client.domain;

public class ChartValue{
	private double value;
	private String color;

	public void setValue(double value){
		this.value = value;
	}

	public void setColor(String color){
		this.color = color;
	}

	public double getValue(){
		return value;
	}

	public String getColor(){
		return color;
	}
}
