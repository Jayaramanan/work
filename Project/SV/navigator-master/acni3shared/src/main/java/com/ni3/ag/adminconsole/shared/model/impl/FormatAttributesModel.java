/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class FormatAttributesModel extends AbstractModel{
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private ObjectDefinition currentObject;
	private ObjectAttribute currentAttribute;

	public void setCurrentAttribute(ObjectAttribute currentAttribute){
		this.currentAttribute = currentAttribute;
	}

	public ObjectAttribute getCurrentAttribute(){
		return currentAttribute;
	}

	public List<Schema> getSchemas(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public void setSchemas(List<Schema> schemas){
		schemaMap.put(currentDatabaseInstance, schemas);
	}

	public ObjectDefinition getCurrentObject(){
		return currentObject;
	}

	public void setCurrentObject(ObjectDefinition currentObject){
		this.currentObject = currentObject;
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return schemaMap.containsKey(instance);
	}

	public void setSchemaList(List<Schema> schemaList){
		schemaMap.put(getCurrentDatabaseInstance(), schemaList);
	}

}
