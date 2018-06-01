/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;

public class AdminConsoleLicenseServiceImplTest extends TestCase{

	private AdminConsoleLicenseServiceImpl service;
	private List<User> users;
	private String testModule = AdminConsoleModule.BaseModule.getValue();

	private User user1;
	private User user2;

	@Override
	protected void setUp() throws Exception{
		service = new AdminConsoleLicenseServiceImpl();
		users = new ArrayList<User>();
		user1 = new User();
		user1.setUserEditions(new ArrayList<UserEdition>());
		UserEdition ue1 = new UserEdition(user1, testModule, "");
		ue1.setIsExpiring(false);
		user1.getUserEditions().add(ue1);

		UserEdition ue11 = new UserEdition(user1, "xx", "");
		ue11.setIsExpiring(false);
		user1.getUserEditions().add(ue11);

		user2 = new User();
		user2.setUserEditions(new ArrayList<UserEdition>());
		UserEdition ue2 = new UserEdition(user2, testModule, "");
		ue2.setIsExpiring(false);
		user2.getUserEditions().add(ue2);

		users.add(user1);
		users.add(user2);
	}

	public void testAdjustExpiringModulesMissingMarks(){

		service.adjustExpiringModules(users, testModule, 1);
		assertTrue(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertFalse(user2.getUserEditions().get(0).isExpiring());

		service.adjustExpiringModules(users, testModule, 2);
		assertTrue(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertTrue(user2.getUserEditions().get(0).isExpiring());
	}

	public void testAdjustExpiringModulesExcessiveMarks(){
		user1.getUserEditions().get(0).setIsExpiring(true);
		user2.getUserEditions().get(0).setIsExpiring(true);

		service.adjustExpiringModules(users, testModule, -1);
		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertTrue(user2.getUserEditions().get(0).isExpiring());

		service.adjustExpiringModules(users, testModule, -2);
		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertFalse(user2.getUserEditions().get(0).isExpiring());
	}

	public void testRemoveExcessiveModulesAllMarkedDiff1(){
		user1.getUserEditions().get(0).setIsExpiring(true);
		user2.getUserEditions().get(0).setIsExpiring(true);
		service.removeExcessiveModules(users, testModule, 1, true);
		assertEquals(1, user1.getUserEditions().size());
		assertEquals(1, user2.getUserEditions().size());
	}

	public void testRemoveExcessiveModulesAllMarkedDiff2(){
		user1.getUserEditions().get(0).setIsExpiring(true);
		user2.getUserEditions().get(0).setIsExpiring(true);
		service.removeExcessiveModules(users, testModule, 2, true);
		assertEquals(1, user1.getUserEditions().size());
		assertEquals(0, user2.getUserEditions().size());
	}

	public void testRemoveExcessiveModulesOneMarkedDiff1(){
		user1.getUserEditions().get(0).setIsExpiring(false);
		user2.getUserEditions().get(0).setIsExpiring(true);
		int difference = service.removeExcessiveModules(users, testModule, 1, true);
		assertEquals(2, user1.getUserEditions().size());
		assertEquals(0, user2.getUserEditions().size());
		assertEquals(0, difference);
	}

	public void testRemoveExcessiveModulesOneMarkedDiff2(){
		user1.getUserEditions().get(0).setIsExpiring(false);
		user2.getUserEditions().get(0).setIsExpiring(true);
		int difference = service.removeExcessiveModules(users, testModule, 2, true);
		assertEquals(2, user1.getUserEditions().size());
		assertEquals(0, user2.getUserEditions().size());
		assertEquals(1, difference);

		difference = service.removeExcessiveModules(users, testModule, 1, false);
		assertEquals(1, user1.getUserEditions().size());
		assertEquals(0, user2.getUserEditions().size());
		assertEquals(0, difference);
	}

	public void testCheckLicenseModulesMaxNonExp2(){
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(5);
		mDescr.setMaxNonExpiringUserCount(2);
		service.checkLicenseModules(users, mDescr);
		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertFalse(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxNonExp1(){
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(5);
		mDescr.setMaxNonExpiringUserCount(1);
		service.checkLicenseModules(users, mDescr);
		assertTrue(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertFalse(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxNonExp0(){
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(5);
		mDescr.setMaxNonExpiringUserCount(0);
		service.checkLicenseModules(users, mDescr);
		assertTrue(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertTrue(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxNonExp2InitialExpiring(){
		user1.getUserEditions().get(0).setIsExpiring(true);
		user2.getUserEditions().get(0).setIsExpiring(true);
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(5);
		mDescr.setMaxNonExpiringUserCount(2);
		service.checkLicenseModules(users, mDescr);
		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertFalse(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxNonExp1InitialExpiring(){
		user1.getUserEditions().get(0).setIsExpiring(true);
		user2.getUserEditions().get(0).setIsExpiring(true);
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(5);
		mDescr.setMaxNonExpiringUserCount(1);
		service.checkLicenseModules(users, mDescr);
		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertTrue(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxNonExp0InitialExpiring(){
		user1.getUserEditions().get(0).setIsExpiring(true);
		user2.getUserEditions().get(0).setIsExpiring(true);
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(5);
		mDescr.setMaxNonExpiringUserCount(0);
		service.checkLicenseModules(users, mDescr);
		assertTrue(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());
		assertTrue(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxUserCount1NoneExpiring(){
		user1.getUserEditions().get(0).setIsExpiring(false);
		user2.getUserEditions().get(0).setIsExpiring(false);
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(1);
		mDescr.setMaxNonExpiringUserCount(1);
		service.checkLicenseModules(users, mDescr);

		assertEquals(1, user1.getUserEditions().size());
		assertEquals(1, user2.getUserEditions().size());

		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user2.getUserEditions().get(0).isExpiring());
	}

	public void testCheckLicenseModulesMaxUserCount1SecondExpiring(){
		user1.getUserEditions().get(0).setIsExpiring(false);
		user2.getUserEditions().get(0).setIsExpiring(true);
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(1);
		mDescr.setMaxNonExpiringUserCount(1);
		service.checkLicenseModules(users, mDescr);

		assertEquals(2, user1.getUserEditions().size());
		assertEquals(0, user2.getUserEditions().size());

		assertFalse(user1.getUserEditions().get(0).isExpiring());
		assertFalse(user1.getUserEditions().get(1).isExpiring());

	}

	public void testCheckLicenseModulesMaxUserCount0(){
		user1.getUserEditions().get(0).setIsExpiring(false);
		user2.getUserEditions().get(0).setIsExpiring(true);
		ACModuleDescription mDescr = new ACModuleDescription(AdminConsoleModule.BaseModule);
		mDescr.setUserCount(0);
		mDescr.setMaxNonExpiringUserCount(0);
		service.checkLicenseModules(users, mDescr);

		assertEquals(1, user1.getUserEditions().size());
		assertEquals(0, user2.getUserEditions().size());

		assertFalse(user1.getUserEditions().get(0).isExpiring());
	}
}
