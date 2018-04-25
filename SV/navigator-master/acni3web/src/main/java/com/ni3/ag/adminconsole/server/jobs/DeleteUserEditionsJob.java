/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jobs;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.shared.service.def.NavigatorLicenseService;

public class DeleteUserEditionsJob implements Job{

	private static final Logger log = Logger.getLogger(DeleteUserEditionsJob.class);
	public static final String DATA_SOURCE = "dataSource";
	public static final String SERVICE = "service";

	private NavigatorLicenseService service;
	private ACRoutingDataSource dataSource;

	private void init(JobExecutionContext jc){
		JobDataMap jdm = jc.getMergedJobDataMap();
		dataSource = (ACRoutingDataSource) jdm.get(DATA_SOURCE);
		service = (NavigatorLicenseService) jdm.get(SERVICE);
		log.debug("dataSource extracted: " + dataSource);
	}

	@Override
	public void execute(JobExecutionContext jc) throws JobExecutionException{
		init(jc);
		log.debug("STARTED");
		List<String> dataSources = dataSource.getDatabaseInstanceNames();
		for (int i = 0; i < dataSources.size(); i++){
			processDatasource(dataSources.get(i));
		}
		log.debug("FINISHED");

	}

	private void processDatasource(String ds){
		ThreadLocalStorage.getInstance().setCurrentDatabaseInstanceId(ds);
		try{

			service.checkExpiringLicenseModules();
		} catch (Throwable e){
			log.error("Error checking expiring modules, error = " + e.getMessage());
		}
		log.debug("Processed dataSource: " + ds + " !");
	}

}
