/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.User;

public class UserAdminValidationRuleTest extends ACTestCase{
	UserAdminValidationRule rule;
	User user;
	List<User> users;

	@Override
	protected void setUp() throws Exception{
		users = new ArrayList<User>();
		user = new User();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setUserName("username");
		user.setPassword("password");
		users.add(user);
		rule = new UserAdminValidationRule(users, users);
	}

	public void testPerformCheckSuccess(){
		assertNull(rule.performCheck());
	}

	public void testPerformCheckFirstNameEmpty(){
		user.setFirstName(null);
		assertNotNull(rule.performCheck());
		user.setFirstName("");
		assertNotNull(rule.performCheck());
	}

	public void testPerformCheckLastNameEmpty(){
		user.setLastName(null);
		assertNotNull(rule.performCheck());
		user.setLastName("");
		assertNotNull(rule.performCheck());
	}

	public void testPerformCheckUserNameEmpty(){
		user.setUserName(null);
		assertNotNull(rule.performCheck());
		user.setUserName("");
		assertNotNull(rule.performCheck());
	}

	public void testPerformCheckPasswordEmpty(){
		user.setPassword(null);
		assertNotNull(rule.performCheck());
		user.setPassword("");
		assertNotNull(rule.performCheck());
	}

	public void testPerformCheckDuplicateUserName(){
		User user1 = new User();
		user1.setFirstName("firstname1");
		user1.setLastName("lastname1");
		user1.setUserName("username");
		user1.setPassword("password1");
		List<User> allUsers = new ArrayList<User>();
		allUsers.add(user);
		allUsers.add(user1);
		rule = new UserAdminValidationRule(users, allUsers);
		assertNotNull(rule.performCheck());
	}

	public void testPerformCheckUniqueUserName(){
		User user1 = new User();
		user1.setFirstName("firstname");
		user1.setLastName("lastname");
		user1.setUserName("username1");
		user1.setPassword("password");
		List<User> allUsers = new ArrayList<User>();
		allUsers.add(user);
		allUsers.add(user1);
		rule = new UserAdminValidationRule(users, allUsers);
		assertNull(rule.performCheck());
	}
}
