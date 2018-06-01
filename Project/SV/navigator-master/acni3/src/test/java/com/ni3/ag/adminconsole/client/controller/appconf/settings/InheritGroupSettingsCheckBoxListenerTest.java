/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Setting;

public class InheritGroupSettingsCheckBoxListenerTest extends ACTestCase{

	public void testGetInheritanceGroupSettingExists(){
		Group group = new Group();
		List<GroupSetting> settings = new ArrayList<GroupSetting>();
		settings.add(new GroupSetting(group, Setting.APPLET_SECTION, "property", "name"));
		GroupSetting inheritanceSet = new GroupSetting(group, Setting.APPLET_SECTION,
		        Setting.INHERITS_GROUP_SETTINGS_PROPERTY, "true");
		settings.add(inheritanceSet);
		group.setGroupSettings(settings);
		InheritGroupSettingsCheckBoxListener listener = new InheritGroupSettingsCheckBoxListener(true, false, group);

		Setting s = listener.getInheritanceSetting(settings);

		assertEquals(inheritanceSet, s);
	}

	public void testGetInheritanceGroupSettingNotExists(){
		Group group = new Group();
		List<GroupSetting> settings = new ArrayList<GroupSetting>();
		settings.add(new GroupSetting(group, Setting.APPLET_SECTION, "property", "name"));
		group.setGroupSettings(settings);
		InheritGroupSettingsCheckBoxListener listener = new InheritGroupSettingsCheckBoxListener(true, false, group);

		Setting s = listener.getInheritanceSetting(new ArrayList<Setting>());

		assertNull(s);
	}

	public void testCreateInheritanceGroupSetting(){
		Group group = new Group();
		GroupSetting inheritanceSet = new GroupSetting(group, Setting.APPLET_SECTION,
		        Setting.INHERITS_GROUP_SETTINGS_PROPERTY, "true");
		InheritGroupSettingsCheckBoxListener listener = new InheritGroupSettingsCheckBoxListener(true, false, group);
		Setting s = listener.createInheritanceSetting("false");
		assertNotSame(inheritanceSet, s);
		inheritanceSet.setValue("false");
		assertEquals(inheritanceSet, s);
	}

	public void testAddFixedSettings(){
		Group group = new Group();
		InheritGroupSettingsCheckBoxListener listener = new InheritGroupSettingsCheckBoxListener(true, false, group);
		List<Setting> settings = new ArrayList<Setting>();
		Setting tabSwitchSetting = new GroupSetting(group, Setting.APPLET_SECTION, Setting.TAB_SWITCH_ACTION_PROPERTY, "2");
		settings.add(tabSwitchSetting);
		List<Setting> outSettings = new ArrayList<Setting>();
		listener.leaveFixedSettings(outSettings, settings);
		assertTrue(outSettings.contains(tabSwitchSetting));
	}
}
