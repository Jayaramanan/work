/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class NavigatorLicenseTreeModelTest extends ACTestCase{
	private NavigatorLicenseTreeModel model;
	private List<DatabaseInstance> dbNames;
	private List<Group> groups;

	public void setUp(){
		dbNames = new ArrayList<DatabaseInstance>();
		dbNames.add(new DatabaseInstance("test_db"));
		Map<DatabaseInstance, List<Group>> map = new HashMap<DatabaseInstance, List<Group>>();
		groups = new ArrayList<Group>();
		Group g1 = new Group();
		g1.setId(1);
		groups.add(g1);
		Group g2 = new Group();
		g2.setId(2);
		groups.add(g2);
		map.put(dbNames.get(0), groups);
		model = new NavigatorLicenseTreeModel(map, dbNames);
	}

	public void testGetChild(){
		Object child = model.getChild(new ACRootNode(), 0);
		assertEquals(dbNames.get(0), child);
		child = model.getChild(dbNames.get(0), 1);
		assertEquals(groups.get(1), child);
		child = model.getChild(groups.get(0), 45);
		assertNull(child);
	}

	public void testGetChildCount(){
		int c = model.getChildCount(new ACRootNode());
		assertEquals(dbNames.size(), c);
		c = model.getChildCount(dbNames.get(0));
		assertEquals(groups.size(), c);
		c = model.getChildCount(groups.get(1));
		assertEquals(0, c);
	}

	public void testGetIndexOfChild(){
		int i = model.getIndexOfChild(new ACRootNode(), dbNames.get(0));
		assertEquals(0, i);
		i = model.getIndexOfChild(new ACRootNode(), groups.get(1));
		assertEquals(-1, i);
		i = model.getIndexOfChild(dbNames.get(0), groups.get(1));
		assertEquals(1, i);
	}

	public void testIsLeaf(){
		assertFalse(model.isLeaf(dbNames.get(0)));
		assertTrue(model.isLeaf(groups.get(1)));
		assertFalse(model.isLeaf(new ACRootNode()));
		dbNames.clear();
		assertTrue(model.isLeaf(new ACRootNode()));
	}
}
