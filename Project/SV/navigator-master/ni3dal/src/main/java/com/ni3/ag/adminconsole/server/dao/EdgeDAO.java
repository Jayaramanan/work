/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Edge;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public interface EdgeDAO{
	Edge getEdge(Integer edgeId);

	void deleteReferencedCisEdges(ObjectDefinition object);

	// used by API
	Integer getNewEdgeId();

	// used by API
	List<Object[]> getIDsForUserTable(String tableName);

	// used by API
	List<Object[]> getEdgeUserData(List<Edge> edges, List<ObjectAttribute> attributes, ObjectDefinition od);

}
