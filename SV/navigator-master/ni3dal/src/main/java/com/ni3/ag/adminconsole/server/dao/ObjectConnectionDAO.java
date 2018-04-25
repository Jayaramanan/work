/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public interface ObjectConnectionDAO{
	public List<ObjectConnection> getObjectConnections();

	public List<ObjectConnection> getObjectConnections(ObjectDefinition object);

	public void saveOrUpdateNoMerge(ObjectConnection objectConnection);

	public void saveOrUpdate(ObjectConnection objectConnection);

	public void saveOrUpdateAll(List<ObjectConnection> objectConnection);

	public void delete(ObjectConnection objectConnection);

	public void deleteAll(List<ObjectConnection> objectConnection);

	public void deleteConnectionsByObject(ObjectDefinition objectDefinition);

	public List<ObjectConnection> getConnectionsByObject(final ObjectDefinition objectDefinition);

	public List<ObjectConnection> getConnectionsByConnectionType(PredefinedAttribute ct);
}
