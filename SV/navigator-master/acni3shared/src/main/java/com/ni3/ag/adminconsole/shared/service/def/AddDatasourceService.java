/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import com.ni3.ag.adminconsole.validation.ACException;

public interface AddDatasourceService{
	public void addDataSource(String navHost, String dbid, String datasourceName, String mappath, String docroot,
	        String rasterServer, String deltaThreshold, String deltaOutThreshold, String modulePath) throws ACException;

	public void deleteDataSource(String dbid) throws ACException;

	public boolean databaseInstanceExists(String dbid);
}
