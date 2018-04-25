package com.ni3.ag.navigator.server.services.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.ni3.ag.navigator.server.dao.ObjectDAO;
import com.ni3.ag.navigator.server.datasource.postgres.DefaultPostgreSQLDataSource;
import com.ni3.ag.navigator.server.domain.CisObject;
import com.ni3.ag.navigator.server.domain.Edge;
import com.ni3.ag.navigator.server.domain.Node;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.services.CISObjectProviderService;
import com.ni3.ag.navigator.server.util.Utility;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PostgresCISObjectProviderService extends JdbcDaoSupport implements CISObjectProviderService{
	private static final Logger log = Logger.getLogger(PostgresCISObjectProviderService.class);
	private ObjectDAO objectDAO;

	public void setObjectDAO(ObjectDAO objectDAO){
		this.objectDAO = objectDAO;
	}

	private RowMapper nodeRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			Node n = new Node();
			n.setID(rs.getInt("id"));
			n.setType(rs.getInt("objecttype"));
			n.setCreatorUser(rs.getInt("creator"));
			n.setCreatorGroup(rs.getInt("groupid"));
			n.setStatus(rs.getInt("status"));
			return n;
		}
	};
	private RowMapper edgeRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			Edge e = new Edge();
			e.setID(rs.getInt("id"));
			e.setFavoriteId(rs.getInt("favoritesid"));
			e.setType(rs.getInt("edgetype"));
			e.setStatus(rs.getInt("status"));
			e.setDirected(rs.getInt("directed"));
			e.setConnectionType(rs.getInt("connectiontype"));
			e.setStrength(rs.getFloat("strength"));
			e.setInPath(rs.getInt("inpath"));
			e.setCreatorUser(rs.getInt("creator"));
			e.setCreatorGroup(rs.getInt("groupid"));
			e.setContextEdge(rs.getBoolean("isctx"));
			return e;
		}
	};
	private RowMapper idMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
			return rs.getInt("id");
		}
	};

	@Override
	public void init(){
	}

	@Override
	@SuppressWarnings("unchecked")
	public Node getNode(int id){
		List<Node> result = getJdbcTemplate().query("select o.id, o.objecttype, o.status, o.creator, sug.groupid " +
				"from cis_objects o left join sys_user_group sug on o.creator=sug.userid where id = ? and o.status in (0, 1)",
				new Object[]{id}, nodeRowMapper);
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Edge> getNodeInEdges(int id){
		return getJdbcTemplate().query("select e.id, e.edgetype, e.favoritesid, o.status, e.directed, " +
				"e.connectiontype, e.strength, e.inpath, o.creator, sug.groupid, od.objecttypeid = 6 as isctx " +
				"from cis_edges e left join cis_objects o on o.id = e.id " +
				"left join sys_user_group sug on o.creator = sug.userid " +
				"left join sys_object od on e.edgetype=od.id " +
				"left join cis_objects no on no.id = e.fromid " +
				"where e.toid = ? and o.status in (1, 0) and no.status in (1,0)", new Object[]{id}, edgeRowMapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Edge> getNodeOutEdges(int id){
		return getJdbcTemplate().query("select e.id, e.edgetype, e.favoritesid, o.status, e.directed, " +
				"e.connectiontype, e.strength, e.inpath, o.creator, sug.groupid, od.objecttypeid = 6 as isctx " +
				"from cis_edges e left join cis_objects o on o.id = e.id " +
				"left join sys_user_group sug on o.creator = sug.userid " +
				"left join sys_object od on e.edgetype=od.id " +
				"left join cis_objects no on no.id = e.toid " +
				"where e.fromid = ? and o.status in (1, 0) and no.status in (1,0)", new Object[]{id}, edgeRowMapper);
	}

	@Override
	public Node getFromNode(int id){
		return getNode(getJdbcTemplate().queryForInt("select fromid from cis_edges where id = ?", new Object[]{id}));
	}

	@Override
	public Node getToNode(int id){
		return getNode(getJdbcTemplate().queryForInt("select toid from cis_edges where id = ?", new Object[]{id}));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Edge getEdge(int id){
		List<Edge> edges = getJdbcTemplate().query("select e.id, e.edgetype, e.favoritesid, o.status, e.directed, " +
						"e.connectiontype, e.strength, e.inpath, o.creator, sug.groupid, od.objecttypeid = 6 as isctx " +
						"from cis_edges e left join cis_objects o on o.id = e.id " +
						"left join sys_user_group sug on o.creator = sug.userid " +
						"left join sys_object od on e.edgetype=od.id " +
						"where e.id = ? and o.status in (1, 0)", new Object[]{id}, edgeRowMapper);
		return edges.isEmpty() ? null : edges.get(0) ;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getEdgeList(List<Integer> fromIds, List<Integer> toIds, List<Integer> edgeIds, int limit){
		StringBuilder sb = new StringBuilder();
		sb.append("select e.id from cis_edges e left join cis_objects co on co.id = e.id where ((e.fromid in (")
				.append(Utility.listToString(fromIds)).append(") and e.toid in (")
				.append(Utility.listToString(toIds)).append(")) or (e.toid in (")
				.append(Utility.listToString(fromIds)).append(") and e.fromid in (")
				.append(Utility.listToString(toIds)).append("))) and e.id in (").append(Utility.listToString(edgeIds))
				.append(") and co.status in (0, 1)");
		if (limit > 0)
			sb.append(" LIMIT ").append(limit);
		return getJdbcTemplate().query(sb.toString(), idMapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getEdgeListByFavorite(int favoriteId){
		final String sql = "select e.id from cis_edges e "
				+ "inner join cis_objects o on o.id = e.id " 
				+ "inner join cis_objects ofrom on ofrom.id = e.fromid "
				+ "inner join cis_objects oto on oto.id = e.toid "
				+ "where e.favoritesid = ? and o.status in (0,1) and ofrom.status in (0,1) and oto.status in (0,1)";
		return getJdbcTemplate().query(sql, new Object[] { favoriteId }, idMapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getEdgeList(List<Integer> nodeIds, List<Integer> edgeIds, int limit){
		StringBuilder sb = new StringBuilder();
		sb.append("select e.id from cis_edges e left join cis_objects co on co.id = e.id where (e.fromid in (").append(Utility.listToString(nodeIds)).append(") or e.toid in (")
				.append(Utility.listToString(nodeIds)).append(")) and e.id in (").append(Utility.listToString(edgeIds)).append(") and co.status in (0, 1)");
		if (limit > 0)
			sb.append(" LIMIT ").append(limit);
		return getJdbcTemplate().query(sb.toString(), idMapper);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getConnectedNodesForNode(Integer id){
		final String sql = "select e.fromid as id from cis_edges e left join cis_objects co on co.id = e.id where e.toid = ? and co.status in (1, 0) union select e.toid as id from cis_edges e  left join cis_objects co on co.id = e.id where e.fromid = ? and co.status in (1, 0) ";
		return getJdbcTemplate().query(sql, new Object[]{id, id}, idMapper);
	}

	@Override
	public Map<Integer, Integer> getEdgesWithTypesForNode(int id){
		final String sql = "select e.id, e.edgetype from cis_edges e left join cis_objects co on co.id = e.id where e.fromid = ? and co.status in (0, 1) union select e.id, e.edgetype from cis_edges e left join cis_objects co on co.id = e.id where e.toid = ? and co.status in (0, 1)";
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		getJdbcTemplate().query(sql, new Object[]{id, id}, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				result.put(rs.getInt("id"), rs.getInt("edgetype"));
				return null;
			}
		});
		return result;
	}

	@Override
	public void fillLastModified(Date lastUpdateTime, List<ObjectDefinition> types){
		objectDAO.fillLastModified(lastUpdateTime, types);
	}

	@Override
	public List<CisObject> getUpdatedObjects(Date lastUpdateTime, List<ObjectDefinition> types){
		return objectDAO.getUpdatedObjects(lastUpdateTime, types);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends Integer> getNodeIds(ObjectDefinition od){
		String table = DefaultPostgreSQLDataSource.getTableNameForEntity(od);
		final String sql = "select id from " + table;
		log.debug(sql);
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int i) throws SQLException{
				return resultSet.getInt(1);
			}
		});
	}
}
