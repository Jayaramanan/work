/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class ChartModel extends AbstractModel{
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private Map<DatabaseInstance, List<ObjectDefinition>> nodeMap = new HashMap<DatabaseInstance, List<ObjectDefinition>>();
	private Object currentObject;
	private ObjectChart currentObjectChart;
	private String newChartName;

	public void setSchemas(List<Schema> schemas){
		schemaMap.put(currentDatabaseInstance, schemas);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public List<Schema> getSchemas(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public void setCurrentObject(Object o){
		this.currentObject = o;
	}

	public Object getCurrentObject(){
		return currentObject;
	}

	public ObjectChart getCurrentObjectChart(){
		return currentObjectChart;
	}

	public void setCurrentObjectChart(ObjectChart currentObjectChart){
		this.currentObjectChart = currentObjectChart;
	}

	public boolean isSchemaSelected(){
		return currentObject != null && currentObject instanceof Schema;
	}

	public boolean isChartSelected(){
		return currentObject != null && currentObject instanceof Chart;
	}

	public void setObjectDefinitions(List<ObjectDefinition> nodes){
		this.nodeMap.put(currentDatabaseInstance, nodes);
	}

	public List<ObjectDefinition> getObjectDefinitions(){
		return nodeMap.get(currentDatabaseInstance);
	}

	public void clearObjectDefinitions(){
		nodeMap.clear();
	}

	public void setNewChartName(String name){
		this.newChartName = name;
	}

	public String getNewChartName(){
		return this.newChartName;
	}

	public boolean isInstanceLoaded(){
		return schemaMap.containsKey(currentDatabaseInstance);
	}

}
