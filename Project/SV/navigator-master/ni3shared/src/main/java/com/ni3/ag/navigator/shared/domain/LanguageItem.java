package com.ni3.ag.navigator.shared.domain;

public class LanguageItem{
	private String property;
	private String value;
	private int id;

	public String getProperty(){
		return property;
	}

	public String getValue(){
		return value;
	}

	public void setId(int id){
		this.id = id;
	}

	public void setProperty(String property){
		this.property = property;
	}

	public void setValue(String value){
		this.value = value;
	}

	public int getId(){
		return id;
	}
}
