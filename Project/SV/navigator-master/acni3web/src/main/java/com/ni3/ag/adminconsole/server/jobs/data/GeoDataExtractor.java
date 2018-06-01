package com.ni3.ag.adminconsole.server.jobs.data;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

public class GeoDataExtractor{
	private static final Logger log = Logger.getLogger(GeoDataExtractor.class);

	public void getAllGeoData(ACRoutingDataSource dataSource, ExtractStorage storage) throws SQLException, IOException{
		long startTime = System.currentTimeMillis();
		log.info("Extracting geo data");
		Connection connection = null;
		Statement statement = null;
		try{
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			Set<String> geoDataTables = getGeoTables(statement);
			for (String tableName : geoDataTables)
				extractGeoData(tableName, storage, statement);
		} catch (SQLException e){
			log.error("Error extracting geo data: " + e, e);
			throw e;
		} catch (IOException e){
			log.error("Error " + e, e);
			throw e;
		} finally{
			if (statement != null)
				try{
					statement.close();
				} catch (SQLException ex){
					log.error("Error closing statement: " + ex, ex);
				}
			if (connection != null)
				try{
					connection.close();
				} catch (SQLException e){
					log.error("Error closing connection", e);
				}
			log.info("Completed extracting geo data");
			log.debug("Spent time: " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}

	private void extractGeoData(String tableName, ExtractStorage storage, Statement statement) throws SQLException, IOException{
		log.debug("Getting data from " + tableName);

		String sql = "select * from " + tableName;
		log.debug("SQL: " + sql);
		ResultSet rs = statement.executeQuery(sql);
		ResultSetMetaData meta = rs.getMetaData();

		String dropSQL = makeDropSQL(tableName);
		String createSQL = makeCreateSQL(tableName, meta);
		log.debug(dropSQL);
		log.debug(createSQL);
		storage.add(dropSQL);
		storage.add(createSQL);

		String header = null;
		while (rs.next()){
			if (header == null)
				header = createGeoTableInsertHeader(tableName, meta);
			String gtiSQL = makeGeoTableInsertSQL(header, rs);
			log.trace(gtiSQL);
			storage.add(gtiSQL);
		}
		rs.close();

		dropSQL = makeDropMappingSQL(tableName);
		createSQL = createMappingSQL(tableName);
		log.debug(dropSQL);
		log.debug(createSQL);
		storage.add(dropSQL);
		storage.add(createSQL);
	}

	private String createMappingSQL(String tableName){
		return "CREATE TABLE " + tableName + "_mapping" +
				"(" +
				"  nodeid integer NOT NULL,\n" +
				"  gid integer NOT NULL,\n" +
				"  CONSTRAINT pk_" + tableName + "_mapping PRIMARY KEY (nodeid)\n" +
				")";
	}

	private String makeDropMappingSQL(String tableName){
		return "drop table if exists " + tableName + "_mapping";
	}

	private String makeCreateSQL(String tableName, ResultSetMetaData meta) throws SQLException{
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(tableName).append("(");
		int count = meta.getColumnCount();
		for (int i = 1; i <= count; i++){
			sb.append(i != 1 ? "," : "");
			sb.append(meta.getColumnName(i)).append(" ").append(makeColumnDatatype(meta, i));
		}
		sb.append(",");
		sb.append("CONSTRAINT ").append(tableName).append("_pkey PRIMARY KEY (gid),");
		sb.append("CONSTRAINT enforce_dims_the_geom CHECK (st_ndims(the_geom) = 2),");
		sb.append("CONSTRAINT enforce_geotype_the_geom CHECK (geometrytype(the_geom) = 'MULTIPOLYGON'::text OR the_geom IS NULL),");
		sb.append("CONSTRAINT enforce_srid_the_geom CHECK (st_srid(the_geom) = 4326));");
		sb.append("CREATE INDEX ").append(tableName).append("_the_geom_gist ON ").append(tableName).append(" USING gist (the_geom);");
		return sb.toString();
	}

	private String makeColumnDatatype(ResultSetMetaData meta, int i) throws SQLException{
		int type = meta.getColumnType(i);
		switch (type){
			case Types.VARCHAR:
				return "TEXT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.NUMERIC:
			case Types.DECIMAL:
				return "NUMERIC";
			case Types.DOUBLE:
				return "double precision";
			default:
				if ("geometry".equals(meta.getColumnTypeName(i)))
					return "geometry";
				else
					throw new SQLException("Don't know how to handle column of type " + type);
		}
	}

	private String makeDropSQL(String tableName){
		return "drop table if exists " + tableName;
	}

	private String makeGeoTableInsertSQL(String header, ResultSet rs) throws SQLException{
		ResultSetMetaData meta = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		sb.append(header).append("(");
		for (int i = 1; i <= meta.getColumnCount(); i++){
			sb.append(i != 1 ? "," : "").append(wrapValue(rs, i));
		}
		sb.append(")");
		return sb.toString();
	}

	private String createGeoTableInsertHeader(final String tableName, final ResultSetMetaData meta) throws SQLException{
		StringBuilder insertDescription = new StringBuilder();
		insertDescription.append("insert into ").append(tableName).append(" (");
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

	private Object wrapValue(ResultSet resultSet, int i) throws SQLException{
		Object o = resultSet.getObject(i);
		if (o == null)
			return "null";
		if (o instanceof String){
			return "'" + ((String) o).replaceAll("'", "''") + "'";
		} else if (o instanceof Integer)
			return o.toString();
		else if (o instanceof BigDecimal)
			return o.toString();
		else if (o instanceof PGgeometry){
			PGgeometry pg = (PGgeometry) o;
			return "'" + pg.toString().replaceAll("'", "''") + "'";
		} else if(o instanceof Double)
			return o.toString();
		else if(o instanceof PGobject){
			PGobject pg = (PGobject) o;
			return "'" + pg.toString().replaceAll("'", "''") + "'";
		} else
			log.error("Error do not know how to handle type " + o.getClass());
		return o;
	}

	public void storeGeoData(ACRoutingDataSource dataSource, ExtractStorage storage) throws SQLException{
		Connection c = null;
		try{
			c = dataSource.getConnection();
			boolean b = c.getAutoCommit();
			c.setAutoCommit(false);
			storeAllGeoItems(c, storage);
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
				if (c != null)
					c.close();
			} catch (SQLException e){
				log.error("Error closing connection", e);
			}
		}
	}

	private void storeAllGeoItems(Connection c, ExtractStorage storage) throws SQLException{
		int count = 0;
		int size = storage.size();
		Statement statement = c.createStatement();
		for (String geoInsert : storage){
			count++;
			try{
				statement.executeUpdate(geoInsert);
			} catch (SQLException ex){
				log.error("SQL execution failed");
				log.error(geoInsert);
				throw ex;
			}
			if (count % 100 == 0)
				log.debug("Stored " + count + "/" + size);
		}
		log.debug("Stored " + count + "/" + size);
	}
}
