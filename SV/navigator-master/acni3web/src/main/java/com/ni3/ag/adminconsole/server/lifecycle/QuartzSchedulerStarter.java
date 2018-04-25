/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.lifecycle;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.ni3.ag.adminconsole.server.jobs.DeleteUserEditionsJob;
import com.ni3.ag.adminconsole.server.jobs.MapExtractionJob;
import com.ni3.ag.adminconsole.server.jobs.ThickClientDatabaseExtractJob;
import com.ni3.ag.adminconsole.shared.service.def.MapJobService;
import com.ni3.ag.adminconsole.shared.service.def.NavigatorLicenseService;
import com.ni3.ag.adminconsole.shared.service.def.SyncQuartzJobStarterService;
import com.ni3.ag.adminconsole.shared.service.def.ThickClientJobService;

public class QuartzSchedulerStarter{
	private static final Logger log = Logger.getLogger(QuartzSchedulerStarter.class);
	private static final String THICK_CLIENT_DATABASE_EXTRACTER = "thickClientDatabaseExtracter";
	private static final String OFFLINE_JOBS = "offlineJobs";
	private static final String THICK_CLIENT_DATABASE_EXTRACTER_TRIGGER = "thickClientDatabaseExtracterTrigger";
	private static final String OFFLINE_TRIGGERS = "offlineTriggers";
	private Properties properties;

	private DataSource dataSource;
	private MapJobService mapJobService;
	private ThickClientJobService thickClientJobService;
	private NavigatorLicenseService navigatorLicenseService;
	private Scheduler scheduler;

	public void setNavigatorLicenseService(NavigatorLicenseService navigatorLicenseService){
		this.navigatorLicenseService = navigatorLicenseService;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setProperties(Properties properties){
		this.properties = properties;
	}

	public void setMapJobService(MapJobService mapJobService){
		this.mapJobService = mapJobService;
	}

	public void setThickClientJobService(ThickClientJobService thickClientJobService){
		this.thickClientJobService = thickClientJobService;
	}

	public void init() throws SchedulerException, ParseException{
		log.info("Starting Quartz scheduler");
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		scheduler = schedulerFactory.getScheduler();
		scheduleAndStart(scheduler);
	}

	private void scheduleAndStart(Scheduler scheduler) throws SchedulerException, ParseException{
		scheduleDataExporter(scheduler);
		// scheduleMapExtraction(scheduler);
		scheduleUserEditionRemoving(scheduler);
		scheduler.start();
	}

	private void scheduleUserEditionRemoving(Scheduler scheduler) throws SchedulerException, ParseException{
		JobDetail databaseExtractJob = new JobDetail("UserEditionDeleter", "LicenseJobs", DeleteUserEditionsJob.class);

		CronTrigger databaseExtracterTrigger = new CronTrigger("UserEditionDeleteTrigger", "LicenseTriggers", properties
		        .getProperty("licensing.deleteUserEditions"));
		JobDataMap extracterMap = new JobDataMap(getJobDataMap(SyncQuartzJobStarterService.DELETE_USER_EDITIONS_JOB_ID));
		databaseExtractJob.setJobDataMap(extracterMap);

		scheduler.scheduleJob(databaseExtractJob, databaseExtracterTrigger);
	}

	private void scheduleDataExporter(Scheduler scheduler) throws SchedulerException, ParseException{
		JobDetail databaseExtractJob = new JobDetail(THICK_CLIENT_DATABASE_EXTRACTER, OFFLINE_JOBS,
		        ThickClientDatabaseExtractJob.class);

		CronTrigger databaseExtracterTrigger = new CronTrigger(THICK_CLIENT_DATABASE_EXTRACTER_TRIGGER, OFFLINE_TRIGGERS,
		        properties.getProperty("thickClient.extractJob"));
		JobDataMap extracterMap = new JobDataMap(getJobDataMap(SyncQuartzJobStarterService.THICK_CLIENT_EXTRACT_JOB_ID));
		databaseExtractJob.setJobDataMap(extracterMap);

		scheduler.scheduleJob(databaseExtractJob, databaseExtracterTrigger);
	}

	private void scheduleMapExtraction(Scheduler scheduler) throws SchedulerException, ParseException{
		JobDetail mapExtractionJob = new JobDetail("MapExtractor", "MapJobs", MapExtractionJob.class);

		CronTrigger mapExtractionTrigger = new CronTrigger("MapExtractionTrigger", "MapExtractionTriggers", properties
		        .getProperty("thickClient.extractMap"));
		JobDataMap map = new JobDataMap(getJobDataMap(SyncQuartzJobStarterService.MAP_EXTRACTION_JOB_ID));
		mapExtractionJob.setJobDataMap(map);

		scheduler.scheduleJob(mapExtractionJob, mapExtractionTrigger);
	}

	public Map<String, Object> getJobDataMap(int jobId){
		Map<String, Object> map = new HashMap<String, Object>();
		switch (jobId){
			case SyncQuartzJobStarterService.THICK_CLIENT_EXTRACT_JOB_ID:
				map.put(ThickClientDatabaseExtractJob.DATA_SOURCE, dataSource);
				map.put(ThickClientDatabaseExtractJob.THICK_CLIENT_JOB_SERVICE, thickClientJobService);
				break;
			case SyncQuartzJobStarterService.MAP_EXTRACTION_JOB_ID:
				map.put(MapExtractionJob.DATA_SOURCE, dataSource);
				map.put(MapExtractionJob.MAP_JOB_SERVICE, mapJobService);
				break;
			case SyncQuartzJobStarterService.DELETE_USER_EDITIONS_JOB_ID:
				map.put(DeleteUserEditionsJob.DATA_SOURCE, dataSource);
				map.put(DeleteUserEditionsJob.SERVICE, navigatorLicenseService);
				break;
		}
		return map;
	}

	public void destroy(){
		log.info("Destroying quartz");
		try{
			scheduler.shutdown();
		} catch (SchedulerException e){
			log.error("error stoping scheduler", e);
		}
	}
}
