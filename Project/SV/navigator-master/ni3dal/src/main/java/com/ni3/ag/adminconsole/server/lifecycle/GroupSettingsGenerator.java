/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Setting;

public class GroupSettingsGenerator{

	public static List<GroupSetting> generateSettings(Group group){
		List<GroupSetting> settings = new ArrayList<GroupSetting>();
		settings.add(new GroupSetting(group, Setting.APPLET_SECTION, Setting.INHERITS_GROUP_SETTINGS_PROPERTY, "true"));
		return settings;
	}

}
