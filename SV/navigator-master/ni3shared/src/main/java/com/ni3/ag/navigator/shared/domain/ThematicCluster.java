/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

public class ThematicCluster{
	private int id;
	private int thematicMapId;
	private Double fromValue;
	private Double toValue;
	private String color;
	private String gisIds;
    private String description;

    public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getThematicMapId(){
		return thematicMapId;
	}

	public void setThematicMapId(int thematicMapId){
		this.thematicMapId = thematicMapId;
	}

	public Double getFromValue(){
		return fromValue;
	}

	public void setFromValue(Double fromValue){
		this.fromValue = fromValue;
	}

	public Double getToValue(){
		return toValue;
	}

	public void setToValue(Double toValue){
		this.toValue = toValue;
	}

	public String getColor(){
		return color;
	}

	public void setColor(String color){
		this.color = color;
	}

	public String getGisIds(){
		return gisIds;
	}

	public void setGisIds(String gisIds){
		this.gisIds = gisIds;
	}

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
