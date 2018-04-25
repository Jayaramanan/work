/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ApplicationSettingsValidationRule;

public class ApplicationSettingsValidationRuleTest extends TestCase{

	public void testNotNull(){
		SettingsModel model = new SettingsModel();
		model.setApplicationSettings(new ArrayList<ApplicationSetting>());
		ACValidationRule rule = new ApplicationSettingsValidationRule();
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}

	public void testRule(){
		SettingsModel model = new SettingsModel();
		model.setApplicationSettings(new ArrayList<ApplicationSetting>());
		model.getApplicationSettings().add(new ApplicationSetting());
		ACValidationRule rule = new ApplicationSettingsValidationRule();
		rule.performCheck(model);
		assertEquals(rule.getErrorEntries().size(), 2);
	}

	public void testRule2(){
		SettingsModel model = new SettingsModel();
		model.setApplicationSettings(new ArrayList<ApplicationSetting>());
		ApplicationSetting as = new ApplicationSetting();
		as.setSection(" ");
		as.setProp(" ");
		as.setValue(" ");
		model.getApplicationSettings().add(as);
		ACValidationRule rule = new ApplicationSettingsValidationRule();
		rule.performCheck(model);
		assertEquals(rule.getErrorEntries().size(), 2);
	}

	public void testRule3(){
		SettingsModel model = new SettingsModel();
		model.setApplicationSettings(new ArrayList<ApplicationSetting>());
		ApplicationSetting as = new ApplicationSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		model.getApplicationSettings().add(as);
		as = new ApplicationSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		model.getApplicationSettings().add(as);
		ACValidationRule rule = new ApplicationSettingsValidationRule();
		rule.performCheck(model);
		assertEquals(rule.getErrorEntries().size(), 1);
	}
}
