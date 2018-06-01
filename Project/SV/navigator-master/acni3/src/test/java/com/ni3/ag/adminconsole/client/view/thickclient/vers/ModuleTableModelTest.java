/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;

import junit.framework.TestCase;

public class ModuleTableModelTest extends TestCase{

	private ModuleTableModel model;
	private List<Module> modules;
	private List<Group> groups;

	public void setUp(){
		generateGroups();
		generateModules();
		model = new ModuleTableModel(modules, groups);
	}

	private void generateGroups(){
		groups = new ArrayList<Group>();
		Group ga = new Group();
		ga.setName("admins");
		ga.setUsers(new ArrayList<User>());
		groups.add(ga);
		User admin1 = new User();
		admin1.setUserModules(new ArrayList<ModuleUser>());
		ga.getUsers().add(admin1);
		User admin2 = new User();
		admin2.setUserModules(new ArrayList<ModuleUser>());
		ga.getUsers().add(admin2);
		Group gu = new Group();
		gu.setName("users");
		gu.setUsers(new ArrayList<User>());
		groups.add(gu);
		User user = new User();
		user.setUserModules(new ArrayList<ModuleUser>());
		gu.getUsers().add(user);
	}

	private void generateModules(){
		modules = new ArrayList<Module>();
		for (int i = 0; i < 10; i++){
			Module m = new Module();
			m.setName("m" + i);
			m.setPath("p" + i);
			m.setVersion("v" + i);
			m.setHash("h" + i);
			m.setArchivePassword("pass" + i);
			modules.add(m);
			if (i % 5 == 0){
				Group admins = groups.get(0);
				User admin1 = admins.getUsers().get(0);
				ModuleUser mu = new ModuleUser();
				mu.setCurrent(m);
				mu.setUser(admin1);
				admin1.getUserModules().add(mu);
			} else if (i % 3 == 0){
				Group admins = groups.get(0);
				User admin2 = admins.getUsers().get(1);
				ModuleUser mu = new ModuleUser();
				mu.setCurrent(m);
				mu.setUser(admin2);
				admin2.getUserModules().add(mu);
			} else if (i % 2 == 0){
				Group users = groups.get(1);
				User user = users.getUsers().get(0);
				ModuleUser mu = new ModuleUser();
				mu.setCurrent(m);
				mu.setUser(user);
				user.getUserModules().add(mu);
			}
		}
	}

	public void testColumnCount(){
		assertEquals(6, model.getColumnCount());
	}

	public void testRowCount(){
		assertEquals(model.getRowCount(), modules.size());
	}

	public void testValueAt(){
		for (int i = 0; i < modules.size(); i++){
			Module m = modules.get(i);
			assertEquals(m.getName(), model.getValueAt(i, 0));
			assertEquals(m.getPath(), model.getValueAt(i, 1));
			assertEquals(m.getVersion(), model.getValueAt(i, 2));
			assertEquals(m.getHash(), model.getValueAt(i, 3));
			assertEquals(m.getArchivePassword(), model.getValueAt(i, 4));
		}
	}

	private boolean isModuleUsed(Module m){
		for (Group g : groups){
			for (User u : g.getUsers()){
				for (ModuleUser mu : u.getUserModules()){
					if (m.equals(mu.getCurrent()) || m.equals(mu.getTarget()))
						return true;
				}
			}
		}
		return false;
	}

	public void testColumnEditable(){
		for (int i = 0; i < model.getRowCount(); i++){
			Module m = modules.get(i);
			boolean used = isModuleUsed(m);
			assertEquals(!used, model.isCellEditable(i, 0));
			assertEquals(!used, model.isCellEditable(i, 1));
			assertEquals(!used, model.isCellEditable(i, 2));
			assertEquals(!used, model.isCellEditable(i, 3));
			assertEquals(!used, model.isCellEditable(i, 4));
		}
	}

	public void testSetValueAt(){
		for (int i = 0; i < modules.size(); i++){
			// modules with indexes 1 and 7 are not used
			if (i != 1 && i != 7)
				continue;

			model.setValueAt("newModule" + i, i, 0);
			model.setValueAt("module" + i + ".zip", i, 1);
			model.setValueAt("1" + i, i, 2);
			model.setValueAt("newHash" + i, i, 3);
			model.setValueAt("newPass" + i, i, 4);

			Module m = modules.get(i);
			assertEquals(m.getName(), "newModule" + i);
			assertEquals(m.getPath(), "module" + i + ".zip");
			assertEquals(m.getVersion(), "1" + i);
			assertEquals(m.getHash(), "newHash" + i);
			assertEquals(m.getArchivePassword(), "newPass" + i);
		}
	}

}
