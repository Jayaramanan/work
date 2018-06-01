/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.UserSettingsValidationRule;

public class UserSettingsValidationRuleTest extends TestCase{

	public void testPerformCheckOneDuplicate(){
		ACValidationRule rule = new UserSettingsValidationRule();
		SettingsModel model = new SettingsModel();
		User u = new User();
		model.setCurrentObject(u);
		UserSetting s1 = new UserSetting();
		s1.setProp("prop1");
		s1.setSection("zz");
		s1.setValue("dd");
		UserSetting s2 = new UserSetting();
		s2.setProp("prop2");
		s2.setSection("zz");
		s2.setValue("dd");
		UserSetting s3 = new UserSetting();
		s3.setProp("prop1");
		s3.setSection("zz");
		s3.setValue("dd");
		u.setSettings(new ArrayList<UserSetting>());
		u.getSettings().add(s1);
		u.getSettings().add(s2);
		u.getSettings().add(s3);

		rule.performCheck(model);
		assertEquals(1, rule.getErrorEntries().size());
		assertEquals(TextID.MsgDuplicatePropertyName, rule.getErrorEntries().get(0).getId());
	}

	public void testPerformCheckTwoDuplicates(){
		ACValidationRule rule = new UserSettingsValidationRule();
		SettingsModel model = new SettingsModel();
		User u = new User();
		model.setCurrentObject(u);
		UserSetting s1 = new UserSetting();
		s1.setProp("prop1");
		s1.setSection("zz");
		s1.setValue("dd");
		UserSetting s2 = new UserSetting();
		s2.setProp("prop2");
		s2.setSection("zz");
		s2.setValue("dd");
		UserSetting s3 = new UserSetting();
		s3.setProp("prop1");
		s3.setSection("zz");
		s3.setValue("dd");
		UserSetting s4 = new UserSetting();
		s4.setProp("prop1");
		s4.setSection("zz");
		s4.setValue("dd");
		u.setSettings(new ArrayList<UserSetting>());
		u.getSettings().add(s1);
		u.getSettings().add(s2);
		u.getSettings().add(s3);
		u.getSettings().add(s4);

		rule.performCheck(model);
		assertEquals(2, rule.getErrorEntries().size());
		assertEquals(TextID.MsgDuplicatePropertyName, rule.getErrorEntries().get(0).getId());
	}

	public void testPerformCheckSuccess(){
		ACValidationRule rule = new UserSettingsValidationRule();
		SettingsModel model = new SettingsModel();
		User u = new User();
		model.setCurrentObject(u);
		u.setSettings(new ArrayList<UserSetting>());

		rule.performCheck(model);
		assertEquals(0, rule.getErrorEntries().size());
	}
}
