/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ReportsTreeModelTest extends ACTestCase{
	private ReportsTreeModel model;
	private DatabaseInstance dbi;
	private List<ReportTemplate> reports;

	public void setUp(){
		dbi = new DatabaseInstance("test_db");
		List<DatabaseInstance> dbNames = new ArrayList<DatabaseInstance>();
		dbNames.add(dbi);
		ReportTemplate rt1 = new ReportTemplate();
		rt1.setId(1);
		ReportTemplate rt2 = new ReportTemplate();
		rt2.setId(2);
		reports = new ArrayList<ReportTemplate>();
		reports.add(rt1);
		reports.add(rt2);

		Map<DatabaseInstance, List<ReportTemplate>> schemaMap = new HashMap<DatabaseInstance, List<ReportTemplate>>();
		schemaMap.put(dbi, reports);

		model = new ReportsTreeModel(schemaMap, dbNames);
	}

	public void testGetChild(){
		Object child = model.getChild(new ACRootNode(), 0);
		assertEquals(dbi, child);
		ReportTemplate rt = reports.get(0);
		child = model.getChild(dbi, 0);
		assertEquals(rt, child);
		child = model.getChild(dbi, 1);
		assertEquals(reports.get(1), child);
		child = model.getChild("", 2);
		assertNull(child);
	}

	public void testGetChildCount(){
		ACRootNode root = new ACRootNode();
		int c = model.getChildCount(root);
		assertEquals(1, c);
		c = model.getChildCount(dbi);
		assertEquals(reports.size(), c);
		reports.clear();
		c = model.getChildCount(dbi);
		assertEquals(0, c);
	}

	public void testGetIndexOfChild(){
		ACRootNode root = new ACRootNode();
		int c = model.getIndexOfChild(root, dbi);
		assertEquals(0, c);
		ReportTemplate rt = reports.get(0);
		c = model.getIndexOfChild(dbi, rt);
		assertEquals(0, c);
		c = model.getIndexOfChild(dbi, reports.get(1));
		assertEquals(1, c);
		c = model.getIndexOfChild(dbi, "");
		assertEquals(-1, c);
	}

	public void testIsLeaf(){
		assertFalse(model.isLeaf(new ACRootNode()));
		assertFalse(model.isLeaf(dbi));
		ReportTemplate rt = reports.get(0);
		assertTrue(model.isLeaf(rt));
		assertTrue(model.isLeaf(""));
	}
}
