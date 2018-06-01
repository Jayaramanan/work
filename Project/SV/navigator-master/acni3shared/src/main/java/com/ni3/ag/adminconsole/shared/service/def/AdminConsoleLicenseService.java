/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.license.ACModuleDescription;

public interface AdminConsoleLicenseService{

	public List<User> getAdministrators();

	public void updateUsers(List<User> users);

	public List<ACModuleDescription> getModuleDescriptions();

	public void checkLicenseModules();

}
