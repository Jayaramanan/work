/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jobs.data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DataCleaner extends HibernateDaoSupport implements Cleaner{
	private static final Logger log = Logger.getLogger(DataCleaner.class);
	// !!!!Do not change order of tables in list
	private static final String[] TABLES = new String[]{"sys_chart_attribute", "cis_edges", "cis_nodes",
			"cis_edges_scope", "cis_nodes_scope", "cis_objects", "cis_favorites", "cis_favorites_folder", "gis_territory",
			"gis_overlay", "geo_thematiccluster", "geo_thematicmap", "geo_thematicfolder", "sys_attribute_group",
			"sys_chart_group", "sys_chart_job", "sys_chart", "sys_clusters", "sys_delta_user", "sys_delta_params",
			"sys_delta_header", "sys_formula", "sys_group_prefilter", "sys_group_scope", "sys_licenses", "gis_map",
			"sys_map_job", "sys_module_user", "sys_module_list", "sys_metaphor_data", "sys_metaphor", "cht_icons",
			"sys_context_attributes", "sys_context", "sys_object_chart", "sys_object_connection",
			"cht_predefinedattributes", "sys_object_attributes", "sys_object_group", "sys_schema_group",
			"sys_offline_job", "sys_report_template", "sys_settings_application", "sys_settings_group", "sys_settings_user",
			"sys_url_group", "sys_url", "sys_user_activity", "sys_user_data_error", "sys_user_edition", "sys_user_group",
			"sys_user_language", "cht_language", "sys_group", "sys_object", "sys_schema", "sys_user"};
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public void cleanData() throws ACException{
		log.info("Cleaning target database...");
		Connection cc = null;
		Statement st = null;
		String sql = null;
		try{
			cc = dataSource.getConnection();
			st = cc.createStatement();

			Set<String> geoTables = getGeoTables(st);

			for (String s : TABLES){
				sql = "TRUNCATE TABLE " + s + " CASCADE";
				log.debug("executing: " + sql);
				st.execute(sql);
			}
			log.info("droping user tables");
			List<String> userTables = new ArrayList<String>();
			sql = "select tablename from pg_tables where tablename ilike 'usr_%' and schemaname = current_schema()";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()){
				String name = rs.getString(1);
				log.debug("found user table: " + name);
				userTables.add(name);
			}
			rs.close();
			log.debug("user tables collected, start to drop");
			for (String s : userTables){
				sql = "drop table if exists " + s;
				log.debug("executing: " + sql);
				st.execute(sql);
			}

			for(String tableName : geoTables){
				sql = "drop table if exists " + tableName;
				log.debug(sql);
				st.executeUpdate(sql);
				sql = "drop table if exists " + tableName + "_mapping";
				log.debug(sql);
				st.executeUpdate(sql);
			}
			log.debug("drop completed");
		} catch (SQLException e){
			log.error("error executing sql: " + sql, e);
			throw new ACException(TextID.MsgEmpty, new String[]{"" + e + " | " + e.getMessage()});
		} finally{
			if (st != null){
				try{
					st.close();
				} catch (SQLException e2){
					log.error("Error closing statement", e2);
				}
			}
			if (cc != null){
				try{
					cc.close();
				} catch (SQLException e){
					log.error("error closing connections");
				}
			}
		}
		log.info("Cleaning done");
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
}
