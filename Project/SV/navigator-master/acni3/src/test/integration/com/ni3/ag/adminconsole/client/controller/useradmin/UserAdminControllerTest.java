/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.util.ArrayList;
import java.util.List;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.UserAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.UserTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserAdminControllerTest extends ACTestCase{
	UserAdminController controller;
	UserAdminView view;
	UserAdminModel model;

	List<User> users;
	List<Group> groups;
	User user1;
	User user2;
	Group group1;

	@Override
	protected void setUp() throws Exception{
		ACMain.ScreenWidth = 500.;
		ACMain.ScreenHeight = 500.;
		controller = (UserAdminController) ACSpringFactory.getInstance().getBean("userAdminController");
		view = controller.getView();
		view.initializeComponents();
		model = controller.getModel();

		user1 = getUser("User1");
		user2 = getUser("User2");
		users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);

		group1 = getGroup("Group1");
		group1.setUsers(users);
		Group group2 = getGroup("Group2");
		groups = new ArrayList<Group>();
		groups.add(group1);
		groups.add(group2);

		model.setCurrentGroup(group1);
		model.setGroups(groups);
		model.setDeletedUsers(new ArrayList<User>());
	}

	public Group getGroup(String name){
		Group group1 = new Group();
		group1.setName(name);
		group1.setUsers(new ArrayList<User>());
		return group1;
	}

	public User getUser(String name){
		User user1 = new User();
		user1.setFirstName(name);
		user1.setLastName(name);
		user1.setUserName(name);
		user1.setPassword(name);
		return user1;
	}

	public void testAddNewUser(){
		view.getUserPanel().setTableModel(new UserTableModel(users));
		controller.addNewUser();
		assertEquals(3, users.size());
		assertEquals(3, model.getGroups().get(0).getUsers().size());
		assertSame(users.get(2), model.getGroups().get(0).getUsers().get(2));
		assertSame(users.get(2), model.getCurrentGroup().getUsers().get(2));
	}

	public void testAddNewUserAllUserMode(){
		model.setCurrentGroup(null);
		view.getUserPanel().setCurrentMode("AllUsers");
		view.getUserPanel().setTableModel(new UserTableModel(users));
		controller.addNewUser();
		assertEquals(3, users.size());
		assertEquals(3, model.getGroups().get(0).getUsers().size());
		assertSame(users.get(2), model.getGroups().get(0).getUsers().get(2));
	}

	public void testDeleteNewUser(){
		user1.setGroups(new ArrayList<Group>());
		user1.getGroups().add(group1);
		view.getUserPanel().setTableModel(new UserTableModel(users));
		controller.deleteUser(user1);

		assertEquals(1, users.size());
		assertEquals(1, model.getGroups().get(0).getUsers().size());
		assertEquals(0, model.getDeletedUsers().size());
		assertSame(user2, model.getGroups().get(0).getUsers().get(0));
	}

	public void testDeleteExistingUser(){
		view.getUserPanel().setTableModel(new UserTableModel(users));
		user1.setId(1);
		user1.setGroups(new ArrayList<Group>());
		user1.getGroups().add(group1);
		controller.deleteUser(user1);

		assertEquals(1, users.size());
		assertEquals(1, model.getGroups().get(0).getUsers().size());
		assertEquals(1, model.getDeletedUsers().size());
		assertSame(user2, model.getGroups().get(0).getUsers().get(0));
	}

	public void testRefreshPanelData(){
		controller.refreshPanelData(Translation.get(TextID.GroupMembers));
		assertEquals(Translation.get(TextID.GroupMembers), model.getCurrentPanel());
	}

	public void testCopyUser(){
		user1.setSettings(new ArrayList<UserSetting>());
		UserSetting set1 = new UserSetting();
		set1.setProp("prop");
		set1.setValue("value");
		set1.setUser(user1);

		UserSetting set2 = new UserSetting();
		set2.setProp("prop2");
		set2.setValue("value2");
		set2.setUser(user1);

		user1.getSettings().add(set1);
		user1.getSettings().add(set2);

		user1.setGroups(new ArrayList<Group>());
		user1.getGroups().add(group1);
		view.getUserPanel().setTableModel(new UserTableModel(users));
		User result = controller.copyUser(user1);
		assertNotNull(result);
		assertEquals(user1.getSettings().size(), result.getSettings().size());
		assertEquals(group1, result.getGroups().get(0));
		assertEquals(set1.getProp(), result.getSettings().get(0).getProp());
		assertEquals(set1.getValue(), result.getSettings().get(0).getValue());

		assertEquals(set2.getProp(), result.getSettings().get(1).getProp());
		assertEquals(set2.getValue(), result.getSettings().get(1).getValue());
	}
}
