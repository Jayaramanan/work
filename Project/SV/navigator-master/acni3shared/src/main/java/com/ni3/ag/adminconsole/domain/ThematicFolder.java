package com.ni3.ag.adminconsole.domain;

import java.util.List;

public class ThematicFolder{
	public static final String ID = "id";

	private int id;
	private Schema schema;
	private String name;
	private List<ThematicMap> maps;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public List<ThematicMap> getMaps(){
		return maps;
	}

	public void setMaps(List<ThematicMap> maps){
		this.maps = maps;
	}
}