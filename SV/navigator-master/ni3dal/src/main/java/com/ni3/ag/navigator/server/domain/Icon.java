package com.ni3.ag.navigator.server.domain;

public class Icon{
	private String iconName;
	private int id;

	public Icon(int id, String iconName){
		this.id = id;
		this.iconName = iconName;
	}

	public int getId(){
		return id;
	}

	public String getIconName(){
		return iconName;
	}
}
