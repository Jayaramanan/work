/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface ThickClientJobService{
	public List<OfflineJob> getOfflineJobs();

	public List<User> getUsers();

	public void applyOfflineJobs(List<OfflineJob> jobsToUpdate, List<OfflineJob> jobsToDelete) throws ACException;

	public void processJobs() throws ACException;

	public void processJob(OfflineJob job, boolean schedulerCall) throws ACException;

	public List<Group> getGroupsWithOfflineUsers();

}
