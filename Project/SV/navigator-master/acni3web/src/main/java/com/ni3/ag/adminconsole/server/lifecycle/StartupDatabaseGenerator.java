/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.lifecycle;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;

public class StartupDatabaseGenerator{
	private static final Logger log = Logger.getLogger(StartupDatabaseGenerator.class);
	private DataSource dataSource;
	private ExternalDataImporter dataImporter;

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setDataImporter(ExternalDataImporter dataImporter){
		this.dataImporter = dataImporter;
	}

	public ExternalDataImporter getDataImporter(){
		return dataImporter;
	}

	public void init(){
		ACRoutingDataSource acDataSource = (ACRoutingDataSource) dataSource;
		String current = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		for (String s : acDataSource.getDatasourceDescriptors().keySet()){
			ThreadLocalStorage.getInstance().setCurrentDatabaseInstanceId(s);
			InstanceDescriptor ds = acDataSource.getDatasourceDescriptors().get(s);
			boolean generate = acDataSource.getGenerate(ds);
			if (!generate)
				continue;
			try{
				dataImporter.importExternalData(ds.getDatabaseType());
			} catch (Exception e){
				log.error("Error generating database for dbid: " + ds.getDBID(), e);
			}
		}
		if (current != null)
			ThreadLocalStorage.getInstance().setCurrentDatabaseInstanceId(current);
	}
}
