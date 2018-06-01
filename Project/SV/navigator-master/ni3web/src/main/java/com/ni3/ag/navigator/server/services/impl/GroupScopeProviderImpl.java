package com.ni3.ag.navigator.server.services.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.services.GroupScopeProvider;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GroupScopeProviderImpl extends JdbcDaoSupport implements GroupScopeProvider{
	private static final Logger log = Logger.getLogger(GroupScopeProviderImpl.class);
	private static final String EDGE_SCOPE_FIELD = "edgescope";
	private static final String NODE_SCOPE_FIELD = "nodescope";

	@Override
	public List<Integer> getEdgeScope(int groupId){
		String sql = getScopeByGroup(groupId, EDGE_SCOPE_FIELD);
		if (sql == null || sql.trim().isEmpty())
			sql = "";
		else
			sql += " UNION ";
		sql += "select edgeid from cis_edges_scope where groupId = " + groupId + " and flag='N'";
		try{
			return extractScope(sql);
		} catch (SQLException e){
			log.error("Error get scoped edges by group: " + groupId, e);
			return new ArrayList<Integer>();
		}
	}

	@Override
	public List<Integer> getNodeScope(int groupId){
		List<Integer> result = new ArrayList<Integer>();
		String sql = getScopeByGroup(groupId, NODE_SCOPE_FIELD);
		if (sql == null || sql.trim().isEmpty())
			sql = "";
		else
			sql += " UNION ";
		sql += "select nodeid from cis_nodes_scope where groupId = " + groupId + " and flag='N'";
		try{
			return extractScope(sql);
		} catch (SQLException e){
			log.error("Error get scoped edges by group: " + groupId, e);
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	private List<Integer> extractScope(String sql) throws SQLException{
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return rs.getInt(1);
			}
		});
	}

	private String getScopeByGroup(int groupId, String field){
		final String sql = "SELECT " + field + " FROM SYS_GROUP_SCOPE WHERE EdgeScope IS NOT NULL " + "AND GroupID=?";
		List<?> scopes = getJdbcTemplate().queryForList(sql, new Object[]{groupId}, String.class);
		return scopes.isEmpty() ? null : (String) scopes.get(0);
	}
}
