/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.GisTerritoryDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.service.def.GeoAnalyticsService;

public class GeoAnalyticsServiceImpl implements GeoAnalyticsService{
	private GisTerritoryDAO gisTerritoryDAO;
	private SchemaDAO schemaDAO;

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setGisTerritoryDAO(GisTerritoryDAO gisTerritoryDAO){
		this.gisTerritoryDAO = gisTerritoryDAO;
	}

	@Override
	public void applyGisTerritories(List<GisTerritory> territoriesToUpdate, List<GisTerritory> territoriesToDelete){
		if (territoriesToUpdate != null && !territoriesToUpdate.isEmpty()){
			gisTerritoryDAO.saveOrUpdateAll(territoriesToUpdate);
		}

		if (territoriesToDelete != null && !territoriesToDelete.isEmpty()){
			gisTerritoryDAO.deleteAll(territoriesToDelete);
		}
	}

	@Override
	public List<GisTerritory> getGisTerritories(){
		List<GisTerritory> gisTerritories = gisTerritoryDAO.getGisTerritories();
		return gisTerritories;
	}

	@Override
	public List<Schema> getSchemas(){
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema sch : schemas){
			Hibernate.initialize(sch.getObjectDefinitions());
			for (ObjectDefinition od : sch.getObjectDefinitions()){
				Hibernate.initialize(od.getObjectAttributes());
			}
		}
		return schemas;
	}

}
