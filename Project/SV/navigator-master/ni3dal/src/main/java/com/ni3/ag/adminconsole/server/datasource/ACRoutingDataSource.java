/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.datasource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ACRoutingDataSource extends AbstractRoutingDataSource{

	private static final String NAVIGATOR_HOST = "navigator.host";
	private static final String MAP_PATH = "mappath";
	private static final String DOCROOT_PATH = "docroot";
	private static final String RASTER_SERVER = "rasterserver";
	private static final String DELTA_THRESHOLD = "delta.threshold";
	private static final String DELTA_OUT_THRESHOLD = "deltaOut.threshold";
	private static final String OFFLINE_MODULE_PATH = "offline.modules.path";
	
	private Logger log = Logger.getLogger(ACRoutingDataSource.class);
	private String databasePropertyFileName;
	private Properties props;
	private String databaseVersion;

	private static String INSTANCE_PROPERTY = "com.ni3.ag.adminconsole.instance";
	private static String DATASOURCE_PROPERTY = "datasource";
	private static String DBID_PROPERTY = "dbid";

	private static String SYS_IAM_TABLE_NAME = "sys_iam";
	private static String DB_TYPE_POSTGRESQL = "PostgreSQL";
	private static String DB_TYPE_MSSQL = "MSSQL";

	private boolean ignoreGenerate;

	private List<String> databaseInstanceNames = new ArrayList<String>();
	private Map<String, InstanceDescriptor> instanceDescriptors = new LinkedHashMap<String, InstanceDescriptor>();
	private HashMap<String, DataSource> res;

	public String getDatabaseVersion(){
		return databaseVersion;
	}

	public void setDatabaseVersion(String databaseVersion){
		this.databaseVersion = databaseVersion;
	}

	public Properties getProps(){
		return props;
	}

	public void setProps(Properties props){
		this.props = props;
	}

	public String getDatabasePropertyFileName(){
		return databasePropertyFileName;
	}

	public void setDatabasePropertyFileName(String propertyFileName){
		this.databasePropertyFileName = propertyFileName;
	}

	public void afterPropertiesSet(){
		Properties properties = getProps();
		for (Map.Entry<Object, Object> entry : properties.entrySet()){
			log.info("Loaded property : " + entry.getKey());
		}

		setTargetDataSources(createTargetDatasources());
		super.afterPropertiesSet();
	}

	private HashMap<String, DataSource> createTargetDatasources(){
		res = new HashMap<String, DataSource>();

		int instanceNumber = 1;
		while (haveMoreDatabasePropertiesToParse(instanceNumber)){
			String dbid = props.getProperty(getPropertyName(instanceNumber, DBID_PROPERTY));
			String navHost = props.getProperty(getPropertyName(instanceNumber, NAVIGATOR_HOST));
			String mapPath = props.getProperty(getPropertyName(instanceNumber, MAP_PATH));
			String docrootPath = props.getProperty(getPropertyName(instanceNumber, DOCROOT_PATH));
			String rasterServer = props.getProperty(getPropertyName(instanceNumber, RASTER_SERVER));
			String deltaThreshold = props.getProperty(getPropertyName(instanceNumber, DELTA_THRESHOLD));
			String deltaOutThreshold = props.getProperty(getPropertyName(instanceNumber, DELTA_OUT_THRESHOLD));
			String datasourceName = props.getProperty(getPropertyName(instanceNumber, DATASOURCE_PROPERTY));
			String modulePath = props.getProperty(getPropertyName(instanceNumber, OFFLINE_MODULE_PATH));
			log.info("dbid = " + dbid);
			log.info("navigator host: " + navHost);
			InstanceDescriptor instanceDescriptor = null;

			if (datasourceName == null){
				instanceDescriptor = new InstanceDescriptor(dbid, navHost);
				int datasourceNumber = 1;

				while ((datasourceName = props.getProperty(getDatasourceName(instanceNumber, datasourceNumber))) != null){
					String dsIdentifier = dbid + datasourceNumber;
					DataSource dataSource = createDataSource(dbid, dsIdentifier, datasourceName);
					if (dataSource != null){
						res.put(dsIdentifier, dataSource);
						instanceDescriptor.addDataSource(datasourceName, dsIdentifier);
					}

					datasourceNumber++;
				}
			} else{
				String dsIdentifier = dbid;
				DataSource dataSource = createDataSource(dbid, dsIdentifier, datasourceName);
				if (dataSource != null){
					instanceDescriptor = new InstanceDescriptor(dbid, navHost);
					instanceDescriptor.addDataSource(datasourceName, dbid);
					res.put(dbid, dataSource);
				}
			}

			if (instanceDescriptor != null && instanceDescriptor.hasDatasource()){
				databaseInstanceNames.add(dbid);
				instanceDescriptors.put(dbid, instanceDescriptor);
				if (mapPath != null){
					instanceDescriptor.setMapPath(mapPath);
				}
				if (rasterServer != null){
					instanceDescriptor.setRasterServer(rasterServer);
				}
				if (docrootPath != null){
					instanceDescriptor.setDocrootPath(docrootPath);
				}
				if (deltaThreshold != null)
					try{
						instanceDescriptor.setDeltaThreshold(deltaThreshold);
					} catch (ACException e){
						log.error("", e);
					}
				if (deltaOutThreshold != null)
					try{
						instanceDescriptor.setDeltaOutThreshold(deltaOutThreshold);
					} catch (ACException e){
						log.error("", e);
					}
				if(modulePath != null)
					instanceDescriptor.setModulePath(modulePath);
			}

			instanceNumber++;
		}

		return res;
	}

	private DataSource createDataSource(String dbid, String dsIdentifier, String dsName){
		try{
			InitialContext context = new InitialContext();

			DataSource dataSource = (DataSource) context.lookup(dsName);
			log.info("Created routable datasource:");
			log.info("dataSource = " + dsName);
			log.info("dsIdentifier = " + dsIdentifier);
			if (testConnection(dataSource, dsName))
				return dataSource;
		} catch (NamingException e){
			log.error("Cannot find datasource with name " + dsName);
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private boolean testConnection(DataSource dataSource, String dsName){
		boolean success = false;
		Connection conn = null;
		try{
			conn = dataSource.getConnection();
			success = true;
		} catch (SQLException e){
			log.error("No connection to database, datasource = " + dsName, e);
		} finally{
			try{
				if (conn != null){
					conn.close();
				}
			} catch (SQLException e){
				log.error(e);
			}
		}
		return success;
	}

	private String getPropertyName(int instanceNumber, String propName){
		return INSTANCE_PROPERTY + instanceNumber + "." + propName;
	}

	private String getDatasourceName(int instanceNumber, int datasourceNumber){
		return INSTANCE_PROPERTY + instanceNumber + "." + DATASOURCE_PROPERTY + datasourceNumber;
	}

	private boolean sysIamExists(String dbType){
		boolean exists = false;
		String query = "select * from " + SYS_IAM_TABLE_NAME + ";";
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = this.getConnection();
			Statement st = conn.createStatement();
			rs = st.executeQuery(query);
			exists = rs.next();
		} catch (SQLException e){
			log.warn("ERROR getting data from sys_iam");
		} finally{

			if (rs != null){
				try{
					rs.close();
				} catch (SQLException e){
					log.error(e.getMessage(), e);
				}
				rs = null;
			}
			if (conn != null){
				try{
					conn.close();
				} catch (SQLException e){
					log.error(e.getMessage(), e);
				}
				conn = null;
			}
		}
		return exists;
	}

	private boolean haveMoreDatabasePropertiesToParse(int instanceNumber){
		return props.getProperty(getPropertyName(instanceNumber, DBID_PROPERTY)) != null;
	}

	public List<String> getDatabaseInstanceNames(){
		return databaseInstanceNames;
	}

	@Override
	public Object determineCurrentLookupKey(){
		String currentDatabaseInstanceId = getCurrentDatabaseInstanceId();
		InstanceDescriptor descr = instanceDescriptors.get(currentDatabaseInstanceId);
		if (descr == null)
			return currentDatabaseInstanceId;
		return descr.getDsIdentifier();
	}

	public String getCurrentDatabaseInstanceId(){
		ThreadLocalStorage idStorage = ThreadLocalStorage.getInstance();
		String currentDatabaseInstanceId = idStorage.getCurrentDatabaseInstanceId();
		if (currentDatabaseInstanceId == null){
			currentDatabaseInstanceId = props.getProperty(getPropertyName(1, DBID_PROPERTY));
			log.warn("DatabaseInstanceId = null, default is taken: " + currentDatabaseInstanceId);
		}
		return currentDatabaseInstanceId;
	}

	public Map<String, InstanceDescriptor> getDatasourceDescriptors(){
		return instanceDescriptors;
	}

	/**
	 * Determine whether to generate the database
	 * 
	 * @param ds
	 * @return
	 */
	public boolean getGenerate(InstanceDescriptor ds){
		getDatabaseType(ds);
		if (ignoreGenerate)
			return false;

		boolean generate = !sysIamExists(ds.getDatabaseType());

		log.info("updated routable datasource:");
		log.info("DBID = " + ds.getDBID());
		log.info("db type: " + ds.getDatabaseType());
		log.info("Generate schema: " + generate);
		return generate;
	}

	private void getDatabaseType(InstanceDescriptor ds){
		String type = "";
		Connection conn = null;
		try{
			conn = getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			String productName = dbmd.getDatabaseProductName();
			productName = productName.toLowerCase();
			if (productName.matches("(.)*(" + DB_TYPE_POSTGRESQL.toLowerCase() + ")(.)*"))
				type = DB_TYPE_POSTGRESQL;
			else if (productName.matches("(.)*(" + DB_TYPE_MSSQL.toLowerCase() + ")(.)*"))
				type = DB_TYPE_MSSQL;
		} catch (SQLException e){
			log.error(e.getMessage(), e);
		} finally{
			try{
				if (conn != null)
					conn.close();
				conn = null;
			} catch (SQLException e){
				log.error("Cannot close connection", e);
			}
			conn = null;
		}
		ds.setDatabaseType(type);
	}

	public void setIgnoreGenerate(boolean ignoreGenerate){
		this.ignoreGenerate = ignoreGenerate;
	}

	public boolean getIgnoreGenerate(){
		return ignoreGenerate;
	}

	public boolean isCluster(){
		Iterator<String> it = instanceDescriptors.keySet().iterator();
		while (it.hasNext()){
			String dbid = it.next();
			if (dbid.equals(getCurrentDatabaseInstanceId())){
				InstanceDescriptor is = instanceDescriptors.get(dbid);
				return is.isClusteredInstance();
			}
		}
		return false;
	}

	public InstanceDescriptor getCurrentInstanceDescriptor(){
		return instanceDescriptors.get(getCurrentDatabaseInstanceId());
	}

	public void addDataSource(String dataSourceName){
		if (instanceDescriptors.keySet().contains(dataSourceName))
			return;
		res.put(dataSourceName, createDataSource(dataSourceName, dataSourceName, dataSourceName));
		InstanceDescriptor id = new InstanceDescriptor(dataSourceName, null);
		id.setVisible(false);
		id.addDataSource(dataSourceName, dataSourceName);
		instanceDescriptors.put(dataSourceName, id);
		setTargetDataSources(res);
		super.afterPropertiesSet();
	}

	private void testForDuplicateDataSourceName(String dataSourceName) throws ACException{
		for (String testDbId : instanceDescriptors.keySet()){
			InstanceDescriptor testId = instanceDescriptors.get(testDbId);
			if (testId.isVisible() && dataSourceName.equals(testId.getDataSourceName()))
				throw new ACException(TextID.MsgDuplicateDataSourceName, new String[] { dataSourceName });
		}
	}

	public void addTempDataSource(String dataSourceName, String dbid, String navigatorHost, String mappath, String docroot,
	        String rasterServer, String deltaThreshold, String deltaOutThreshold, String modulePath) throws ACException{

		InstanceDescriptor test = instanceDescriptors.get(dbid);
		if (test != null && test.isVisible())
			throw new ACException(TextID.MsgDuplicateDBID, new String[] { dbid });

		testForDuplicateDataSourceName(dataSourceName);
		DataSource ds = createDataSource(dbid, dbid, dataSourceName);
		if (ds == null)
			throw new ACException(TextID.MsgNoSuchDatasource, new String[] { dataSourceName });
		res.put(dbid, createDataSource(dbid, dbid, dataSourceName));
		InstanceDescriptor id = new InstanceDescriptor(dbid, navigatorHost);
		id.addDataSource(dataSourceName, dbid);
		id.setMapPath(mappath);
		id.setDocrootPath(docroot);
		id.setRasterServer(rasterServer);
		if (deltaThreshold != null && !"".equals(deltaThreshold.trim()))
			id.setDeltaThreshold(deltaThreshold);
		if (deltaOutThreshold != null && !"".equals(deltaOutThreshold.trim()))
			id.setDeltaOutThreshold(deltaOutThreshold);
		id.setModulePath(modulePath);
		instanceDescriptors.put(dbid, id);
		setTargetDataSources(res);
		super.afterPropertiesSet();
	}

	public void deleteDataSourceTemporary(String dbid) throws ACException{
		if (!instanceDescriptors.keySet().contains(dbid))
			throw new ACException(TextID.MsgNoSuchDatasource, new String[] { dbid });
		InstanceDescriptor id = instanceDescriptors.get(dbid);
		id.setVisible(false);
	}

	public boolean isDatasourceVersionMatchCurrent(InstanceDescriptor id){
		String dataSourceVers = getSysIamVersion(id);
		String expectedVers = getExpectedVersion();
		return expectedVers.equals(dataSourceVers);
	}

	private String getExpectedVersion(){
		return databaseVersion == null ? "" : databaseVersion;
	}

	public String getSysIamVersion(InstanceDescriptor ds){
		if (!sysIamExists(ds.getDatabaseType()))
			return "";
		String query = "select version from sys_iam where name = '" + ds.getDatabaseType() + "'";
		String vers = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = this.getConnection();
			Statement st = conn.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()){
				vers = rs.getString(1);
			}
		} catch (SQLException e){
			log.info("ERROR validate user data", e);
		} finally{
			if (rs != null){
				try{
					rs.close();
				} catch (SQLException e){
					log.error(e);
				}
				rs = null;
			}
			if (conn != null){
				try{
					conn.close();
				} catch (SQLException e){
					log.error(e);
				}
				conn = null;
			}
		}
		return vers == null ? "" : vers;
	}

	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return java.util.logging.Logger.getLogger("ACRountingDataSource");
	}
}
