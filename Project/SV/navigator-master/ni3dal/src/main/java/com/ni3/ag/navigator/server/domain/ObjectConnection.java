package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;

import com.ni3.ag.navigator.shared.domain.LineStyle;

public class ObjectConnection implements Serializable{
	private int id;
	private ObjectDefinition fromObject;
	private ObjectDefinition toObject;
	private ObjectDefinition connectionObject;
	private int connectionType;
	private LineStyle lineStyle;
	private float lineWidth;
	private String color;
	private LineWidth lineWidth_;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public ObjectDefinition getFromObject(){
		return fromObject;
	}

	public void setFromObject(ObjectDefinition fromObject){
		this.fromObject = fromObject;
	}

	public ObjectDefinition getToObject(){
		return toObject;
	}

	public void setToObject(ObjectDefinition toObject){
		this.toObject = toObject;
	}

	public ObjectDefinition getConnectionObject(){
		return connectionObject;
	}

	public void setConnectionObject(ObjectDefinition connectionObject){
		this.connectionObject = connectionObject;
	}

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

	public int getLineStyle_(){
		return lineStyle.toInt();
	}

	public void setLineStyle_(int lineStyle_){
		this.lineStyle = LineStyle.fromInt(lineStyle_);
	}

	public LineWidth getLineWidth_(){
		return lineWidth_;
	}

	public void setLineWidth_(LineWidth lineWidth_){
		this.lineWidth_ = lineWidth_;
		this.lineWidth = lineWidth_.getWidth();
	}
}