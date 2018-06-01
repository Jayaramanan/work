/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteSettingsValidationRuleTest extends TestCase{
	private ACValidationRule rule;
	private SettingsModel model;

	public void setUp(){
		rule = new DeleteSettingsValidationRule();
		model = new SettingsModel();
	}

	public void testDeleteSystemSettings(){
		List<Setting> settings = new ArrayList<Setting>();
		User user = new User();
		settings.add(new UserSetting(user, ApplicationSetting.APPLET_SECTION, UserSetting.INHERITS_GROUP_SETTINGS_PROPERTY,
		        "true"));
		model.setDeletableSettings(settings);

		model.setCurrentObject(user);

		assertFalse(rule.performCheck(model));
		assertEquals(rule.getErrorEntries().size(), 1);
	}

	public void testDeleteSystemApplicationSettings(){
		List<Setting> settings = new ArrayList<Setting>();
		settings.add(new ApplicationSetting(ApplicationSetting.APPLET_SECTION,
		        ApplicationSetting.SETTINGS_MENU_TREE_NODES[0], "true"));
		model.setDeletableSettings(settings);
		model.setCurrentObject(new DatabaseInstance("test"));

		assertFalse(rule.performCheck(model));
		assertEquals(rule.getErrorEntries().size(), 1);
	}
}
