/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.ac;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;

public class UserACEditionTableModelTest extends ACTestCase{
	private UserACEditionTableModel model;
	private List<User> users;
	private List<ACModuleDescription> mDescriptions;

	public void setUp(){
		mDescriptions = new ArrayList<ACModuleDescription>();
		for (AdminConsoleModule acModule : AdminConsoleModule.values())
			mDescriptions.add(new ACModuleDescription(acModule));

		User u1 = new User();
		u1.setId(1);
		User u2 = new User();
		u2.setId(2);

		AdminConsoleModule module1 = mDescriptions.get(0).getModule();
		UserEdition ue1 = new UserEdition(u1, module1.getValue(), "123");
		List<UserEdition> u1Editions = new ArrayList<UserEdition>();
		u1Editions.add(ue1);
		u1.setUserEditions(u1Editions);
		AdminConsoleModule module2 = mDescriptions.get(1).getModule();
		UserEdition ue2 = new UserEdition(u2, module2.getValue(), "abc");
		List<UserEdition> u2Editions = new ArrayList<UserEdition>();
		u2Editions.add(ue2);
		u2.setUserEditions(u2Editions);

		users = new ArrayList<User>();
		users.add(u1);
		users.add(u2);

		model = new UserACEditionTableModel(users, mDescriptions);
	}

	public void testGetRowCount(){
		int size = users.size();
		assertEquals(size, model.getRowCount());
		users.remove(0);
		assertEquals(size - 1, model.getRowCount());
	}

	public void testHasAccess(){
		User u1 = users.get(0);
		User u2 = users.get(1);

		assertTrue(model.hasAccess(u1, 1));
		assertFalse(model.hasAccess(u1, 2));
		assertTrue(model.hasAccess(u2, 2));
		assertFalse(model.hasAccess(u2, 1));
	}

	public void testSetAccess(){
		User u1 = users.get(0);
		User u2 = users.get(1);
		model.setAccess(u1, 1, Boolean.FALSE);
		model.setAccess(u2, 1, Boolean.TRUE);

		boolean u1HasFirstModule = false;
		AdminConsoleModule module = mDescriptions.get(0).getModule();
		for (UserEdition ue : u1.getUserEditions()){
			if (module.getValue().equals(ue.getEdition())){
				u1HasFirstModule = true;
				break;
			}
		}
		assertFalse(u1HasFirstModule);

		boolean u2HasFirstModule = false;
		for (UserEdition ue : u2.getUserEditions()){
			if (module.getValue().equals(ue.getEdition())){
				u2HasFirstModule = true;
				break;
			}
		}
		assertTrue(u2HasFirstModule);
	}

	public void testGetUsedLicenseCount(){
		AdminConsoleModule module0 = mDescriptions.get(0).getModule();
		int c = model.getUsedLicenseCount(module0);
		assertEquals(1, c);
		users.get(0).getUserEditions().clear();
		c = model.getUsedLicenseCount(module0);
		assertEquals(0, c);
	}

	public void testIsCellMarkedForExpiry(){

		assertFalse(model.isCellMarkedForExpiry(0, 1));

		List<UserEdition> editions = users.get(0).getUserEditions();
		editions.get(0).setIsExpiring(true);

		assertTrue(model.isCellMarkedForExpiry(0, 1));
	}

	public void testGetCurrentMarkedCellCount(){
		AdminConsoleModule module = mDescriptions.get(0).getModule();

		int c = model.getCurrentMarkedCellCount(module.getValue());
		assertEquals(0, c);

		List<UserEdition> editions = users.get(0).getUserEditions();
		editions.get(0).setIsExpiring(true);

		c = model.getCurrentMarkedCellCount(module.getValue());
		assertEquals(1, c);

		User user2 = users.get(1);
		editions = user2.getUserEditions();
		UserEdition ue = new UserEdition(user2, module.getValue(), "sdf");
		ue.setIsExpiring(true);
		editions.add(ue);

		c = model.getCurrentMarkedCellCount(module.getValue());
		assertEquals(2, c);
	}

	public void testSetCellMarkedForExpiry(){
		List<UserEdition> editions = users.get(0).getUserEditions();
		assertFalse(editions.get(0).isExpiring());

		model.setCellMarkedForExpiry(0, 1, Boolean.TRUE);
		assertTrue(editions.get(0).isExpiring());
	}
}
