package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.*;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ni3.ag.navigator.server.dao.ThematicMapDAO;
import com.ni3.ag.navigator.shared.domain.ThematicMap;

public class ThematicMapDAOImpl extends JdbcDaoSupport implements ThematicMapDAO{
	private RowMapper thematicMapMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			ThematicMap thematicMap = new ThematicMap();
			thematicMap.setId(resultSet.getInt("id"));
			thematicMap.setName(resultSet.getString("name"));
			thematicMap.setFolderId(resultSet.getInt("folderId"));
			thematicMap.setGroupId(resultSet.getInt("groupId"));
			thematicMap.setLayerId(resultSet.getInt("layerId"));
			thematicMap.setAttribute(resultSet.getString("attribute"));
			return thematicMap;
		}
	};

	@Override
	public int createThematicMap(final ThematicMap thematicMap){
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement = con.prepareStatement(
				        "INSERT INTO geo_thematicmap(name, folderid, groupId, layerId, attribute) VALUES (?,?,?,?,?)",
				        PreparedStatement.RETURN_GENERATED_KEYS);
				statement.setString(1, thematicMap.getName());
				if (thematicMap.getFolderId() > 0)
					statement.setInt(2, thematicMap.getFolderId());
				else
					statement.setNull(2, Types.INTEGER);
				statement.setInt(3, thematicMap.getGroupId());
				statement.setInt(4, thematicMap.getLayerId());
				statement.setString(5, thematicMap.getAttribute());
				return statement;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(psc, keyHolder);
		Number generatedId = (Number) keyHolder.getKeys().get("id");
		return generatedId.intValue();
	}

	@Override
	public void updateThematicMap(ThematicMap thematicMap){
		final String sql = "UPDATE geo_thematicMap set name = ?, folderId = ?, "
		        + "groupId = ?, layerId = ?, attribute = ? where id = ?";
		getJdbcTemplate().update(
		        sql,
		        new Object[] { thematicMap.getName(), thematicMap.getFolderId() > 0 ? thematicMap.getFolderId() : null,
						thematicMap.getGroupId(), thematicMap.getLayerId(),
						thematicMap.getAttribute(), thematicMap.getId() });
	}

	@Override
	public ThematicMap getThematicMap(int id){
		final String sql = "SELECT id, name, folderid, groupid, layerid, attribute FROM geo_thematicMap where id = ?";
		List<?> list = getJdbcTemplate().query(sql, new Object[] { id }, thematicMapMapper);
		if (list.isEmpty())
			return null;
		return (ThematicMap) list.get(0);
	}

	@Override
	public List<ThematicMap> getThematicMapsByFolderId(int folderId, int groupId){
		final String sql = "SELECT id, name, folderid, groupid, layerid, attribute "
		        + "FROM geo_thematicMap where folderId = ? and groupId = ?";
		return getJdbcTemplate().query(sql, new Object[] { folderId, groupId }, thematicMapMapper);
	}

	@Override
	public ThematicMap getThematicMapByName(String name, int folderId, int groupId){
		final String sql = "SELECT id, name, folderid, groupid, layerid, attribute "
		        + "FROM geo_thematicMap where name = ? and folderId = ? and groupId = ?";
		List result = getJdbcTemplate().query(sql, new Object[] { name, folderId, groupId }, thematicMapMapper);
		if (result.isEmpty())
			return null;
		return (ThematicMap) result.get(0);
	}

	@Override
	public void deleteThematicMap(int thematicMapId){
		final String sql = "DELETE FROM geo_thematicmap WHERE id = ?";
		getJdbcTemplate().update(sql, new Object[] { thematicMapId });
	}

	@Override
	public int createThematicMapWithId(final ThematicMap tm){
		PreparedStatementCreator psc = new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException{
				PreparedStatement statement = con.prepareStatement(
				        "INSERT INTO geo_thematicmap(id, name, folderid, groupId, layerId, attribute) " +
								"VALUES (?,?,?,?,?,?)");
				statement.setInt(1, tm.getId());
				statement.setString(2, tm.getName());
				if (tm.getFolderId() > 0)
					statement.setInt(3, tm.getFolderId());
				else
					statement.setNull(3, Types.INTEGER);
				statement.setInt(4, tm.getGroupId());
				statement.setInt(5, tm.getLayerId());
				statement.setString(6, tm.getAttribute());
				return statement;
			}
		};
		getJdbcTemplate().update(psc);
		return tm.getId();
	}

	@Override
	public List<ThematicMap> getThematicMaps(){
		final String sql = "SELECT id, name, folderid, groupid, layerid, attribute FROM geo_thematicMap";
		return getJdbcTemplate().query(sql, thematicMapMapper);
	}

}
