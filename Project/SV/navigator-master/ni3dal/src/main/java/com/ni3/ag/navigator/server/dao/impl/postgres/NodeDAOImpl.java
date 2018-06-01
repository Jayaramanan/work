package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.NodeDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Node;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.geocode.data.GeoCodeItem;
import com.ni3.ag.navigator.server.services.UserDataService;
import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import com.ni3.ag.navigator.shared.domain.DBObject;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class NodeDAOImpl extends JdbcDaoSupport implements NodeDAO{
	private static final Logger log = Logger.getLogger(NodeDAOImpl.class);
	private static final String SELECT_ALL = "SELECT n.ID,n.NodeType,n.Lon,n.Lat,o.status,o.creator,su.groupid "
			+ " FROM CIS_NODES n,CIS_OBJECTS o,SYS_USER_GROUP su"
			+ " WHERE n.ID=o.ID AND o.creator=su.UserID and n.nodetype in ";
	private static final String SELECT_BY_ID = "SELECT n.ID,n.NodeType,n.Lon,n.Lat,o.status,o.creator,su.groupid "
			+ " FROM CIS_NODES n,CIS_OBJECTS o,SYS_USER_GROUP su " + " WHERE n.ID=o.ID AND o.creator=su.UserID"
			+ " AND n.ID=? and o.status in (" + ObjectStatus.Normal.toInt() + ", " + ObjectStatus.Locked.toInt() + ")";

	private static final String UPDATE_NODE_METAPHOR = "UPDATE CIS_NODES SET iconname=? WHERE ID=?";
	private UserDataService userDataService;

	public void setUserDataService(UserDataService userDataService){
		this.userDataService = userDataService;
	}

	@Override
	public Node get(final int id){
		log.debug("Executing : " + SELECT_BY_ID + "\nID=" + id);
		List<?> list = getJdbcTemplate().query(SELECT_BY_ID, new Object[] { id }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultset, int rowNum) throws SQLException{
				final Node newNode = new Node(resultset.getInt(1));
				newNode.setType(resultset.getInt(2));
				newNode.setStatus(resultset.getInt(5));
				newNode.setCreatorUser(resultset.getInt(6));
				newNode.setCreatorGroup(resultset.getInt(7));
				return newNode;
			}
		});
		if (list.isEmpty())
			return null;
		return (Node) list.get(0);
	}

	@Override
	public boolean updateNodeMetaphor(int nodeId, String iconName){
		getJdbcTemplate().update(UPDATE_NODE_METAPHOR, new Object[] { iconName, nodeId });
		return true;
	}

	@Override
	public boolean updateNodeGeoCoords(int nodeId, double lon, double lat){
		return updateNodeGeoCoords(nodeId, lon, lat, false);
	}

	@Override
	public boolean updateNodeGeoCoords(int nodeId, double lon, double lat, boolean touchLastModified){
		String sql = "UPDATE CIS_NODES SET Lon=?,LAT=? WHERE ID=?";
		getJdbcTemplate().update(sql, new Object[] { lon, lat, nodeId });
		if (touchLastModified){
			final String lastmodifiedSqlString = "UPDATE cis_objects set lastmodified = now() WHERE id = ?";
			getJdbcTemplate().update(lastmodifiedSqlString, new Object[] { nodeId });

		}
		return true;
	}

	@Override
	public Map<Attribute, Object> getNodeData(int nodeId, final ObjectDefinition entity){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(nodeId);
		Map<Integer, DBObject> edges = userDataService.getDataForIdList(entity, ids);
		DBObject edge = edges.get(nodeId);
		if (edge == null){
			log.error("Error cannot find edge with id: " + nodeId);
			return null;
		}

		return ObjectDAOImpl.getAttributeValueMap(edge, entity);
	}

	@Override
	public Map<Integer, Node> getNodes(List<ObjectDefinition> types){
		String sql = SELECT_ALL + makeInFilter(types) + " and o.status in (" + ObjectStatus.Normal.toInt() + ", "
				+ ObjectStatus.Locked.toInt() + ") ORDER BY 1";
		log.debug("Executing : " + sql);
		final Map<Integer, Node> result = new HashMap<Integer, Node>();
		getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				final Node newNode = new Node(rs.getInt(1));
				newNode.setType(rs.getInt(2));
				newNode.setStatus(rs.getInt(5));
				newNode.setCreatorUser(rs.getInt(6));
				newNode.setCreatorGroup(rs.getInt(7));
				result.put(newNode.getID(), newNode);
				return newNode;
			}
		});

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

	public List<GeoCodeItem> getAllToCode(String sql){
		return getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				return new GeoCodeItem(rs.getInt(1), rs.getString(2));
			}
		});
	}

	@Override
	public Node get(int id, ObjectDefinition entity){
		return get(id);
	}
}
