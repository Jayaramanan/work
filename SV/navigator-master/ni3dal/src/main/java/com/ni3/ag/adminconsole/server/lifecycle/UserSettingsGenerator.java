/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;

public class UserSettingsGenerator{
	public static List<UserSetting> generateSettings(User u){
		List<UserSetting> settings = new ArrayList<UserSetting>();
		settings.add(new UserSetting(u, Setting.APPLET_SECTION, Setting.INHERITS_GROUP_SETTINGS_PROPERTY, "true"));
		return settings;
	}
}
