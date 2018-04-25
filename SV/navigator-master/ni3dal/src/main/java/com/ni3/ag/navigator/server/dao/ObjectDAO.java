package com.ni3.ag.navigator.server.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.ni3.ag.navigator.server.domain.CisObject;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;

public interface ObjectDAO{
	List<CisObject> getUpdatedObjects(Date lastUpdateTime, List<ObjectDefinition> types);

	CisObject get(int objectId);

	void fillLastModified(Date lastModified, List<ObjectDefinition> types);

	String getSrcIdById(Integer id, ObjectDefinition od) throws SQLException;

	void setChanged(Integer id);
}
