/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.sql.Connection;
import java.util.List;

import com.ni3.ag.adminconsole.domain.CisObject;
import com.ni3.ag.adminconsole.domain.DataType;

public interface ObjectDAO{
	public CisObject get(Integer id);

	public void delete(CisObject object);

	Object getData(String sql, Object[] params);

	void executeUpdate(String sql, Object[] params, DataType[] types);

	Connection getConnection();

	public int getMaxIdForRange(int userRangeStart, int userRangeEnd);

	List<Object[]> getIDsForUserTable(String tableName);

	// used by API
    int executeUpdate(String updateSql);

    // used by API
    List<Integer> getEdgesByNode(int nodeId);
}
