/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.navigator.server.dao.ObjectGroupDAO;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.ObjectDefinitionGroup;

public class ObjectGroupDAOImpl extends JdbcDaoSupport implements ObjectGroupDAO{
	private RowMapper objectGroupRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			final ObjectDefinitionGroup og = new ObjectDefinitionGroup();
			og.setObject(new ObjectDefinition(resultSet.getInt("ObjectId")));
			og.setGroupId(resultSet.getInt("GroupId"));
			og.setCanRead(resultSet.getInt("CanRead") == 1);
			og.setCanCreate(resultSet.getInt("CanCreate") == 1);
			og.setCanUpdate(resultSet.getInt("CanUpdate") == 1);
			og.setCanDelete(resultSet.getInt("CanDelete") == 1);
			return og;
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectDefinitionGroup> getByObjectDefinitionId(int objectDefinitionId){
		final String query = "SELECT objectid, groupid, canread, cancreate, canupdate, candelete FROM sys_object_group WHERE objectid = ? ORDER BY groupid";
		return getJdbcTemplate().query(query, new Object[] { objectDefinitionId }, objectGroupRowMapper);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectDefinitionGroup> getByGroupId(int groupId, int schemaId){
		final String query = "SELECT og.objectid, og.groupid, og.canread, og.cancreate, og.canupdate, og.candelete FROM sys_object_group og"
		        + " INNER JOIN sys_object o on (og.objectid = o.id) WHERE og.groupid = ? and o.schemaid = ? ORDER BY og.objectid";
		return getJdbcTemplate().query(query, new Object[] { groupId, schemaId}, objectGroupRowMapper);
	}
}
