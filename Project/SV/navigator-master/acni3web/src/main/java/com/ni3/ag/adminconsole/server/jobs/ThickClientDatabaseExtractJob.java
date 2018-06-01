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
import com.ni3.ag.adminconsole.shared.service.def.ThickClientJobService;

public class ThickClientDatabaseExtractJob implements Job{
	private static final Logger log = Logger.getLogger(ThickClientDatabaseExtractJob.class);

	public static final String DATA_SOURCE = "dataSource";
	public static final String THICK_CLIENT_JOB_SERVICE = "thickClientJobService";

	private ACRoutingDataSource dataSource;
	private ThickClientJobService thickClientJobService;

	private void init(JobExecutionContext jc){
		JobDataMap jdm = jc.getMergedJobDataMap();
		dataSource = (ACRoutingDataSource) jdm.get(DATA_SOURCE);
		thickClientJobService = (ThickClientJobService) jdm.get(THICK_CLIENT_JOB_SERVICE);

		log.debug("dataSource extracted: " + dataSource);
	}

	@Override
	public void execute(JobExecutionContext jc) throws JobExecutionException{
		try{
			log.debug("STARTED");
			init(jc);
			List<String> dataSources = dataSource.getDatabaseInstanceNames();
			for (int i = 0; i < dataSources.size(); i++){
				processDatasource(dataSources.get(i));
			}
			log.debug("FINISHED");
		} catch (Throwable e){
			log.error("Job caused uncatched exception", e);
		}
	}

	private void processDatasource(String ds){
		ThreadLocalStorage.getInstance().setCurrentDatabaseInstanceId(ds);
		try{
			thickClientJobService.processJobs();
		} catch (Throwable e){
			log.error("Error extracting data, error = ", e);
		}
		log.debug("Processed dataSource: " + ds + " !");
	}

}
