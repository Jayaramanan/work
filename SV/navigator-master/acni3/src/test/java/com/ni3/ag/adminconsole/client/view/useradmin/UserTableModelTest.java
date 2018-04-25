/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;

public class UserTableModelTest extends ACTestCase{
	public void testUserHasValidGroup(){
		UserTableModel model = new UserTableModel();
		User user = new User();

		assertFalse(model.userHasValidGroup(user));

		Group g = new Group();
		g.setId(-1);
		List<Group> groups = new ArrayList<Group>();
		groups.add(g);
		user.setGroups(groups);

		assertFalse(model.userHasValidGroup(user));

		g.setId(1);

		assertTrue(model.userHasValidGroup(user));
	}
}
