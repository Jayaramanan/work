/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.dto.ErrorContainer;

public class UserSettingsValidationRuleTest extends ACTestCase{
	UserSettingsValidationRule rule;
	List<UserSetting> settings;

	@Override
	protected void setUp() throws Exception{
		settings = new ArrayList<UserSetting>();
		rule = new UserSettingsValidationRule(settings);
	}

	public void testPerformCheckOneDuplicate(){
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
		settings.add(s1);
		settings.add(s2);
		settings.add(s3);

		ErrorContainer result = rule.performCheck();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgDuplicatePropertyName", result.getErrors().get(0));
	}

	public void testPerformCheckTwoDuplicates(){
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
		s4.setProp("prop2");
		s4.setSection("zz");
		s4.setValue("dd");
		settings.add(s1);
		settings.add(s2);
		settings.add(s3);
		settings.add(s4);

		ErrorContainer result = rule.performCheck();
		assertEquals(2, result.getErrors().size());
		assertEquals("MsgDuplicatePropertyName", result.getErrors().get(0));
	}

	public void testPerformCheckSuccess(){
		UserSetting s1 = new UserSetting();
		s1.setProp("prop1");
		s1.setSection("zz");
		s1.setValue("dd");
		UserSetting s2 = new UserSetting();
		s2.setProp("prop2");
		s2.setSection("zz");
		s2.setValue("dd");
		UserSetting s3 = new UserSetting();
		s3.setProp("prop3");
		s3.setSection("zz");
		s3.setValue("dd");
		settings.add(s1);
		settings.add(s2);
		settings.add(s3);

		ErrorContainer result = rule.performCheck();
		assertEquals(result.getErrors().size(), 0);
	}
}
