/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.NavigatorModule;

public interface NavigatorLicenseService{
	public List<Group> getGroups();

	public List<LicenseData> getNavigatorLicenseData();

	public void updateGroups(List<Group> groups);

	public Group reloadGroup(Integer id);

	public void updateUsers(List<User> users);

	public Map<NavigatorModule, Integer> checkExpiringLicenseModules();

	public Map<NavigatorModule, Integer> getAvailableEditionCount();

}
