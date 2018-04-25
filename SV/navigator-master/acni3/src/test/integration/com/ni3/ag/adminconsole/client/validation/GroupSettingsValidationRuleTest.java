/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.GroupSetting;

public class GroupSettingsValidationRuleTest extends ACTestCase{
	List<GroupSetting> settings;
	
	public void setUp(){
		settings = new ArrayList<GroupSetting>();
	}

	public void testRule(){
		settings.clear();
		settings.add(new GroupSetting());
		GroupSettingsValidationRule rule = new GroupSettingsValidationRule(settings);
		assertEquals(rule.performCheck().getErrors().size(), 3);		
	}
	
	public void testRule2(){
		settings.clear();
		GroupSetting as = new GroupSetting();
		as.setSection(" ");
		as.setProp(" ");
		as.setValue(" ");
		settings.add(as);
		GroupSettingsValidationRule rule = new GroupSettingsValidationRule(settings);
		assertEquals(rule.performCheck().getErrors().size(), 3);		
	}
	
	public void testRule3(){
		settings.clear();
		GroupSetting as = new GroupSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		settings.add(as);
		as = new GroupSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		settings.add(as);
		GroupSettingsValidationRule rule = new GroupSettingsValidationRule(settings);
		assertEquals(rule.performCheck().getErrors().size(), 1);		
	}

}
