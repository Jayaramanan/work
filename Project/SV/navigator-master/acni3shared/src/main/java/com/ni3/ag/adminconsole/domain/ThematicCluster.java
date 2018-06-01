package com.ni3.ag.adminconsole.domain;

public class ThematicCluster{
	public static final String ID = "id";

	private int id;
	private ThematicMap thematicMap;
	private double fromValue;
	private double toValue;
	private String color;
	private String gIds;
	private String description;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public ThematicMap getThematicMap(){
		return thematicMap;
	}

	public void setThematicMap(ThematicMap thematicMap){
		this.thematicMap = thematicMap;
	}

	public double getFromValue(){
		return fromValue;
	}

	public void setFromValue(double fromValue){
		this.fromValue = fromValue;
	}

	public double getToValue(){
		return toValue;
	}

	public void setToValue(double toValue){
		this.toValue = toValue;
	}

	public String getColor(){
		return color;
	}

	public void setColor(String color){
		this.color = color;
	}

	public String getgIds(){
		return gIds;
	}

	public void setgIds(String gIds){
		this.gIds = gIds;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}
}