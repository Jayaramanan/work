/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.mapextraction;

import java.util.Date;

import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.server.dao.MapJobDAO;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;

public class MapJobStatusUpdater{

	private MapJobDAO mapJobDAO;

	public void setMapJobDAO(MapJobDAO mapJobDAO){
		this.mapJobDAO = mapJobDAO;
	}

	public void setJobStatus(MapJob job, MapJobStatus status){
		job.setStatus(status.getValue());
		if (MapJobStatus.ProcessingMaps.equals(status)){
			job.setTimeStart(new Date(System.currentTimeMillis()));
		} else{
			job.setTimeEnd(new Date(System.currentTimeMillis()));
		}
		mapJobDAO.saveAndFlush(job);
	}
}
