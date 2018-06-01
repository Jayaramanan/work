/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;

public class VersioningControllerTest extends ACTestCase{
	private VersioningController controller;

	public void setUp(){
		controller = new VersioningController();
	}

	public void testFilterGroups(){
		User user = new User();
		user.setId(23);
		user.setHasOfflineClient(true);
		User userNoOffline = new User();
		userNoOffline.setId(34);
		userNoOffline.setHasOfflineClient(false);
		List<User> users = new ArrayList<User>();
		users.add(user);
		users.add(userNoOffline);
		Group group = new Group();
		group.setUsers(users);
		List<Group> groups = new ArrayList<Group>();
		groups.add(group);
		List<Group> filteredGroups = controller.filterGroups(groups);

		List<User> filteredUsers = new ArrayList<User>();
		for (Group g : filteredGroups)
			filteredUsers.addAll(g.getUsers());

		assertTrue(filteredUsers.contains(user));
		assertFalse(filteredUsers.contains(userNoOffline));
	}

}
