/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class MetaphorModel extends AbstractModel{
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private ObjectDefinition currentObjectDefinition;
	private List<String> metaphorSets;
	private String currentMetaphorSet;

	private Map<DatabaseInstance, List<Icon>> iconMap = new HashMap<DatabaseInstance, List<Icon>>();

	public MetaphorModel(){
	}

	public List<Schema> getSchemas(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public void setSchemas(List<Schema> schemaList){
		schemaMap.put(currentDatabaseInstance, schemaList);
	}

	public ObjectDefinition getCurrentObjectDefinition(){
		return currentObjectDefinition;
	}

	public void setCurrentObjectDefinition(ObjectDefinition currentObjectDefinition){
		this.currentObjectDefinition = currentObjectDefinition;
	}

	public List<String> getMetaphorSets(){
		return metaphorSets;
	}

	public void setMetaphorSets(List<String> metaphorSets){
		this.metaphorSets = metaphorSets;
	}

	public String getCurrentMetaphorSet(){
		return currentMetaphorSet;
	}

	public void setCurrentMetaphorSet(String currentMetaphorSet){
		this.currentMetaphorSet = currentMetaphorSet;
	}

	public List<Icon> getIcons(){
		return iconMap.get(currentDatabaseInstance);
	}

	public void setIcons(List<Icon> icons){
		iconMap.put(currentDatabaseInstance, icons);
	}

	public boolean isInstanceLoaded(){
		return schemaMap.containsKey(currentDatabaseInstance);
	}

	public boolean isIconLoaded(){
		return iconMap.containsKey(currentDatabaseInstance);
	}
}
