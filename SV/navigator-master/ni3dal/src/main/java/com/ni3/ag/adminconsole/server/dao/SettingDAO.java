/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.GroupSetting;

public interface SettingDAO{
	String getSetting(Integer userId, String section, String prop);

	GroupSetting getGroupSetting(Integer groupId, String section, String prop);

	ApplicationSetting getApplicationSetting(String section, String prop);
}
