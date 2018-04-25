/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.User;

public interface ObjectDefinitionDAO{
	public List<ObjectDefinition> getObjectDefinitions();

	public ObjectDefinition getObjectDefinition(int id);

	public ObjectDefinition saveOrUpdate(ObjectDefinition objectDefinition);

	public void deleteObject(ObjectDefinition o);

	public ObjectDefinition save(ObjectDefinition clone);

	public List<ObjectDefinition> getNodeLikeObjectDefinitions();

	public ObjectDefinition merge(ObjectDefinition od);

	public ObjectDefinition getObjectDefinitionByName(String objectName, Integer schemaId);

	public List<ObjectDefinition> getObjectDefinitionsByUser(User u);

	public List<ObjectDefinition> getSchemaNodeLikeObjects(Integer schemaId);

	public List<ObjectDefinition> getSchemaEdgeLikeObjects(Integer schemaId);

	List<ObjectDefinition> getNodeObjectsWithNotFixedAttributes();

	public ObjectDefinition getObjectDefinitionWithInMetaphor(Integer id);

	void deleteObjectChartsByObject(ObjectDefinition object);

	void evict(ObjectDefinition od);
}
