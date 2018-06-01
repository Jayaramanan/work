/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class LicenseTreeModelTest extends ACTestCase{
	private LicenseTreeModel model;
	private List<DatabaseInstance> dbNames;
	private List<LicenseData> ldataList;

	public void setUp(){
		dbNames = new ArrayList<DatabaseInstance>();
		dbNames.add(new DatabaseInstance("test_db"));
		Map<DatabaseInstance, List<LicenseData>> map = new HashMap<DatabaseInstance, List<LicenseData>>();
		ldataList = new ArrayList<LicenseData>();
		LicenseData ldata = new LicenseData();
		License l = new License();
		l.setId(1);
		ldata.setLicense(l);
		ldataList.add(ldata);
		map.put(dbNames.get(0), ldataList);
		model = new LicenseTreeModel(map, dbNames);
	}

	public void testGetChild(){
		Object c = model.getChild(new ACRootNode(), 0);
		assertEquals(dbNames.get(0), c);
		c = model.getChild(dbNames.get(0), 0);
		assertEquals(ldataList.get(0), c);
		c = model.getChild(ldataList.get(0), 12);
		assertNull(c);
	}

	@SuppressWarnings("deprecation")
	public void testGetChildCount(){
		int c = model.getChildCount(new ACRootNode());
		assertEquals(dbNames.size(), c);
		ldataList.add(new LicenseData());
		c = model.getChildCount(dbNames.get(0));
		assertEquals(0, c);
		dbNames.get(0).setConnected(true);
		c = model.getChildCount(dbNames.get(0));
		assertEquals(2, c);
		c = model.getChildCount(ldataList);
		assertEquals(0, c);
	}

	public void testGetIndexOfChild(){
		int i = model.getIndexOfChild(new ACRootNode(), dbNames.get(0));
		assertEquals(0, i);
		LicenseData ld = new LicenseData();
		License l = new License();
		l.setId(2);
		ld.setLicense(l);
		ldataList.add(ld);
		i = model.getIndexOfChild(dbNames.get(0), ld);
		assertEquals(1, i);
		i = model.getIndexOfChild(dbNames.get(0), new License());
		assertEquals(-1, i);
	}

	@SuppressWarnings("deprecation")
	public void testIsLeaf(){
		assertFalse(model.isLeaf(new ACRootNode()));
		assertTrue(model.isLeaf(dbNames.get(0)));
		dbNames.get(0).setConnected(true);
		assertFalse(model.isLeaf(dbNames.get(0)));
		assertTrue(model.isLeaf(new DatabaseInstance("ttt")));
	}
}
