/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;

public interface SchemaDAO{
	public List<Schema> getSchemas();

	public List<Schema> getSchemasWithNodesAndInMetaphor();

	public List<Schema> getSchemasWithEdges();

	public List<Schema> getSchemasWithPredefinedAttributes();

	public Schema getSchemaByName(String value);

	public Schema getSchema(Integer schemaID);

	public Schema saveOrUpdate(Schema schema);

	public Schema save(Schema schema);

	public Schema merge(Schema schema);

	public void deleteSchema(Schema schema);

	public List<Schema> getSchemasByUser(User u);

	public void saveOrUpdateAll(List<Schema> schemas);

	public void evictAll(List<Schema> schemas);

	List<Schema> getSchemasWithNodes();

	public List<Schema> getSchemasWithAggregableAttributes();
}
