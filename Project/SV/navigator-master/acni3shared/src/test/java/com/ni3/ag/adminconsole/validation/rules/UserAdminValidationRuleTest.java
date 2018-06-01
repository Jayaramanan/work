/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.UserAdminValidationRule;

public class UserAdminValidationRuleTest extends TestCase{

	public void testPerformCheckSuccess(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.seteMail("e");
		u.setSID("s");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertTrue(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckEmailEmpty(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.setSID("s");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckSIDEmpty(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.setSID("");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckFirstNameEmpty(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckLastNameEmpty(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setPassword("p");
		u.setUserName("us");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckUserNameEmpty(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckPasswordEmpty(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setUserName("us");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckDuplicateUserName(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		g.getUsers().add(u);
		u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckUniqueUserName(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.seteMail("e1");
		u.setSID("s");
		g.getUsers().add(u);
		u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us1");
		u.seteMail("e2");
		u.setSID("s1");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertTrue(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckDuplicateUserEmails(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.seteMail("email");
		g.getUsers().add(u);
		u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us1");
		u.seteMail("email");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckUniqueUserEmails(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.seteMail("email");
		u.setSID("s");
		g.getUsers().add(u);
		u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us1");
		u.seteMail("email1");
		u.setSID("s1");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertTrue(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(0, rule.getErrorEntries().size());
	}
	
	public void testPerformCheckDuplicateSids(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.seteMail("email1");
		u.setSID("sid");
		g.getUsers().add(u);
		u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us1");
		u.seteMail("email2");
		u.setSID("sid");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertFalse(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckUniqueSids(){
		ACValidationRule rule = new UserAdminValidationRule();
		UserAdminModel model = new UserAdminModel();
		model.setGroups(new ArrayList<Group>());
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us");
		u.seteMail("email");
		u.setSID("sid1");
		g.getUsers().add(u);
		u = new User();
		u.setFirstName("f");
		u.setLastName("l");
		u.setPassword("p");
		u.setUserName("us1");
		u.seteMail("email1");
		u.setSID("sid2");
		g.getUsers().add(u);
		model.getGroups().add(g);
		model.setCurrentGroup(g);
		assertTrue(rule.performCheck(model));
		assertNotNull(rule.getErrorEntries());
		assertEquals(0, rule.getErrorEntries().size());
	}
}
