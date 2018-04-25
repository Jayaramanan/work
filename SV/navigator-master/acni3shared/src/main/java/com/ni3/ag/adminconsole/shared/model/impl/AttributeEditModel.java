/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class AttributeEditModel extends AbstractModel{

	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private ObjectDefinition currentObjectDefinition;

	public ObjectDefinition getCurrentObjectDefinition(){
		return currentObjectDefinition;
	}

	public void setCurrentObjectDefinition(ObjectDefinition currentObjectDefinition){
		this.currentObjectDefinition = currentObjectDefinition;
	}

	public void setSchemaList(List<Schema> schemaList){
		schemaMap.put(getCurrentDatabaseInstance(), schemaList);
	}

	public List<Schema> getSchemaList(){
		return schemaMap.get(getCurrentDatabaseInstance());
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return schemaMap.containsKey(instance);
	}

}
