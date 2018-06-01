/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;

public class ApplicationSettingsValidationRuleTest extends ACTestCase{
	List<ApplicationSetting> settings;
	
	public void setUp(){
		settings = new ArrayList<ApplicationSetting>();
	}

	public void testRule(){
		settings.clear();
		settings.add(new ApplicationSetting());
		ApplicationSettingsValidationRule rule = new ApplicationSettingsValidationRule(settings);
		assertEquals(rule.performCheck().getErrors().size(), 3);		
	}
	
	public void testRule2(){
		settings.clear();
		ApplicationSetting as = new ApplicationSetting();
		as.setSection(" ");
		as.setProp(" ");
		as.setValue(" ");
		settings.add(as);
		ApplicationSettingsValidationRule rule = new ApplicationSettingsValidationRule(settings);
		assertEquals(rule.performCheck().getErrors().size(), 3);
	}
	
	public void testRule3(){
		settings.clear();
		ApplicationSetting as = new ApplicationSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		settings.add(as);
		as = new ApplicationSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		settings.add(as);
		ApplicationSettingsValidationRule rule = new ApplicationSettingsValidationRule(settings);
		assertEquals(rule.performCheck().getErrors().size(), 1);
	}
}
