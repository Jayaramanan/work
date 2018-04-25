package com.ni3.ag.navigator.client.domain;

import com.ni3.ag.navigator.shared.domain.LineStyle;

public class ObjectConnection{
	private int connectionType;
	private LineStyle lineStyle;
	private float lineWidth;
	private String color;
	private int connectionObject;
	private int fromObject;
	private int toObject;

	public int getConnectionType(){
		return connectionType;
	}

	public void setConnectionType(int connectionType){
		this.connectionType = connectionType;
	}

	public LineStyle getLineStyle(){
		return lineStyle;
	}

	public void setLineStyle(LineStyle lineStyle){
		this.lineStyle = lineStyle;
	}

	public float getLineWidth(){
		return lineWidth;
	}

	public void setLineWidth(float lineWidth){
		this.lineWidth = lineWidth;
	}

	public String getColor(){
		return color;
	}

	public void setColor(String color){
		this.color = color;
	}

	public int getConnectionObject(){
		return connectionObject;
	}

	public void setConnectionObject(int connectionObject){
		this.connectionObject = connectionObject;
	}

	public int getFromObject(){
		return fromObject;
	}

	public void setFromObject(int fromObject){
		this.fromObject = fromObject;
	}

	public int getToObject(){
		return toObject;
	}

	public void setToObject(int toObject){
		this.toObject = toObject;
	}
}
