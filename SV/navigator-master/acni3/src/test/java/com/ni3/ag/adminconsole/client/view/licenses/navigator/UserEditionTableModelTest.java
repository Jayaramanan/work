/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.navigator;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.NavigatorModule;

public class UserEditionTableModelTest extends ACTestCase{
	private UserEditionTableModel model;
	private List<ModuleDescription> modules;
	private User user;
	private List<UserEdition> userEditions;
	private int baseModuleIndex = -1;

	@Override
	protected void setUp() throws Exception{
		List<User> users = new ArrayList<User>();
		user = new User();
		user.setId(5);
		userEditions = getAllUserEditions(user);
		user.setUserEditions(userEditions);
		users.add(user);

		modules = new ArrayList<ModuleDescription>();
		for (NavigatorModule module : NavigatorModule.values()){
			modules.add(new ModuleDescription(module, 1));
		}
		model = new UserEditionTableModel(users, new ArrayList<User>(), modules);
	}

	public void testHasAccessAllAccess(){
		for (int col = 0; col < modules.size(); col++){
			assertTrue(model.hasAccess(user, col + 1));
		}
	}

	public void testHasAccessNoAccess(){
		user.getUserEditions().clear();
		for (int col = 0; col < modules.size(); col++){
			assertFalse(model.hasAccess(user, col + 1));
		}
	}

	public void testHasAccessNotAllAccess(){
		userEditions.remove(0);
		assertFalse(model.hasAccess(user, 1));
		for (int col = 1; col < modules.size(); col++){
			assertTrue(model.hasAccess(user, col + 1));
		}
	}

	public void testSetAccessBaseToFalse(){
		model.setAccess(user, baseModuleIndex, false);
		for (int col = 0; col < modules.size(); col++){
			assertFalse(model.hasAccess(user, col + 1));
		}
		assertEquals(0, userEditions.size());
	}

	public void testSetAccessToFalse(){
		model.setAccess(user, 2, false);
		assertTrue(model.hasAccess(user, 1));
		assertFalse(model.hasAccess(user, 2));
		for (int col = 2; col < modules.size(); col++){
			assertTrue(model.hasAccess(user, col + 1));
		}
		assertEquals(NavigatorModule.values().length - 1, userEditions.size());
	}

	public void testSetAccessToTrue(){
		userEditions.clear();
		model.setAccess(user, 1, true);
		assertTrue(model.hasAccess(user, 1));
		assertEquals(1, userEditions.size());
		for (int col = 1; col < modules.size(); col++){
			assertFalse(model.hasAccess(user, col + 1));
		}
	}

	public void testIsCellEditable(){
		assertFalse(model.isCellEditable(0, 0));
		for (int col = 0; col < modules.size(); col++){
			assertTrue(model.isCellEditable(0, col + 1));
		}
	}

	public void testIsCellEditableNoBase(){
		model.setAccess(user, baseModuleIndex, false);
		assertFalse(model.isCellEditable(0, 0));
		assertTrue(model.isCellEditable(0, 1));
		for (int col = 1; col < modules.size(); col++){
			assertFalse(model.isCellEditable(0, col + 1));
		}
	}

	private List<UserEdition> getAllUserEditions(User user){
		List<UserEdition> editions = new ArrayList<UserEdition>();
		int i = 1;
		for (NavigatorModule module : NavigatorModule.values()){
			if (module.equals(NavigatorModule.BaseModule)){
				baseModuleIndex = i;
			}
			UserEdition ue = new UserEdition(user, module.getValue(), null);
			ue.setIsExpiring(false);
			editions.add(ue);
			i++;
		}
		return editions;
	}

}
