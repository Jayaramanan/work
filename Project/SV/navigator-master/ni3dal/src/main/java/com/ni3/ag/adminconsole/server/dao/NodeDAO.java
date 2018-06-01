/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Node;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public interface NodeDAO{

	Node getNode(Integer nodeId);

	int executeUpdate(String query);

	Integer getNewNodeId();

	Object getData(String sql, Object[] params);

	Integer getRowCount(String usrTable);

	Object getUniqueResult(String sql, Object[] params);

	void deleteReferencedCisNodes(ObjectDefinition object);

	// used by API
	List<Object[]> getIDsForUserTable(String tableName);

}
