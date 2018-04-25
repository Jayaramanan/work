package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.EdgeDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.UserDataService;
import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class EdgeDAOImpl extends JdbcDaoSupport implements EdgeDAO{
	private static final int CONTEXT_EDGE_OBJECT_TYPE_ID = 6;
	private static final Logger log = Logger.getLogger(EdgeDAOImpl.class);
	private static final String SELECT_ALL = "SELECT e.ID, FromID, ToID, EdgeType, ConnectionType, o.status, e.Directed, e.Strength, e.InPath,"
			+ "	e.favoritesid, o.creator, su.groupid, od.objecttypeid"
			+ " FROM CIS_EDGES e, CIS_OBJECTS o, SYS_USER_GROUP su, sys_object_definition od"
			+ " WHERE e.ID=o.ID AND su.UserID=o.creator and e.edgetype = od.id and"
			+ " o.status in ("
			+ ObjectStatus.Normal.toInt() + ", " + ObjectStatus.Locked.toInt() + ")";
	private static final String SELECT_BY_ID = "SELECT a.ID,FromID,ToID,EdgeType,ConnectionType,o.status,a.Directed,a.Strength,a.InPath,"
			+ "a.favoritesid,o.creator,su.groupid, od.objecttypeid "
			+ "FROM CIS_EDGES a,CIS_OBJECTS o,SYS_USER_GROUP su, sys_object_definition od "
			+ "WHERE o.status in ("
			+ ObjectStatus.Normal.toInt()
			+ ", "
			+ ObjectStatus.Locked.toInt()
			+ ") and a.ID=o.ID AND o.creator=su.UserID and a.edgetype = od.id AND a.ID=?";

	private UserDataService userDataService;

	public void setUserDataService(UserDataService userDataService){
		this.userDataService = userDataService;
	}

	private RowMapper edgeRowMapper = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultset, int rowNum) throws SQLException{
			final Edge newEdge = new Edge();
			newEdge.setID(resultset.getInt("id"));

			newEdge.setFromNode(new Node(resultset.getInt("fromId")));
			newEdge.setToNode(new Node(resultset.getInt("toId")));

			newEdge.setType(resultset.getInt("edgeType"));
			newEdge.setConnectionType(resultset.getInt("connectionType"));
			newEdge.setStatus(resultset.getInt("status"));

			newEdge.setDirected(resultset.getInt("directed"));
			newEdge.setStrength(resultset.getInt("strength"));
			newEdge.setInPath(resultset.getInt("inPath"));
			newEdge.setFavoriteId(resultset.getInt("favoritesId"));
			newEdge.setCreatorUser(resultset.getInt("creator"));
			newEdge.setCreatorGroup(resultset.getInt("groupId"));
			newEdge.setContextEdge(resultset.getInt("objecttypeid") == CONTEXT_EDGE_OBJECT_TYPE_ID);

			return newEdge;
		}
	};

	@Override
	public List<GroupObjectPermissions> getEdgePermissionsForUser(final User user, final Integer nodeId){
		// @formatter:off
		final String query = "SELECT ce.id AS edgeId, coalesce(X.canread,0) AS canRead,"
				+ "	coalesce(X.cancreate,0) AS canCreate, coalesce(X.canupdate,0) AS canUpdate,	coalesce(X.candelete,0) AS canDelete"
				+ " FROM cis_edges ce" + " INNER JOIN cis_objects o ON (o.id = ce.id AND o.status in ("
				+ ObjectStatus.Normal.toInt() + ", " + ObjectStatus.Locked.toInt() + "))" + " LEFT OUTER JOIN (SELECT "
				+ "		co.id,sog.canread,sog.cancreate,sog.canupdate,sog.candelete " + "	FROM" + "		cis_objects co,"
				+ "		sys_object_group sog," + "		sys_user_group sug " + "	WHERE" + "		sog.objectid = co.objecttype AND "
				+ "		sog.groupid = sug.groupid AND " + "		sug.userid = ?" + ") X ON ce.id = X.id " + "WHERE "
				+ "	(ce.fromid = ? OR ce.toid = ?)";
		// @formatter:on

		return getJdbcTemplate().query(query, new Object[] { user.getId(), nodeId, nodeId }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
				final GroupObjectPermissions objectPermissions = new GroupObjectPermissions();
				objectPermissions.setObjectId(resultSet.getInt(1));
				objectPermissions.setCanRead(resultSet.getInt(2) == 1);
				objectPermissions.setCanCreate(resultSet.getInt(3) == 1);
				objectPermissions.setCanUpdate(resultSet.getInt(4) == 1);
				objectPermissions.setCanDelete(resultSet.getInt(5) == 1);

				return objectPermissions;
			}
		});
	}

	@Override
	public Map<Integer, Edge> getEdges(List<ObjectDefinition> types){
		if (types.isEmpty())
			return new HashMap<Integer, Edge>();
		final String sql = SELECT_ALL + " and e.edgetype in " + makeInFilter(types) + " ORDER BY e.ID";

		final Map<Integer, Edge> result = new HashMap<Integer, Edge>();
		final List<Edge> edges = getJdbcTemplate().query(sql, edgeRowMapper);
		for (Edge e : edges){
			result.put(e.getID(), e);
		}

		return result;
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

	@Override
	public Edge get(int edgeID){
		List<?> list = getJdbcTemplate().query(SELECT_BY_ID, new Object[] { edgeID }, edgeRowMapper);
		return list.isEmpty() ? null : (Edge) list.get(0);
	}

	@Override
	public Map<Attribute, Object> getEdgeData(int edgeId, final ObjectDefinition entity){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(edgeId);
		Map<Integer, DBObject> edges = userDataService.getDataForIdList(entity, ids);
		DBObject edge = edges.get(edgeId);
		if(edge == null){
			log.error("Error cannot find edge with id: " + edgeId);
			return null;
		}

		return ObjectDAOImpl.getAttributeValueMap(edge, entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getEdgeIdsByFavorite(int favoriteId){
		String sql = "SELECT ID FROM CIS_EDGES WHERE FAVORITESID = ? AND edgetype in " +
				"(select id from sys_object where objecttypeid=?)";
		return (List<Integer>) getJdbcTemplate().queryForList(sql,
				new Object[] { favoriteId, ObjectDefinition.CONTEXT_EDGE_OBJECT_TYPE_ID }, Integer.class);
	}
}
