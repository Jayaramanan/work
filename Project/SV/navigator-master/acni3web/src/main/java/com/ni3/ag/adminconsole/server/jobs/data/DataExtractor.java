/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jobs.data;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.util.Base64;
import com.ni3.ag.adminconsole.validation.ACException;

public class DataExtractor{
	private static final List<String> tableNames = new ArrayList<String>();
	private static final List<String> sequenceNames = new ArrayList<String>();
	private static final HashMap<String, String> additionalPreCommands = new HashMap<String, String>();
	private static final HashMap<String, String> additionalPostCommands = new HashMap<String, String>();
	private static final Logger log = Logger.getLogger(DataExtractor.class);
	static{
		tableNames.add("sys_group");
		tableNames.add("sys_user");
		tableNames.add("sys_user_group");
		tableNames.add("sys_schema");
		tableNames.add("sys_object");
		tableNames.add("sys_object_attributes");
		additionalPreCommands.put("cht_predefinedattributes",
		        "alter table cht_predefinedattributes drop constraint cht_predefinedattributes_parent_fk");
		tableNames.add("cht_predefinedattributes");
		additionalPostCommands
		        .put(
		                "cht_predefinedattributes",
		                "alter table cht_predefinedattributes add constraint cht_predefinedattributes_parent_fk FOREIGN KEY (parent) REFERENCES cht_predefinedattributes (id)");
		tableNames.add("sys_chart");
		tableNames.add("cht_language");
		tableNames.add("sys_user_language");
		additionalPreCommands.put("cis_favorites_folder",
		        "alter table cis_favorites_folder drop constraint fk_cis_favorites_folder_03");
		tableNames.add("cis_favorites_folder");
		additionalPostCommands
		        .put(
		                "cis_favorites_folder",
		                "alter table cis_favorites_folder add constraint fk_cis_favorites_folder_03 FOREIGN KEY (parentid) REFERENCES cis_favorites_folder (id)");
		tableNames.add("cis_favorites");
		tableNames.add("gis_territory");
		tableNames.add("gis_map");
		tableNames.add("gis_overlay");
		tableNames.add("geo_thematicfolder");
		tableNames.add("geo_thematicmap");
		tableNames.add("geo_thematiccluster");
		tableNames.add("sys_attribute_group");
		tableNames.add("sys_chart_group");
		tableNames.add("sys_group_prefilter");
		tableNames.add("sys_group_scope");
		tableNames.add("sys_object_chart");
		tableNames.add("sys_chart_attribute");
		tableNames.add("sys_object_connection");
		tableNames.add("sys_object_group");
		tableNames.add("sys_schema_group");
		tableNames.add("sys_url");
		tableNames.add("sys_url_group");
		tableNames.add("cht_icons");
		tableNames.add("sys_metaphor");
		tableNames.add("sys_metaphor_data");
		tableNames.add("sys_settings_application");
		tableNames.add("sys_settings_group");
		tableNames.add("sys_settings_user");
		tableNames.add("sys_licenses");
		tableNames.add("sys_user_edition");
		tableNames.add("sys_formula");
		tableNames.add("sys_context");
		tableNames.add("sys_context_attributes");
		tableNames.add("sys_report_template");

		sequenceNames.add("sys_schema_object_id_seq");
		sequenceNames.add("cht_predefinedattributes_id_seq");
	}

	private UserDataExtractor userDataExtractor;
	private GeoDataExtractor geoDataExtractor;
	private SchemaAdminService schemaAdminService;
	private Map<Integer, Integer> maxUserDelta;

	public void setUserDataExtractor(UserDataExtractor userDataExtractor){
		this.userDataExtractor = userDataExtractor;
	}

	public void setSchemaAdminService(SchemaAdminService schemaAdminService){
		this.schemaAdminService = schemaAdminService;
	}

	public void setGeoDataExtractor(GeoDataExtractor geoDataExtractor){
		this.geoDataExtractor = geoDataExtractor;
	}

	public void getAllData(ACRoutingDataSource dataSource, ExtractStorage storage) throws SQLException{
		Connection c = null;
		try{
			c = dataSource.getConnection();
			for (String s : tableNames){
				log.info("Extracting: " + s);
				if (additionalPreCommands.containsKey(s)){
					String o = additionalPreCommands.get(s);
					storage.add(o);
				}
				int count = getDataForTable(c, storage, s);
				if (additionalPostCommands.containsKey(s)){
					String o = additionalPostCommands.get(s);
					storage.add(o);
				}

				log.info("Extracted: " + count);
			}

			for (String s : sequenceNames)
				storage.add("alter sequence " + s + " restart with " + (getSeqValue(c, s) + 1));
		} finally{
			if (c != null)
				c.close();
		}
	}

	private int getSeqValue(Connection c, String s){
		String sql = "select last_value from " + s;
		Statement st = null;
		try{
			st = c.createStatement();
			log.debug("Sql: " + sql);
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			Object o = rs.getObject(1);
			if (o instanceof BigInteger)
				return ((BigInteger) o).intValue();
			else if (o instanceof Integer)
				return (Integer) o;
			else if (o instanceof Long)
				return ((Long) o).intValue();
			log.error("Failed to interpret result of query: " + sql + " RESULT " + o);
			return -1;
		} catch (SQLException e){
			log.error("Error getting current value of sequence " + s, e);
			return -1;
		} finally{
			if (st != null)
				try{
					st.close();
				} catch (SQLException e){
					log.error("Error closing statement", e);
				}
		}
	}

	public void disableUsers(ACRoutingDataSource dataSource, User user) throws Exception{
		String sql = "update sys_user set isactive = 0, password = '00000000000000000000000000000000',"
		        + " sid = '00000000000000000000000000000000'||id where id <> " + user.getId();
		String sql1 = "update sys_user set isactive = 1, password = '" + user.getPassword() + "', sid = '" + user.getSID()
		        + "' where id = " + user.getId();

		Connection c = null;
		try{
			c = dataSource.getConnection();
			c.setAutoCommit(false);
			Statement st = c.createStatement();
			st.executeUpdate(sql);
			st.executeUpdate(sql1);
			c.commit();
		} catch (Exception ex){
			log.error("Error storing system data", ex);
			throw ex;
		} finally{
			try{
				if (c != null)
					c.close();
			} catch (SQLException e){
				log.error("Cannot close connection", e);
			}
		}
	}

	private int getDataForTable(Connection c, ExtractStorage storage, String tableName) throws SQLException{
		int count = 0;
		ResultSet resultSet = null;
		try{
			Statement st = c.createStatement();
			resultSet = st.executeQuery(createSQL(tableName));
			ResultSetMetaData metaData = resultSet.getMetaData();
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT into ").append(tableName).append("(");
			for (int i = 1; i <= metaData.getColumnCount(); i++){
				if (i != 1)
					sb.append(",");
				sb.append(metaData.getColumnName(i));
			}
			sb.append(") values (");

			while (resultSet.next()){
				StringBuilder current = new StringBuilder();
				current.append(sb.toString());
				// boolean skip = false;
				for (int i = 1; i <= metaData.getColumnCount(); i++){
					if (i != 1)
						current.append(",");
					current.append(wrapValue(resultSet, i));
				}
				current.append(");");

				storage.add(current.toString());
				count++;
			}
		} finally{
			if (resultSet != null){
				resultSet.close();
			}
		}
		return count;
	}

	private String createSQL(String tableName){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(tableName);
		log.debug(sql);
		return sql.toString();
	}

	private Object wrapValue(ResultSet resultSet, int i) throws SQLException{
		Object o = resultSet.getObject(i);
		if (o == null)
			return "null";
		if (o instanceof String){
			String val = (String) o;
			o = "\'" + val.replace("'", "''").replace("\n", "\\n") + '\'';
		} else if (o instanceof Date){
			Date d = (Date) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			o = "'" + sdf.format(d) + "'";
		} else if (o instanceof Integer)
			return o;
		else if (o instanceof BigDecimal)
			return o;
		else if (o instanceof byte[]){
			o = "decode('" + Base64.encodeBytes((byte[]) o) + "', 'base64')";
		}
		return o;
	}

	public void storeAllData(ACRoutingDataSource dataSource, ExtractStorage storage) throws ACException{
		Connection c = null;
		try{
			c = dataSource.getConnection();

			boolean b = c.getAutoCommit();
			c.setAutoCommit(false);
			Statement st = c.createStatement();
			int index = 0;
			boolean wasAnySimple = false;
			int size = storage.size();
			for (String o : storage){
				st.addBatch(o);
				wasAnySimple = true;
				if (index % 1000 == 0)
					log.info("Prepared: " + (int) (((double) index) / ((double) size) * 100) + "%");
				index++;
			}
			log.info("Prepared: 100%. Executing...");
			if (wasAnySimple)
				st.executeBatch();
			log.info("System data stored");
			appendSchemaTables(c);

			c.commit();
			c.setAutoCommit(b);
		} catch (Exception ex){
			log.error("Error storing system data", ex);
			throw new ACException(TextID.MsgErrorExtractingThickClient, new String[] { "Failed to store system data" });
		} finally{
			try{
				c.close();
			} catch (SQLException e){
				log.error("Cannot close connection", e);
			}
		}
	}

	private void appendSchemaTables(Connection c) throws SQLException{
		log.debug("call appendSchemaTables");
		Statement st = c.createStatement();
		ResultSet resultSet = st.executeQuery("select id from sys_schema");
		while (resultSet.next()){
			Integer schemaId = resultSet.getInt(1);
			schemaAdminService.updateUserTables(schemaId, 0);
		}
		resultSet.close();
		st.close();
	}

	public void storeUserAllData(ACRoutingDataSource dataSource, ExtractStorage storage) throws SQLException{
		userDataExtractor.storeUserData(dataSource, storage);
	}

	public void storeAllGeoData(ACRoutingDataSource dataSource, ExtractStorage geoExtractStorage) throws SQLException{
		geoDataExtractor.storeGeoData(dataSource, geoExtractStorage);
	}

	public void getUserAllData(ACRoutingDataSource dataSource, ExtractStorage storage, Group group, boolean withFirstDegree)
	        throws SQLException{
		userDataExtractor.setDataSource(dataSource);
		userDataExtractor.getAllUserData(group, storage, withFirstDegree);
	}

	public void getGeoData(ACRoutingDataSource dataSource, ExtractStorage geoExtractStorage) throws SQLException, IOException{
		geoDataExtractor.getAllGeoData(dataSource, geoExtractStorage);
	}

	public void fillMaxUserDeltas(ACRoutingDataSource dataSource, String userIds) throws SQLException{
		maxUserDelta = new HashMap<Integer, Integer>();
		Connection c = null;
		Statement st = null;
		String sql = null;
		try{
			log.debug("gettings max delta for user");
			c = dataSource.getConnection();
			st = c.createStatement();
			sql = "select target_user_id, coalesce(max(id),0) from sys_delta_user";
			sql += " where target_user_id in (" + userIds + ")";
			sql += " group by target_user_id";
			log.debug("executing: " + sql);
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				maxUserDelta.put(rs.getInt(1), rs.getInt(2));
			}
			log.debug("max delta ids found for users, user count = " + maxUserDelta.size());
			rs.close();
		} catch (SQLException e){
			log.error("error exequte query: " + sql, e);
			throw e;
		} finally{
			if (st != null){
				try{
					st.close();
				} catch (SQLException e){
					log.error("error closing statement", e);
				}
			}
			if (c != null){
				try{
					c.close();
				} catch (SQLException e){
					log.error("error close connection", e);
				}
			}
		}
	}

	public void redirectSequences(User user) throws ACException{
		userDataExtractor.redirectSequences(user);
	}

	public Integer getMaxUserDelta(User user){
		return maxUserDelta.get(user.getId());
	}
}
