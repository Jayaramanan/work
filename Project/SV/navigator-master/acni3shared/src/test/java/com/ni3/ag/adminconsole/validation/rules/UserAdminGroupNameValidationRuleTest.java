/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserAdminGroupNameValidationRuleTest extends TestCase{

	ACValidationRule rule;
	UserAdminModel model;

	public void setUp(){
		rule = new UserAdminGroupNameValidationRule();
		model = new UserAdminModel();
	}

	public void testPerformCheckFail(){
		List<Group> groups = generateGroups();
		model.setGroups(groups);
		Group current = new Group();
		current.setName("123");
		model.setCurrentGroup(current);

		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckSuccess(){
		List<Group> groups = generateGroups();
		model.setGroups(groups);
		Group current = new Group();
		current.setName("654");
		model.setCurrentGroup(current);

		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
		assertEquals(0, rule.getErrorEntries().size());
	}

	private List<Group> generateGroups(){
		List<Group> groups = new ArrayList<Group>();
		Group g = new Group();
		g.setName("012");
		groups.add(g);
		g = new Group();
		g.setName("123");
		groups.add(g);
		g = new Group();
		g.setName("234");
		groups.add(g);
		return groups;
	}
}
