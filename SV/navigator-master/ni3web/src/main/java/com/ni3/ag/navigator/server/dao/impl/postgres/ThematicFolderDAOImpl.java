/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ni3.ag.navigator.server.dao.ThematicFolderDAO;
import com.ni3.ag.navigator.shared.domain.ThematicFolder;

public class ThematicFolderDAOImpl extends JdbcDaoSupport implements ThematicFolderDAO{

	private RowMapper thematicFolderMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultset, int rowNum) throws SQLException{
			return new ThematicFolder(resultset.getInt("id"), resultset.getString("name"));
		}
	};

	@Override
	public List<ThematicFolder> getThematicFolders(int schemaId){
		final String sql = "SELECT ID, Name FROM geo_thematicfolder WHERE schemaId=? ORDER BY name";
		return getJdbcTemplate().query(sql, new Object[] { schemaId }, thematicFolderMapper);
	}

	@Override
	public int createThematicFolder(final ThematicFolder folder){
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement = con.prepareStatement(
				        "INSERT INTO geo_thematicfolder(name, schemaId) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, folder.getName());
				statement.setInt(2, folder.getSchemaId());
				return statement;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(psc, keyHolder);
		Number generatedId = (Number) keyHolder.getKeys().get("id");
		return generatedId.intValue();
	}

	@Override
	public ThematicFolder getThematicFolder(String name, int schemaId){
		final String sql = "SELECT ID, Name FROM geo_thematicfolder WHERE name ilike ? and schemaId=? ORDER BY name";
		List<?> folders = getJdbcTemplate().query(sql, new Object[] { name, schemaId }, thematicFolderMapper);
		return folders.isEmpty() ? null : (ThematicFolder) folders.get(0);
	}
}
