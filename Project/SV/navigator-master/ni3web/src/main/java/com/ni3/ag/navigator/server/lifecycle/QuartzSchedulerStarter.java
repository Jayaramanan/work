/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.lifecycle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.ni3.ag.navigator.server.jobs.BackgroundGraphLoaderJob;
import com.ni3.ag.navigator.server.jobs.CacheUpdaterJob;
import com.ni3.ag.navigator.server.jobs.DeltaUserRouterJob;
import com.ni3.ag.navigator.server.jobs.GeoCodingJob;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@SuppressWarnings("serial")
public class QuartzSchedulerStarter extends HttpServlet{
	private static final Logger log = Logger.getLogger(QuartzSchedulerStarter.class);
	private Properties properties;
	private Scheduler scheduler;

	@Override
	public void destroy(){
		log.info("Destroying quartz");
		try{
			scheduler.shutdown();
		} catch (final SchedulerException e){
			log.error("error stoping scheduler", e);
		}
		super.destroy();
	}

	@Override
	public void init() throws ServletException{
		log.debug("Initializing Quartz Scheduler");

		System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");

		super.init();
		properties = new Properties();
		try{
			properties.load(getClass().getResourceAsStream("/jobs.properties"));
			final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			scheduler = schedulerFactory.getScheduler();
			scheduleAndStart();
		} catch (final SchedulerException e){
			log.error("Error starting Quartz scheduler", e);
		} catch (final ParseException e){
			log.error("Error starting Quartz scheduler", e);
		} catch (final IOException e){
			log.error("Error loading properties", e);
		}
	}

	private void scheduleAndStart() throws SchedulerException, ParseException, IOException {
		log.info("Scheduler enabled, creating CronTrigger");
		scheduleDeltaUserRouterJob();
		scheduleCacheUpdater();
        scheduleGeoCoder();
		scheduleGraphBackgroundLoader();
		scheduler.start();
	}

	private void scheduleGraphBackgroundLoader() throws ParseException, SchedulerException{
		if(!shouldStartGraphBackgroundLoader())
			return;
		log.info("Scheduling graph background loader");
		final CronTrigger graphLoaderTrigger = new CronTrigger("graph.loader.trigger", "graphTriggers",
		                properties.getProperty("graph.loader.trigger"));
		final JobDetail graphLoader = new JobDetail("graph", "graphLoader", BackgroundGraphLoaderJob.class);
		JobDataMap dataMap = graphLoader.getJobDataMap();
		dataMap.put("graph.loader.loadCountPerRun", properties.getProperty("graph.loader.loadCountPerRun"));
		scheduler.scheduleJob(graphLoader, graphLoaderTrigger);
	}

	private boolean shouldStartGraphBackgroundLoader(){
		return shouldStart("graph.loader.start");
	}

	private void scheduleGeoCoder() throws SchedulerException, ParseException, IOException {
        if(!shouldStartGeoCoder())
            return;
		log.info("Scheduling geo coder job");
        Properties  geoProperties = new Properties();
        BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream("/geocode.properties"));
        geoProperties.load(bis);
        bis.close();
        final JobDetail geoCoder = new JobDetail("geo", "geoCoder", GeoCodingJob.class);
        JobDataMap dataMap = geoCoder.getJobDataMap();
        for(Object key : geoProperties.keySet()){
            dataMap.put(key, geoProperties.get(key));
        }
        final CronTrigger geoCodingTrigger = new CronTrigger("geoCodingTrigger", "geoTriggers",
                properties.getProperty("geo.coder"));
        scheduler.scheduleJob(geoCoder, geoCodingTrigger);
    }

    private void scheduleCacheUpdater() throws SchedulerException, ParseException{
		log.info("Scheduling cache update job");
		final JobDetail cacheUpdater = new JobDetail("updater", "cache", CacheUpdaterJob.class);
		final CronTrigger cacheUpdaterTrigger = new CronTrigger("cacheUpdaterTrigger", "cacheTriggers",
		        properties.getProperty("cache.updater"));
		scheduler.scheduleJob(cacheUpdater, cacheUpdaterTrigger);
	}

	private void scheduleDeltaUserRouterJob() throws ParseException, SchedulerException{
		if(!shouldStartRouter())
			return;
		log.info("Scheduling delta router job");
		final JobDetail deltaOutRouter = new JobDetail("deltaOutRouter", "delta", DeltaUserRouterJob.class);
		final CronTrigger databaseExtracterTrigger = new CronTrigger("deltaOutRouterTrigger", "deltaTriggers",
		        properties.getProperty("delta.outDeltaRouter"));
		scheduler.scheduleJob(deltaOutRouter, databaseExtracterTrigger);
	}
    
    private boolean shouldStartRouter(){
        return shouldStart("delta.startRouter");
    }

    private boolean shouldStartGeoCoder() {
        return shouldStart("geo.startGeoCoder");
    }

    private boolean shouldStart(String name){
   		String property = properties.getProperty(name);
   		if (property != null)
   			property = property.trim();
   		return property != null && ("1".equals(property) || "true".equalsIgnoreCase(property));
   	}
}
