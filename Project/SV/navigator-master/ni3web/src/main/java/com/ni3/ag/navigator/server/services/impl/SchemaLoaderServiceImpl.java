/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.SchemaDAO;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SchemaLoaderServiceImpl extends JdbcDaoSupport implements SchemaLoaderService{
	private static final Logger log = Logger.getLogger(SchemaLoaderServiceImpl.class);
	private SchemaDAO schemaDAO;

	private Map<Integer, Schema> schemas;

	SchemaLoaderServiceImpl(){
		schemas = new HashMap<Integer, Schema>();
	}

	@Override
	public Schema getSchema(Integer key){
		Schema schema = schemas.get(key);
		if (schema == null){
			loadAllSchemas();
		}
		schema = schemas.get(key);
		if (schema == null){
			log.error("Schema with key " + key + " was not found");
			throw new RuntimeException("Schema with key " + key + " was not found");
		}
		return schema;
	}

	@Override
	public List<Schema> getAllSchemas(){
		loadAllSchemas();
		List<Schema> res = new ArrayList<Schema>();
		res.addAll(schemas.values());
		return res;
	}

	@Override
	public void invalidate(Integer key){
		log.info("Clearing schema: " + key);
		schemas.remove(key);
	}

	@Override
	public void invalidateAll(){
		log.info("Clearing all schemas");
		schemas.clear();
	}

	private synchronized void loadAllSchemas(){
		List<Schema> loadedSchemas = schemaDAO.getSchemas();
 		for (Schema s : loadedSchemas){
			if (!isLoaded(s.getId())){
				log.debug("loading schema id " + s.getId());
				schemas.put(s.getId(), s);
			} else{
				log.debug("schema with id " + s.getId() + " is loaded already");
			}
		}
	}

	private boolean isLoaded(final Integer schemaKey){
		return schemas.containsKey(schemaKey);
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

}
