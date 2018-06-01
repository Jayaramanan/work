/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class GroupSettingsValidationRuleTest extends TestCase{
	List<GroupSetting> settings;
	ACValidationRule rule;

	public void setUp(){
		settings = new ArrayList<GroupSetting>();
		rule = new GroupSettingsValidationRule();
	}

	public void testNotNull(){
		UserAdminModel model = new UserAdminModel();
		assertNotNull(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
	}

	public void testRule(){
		UserAdminModel model = new UserAdminModel();
		Group g = new Group();
		GroupSetting gs = new GroupSetting();
		List<GroupSetting> groupSettingList = new ArrayList<GroupSetting>();
		groupSettingList.add(gs);
		g.setGroupSettings(groupSettingList);
		model.setCurrentGroup(g);
		rule.performCheck(model);
		assertEquals(rule.getErrorEntries().size(), 2);
	}

	public void testRule2(){
		UserAdminModel model = new UserAdminModel();
		Group g = new Group();
		GroupSetting gs = new GroupSetting();
		gs.setSection(" ");
		gs.setProp(" ");
		gs.setValue(" ");
		List<GroupSetting> groupSettingList = new ArrayList<GroupSetting>();
		groupSettingList.add(gs);
		g.setGroupSettings(groupSettingList);
		model.setCurrentGroup(g);
		rule.performCheck(model);
		assertEquals(rule.getErrorEntries().size(), 2);
	}

	public void testRule3(){
		UserAdminModel model = new UserAdminModel();
		Group g = new Group();
		GroupSetting gs = new GroupSetting();
		gs.setSection("1");
		gs.setProp("2");
		gs.setValue("3");
		GroupSetting as = new GroupSetting();
		as.setSection("1");
		as.setProp("2");
		as.setValue("3");
		List<GroupSetting> groupSettingList = new ArrayList<GroupSetting>();
		groupSettingList.add(gs);
		groupSettingList.add(as);
		g.setGroupSettings(groupSettingList);
		model.setCurrentGroup(g);
		rule.performCheck(model);
		assertEquals(rule.getErrorEntries().size(), 1);
	}

}
