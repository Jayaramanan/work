/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserAdminTreeModelTest extends ACTestCase{
	public void testConstructor(){
		SessionData.getInstance().setDbName("mydb");
		Map<DatabaseInstance, List<Group>> groups = generateGroupMap();
		UserAdminTreeModel model = new UserAdminTreeModel(groups, instances);
		assertEquals(model.getRoot(), new ACRootNode());
	}

	public void testGetChild(){
		SessionData.getInstance().setDbName("mydb");
		Map<DatabaseInstance, List<Group>> groups = generateGroupMap();
		UserAdminTreeModel model = new UserAdminTreeModel(groups, instances);
		assertEquals(10, model.getChildCount(db));
		assertEquals(0, model.getChildCount(new Object()));
		for (int i = 0; i < 10; i++){
			assertEquals(groups.get(db).get(i), model.getChild(db, i));
			assertEquals(i, model.getIndexOfChild(db, groups.get(db).get(i)));
		}
		String[] ar = new String[] { Translation.get(TextID.GroupMembers), Translation.get(TextID.GroupPrivileges) };
		Group g = groups.get(db).get(0);
		for (int i = 0; i < 2; i++){
			assertEquals(ar[i], model.getChild(g, i));
			assertEquals(i, model.getIndexOfChild(g, ar[i]));
		}
	}

	private Map<DatabaseInstance, List<Group>> generateGroupMap(){
		Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
		ArrayList<Group> ar = new ArrayList<Group>();
		for (int id = 1; id <= 10; id++){
			Group g = new Group();
			g.setId(id);
			ar.add(g);
		}
		groupMap.put(db, ar);
		return groupMap;
	}

	private DatabaseInstance db = new DatabaseInstance("db1");
	private List<DatabaseInstance> instances = Arrays.asList(new DatabaseInstance[] { new DatabaseInstance("db1") });

}
