/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.List;

import com.ni3.ag.navigator.server.domain.Schema;

public interface SchemaLoaderService{

	/**
	 * get schema by id
	 * 
	 * @param schemaId
	 *            - schema id
	 * @return schema
	 */
	Schema getSchema(Integer schemaId);

	/**
	 * get all schemas
	 * 
	 * @return list of schemas
	 */
	List<Schema> getAllSchemas();

	/**
	 * reset cache for schema
	 * 
	 * @param schemaId
	 *            - id of the schema to reload
	 */
	void invalidate(Integer schemaId);

	/**
	 * reset all schema cache
	 */
	void invalidateAll();
}
