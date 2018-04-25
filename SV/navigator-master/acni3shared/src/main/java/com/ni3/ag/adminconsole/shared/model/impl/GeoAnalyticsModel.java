/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class GeoAnalyticsModel extends AbstractModel{
	private Map<DatabaseInstance, List<GisTerritory>> territoryMap = new HashMap<DatabaseInstance, List<GisTerritory>>();
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private List<GisTerritory> deletedTerritories = new ArrayList<GisTerritory>();
	private GisTerritory currentTerritory;
	private Schema currentSchema;

	public List<GisTerritory> getGisTerritories(){
		return territoryMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<GisTerritory>> getGisTerritoryMap(){
		return territoryMap;
	}

	public void setGisTerritories(List<GisTerritory> territories){
		territoryMap.put(currentDatabaseInstance, territories);
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return territoryMap.containsKey(instance);
	}

	public List<GisTerritory> getDeletedTerritories(){
		return deletedTerritories;
	}

	public void clearDeletedTerritories(){
		deletedTerritories.clear();
	}

	public GisTerritory getCurrentTerritory(){
		return currentTerritory;
	}

	public void setCurrentTerritory(GisTerritory currentTerritory){
		this.currentTerritory = currentTerritory;
	}

	public void setSchemas(List<Schema> schemas){
		schemaMap.put(currentDatabaseInstance, schemas);
	}

	public List<Schema> setSchemas(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public List<ObjectDefinition> getCurrentObjects(){
		if (currentSchema == null)
			return null;
		else
			return currentSchema.getObjectDefinitions();
	}

	public void setCurrentSchema(Schema sch){
		currentSchema = sch;
	}

	public Schema getCurrentSchema(){
		return currentSchema;
	}

}
