/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.util.List;

public class ThematicFolder{
	public static final String DEFAULT_GEO_ANALYTICS_FOLDER_NAME = "Geo-analytics";
	private int id;
	private String name;
	private int schemaId;
	private List<ThematicMap> thematicMaps;

	public ThematicFolder(int id, String name){
		this.id = id;
		this.name = name;
	}

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

	public int getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(int schemaId){
		this.schemaId = schemaId;
	}

	public List<ThematicMap> getThematicMaps(){
		return thematicMaps;
	}

	public void setThematicMaps(List<ThematicMap> thematicMaps){
		this.thematicMaps = thematicMaps;
	}
}
