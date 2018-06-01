/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class DiagnosticsModel extends AbstractModel{

	private List<DatabaseInstance> databaseInstances;
	private Map<DatabaseInstance, List<Schema>> schemasMap = new HashMap<DatabaseInstance, List<Schema>>();
	private Schema currentSchema;
	private List<DiagnoseTaskResult> currentResults;

	public Map<DatabaseInstance, List<Schema>> getSchemasMap(){
		return schemasMap;
	}

	public List<DatabaseInstance> getDatabaseInstances(){
		return databaseInstances;
	}

	public void setDatabaseInstances(List<DatabaseInstance> databaseInstances){
		this.databaseInstances = databaseInstances;
	}

	public void setSchemasMap(Map<DatabaseInstance, List<Schema>> schemasMap){
		this.schemasMap = schemasMap;
	}

	public void setCurrentSchema(Schema o){
		this.currentSchema = o;
	}

	public Schema getCurrentSchema(){
		return currentSchema;
	}

	public void setCurrentResults(List<DiagnoseTaskResult> results){
		this.currentResults = results;
	}

	public List<DiagnoseTaskResult> getCurrentResults(){
		return currentResults;
	}
}
