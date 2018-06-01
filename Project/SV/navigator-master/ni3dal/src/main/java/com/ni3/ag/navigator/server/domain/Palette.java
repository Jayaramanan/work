package com.ni3.ag.navigator.server.domain;

public class Palette{
	private String color;
	private int id;
	private int sequence;
	private int colorOrder;

	public int getSequence(){
		return sequence;
	}

	public String getColor(){
		return color;
	}

	public void setId(int id){
		this.id = id;
	}

	public void setSequence(int sequence){
		this.sequence = sequence;
	}

	public void setColorOrder(int colorOrder){
		this.colorOrder = colorOrder;
	}

	public void setColor(String color){
		this.color = color;
	}
}
