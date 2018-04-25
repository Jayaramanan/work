/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;

public interface ApplicationSettingsDAO{

	List<ApplicationSetting> getSettings();

	void deleteSettings(List<ApplicationSetting> deletableApplicationSettings);

	void saveOrUpdate(List<ApplicationSetting> applicationSettings);

	void saveOrUpdateAll(List<ApplicationSetting> settings);

	ApplicationSetting getApplicationSetting(String section, String prop);

}
