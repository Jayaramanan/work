package com.ni3.ag.navigator.shared.domain;

import java.util.List;

public class Chart{
	private int id;
	private String name;
	private String comment;
	private int schemaId;

	private List<ObjectChart> objectCharts;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getComment(){
		return comment;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public int getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(int schemaId){
		this.schemaId = schemaId;
	}

	public List<ObjectChart> getObjectCharts(){
		return objectCharts;
	}

	public void setObjectCharts(List<ObjectChart> objectCharts){
		this.objectCharts = objectCharts;
	}

}
