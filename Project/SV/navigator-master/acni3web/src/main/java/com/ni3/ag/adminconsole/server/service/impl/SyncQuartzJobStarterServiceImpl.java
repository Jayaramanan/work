/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

import com.ni3.ag.adminconsole.server.jobs.MapExtractionJob;
import com.ni3.ag.adminconsole.server.jobs.ThickClientDatabaseExtractJob;
import com.ni3.ag.adminconsole.server.lifecycle.QuartzSchedulerStarter;
import com.ni3.ag.adminconsole.shared.service.def.SyncQuartzJobStarterService;

public class SyncQuartzJobStarterServiceImpl implements SyncQuartzJobStarterService{

	private final static Logger log = Logger.getLogger(SyncQuartzJobStarterServiceImpl.class);

	private int jobId;

	private QuartzSchedulerStarter jobStarter;

	public void setJobStarter(QuartzSchedulerStarter jobStarter){
		this.jobStarter = jobStarter;
	}

	@Override
	public void startJob(int jobId){
		this.jobId = jobId;
		Job job = null;
		switch (jobId){
			case THICK_CLIENT_EXTRACT_JOB_ID:
				job = new ThickClientDatabaseExtractJob();
				break;
			case MAP_EXTRACTION_JOB_ID:
				job = new MapExtractionJob();
				break;
		}
		if (job != null)
			launchJob(job);

	}

	private void launchJob(Job job){
		JobDetail jd = new JobDetail();
		Map<String, Object> map = jobStarter.getJobDataMap(jobId);
		JobDataMap jdm = new JobDataMap(map);
		jd.setJobDataMap(jdm);

		Trigger trig = new SimpleTrigger();
		TriggerFiredBundle firedBundle = new TriggerFiredBundle(jd, trig, null, false, null, null, null, null);
		JobExecutionContext context = new JobExecutionContext(null, firedBundle, job);
		try{
			job.execute(context);
		} catch (JobExecutionException e){
			log.error(e.getMessage(), e);
		}
	}
}
