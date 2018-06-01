/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JTree;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;

public class UserSelectionTableModelTest extends TestCase{
	private UserSelectionTableModel model;
	private List<User> allUsers;
	private List<Group> groups;

	public void setUp(){
		groups = new ArrayList<Group>();
		Group group = new Group();
		group.setId(1);
		group.setName("group1");
		groups.add(group);

		Group group2 = new Group();
		group2.setId(2);
		group2.setName("group2");
		groups.add(group2);

		allUsers = generateUsers(group);

		group.setUsers(allUsers);
		group2.setUsers(new ArrayList<User>());

		model = new UserSelectionTableModel(new JTree(), new HashSet<User>());
	}

	private List<User> generateUsers(Group group){
		List<User> users = new ArrayList<User>();
		for (int i = 1; i < 6; i++){
			User user = new User();
			user.setUserName("user" + i);
			user.setId(i + i * 10);
			user.setGroups(new ArrayList<Group>());
			user.getGroups().add(group);
			users.add(user);
		}
		return users;
	}

	public void testIsSelected(){
		assertFalse(model.isSelected(new ACRootNode()));
		for (Group group : groups){
			assertFalse(model.isSelected(group));
		}
		for (User user : allUsers){
			assertFalse(model.isSelected(user));
		}

		model.setSelectedUsers(new HashSet<User>(allUsers));
		assertTrue(model.isSelected(groups.get(0)));
		assertFalse(model.isSelected(groups.get(1)));
		for (User user : allUsers){
			assertTrue(model.isSelected(user));
		}

		model.getSelectedUsers().remove(allUsers.get(0));
		model.getSelectedUsers().remove(allUsers.get(1));
		for (Group group : groups){
			assertFalse(model.isSelected(group));
		}
		for (int i = 0; i < 2; i++){
			assertFalse(model.isSelected(allUsers.get(i)));
		}
		for (int i = 2; i < allUsers.size(); i++){
			assertTrue(model.isSelected(allUsers.get(i)));
		}

	}

	public void testSetSelected(){
		Group group = groups.get(0);
		model.setSelected(group, true);
		assertTrue(model.isSelected(group));
		for (User user : allUsers){
			assertTrue(model.isSelected(user));
		}
		model.setSelected(group, false);
		assertFalse(model.isSelected(group));
		for (User user : allUsers){
			assertFalse(model.isSelected(user));
		}

		for (User user : allUsers){
			assertFalse(model.isSelected(group));
			model.setSelected(user, true);
			assertTrue(model.isSelected(user));
		}
		assertTrue(model.isSelected(group));

		for (User user : allUsers){
			model.setSelected(user, false);
			assertFalse(model.isSelected(group));
			assertFalse(model.isSelected(user));
		}
	}

	public void testSelectedDifferentGroup(){
		model.setSelected(allUsers.get(0), true);
		assertFalse(model.selectedDifferentGroup(groups.get(0)));
		assertTrue(model.selectedDifferentGroup(groups.get(1)));

		model.setSelected(allUsers.get(0), false);
		assertFalse(model.selectedDifferentGroup(groups.get(0)));
		assertFalse(model.selectedDifferentGroup(groups.get(1)));
	}
}
