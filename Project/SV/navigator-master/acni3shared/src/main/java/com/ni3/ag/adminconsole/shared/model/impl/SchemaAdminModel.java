/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Context;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class SchemaAdminModel extends AbstractModel{
	private final static Logger log = Logger.getLogger(SchemaAdminModel.class);

	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private ObjectDefinition currentObjectDefinition;

	private Schema currentSchema;
	/**
	 * Currently selected predefined attributes to delete
	 */
	private List<ObjectAttribute> attributesToDelete;
	/**
	 * All predefined attributes that were marked for deletion before update button was pressed
	 */
	private List<ObjectAttribute> allAttributesToDelete = new ArrayList<ObjectAttribute>();

	private List<Context> contextsToDelete;
	private ObjectAttribute attributeToValidate;

	private String salesforceUrl;
	private String salesforceUsername;
	private String salesforcePassword;

	public ObjectDefinition getCurrentObjectDefinition(){
		return currentObjectDefinition;
	}

	public void setCurrentObjectDefinition(ObjectDefinition currentObjectDefinition){
		this.currentObjectDefinition = currentObjectDefinition;
	}

	public Schema getCurrentSchema(){
		return currentSchema;
	}

	public void setCurrentSchema(Schema currentSchema){
		this.currentSchema = currentSchema;
	}

	public List<Schema> getSchemaList(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public void setSchemaList(List<Schema> schemaList){
		log.debug(currentDatabaseInstance + " : new schema list");
		schemaMap.put(currentDatabaseInstance, schemaList);
	}

	public void setAttributesToDelete(List<ObjectAttribute> attrsToDelete){
		attributesToDelete = attrsToDelete;
		if (attrsToDelete != null)
			allAttributesToDelete.addAll(attrsToDelete);
	}

	public List<ObjectAttribute> getAttributesToDelete(){
		return attributesToDelete;
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return schemaMap.containsKey(instance);
	}

	public void addContextToDelete(Context c){
		if (contextsToDelete == null)
			contextsToDelete = new ArrayList<Context>();
		contextsToDelete.add(c);
	}

	public List<Context> getContextsToDelete(){
		return contextsToDelete;
	}

	public List<ObjectAttribute> getAllAttributesToDelete(){
		return allAttributesToDelete;
	}

	public void clearAllAttributesToDelete(){
		allAttributesToDelete.clear();
	}

	public void setAttributeToValidate(ObjectAttribute oa){
		attributeToValidate = oa;
	}

	public ObjectAttribute getAttributeToValidate(){
		return attributeToValidate;
	}

	public String getSalesforceUrl(){
		return salesforceUrl;
	}

	public void setSalesforceUrl(String salesforceUrl){
		this.salesforceUrl = salesforceUrl;
	}

	public String getSalesforceUsername(){
		return salesforceUsername;
	}

	public void setSalesforceUsername(String salesforceUsername){
		this.salesforceUsername = salesforceUsername;
	}

	public String getSalesforcePassword(){
		return salesforcePassword;
	}

	public void setSalesforcePassword(String salesforcePassword){
		this.salesforcePassword = salesforcePassword;
	}

}
