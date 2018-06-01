/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;

import junit.framework.TestCase;

public class UserModuleTableModelTest extends TestCase{
	private UserModuleTableModel model;
	private List<Module> modules;
	private List<Module> filteredModules;
	private Group group;

	public void setUp(){
		group = generateGroup();
		modules = generateModules();
		filteredModules = new ArrayList<Module>();
		filteredModules.addAll(modules);
		filteredModules.remove(0);
		model = new UserModuleTableModel(group.getUsers(), modules);
	}

	private Group generateGroup(){
		Group g = new Group();
		g.setUsers(new ArrayList<User>());
		User u1 = new User();
		u1.setUserName("u1");
		u1.setUserModules(new ArrayList<ModuleUser>());
		g.getUsers().add(u1);
		User u2 = new User();
		u2.setUserName("u2");
		u2.setUserModules(new ArrayList<ModuleUser>());
		g.getUsers().add(u2);
		return g;
	}

	private List<Module> generateModules(){
		List<Module> modules = new ArrayList<Module>();
		for (String name : Module.NAMES){
			for (int i = 0; i < 10; i++){
				Module m = new Module();
				m.setName(name);
				m.setPath(name + i);
				m.setVersion(name + "v" + i);
				m.setHash(name + "h" + i);
				m.setArchivePassword(name + "pass" + i);
				modules.add(m);
				if (i == 3){
					User user = group.getUsers().get(0);
					ModuleUser mu = new ModuleUser();
					mu.setTarget(m);
					mu.setUser(user);
					user.getUserModules().add(mu);
				}
				if (i == 2){
					User user = group.getUsers().get(1);
					ModuleUser mu = new ModuleUser();
					mu.setTarget(m);
					mu.setUser(user);
					user.getUserModules().add(mu);
				}
			}
		}
		return modules;
	}

	private ModuleUser getUserModule(Module m, User u){
		for (ModuleUser mu : u.getUserModules()){
			if (mu.getCurrent() != null && mu.getCurrent().getName().equals(m.getName()))
				return mu;
			if (mu.getTarget() != null && mu.getTarget().getName().equals(m.getName()))
				return mu;
		}
		return null;
	}

	public void testColumnCount(){
		assertEquals(Module.NAMES.length + 1, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), group.getUsers().size());
	}

	public void testValueAt(){
		for (int i = 0; i < group.getUsers().size(); i++){

			User u = group.getUsers().get(i);
			assertEquals(u, model.getValueAt(i, 0));
		}
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			assertFalse(model.isCellEditable(i, 0));
			for (int j = 1; j < model.getColumnCount(); j++)
				assertTrue(model.isCellEditable(i, j));
		}
	}

	public void testSetValueAt(){
		for (int i = 0; i < group.getUsers().size(); i++){
			User u = group.getUsers().get(i);
			for (int j = 0; j < modules.size(); j++){
				Module m = modules.get(j);
				model.setValueAt(m, i, 1);
				assertNotNull(getUserModule(m, u));
			}
		}
	}

	public void testGetModulesForColumn(){
		String[] names = new String[Module.NAMES.length];
		System.arraycopy(Module.NAMES, 0, names, 0, Module.NAMES.length);
		Arrays.sort(names);
		for (int i = 0; i < names.length; i++){
			List<Module> currentModules = model.getModulesForColumn(i + 1);

			for (int j = 0; j < currentModules.size(); j++)
				if (j == 0)
					assertEquals(currentModules.get(j).getId().intValue(), -1);
				else
					assertEquals(names[i], currentModules.get(j).getName());
		}
	}

	public void testIsAvailableForColumn(){
		Module module = modules.get(2);
		for (int i = 1; i < model.getColumnCount(); i++){
			boolean available = model.isAvailableForColumn(module, i);
			if (model.getColumnName(i).equals(module.getName())){
				assertTrue(available);
			} else{
				assertFalse(available);
			}
		}
	}

	public void testRemoveModuleUser(){
		User user = group.getUsers().get(0);
		assertEquals(Module.NAMES.length, user.getUserModules().size());
		model.removeModuleUser(user, 2);
		assertEquals(Module.NAMES.length - 1, user.getUserModules().size());
		model.removeModuleUser(user, 3);
		assertEquals(Module.NAMES.length - 2, user.getUserModules().size());
	}

}
