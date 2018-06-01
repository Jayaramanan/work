/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.ac;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ACLicenseTreeModelTest extends ACTestCase{
	private ACLicenseTreeModel model;
	private List<DatabaseInstance> dbNames;

	public void setUp(){
		dbNames = new ArrayList<DatabaseInstance>();
		dbNames.add(new DatabaseInstance("test_db"));
		dbNames.add(new DatabaseInstance("test_db_2"));
		model = new ACLicenseTreeModel(dbNames);
	}

	public void testGetChild(){
		Object c = model.getChild(new ACRootNode(), 1);
		assertEquals(dbNames.get(1), c);
		c = model.getChild(dbNames.get(0), 0);
		assertNull(c);
	}

	public void testGetChildCount(){
		int c = model.getChildCount(new ACRootNode());
		assertEquals(dbNames.size(), c);
		c = model.getChildCount(dbNames.get(0));
		assertEquals(0, c);
	}

	public void testGetIndexOfChild(){
		int i = model.getIndexOfChild(new ACRootNode(), dbNames.get(1));
		assertEquals(1, i);
		i = model.getIndexOfChild(dbNames.get(0), new ACRootNode());
		assertEquals(-1, i);
		i = model.getIndexOfChild("", dbNames);
		assertEquals(-1, i);
	}

	public void testIsLeaf(){
		assertFalse(model.isLeaf(new ACRootNode()));
		assertFalse(model.isLeaf(dbNames.get(0)));
	}
}
