/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jobs.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.service.SequenceValueService;
import com.ni3.ag.adminconsole.util.Base64;
import com.ni3.ag.adminconsole.util.IntParser;
import com.ni3.ag.adminconsole.validation.ACException;
import org.apache.log4j.Logger;

public class UserDataExtractor{
	private static final Logger log = Logger.getLogger(UserDataExtractor.class);
	private static final Character USE_SCOPE = 'S';
	private static final String[] EDGE_COLUMNS = {"id", "edgeType", "Cmnt", "Directed", "Strength", "InPath",
			"ConnectionType", "FromID", "ToID", "userid"};
	private static final String[] NODE_COLUMNS = {"id", "nodeType", "lon", "lat", "iconname"};
	private static final String[] OBJECT_COLUMNS = {"id", "objecttype", "userid", "status", "creator"};
	private static final String[] EDGES_SCOPE_COLUMNS = {"edgeid", "groupid", "flag"};
	private static final String[] NODES_SCOPE_COLUMNS = {"nodeid", "groupid", "flag"};
	private static final String IN_NODE_IDS_SQL = "(select id from tmp_nodes)";
	private static final String IN_EDGE_IDS_SQL = "(select id from tmp_edges)";
	private static final String CIS_NODES_TABLE = "cis_nodes";
	private static final String CIS_EDGES_TABLE = "cis_edges";
	private static final String TMP_NODES_TABLE = "tmp_nodes";
	private static final String TMP_EDGES_TABLE = "tmp_edges";

	private SchemaDAO schemaDAO;
	private UserDAO userDAO;
	private SequenceValueService sequenceValueService;
	private Set<Integer> availableNodeSet;
	private Set<Integer> availableEdgeSet;
	private ACRoutingDataSource dataSource;

	public SequenceValueService getSequenceValueService(){
		return sequenceValueService;
	}

	public void setSequenceValueService(SequenceValueService sequenceValueService){
		this.sequenceValueService = sequenceValueService;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setDataSource(ACRoutingDataSource dataSource){
		this.dataSource = dataSource;
	}

	public Hashtable<String, Integer> getClientTableExportPreview(String userIds, boolean withFirstDegree){
		Hashtable<String, Integer> result = new Hashtable<String, Integer>();
		String[] ids = userIds.split(",");
		User user = userDAO.getById(Integer.parseInt(ids[0]));
		Group group = user.getGroups().get(0);
		List<Schema> schemas = schemaDAO.getSchemas();

		Connection c = null;
		try{
			c = dataSource.getConnection();
			c.setAutoCommit(false);
			fillAvailableIds(c, schemas, group, withFirstDegree);
		} catch (SQLException e){
			log.error(e);
			return result;
		} finally{
			try{
				c.rollback();
			} catch (SQLException e){
				log.error(e);
			}
			try{
				c.close();
			} catch (SQLException e){
				log.error(e);
			}
			c = null;
		}

		result.put(CIS_EDGES_TABLE, new Integer(availableEdgeSet.size()));
		result.put(CIS_NODES_TABLE, new Integer(availableNodeSet.size()));
		clearSets();

		return result;
	}

	public void getAllUserData(Group group, ExtractStorage storage, boolean withFirstDegree) throws SQLException{
		List<Schema> schemas = schemaDAO.getSchemas();
		Connection c = null;
		try{
			c = dataSource.getConnection();
			c.setAutoCommit(false);

			fillAvailableIds(c, schemas, group, withFirstDegree);
			clearSets();

			List<Integer> objDefinitionIds = getAvailableObjectDefinitionIds(c);
			for (Schema schema : schemas){
				for (ObjectDefinition od : schema.getObjectDefinitions()){
					if (!objDefinitionIds.contains(od.getId())){
						continue;
					}
					UserDataTable udt = createUserDataTable(od, od.getTableName());
					getUsrData(c, udt, od.isEdge(), storage);

					if (od.hasContextAttributes()){
						udt = createUserDataTable(od, od.getTableName() + ObjectAttribute.CONTEXT_TABLE_SUFFIX);
						getUsrData(c, udt, od.isEdge(), storage);
					}
				}
			}

			// cis_nodes should be extracted first, because of the constraint in cis_edges on fromid and toid
			getCisData(c, CIS_NODES_TABLE, NODE_COLUMNS, "id in " + IN_NODE_IDS_SQL, storage);
			getCisData(c, CIS_EDGES_TABLE, EDGE_COLUMNS, "id in " + IN_EDGE_IDS_SQL, storage);
			getCisData(c, "cis_objects", OBJECT_COLUMNS,
					"id in (" + IN_NODE_IDS_SQL + " union all " + IN_EDGE_IDS_SQL + ")", storage);

			getCisData(c, "cis_edges_scope", EDGES_SCOPE_COLUMNS, "edgeid in " + IN_EDGE_IDS_SQL, storage);
			getCisData(c, "cis_nodes_scope", NODES_SCOPE_COLUMNS, "nodeid in " + IN_NODE_IDS_SQL, storage);

			getGeoData(c, storage);
		} catch (SQLException e){
			log.error(e);
			throw e;
		} finally{
			try{
				c.rollback();
			} catch (SQLException e){
				log.error(e);
			}
			try{
				c.close();
			} catch (SQLException e){
				log.error(e);
			}
		}
	}

	private void getGeoData(Connection c, ExtractStorage storage) throws SQLException{
		long startTime = System.currentTimeMillis();
		log.info("Extracting geo data");
		Statement statement = null;
		try{
			statement = c.createStatement();
			Set<String> geoDataTables = getGeoTables(statement);
			for (String tableName : geoDataTables)
				extractUserGeoData(tableName, storage, statement);
		} catch (SQLException e){
			log.error("Error extracting geo data: " + e, e);
			throw e;
		} finally{
			if (statement != null)
				try{
					statement.close();
				} catch (SQLException ex){
					log.error("Error closing statement: " + ex, ex);
				}
			log.info("Completed extracting geo data");
			log.debug("Spent time: " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}

	private void extractUserGeoData(String tableName, ExtractStorage storage, Statement statement) throws SQLException{
		String sql = "select * from " + tableName + "_mapping g where g.nodeid in (select id from tmp_nodes)";
		ResultSet rs = statement.executeQuery(sql);
		ResultSetMetaData meta = rs.getMetaData();
		String header = createGeoTableInsertHeader(tableName + "_mapping", meta);
		while (rs.next()){
			String gtiSQL = makeGeoTableInsertSQL(header, rs);
			log.trace(gtiSQL);
			storage.add(gtiSQL);
		}
		rs.close();
	}

	private String makeGeoTableInsertSQL(String header, ResultSet rs) throws SQLException{
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		sb.append(header).append("(");
		for (int i = 1; i <= meta.getColumnCount(); i++){
			sb.append(i != 1 ? "," : "").append(wrapValue(rs.getObject(i)));
		}
		sb.append(")");
		return sb.toString();
	}

	private String createGeoTableInsertHeader(final String tableName, final ResultSetMetaData meta) throws SQLException{
		StringBuilder insertDescription = new StringBuilder();
		insertDescription.append(tableName).append(" (");
		for (int i = 1; i <= meta.getColumnCount(); i++){
			insertDescription.append((i != 1) ? ", " : "").append(meta.getColumnName(i));
		}
		insertDescription.append(") values ");
		return insertDescription.toString();
	}

	private Set<String> getGeoTables(Statement statement) throws SQLException{
		Set<String> geoDataTables = new HashSet<String>();
		String sql = "select tablename from gis_overlay";
		log.debug("SQL: " + sql);
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()){
			String s = rs.getString("tablename");
			if (s == null || s.trim().isEmpty())
				continue;
			geoDataTables.add(s);
		}
		rs.close();

		sql = "select tablename from gis_territory";
		log.debug("SQL: " + sql);
		rs = statement.executeQuery(sql);
		while (rs.next()){
			String s = rs.getString("tablename");
			if (s == null || s.trim().isEmpty())
				continue;
			geoDataTables.add(s);
		}
		rs.close();

		return geoDataTables;
	}

	private void fillAvailableIds(Connection c, List<Schema> schemas, Group group, boolean withFirstDegree)
			throws SQLException{
		String nodeIncludeSql = getNodeScopeSql(group);
		String edgeIncludeSql = getEdgeScopeSql(group);
		String nodeExcludeSql = getNodeScopeExcludeSql(group);
		String edgeExcludeSql = getEdgeScopeExcludeSql(group);

		availableNodeSet = new HashSet<Integer>();
		availableEdgeSet = new HashSet<Integer>();

		for (Schema schema : schemas){
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				boolean isEdge = od.isEdge();
				if (!isAvailableObject(od, group)){
					continue;
				}

				if (isEdge){
					List<Integer> ids = getCoreScopeIds(c, od, group, edgeIncludeSql, edgeExcludeSql);
					availableEdgeSet.addAll(ids);
				} else{
					List<Integer> ids = getCoreScopeIds(c, od, group, nodeIncludeSql, nodeExcludeSql);
					availableNodeSet.addAll(ids);
				}
			}
		}

		storeIdsToTempTable(c, availableNodeSet, true, false, true);
		storeIdsToTempTable(c, availableEdgeSet, true, false, false);

		if (withFirstDegree){
			Set<Integer> additionalNodeSet = getAdditionalScopeNodeIds(c);
			availableEdgeSet = getFullScopeEdgeIds(c);
			storeIdsToTempTable(c, additionalNodeSet, false, false, true);
			availableNodeSet.addAll(additionalNodeSet);
		} else{
			availableEdgeSet = getEdgeIdsBetweenCoreNodes(c);
		}

		storeIdsToTempTable(c, availableEdgeSet, false, true, false);
		log.debug("Found total nodes: " + availableNodeSet.size() + ", edges: " + availableEdgeSet.size());
	}

	public void clearSets(){
		availableNodeSet.clear();
		availableNodeSet = null;
		availableEdgeSet.clear();
		availableEdgeSet = null;
	}

	private List<Integer> getCoreScopeIds(Connection c, ObjectDefinition od, Group group, String includeWhere,
										  String excludeWhere) throws SQLException{
		String cisTable = od.isNode() ? CIS_NODES_TABLE : CIS_EDGES_TABLE;
		StringBuffer sql = new StringBuffer("select ct.id from ").append(cisTable).append(" ct, ");
		sql.append(od.getTableName()).append(" ut where ut.id = ct.id");
		StringBuffer whereClause = new StringBuffer("");
		for (ObjectAttribute attr : od.getObjectAttributes()){
			if (attr.isPredefined() && !attr.isInContext()){
				String in = getInClause(attr, group);
				if (in != null && !in.isEmpty()){
					String prefix = attr.getInTable().equalsIgnoreCase(cisTable) ? "ct." : "ut.";
					if (whereClause.length() == 0){
						whereClause.append(" and(");
					} else{
						whereClause.append(" and ");
					}
					whereClause.append(prefix).append(attr.getName()).append(" in (").append(in).append(")");
				}
			}
		}

		if (whereClause.length() > 0){
			whereClause.append(")");
		}
		if (includeWhere != null){
			whereClause.append(" and ct.id in ").append(includeWhere);
		}

		if (excludeWhere != null){
			whereClause.append(" and ct.id not in ").append(excludeWhere);
		}

		if (whereClause.length() > 0){
			sql.append(whereClause);
		}
		log.debug(sql);
		List<Integer> ids = getIntegerList(c, sql.toString());
		return ids;
	}

	private String getInClause(ObjectAttribute attribute, Group group){
		String in = "";
		boolean fullAccess = true;
		for (PredefinedAttribute pa : attribute.getPredefinedAttributes()){
			boolean shouldAdd = true;
			for (GroupPrefilter gpf : group.getPredefAttributeGroups()){
				if (gpf.getPredefinedAttribute().equals(pa)){
					shouldAdd = false;
					fullAccess = false;
				}
			}
			if (shouldAdd){
				in += pa.getId() + ", ";
			}
		}
		if (!fullAccess){
			in = removeLastComma(in);
			return in;
		}
		return null;
	}

	private Set<Integer> getAdditionalScopeNodeIds(Connection c) throws SQLException{
		Set<Integer> idSet = new HashSet<Integer>();
		String sql = "select ce.toid from cis_edges ce where exists (select id from tmp_edges te where te.id = ce.id)"
				+ " and exists (select id from tmp_nodes tn1 where tn1.id = ce.fromid)";
		log.debug(sql);
		List<Integer> relatedIds = getIntegerList(c, sql);
		idSet.addAll(relatedIds);

		sql = "select ce.fromid from cis_edges ce where exists (select id from tmp_edges te where te.id = ce.id)"
				+ " and exists (select id from tmp_nodes tn1 where tn1.id = ce.toid)";
		log.debug(sql);
		relatedIds = getIntegerList(c, sql);
		idSet.addAll(relatedIds);
		return idSet;
	}

	private Set<Integer> getEdgeIdsBetweenCoreNodes(Connection c) throws SQLException{
		String sql = "select ce.id from cis_edges ce where exists (select id from tmp_edges te where te.id = ce.id)";
		sql += " and exists (select id from tmp_nodes tn1 where tn1.id = ce.fromid)";
		sql += " and exists (select id from tmp_nodes tn2 where tn2.id = ce.toid)";
		log.debug(sql);
		List<Integer> ids = getIntegerList(c, sql);
		return new HashSet<Integer>(ids);
	}

	private Set<Integer> getFullScopeEdgeIds(Connection c) throws SQLException{
		Set<Integer> idSet = new HashSet<Integer>();
		String sql = "select ce.id from cis_edges ce where exists (select id from tmp_edges te where te.id = ce.id)";
		sql += " and exists (select id from tmp_nodes tn1 where tn1.id = ce.fromid)";
		log.debug(sql);
		List<Integer> ids = getIntegerList(c, sql);
		idSet.addAll(ids);

		sql = "select ce.id from cis_edges ce where exists (select id from tmp_edges te where te.id = ce.id)";
		sql += " and exists (select id from tmp_nodes tn1 where tn1.id = ce.toid)";

		log.debug(sql);
		ids = getIntegerList(c, sql);
		idSet.addAll(ids);
		return idSet;
	}

	private List<Integer> getAvailableObjectDefinitionIds(Connection c) throws SQLException{
		String sql = "select distinct objectType from cis_objects where id in (" + IN_EDGE_IDS_SQL + " union all "
				+ IN_NODE_IDS_SQL + ")";
		log.debug("sql");
		List<Integer> ids = getIntegerList(c, sql);
		return ids;
	}

	private String removeLastComma(String in){
		String trim = in.trim();
		if (trim.endsWith(",")){
			return trim.substring(0, trim.length() - 1);
		} else
			return trim;
	}

	private int getUsrData(Connection c, UserDataTable udt, boolean isEdge, ExtractStorage dest) throws SQLException{
		String tableName = udt.getTableName();
		StringBuffer sql = new StringBuffer("select ");
		for (int i = 0; i < udt.getColumnNames().size(); i++){
			String columnName = udt.getColumnNames().get(i);
			if (i > 0)
				sql.append(", ");
			sql.append(columnName);
		}
		sql.append(" from ").append(tableName);
		sql.append(" where id in ").append(isEdge ? IN_EDGE_IDS_SQL : IN_NODE_IDS_SQL);
		log.debug(tableName + " SQL: " + sql.toString());
		int count = getUserData(c, sql.toString(), dest, udt);

		log.info("Extracted: " + tableName + ", record count = " + count);

		return count;
	}

	private UserDataTable createUserDataTable(ObjectDefinition od, String tableName){
		UserDataTable udt = new UserDataTable(tableName);
		udt.getColumnNames().add("id");
		for (ObjectAttribute attr : od.getObjectAttributes()){
			if (attr.getInTable().equalsIgnoreCase(tableName) && !attr.getName().equalsIgnoreCase("id"))
				udt.getColumnNames().add(attr.getName());
		}
		return udt;
	}

	private void storeIdsToTempTable(Connection c, Set<Integer> ids, boolean createTable, boolean cleanTable, boolean isNode)
			throws SQLException{
		int size = ids.size();
		String tmpTbl = isNode ? TMP_NODES_TABLE : TMP_EDGES_TABLE;
		log.debug("Store ids to tmp table " + tmpTbl + ",count = " + size);

		String insertSql = "insert into " + tmpTbl + " values(";
		Statement st = null;
		try{
			st = c.createStatement();

			if (createTable){
				String tblSql = "create temp table " + tmpTbl + " (id integer, CONSTRAINT prk_" + tmpTbl
						+ " PRIMARY KEY (id)) on commit drop";
				st.addBatch(tblSql);
			} else if (cleanTable){
				st.addBatch("delete from " + tmpTbl);
			}
			st.executeBatch();

			int i = 0;
			for (Integer id : ids){
				if (isNode && !createTable && !cleanTable && existNodeInCoreScope(id)){
					continue;
				}
				st.addBatch(insertSql + id + ")");
				if ((i > 0 && i % 100000 == 0) || i == size - 1){
					st.executeBatch();
					log.debug("Stored temporary rows: " + (i + 1));
				}
				i++;
			}
			st.executeBatch();
		} finally{
			if (st != null){
				st.close();
			}
		}
	}

	private boolean existNodeInCoreScope(Integer id){
		return availableNodeSet.contains(id);
	}

	private UserDataTable getCisData(Connection c, String tableName, String[] columns, String whereSql, ExtractStorage dest)
			throws SQLException{
		UserDataTable result = new UserDataTable(tableName);
		StringBuffer sql = new StringBuffer("select ");
		for (int i = 0; i < columns.length; i++){
			if (i > 0){
				sql.append(", ");
			}
			sql.append(columns[i]);
			result.getColumnNames().add(columns[i]);
		}
		sql.append(" from ").append(tableName);
		sql.append(" where ").append(whereSql);

		log.debug("Start getting data from table " + tableName + ",sql = " + sql);
		int count = getUserData(c, sql.toString(), dest, result);

		log.info("Extracted: " + tableName + ", record count = " + count);

		return result;
	}

	private String removeSpecialCharacters(String sql){
		String s = sql.replaceAll("(\\\\n)", " ");
		s = s.replaceAll("[\\t\\n\\x0B\\f\\r]", " ");
		return s;
	}

	private String getNodeScopeSql(Group group){
		GroupScope scope = group.getGroupScope();
		if (!USE_SCOPE.equals(group.getNodeScope()) || scope == null){
			return null;
		}

		String scopeSql = scope.getNodeScope();
		if (scopeSql == null || scopeSql.isEmpty()){
			return null;
		}
		scopeSql = removeSpecialCharacters(scopeSql);
		return "(" + scopeSql + ")";
	}

	private String getNodeScopeExcludeSql(Group group){
		if (!USE_SCOPE.equals(group.getNodeScope())){
			return null;
		}
		String sql = "(select nodeid from cis_nodes_scope where groupid = " + group.getId() + ")";
		return sql;
	}

	private String getEdgeScopeSql(Group group){
		GroupScope scope = group.getGroupScope();
		if (!USE_SCOPE.equals(group.getEdgeScope()) || scope == null){
			return null;
		}
		String scopeSql = scope.getEdgeScope();
		if (scopeSql == null || scopeSql.isEmpty()){
			return null;
		}
		log.debug("Edge scope sql for group " + group.getName() + ": " + scopeSql);
		scopeSql = removeSpecialCharacters(scopeSql);
		return "(" + scopeSql + ")";
	}

	private String getEdgeScopeExcludeSql(Group group){
		if (!USE_SCOPE.equals(group.getEdgeScope())){
			return null;
		}
		String sql = "(select edgeid from cis_edges_scope where groupid = " + group.getId() + ")";
		return sql;
	}

	public void storeUserData(ACRoutingDataSource dataSource, ExtractStorage storage) throws SQLException{
		Connection c = null;
		Statement st = null;
		try{
			c = dataSource.getConnection();
			boolean b = c.getAutoCommit();
			c.setAutoCommit(false);
			st = c.createStatement();
			boolean wasAny = false;
			int count = 0;
			int size = storage.size();
			for (String s : storage){
				count++;
				st.addBatch("insert into " + s);
				wasAny = true;
				if (count % 50000 == 0){
					log.info("Preexecute: " + count);
					st.executeBatch();
					st.clearBatch();
					log.info("Executed: " + count + " out of " + size);
					wasAny = false;
				}
			}
			if (wasAny)
				st.executeBatch();
			log.info("Add stored");
			c.commit();
			c.setAutoCommit(b);
		} catch (SQLException e){
			log.error("Sql error ", e);
			Exception next = e.getNextException();
			if (next != null)
				log.error("Next exception: " + next.getMessage(), next);
			try{
				if (c != null)
					c.rollback();
			} catch (SQLException e1){
				log.error("Error rolling back transaction", e1);
			}
			throw e;
		} finally{
			try{
				if (st != null)
					st.close();
			} catch (SQLException e){
				log.error("Error closing statement", e);
			}
			try{
				if (c != null)
					c.close();
			} catch (SQLException e){
				log.error("Error closing connection", e);
			}
		}

	}

	private String getFullInsertSql(String sql, Object[] row){
		StringBuffer sb = new StringBuffer(sql);
		for (int i = 0; i < row.length; i++){
			if (i > 0){
				sb.append(", ");
			}
			sb.append(wrapValue(row[i]));
		}
		sb.append(")");
		return sb.toString();
	}

	private String getInsertSql(UserDataTable udt){
		StringBuffer sql = new StringBuffer();
		sql.append(udt.getTableName()).append(" (");
		List<String> columnNames = udt.getColumnNames();
		for (int i = 0; i < columnNames.size(); i++){
			if (i > 0){
				sql.append(", ");
			}
			sql.append(columnNames.get(i));
		}
		sql.append(") values (");
		return sql.toString();
	}

	/**
	 * First degree object is the object that is reachable from any object a user has privileges to read.
	 *
	 * @param od			  object
	 * @param group		   user group
	 * @param withFirstDegree true if first degree objects are available
	 * @return true if available
	 */
	private boolean isAvailableObject(ObjectDefinition od, Group group){
		List<ObjectGroup> ougs = group.getObjectGroups();
		for (ObjectGroup oug : ougs){
			if (oug.getObject().equals(od) && oug.isCanRead()){
				return true;
			}
		}
		return false;
	}

	private String wrapValue(Object o){
		if (o == null)
			return "null";
		if (o instanceof String){
			String val = (String) o;
			o = "\'" + val.replace("'", "''").replace("\n", "\\n") + '\'';
		} else if (o instanceof Character){
			Character ch = (Character) o;
			if (ch == '\n')
				o = "\\n";
			else if (ch == '\'')
				o = "''''";
			else
				o = "" + '\'' + ch + '\'';
		} else if (o instanceof Date){
			Date d = (Date) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			o = "'" + sdf.format(d) + "'";
		} else if (o instanceof Integer || o instanceof BigInteger || o instanceof Long || o instanceof BigDecimal){
			return o + "";
		} else if (o instanceof byte[]){
			o = "decode('" + Base64.encodeBytes((byte[]) o) + "', 'base64')";
		} else
			log.error("Dont now how wrap value: " + o.getClass());
		return o + "";
	}

	public void redirectSequences(User usr) throws ACException{
		for (String seqName : User.SEQUENCES){
			int currentVal = sequenceValueService.getCurrentValForSequence(seqName, usr);
			userDAO.redirectSequence(seqName, currentVal);
		}
	}

	private List<Integer> getIntegerList(Connection connection, String sql) throws SQLException{
		List<Integer> result = new ArrayList<Integer>();
		Statement statement = null;
		log.debug("Start sql query: " + sql);
		try{
			statement = connection.createStatement();
			statement.setFetchSize(10);
			ResultSet rs = statement.executeQuery(sql);
			int row = 1;
			while (rs.next()){
				result.add(IntParser.getInt(rs.getInt(1)));

				if (row % 100000 == 0)
					log.debug("Got rows: " + (row));
				row++;
			}
			if (row > 1)
				log.debug("Got rows: " + (row));
		} finally{
			if (statement != null){
				statement.close();
			}
		}
		return result;
	}

	private int getUserData(Connection connection, String sql, ExtractStorage dest, UserDataTable udt) throws SQLException{
		log.debug("Start sql query: " + sql);
		Statement statement = null;
		String baseStoreSql = getInsertSql(udt);
		int row = 1;
		try{
			statement = connection.createStatement();
			statement.setFetchSize(10);
			ResultSet rs = statement.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();

			while (rs.next()){
				Object[] arr = new Object[columnCount];
				for (int i = 0; i < arr.length; i++){
					arr[i] = rs.getObject(i + 1);
				}
				dest.add(getFullInsertSql(baseStoreSql, arr));

				if (row % 100000 == 0)
					log.debug("Got rows: " + (row));
				row++;
			}
			if (row > 1)
				log.debug("Got rows: " + (row));
		} finally{
			if (statement != null){
				statement.close();
			}
		}
		return row;
	}
}
