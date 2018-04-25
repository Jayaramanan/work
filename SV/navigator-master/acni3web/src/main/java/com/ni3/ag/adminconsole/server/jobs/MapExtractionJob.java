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
import com.ni3.ag.adminconsole.shared.service.def.MapJobService;

public class MapExtractionJob implements Job{
	private static final Logger log = Logger.getLogger(MapExtractionJob.class);
	public static final String DATA_SOURCE = "dataSource";
	public static final String MAP_JOB_SERVICE = "mapJobService";

	private ACRoutingDataSource dataSource;
	private MapJobService mapJobService;

	private void init(JobExecutionContext jc){
		JobDataMap jdm = jc.getMergedJobDataMap();
		dataSource = (ACRoutingDataSource) jdm.get(DATA_SOURCE);
		mapJobService = (MapJobService) jdm.get(MAP_JOB_SERVICE);
		log.debug("dataSource extracted: " + dataSource);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		init(context);
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
			mapJobService.processJobs();
		} catch (Throwable e){
			log.error("Error extracting map, error = " + e.getMessage());
		}
		log.debug("Processed dataSource: " + ds + " !");
	}
}
