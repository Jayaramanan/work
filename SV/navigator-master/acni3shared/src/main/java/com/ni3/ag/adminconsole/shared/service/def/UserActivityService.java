/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.validation.ACException;

public interface UserActivityService{
	public List<User> getUsersWithActivities(Date from, Date to, User user);

	public List<User> getUsers();

	public String getCurrentServerTime(String pattern);

	public Map<UserActivityType, List<UserActivity>> getActionsWithUsers(Date from, Date to, UserActivityType filter);

	public Object[] getDataWithSummary(Date from, Date to, Object mode, Object filter);

	byte[] getPDFReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException;

	byte[] getXLSReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException;

	byte[] getHTMLReport(Date from, Date to, Object mode, Object filter, Language language) throws ACException;
}
