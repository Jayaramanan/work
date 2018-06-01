/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.Map;

import javax.sql.DataSource;

import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.lifecycle.StartupDatabaseGenerator;
import com.ni3.ag.adminconsole.shared.service.def.AddDatasourceService;
import com.ni3.ag.adminconsole.validation.ACException;

public class AddDatasourceServiceImpl implements AddDatasourceService{

	private DataSource dataSource;
	private StartupDatabaseGenerator dbGenerator;

	public void setDbGenerator(StartupDatabaseGenerator dbGenerator){
		this.dbGenerator = dbGenerator;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public void addDataSource(String navHost, String dbid, String datasourceName, String mappath, String docroot,
	        String rasterServer, String deltaThreshold, String deltaOutThreshold, String modulePath) throws ACException{
		((ACRoutingDataSource) dataSource).addTempDataSource(datasourceName, dbid, navHost, mappath, docroot, rasterServer,
		        deltaThreshold, deltaOutThreshold, modulePath);
		dbGenerator.init();
	}

	@Override
	public void deleteDataSource(String dbid) throws ACException{
		((ACRoutingDataSource) dataSource).deleteDataSourceTemporary(dbid);
	}

	@Override
	public boolean databaseInstanceExists(String dbid){
		Map<String, InstanceDescriptor> descriptorMap = ((ACRoutingDataSource) dataSource).getDatasourceDescriptors();
		InstanceDescriptor id = descriptorMap.get(dbid);
		return id != null;
	}

}
