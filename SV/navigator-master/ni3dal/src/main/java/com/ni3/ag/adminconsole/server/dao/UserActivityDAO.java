/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.Date;
import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;

public interface UserActivityDAO{

	List<User> getUsersWithActivities(Date from, Date to, User user);

	List<UserActivity> getActionsWithUsers(Date from, Date to, UserActivityType filter);

	void saveOrUpdateAll(List<UserActivity> activitiesToUpdate);
}
