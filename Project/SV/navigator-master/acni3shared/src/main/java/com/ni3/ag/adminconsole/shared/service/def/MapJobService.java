/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Map;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.validation.ACException;

public interface MapJobService{

	public List<MapJob> getMapJobs();

	public List<User> getUsers();

	public void applyMapJobs(List<MapJob> jobsToUpdate, List<MapJob> jobsToDelete) throws ACException;

	void processJob(MapJob job) throws ACException;

	public Map getMap(Integer mapId);

	public String getRasterServerUrl();

	public String getSetting(Integer userId, String section, String prop);

	public void processJobs() throws ACException;

	ErrorContainer validateDeleteJob(MapJob job);

}