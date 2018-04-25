/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.server.dao.DatabaseVersionDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseVersionService;
import com.ni3.ag.adminconsole.validation.ACException;

public class DatabaseVersionServiceImpl implements DatabaseVersionService{
	private Logger log = Logger.getLogger(DatabaseVersionServiceImpl.class);
	private ACRoutingDataSource routingDataSource;
	private String databaseVersion;
	private DatabaseVersionDAO databaseVersionDAO;
	private String dbType;

	public ACRoutingDataSource getRoutingDataSource(){
		return routingDataSource;
	}

	public void setRoutingDataSource(ACRoutingDataSource routingDataSource){
		this.routingDataSource = routingDataSource;
	}

	public String getDatabaseVersion(){
		return databaseVersion;
	}

	public void setDatabaseVersion(String databaseVersion){
		this.databaseVersion = databaseVersion;
	}

	public DatabaseVersionDAO getDatabaseVersionDAO(){
		return databaseVersionDAO;
	}

	public void setDatabaseVersionDAO(DatabaseVersionDAO databaseVersionDAO){
		this.databaseVersionDAO = databaseVersionDAO;
	}

	private void getDatabaseType(){
		Map<String, InstanceDescriptor> dataSources = routingDataSource.getDatasourceDescriptors();
		String key = routingDataSource.getCurrentDatabaseInstanceId();
		InstanceDescriptor dsd = dataSources.get(key);
		this.dbType = dsd.getDatabaseType();
	}

	@Override
	public void checkDatabaseVersion() throws ACException{
		if (dbType == null)
			getDatabaseType();
		String version = databaseVersionDAO.getVersion(dbType);
		log.info("Database: " + dbType + ", expected version " + databaseVersion + ", real version " + version);

		if (version == null){
			throw new ACException(TextID.MsgDatabaseWrongVersion, new String[] { databaseVersion,
			        version == null ? "--" : version });
		}

		int[] realVersion = parseVersion(version);
		int[] expectedVersion = parseVersion(databaseVersion);

		if (realVersion[0] != expectedVersion[0] || realVersion[1] != expectedVersion[1]){
			throw new ACException(TextID.MsgDatabaseWrongVersion, new String[] { databaseVersion,
			        version == null ? "--" : version });
		}
	}

	private int[] parseVersion(String version){
		StringTokenizer strt = new StringTokenizer(version, ".");
		String astr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		String bstr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		String cstr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		return new int[] { Integer.parseInt(astr), Integer.parseInt(bstr), Integer.parseInt(cstr) };
	}

	@Override
	public String getActualVersion(){
		if (dbType == null)
			getDatabaseType();
		return databaseVersionDAO.getVersion(dbType);
	}

	@Override
	public String getExpectedVersion(){
		return databaseVersion;
	}
}
