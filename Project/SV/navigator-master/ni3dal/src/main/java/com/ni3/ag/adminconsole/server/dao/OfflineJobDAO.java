/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.domain.User;

public interface OfflineJobDAO{
	List<OfflineJob> getScheduledExportJobs();

	void saveOrUpdate(OfflineJob job);

	List<OfflineJob> getAllJobs();

	List<User> getThickClientUsers();

	OfflineJob getOfflineJob(Integer id);

	void delete(OfflineJob job);

	void saveAndFlush(OfflineJob job);

	OfflineJob merge(OfflineJob job);

	Integer getJobCountByUser(User u);

	List<Group> getGroupsWithOfflineUsers();
}
