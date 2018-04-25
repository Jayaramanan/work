package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;

public class LineWidth implements Serializable{
	private int id;
	private String label;
	private float width;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public float getWidth(){
		return width;
	}

	public void setWidth(float width){
		this.width = width;
	}
}
