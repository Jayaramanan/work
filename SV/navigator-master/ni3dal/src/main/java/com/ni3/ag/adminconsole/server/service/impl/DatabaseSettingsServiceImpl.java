/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.DeltaThreshold;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseSettingsService;

public class DatabaseSettingsServiceImpl implements DatabaseSettingsService{

	private DataSource dataSource;
	private String databaseName;

	private Properties databaseProperties;

	public void setDatabaseProperties(Properties databaseProperties){
		this.databaseProperties = databaseProperties;
	}

	@Override
	public Map<String, String> getCommonProperties(){
		Map<String, String> ret = new LinkedHashMap<String, String>();
		ret.put(EXPORT_SIZE, databaseProperties.getProperty(EXPORT_SIZE));
		ret.put(EXPIRY_PERIOD, databaseProperties.getProperty(EXPIRY_PERIOD));
		ret.put(HIBERNATE_DIALECT, databaseProperties.getProperty(HIBERNATE_DIALECT));
		ret.put(OFFLINE_TMP_DATASOURCE, databaseProperties.getProperty(OFFLINE_TMP_DATASOURCE));
		ret.put(OFFLINE_TMP_DBNAME, databaseProperties.getProperty(OFFLINE_TMP_DBNAME));
		ret.put(OFFLINE_TMP_DBHOST, databaseProperties.getProperty(OFFLINE_TMP_DBHOST));
		ret.put(OFFLINE_TMP_DBPORT, databaseProperties.getProperty(OFFLINE_TMP_DBPORT));
		ret.put(OFFLINE_TMP_DBUSER, databaseProperties.getProperty(OFFLINE_TMP_DBUSER));
		ret.put(OFFLINE_TMP_DBPASS, databaseProperties.getProperty(OFFLINE_TMP_DBPASS));
		ret.put(OFFLINE_PGDUMP, databaseProperties.getProperty(OFFLINE_PGDUMP));

		return ret;
	}

	public String getDatabaseName(){
		return databaseName;
	}

	public void setDatabaseName(String databaseName){
		this.databaseName = databaseName;
	}

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public List<DatabaseInstance> getDatabaseInstanceNames(){
		Map<String, InstanceDescriptor> instanceDescriptors = ((ACRoutingDataSource) dataSource).getDatasourceDescriptors();
		List<DatabaseInstance> databaseInstances = new ArrayList<DatabaseInstance>();
		for (InstanceDescriptor instance : instanceDescriptors.values()){
			if (!instance.isVisible())
				continue;
			DatabaseInstance di = new DatabaseInstance(instance.getDBID());
			di.setNavigatorHost(instance.getNavigatorHost());
			di.setDatasourceNames(instance.getDataSourceNames());
			di.setMapPath(instance.getMapPath());
			di.setDocrootPath(instance.getDocrootPath());
			di.setRasterServerUrl(instance.getRasterServer());
			di.setInited(true);
			di.setModulePath(instance.getModulePath());
			DeltaThreshold dt = instance.getDeltaThreshold();
			if (dt != null)
				di.setDeltaThreshold(dt.getOkMaxRecords() + "/" + dt.getWarningMaxRecords());
			DeltaThreshold dOutT = instance.getDeltaOutThreshold();
			if (dOutT != null)
				di.setDeltaOutThreshold(dOutT.getOkMaxRecords() + "/" + dOutT.getWarningMaxRecords());
			databaseInstances.add(di);
		}
		return databaseInstances;
	}

}
