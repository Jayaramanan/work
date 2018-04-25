/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;

public interface SettingsService{

	List<Group> getGroups();

	List<ApplicationSetting> getApplicationSettings();

	void updateApplicationSettings(List<ApplicationSetting> applicationSettings,
	        List<ApplicationSetting> deletableApplicationSettings);

	void updateGroupSettings(Group currentObject);

	void updateUserSettings(User currentObject);

	List<Language> getLanguages();

	List<Schema> getSchemas();

	Group reloadGroup(Integer id);

	User reloadUser(Integer id);

	List<User> getAllUsers();

	Setting getApplicationSetting(String section, String prop);

	void updateApplicationSetting(String section, String prop, String value);

    void updateUserSettings(List<User> currentUsers);
}
