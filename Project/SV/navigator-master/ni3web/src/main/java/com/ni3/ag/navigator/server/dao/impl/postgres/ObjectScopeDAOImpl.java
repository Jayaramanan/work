package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.dao.ObjectScopeDAO;
import com.ni3.ag.navigator.server.domain.EdgeScope;
import com.ni3.ag.navigator.server.domain.NodeScope;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ObjectScopeDAOImpl extends JdbcDaoSupport implements ObjectScopeDAO{
	private static final Logger log = Logger.getLogger(ObjectScopeDAOImpl.class);

	@Override
	public List<EdgeScope> getEdgeScopes(List<ObjectDefinition> types, Integer edgeId){
		if (types.isEmpty())
			return new ArrayList<EdgeScope>();
		String sql = "SELECT ces.EdgeID,ces.GroupID,ces.Flag FROM CIS_EDGES_SCOPE ces ";
		sql += " left join cis_edges ce on ce.id=ces.edgeid ";
		sql += " where ce.edgetype in " + makeInFilter(types);
		if (edgeId != null){
			sql += " AND ces.edgeId = " + edgeId;
		}
		sql += " ORDER BY 1,2";
		try{
			return extractEdgesResult(sql);
		} catch (SQLException e){
			log.error("Error get edge scopes: " + sql, e);
		}
		return new ArrayList<EdgeScope>();
	}

	@Override
	public List<NodeScope> getNodeScopes(List<ObjectDefinition> types, Integer nodeId){
		String sql = "SELECT cns.NodeID,cns.GroupID,cns.Flag FROM CIS_NODES_SCOPE cns ";
		sql += " left join cis_nodes co on co.id=cns.nodeid ";
		sql += " where co.nodetype in " + makeInFilter(types);
		if (nodeId != null){
			sql += " AND cns.nodeId = " + nodeId;
		}
		sql += " ORDER BY 1,2";
		try{
			return extractNodesResult(sql);
		} catch (SQLException e){
			log.error("Error get node scopes from database: " + sql, e);
		}
		return new ArrayList<NodeScope>();
	}

	private String makeInFilter(List<ObjectDefinition> types){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean first = true;
		for (ObjectDefinition ent : types){
			int id = ent.getId();
			if (first){
				sb.append(id);
				first = false;
			} else
				sb.append(", ").append(id);
		}
		sb.append(")");
		return sb.toString();
	}

	private List<EdgeScope> extractEdgesResult(String sql) throws SQLException{
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultset, int rowNum) throws SQLException{
				return new EdgeScope(resultset.getInt(1), resultset.getInt(2), resultset.getString(3));
			}
		});
	}

	private List<NodeScope> extractNodesResult(String sql) throws SQLException{
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultset, int rowNum) throws SQLException{
				return new NodeScope(resultset.getInt(1), resultset.getInt(2), resultset.getString(3));
			}
		});
	}
}
