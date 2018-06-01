/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphCache;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.dao.GeoAnalyticsDAO;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.DataFilter;
import com.ni3.ag.navigator.server.domain.GISGeometry;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;
import com.ni3.ag.navigator.shared.domain.GisTerritory;
import com.ni3.ag.navigator.shared.domain.Prefilter;
import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;

public class GeoAnalyticsDAOImpl extends JdbcDaoSupport implements GeoAnalyticsDAO{
	private static final Logger log = Logger.getLogger(GeoAnalyticsDAOImpl.class);
	private static final String GEO_MAPPING_TABLE_SUFFIX = "_mapping";
	private RowMapper territoryMapping = new RowMapper(){
		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
			GeoTerritory aggr = new GeoTerritory();
			aggr.setId(resultSet.getInt("gId"));
			aggr.setSum(resultSet.getDouble("sum"));
			aggr.setNodeCount(resultSet.getInt("count"));
			return aggr;
		}
	};

	private synchronized GraphNi3Engine getGraph(int schemaId){
		log.debug("getGraph for schema " + schemaId);
		GraphNi3Engine graph = GraphCache.getInstance().getGraph(schemaId);
		log.debug("graph=" + graph);
		if (graph == null){
			log.debug("graph is not inited yet - creating one");
			graph = NSpringFactory.getInstance().getGraphEngineFactory().newGraph(schemaId);
		}
		GraphCache.getInstance().setGraph(graph);
		if (!graph.isGraphLoaded()){
			log.error("Graph is not loaded probably due to an error");
		}
		return graph;
	}

	@Override
	public List<GeoTerritory> getAggregationsPerTerritory(final Attribute attribute, List<Integer> nodeIds,
			List<Integer> gIds, String geoTableName, int schemaId){
		ObjectDefinition entity = attribute.getEntity();
		final Collection<Integer> ids = new HashSet<Integer>();
		for (Attribute a : entity.getAttributes()){
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(
					a.getDataSource());
			if (attributeDataSource.isPrimary()){
				ids.addAll(attributeDataSource.getIdList(entity));
				break;
			}
		}
		if (nodeIds != null && !nodeIds.isEmpty()){ // only selected nodes
			ids.retainAll(nodeIds);
		}
		AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(
				attribute.getDataSource());
		ids.retainAll(attributeDataSource.getNotNull(attribute));
		if (ids.isEmpty())
			return Collections.emptyList();
		User user = NSpringFactory.getInstance().getThreadLocalStorage().getCurrentUser();
		GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		Group group = groupDAO.getByUser(user.getId());
		GraphNi3Engine graph = getGraph(schemaId);
		graph.retainVisibleNodes(ids, group, new DataFilter(new ArrayList<Prefilter>())); // TODO get data filter from the client
		if (ids.isEmpty())
			return Collections.emptyList();
		final Map<Integer, DBObject> objects = new HashMap<Integer, DBObject>();
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute);
		attributeDataSource.get(ids, attributes, objects);

		String tableName = geoTableName + GEO_MAPPING_TABLE_SUFFIX;
		String sql = "SELECT gId, nodeid FROM " + tableName;
		if (log.isDebugEnabled()){
			log.debug(sql);
		}
		final Map<Integer, Double> gidSums = new HashMap<Integer, Double>();
		final Map<Integer, Integer> gidCounts = new HashMap<Integer, Integer>();
		getJdbcTemplate().query(sql, new RowMapper(){
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				int gid = rs.getInt(1);
				int nodeId = rs.getInt(2);
				if (!ids.contains(nodeId))
					return null;
				if (!gidCounts.containsKey(gid))
					gidCounts.put(gid, 0);
				gidCounts.put(gid, gidCounts.get(gid) + 1);
				double currentValue = Double.parseDouble(objects.get(nodeId).getData().get(attribute.getId()));
				if (!gidSums.containsKey(gid))
					gidSums.put(gid, 0.);
				gidSums.put(gid, gidSums.get(gid) + currentValue);
				return null;
			}
		});

		Set<Integer> foundGids = new HashSet<Integer>();
		foundGids.addAll(gidSums.keySet());
		foundGids.addAll(gidCounts.keySet());

		List<GeoTerritory> aggregations = new ArrayList<GeoTerritory>();
		for (Integer gid : foundGids){
			if (gIds == null || gIds.isEmpty() || gIds.contains(gid)){
				double sum = gidSums.get(gid);
				int count = gidCounts.get(gid);
				aggregations.add(new GeoTerritory(gid, sum, count));
			}
		}
		return aggregations;
	}

	@Override
	public List<GeoTerritory> getAllGeoTerritories(final GisTerritory territory){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT gId, ").append(territory.getDisplayColumn());
		sql.append(" FROM ").append(territory.getTableName());
		sql.append(" ORDER BY ").append(territory.getDisplayColumn());
		if (log.isDebugEnabled()){
			log.debug(sql);
		}
		return getJdbcTemplate().query(sql.toString(), new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
				GeoTerritory t = new GeoTerritory();
				t.setId(resultSet.getInt("gId"));
				t.setName(resultSet.getString(territory.getDisplayColumn()));
				return t;
			}
		});
	}

	@Override
	public Map<Integer, Integer> getNodeToTerritoryMapping(int entityId, List<Integer> nodeIds, final List<Integer> gIds,
			String geoTableName){
		String tableName = geoTableName + GEO_MAPPING_TABLE_SUFFIX;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT gis.gId, n.Id as nodeId");
		sql.append(" FROM cis_nodes n, ").append(tableName).append(" gis");
		sql.append(" WHERE n.id = gis.nodeId AND n.nodeType = ?");
		if (nodeIds != null && !nodeIds.isEmpty()){ // only selected nodes
			sql.append(" AND n.id IN (").append(Utility.listToString(nodeIds)).append(")");
		}
		if (log.isDebugEnabled()){
			log.debug(sql);
		}
		final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		getJdbcTemplate().query(sql.toString(), new Object[] { entityId }, new RowMapper(){
			@Override
			public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException{
				if (gIds == null || gIds.isEmpty() || gIds.contains(resultSet.getInt("gId"))){
					map.put(resultSet.getInt("nodeId"), resultSet.getInt("gId"));
				}
				return null;
			}
		});
		return map;
	}

	@Override
	public List<GISGeometry> getThematicData(List<Integer> gisIds, String geoTableName){
		PreparedStatement statement = null;
		List<GISGeometry> result = new ArrayList<GISGeometry>();
		Connection connection = getPGConnection();
		try{
			String inIds = Utility.listToString(gisIds);
			// TODO: Navigator should use 900913 as well
			final String sql = "SELECT gId, ST_transform(the_geom, 4326) AS the_geom FROM " + geoTableName
					+ " where gId in (" + inIds + ")";
			statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()){
				final PGgeometry geometry = (PGgeometry) resultSet.getObject("the_geom");
				result.add(new GISGeometry(resultSet.getInt("gId"), geometry));
			}
			resultSet.close();
		} catch (Exception e){
			log.error("Can't read geo territory geometry", e);
		} finally{
			if (statement != null)
				try{
					statement.clearParameters();
				} catch (SQLException e){
					log.error("Error close statement", e);
				}
			// closed by transaction manager
			// if (connection != null){
			// try{
			// connection.close();
			// } catch (SQLException e){
			// log.error(e);
			// }
			// }
		}

		return result;
	}

	public List<GISGeometry> getAllThematicData(String geoTableName){
		PreparedStatement statement = null;
		List<GISGeometry> result = new ArrayList<GISGeometry>();
		Connection connection = getPGConnection();
		try{
//			String inIds = Utility.listToString(gisIds);
			// TODO: Navigator should use 900913 as well
			final String sql = "SELECT gId, ST_transform(the_geom, 4326) AS the_geom FROM " + geoTableName;
//					+ " where gId in (" + inIds + ")";
			statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()){
				final PGgeometry geometry = (PGgeometry) resultSet.getObject("the_geom");
				result.add(new GISGeometry(resultSet.getInt("gId"), geometry));
			}
			resultSet.close();
		} catch (Exception e){
			log.error("Can't read geo territory geometry", e);
		} finally{
			if (statement != null)
				try{
					statement.clearParameters();
				} catch (SQLException e){
					log.error("Error close statement", e);
				}
			// closed by transaction manager
			// if (connection != null){
			// try{
			// connection.close();
			// } catch (SQLException e){
			// log.error(e);
			// }
			// }
		}

		return result;
	}

	private Connection getPGConnection(){
		Connection conn = getConnection();
		try{
			final Connection connection = new CommonsDbcpNativeJdbcExtractor().getNativeConnection(conn);
			if (connection instanceof org.postgresql.PGConnection){
				((org.postgresql.PGConnection) connection).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
				((org.postgresql.PGConnection) connection).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
			}
		} catch (SQLException e){
			log.error(e);
		} catch (ClassNotFoundException e){
			log.error(e);
		}
		return conn;
	}
}
