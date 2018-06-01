/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.useradmin.charts.ChartPrivilegesTreeTableModel;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;

public class ChartPrivilegesTreeTableModelTest extends ACTestCase{
	private ChartPrivilegesTreeTableModel model;
	private List<Schema> schemas;

	public void setUp(){
		schemas = new ArrayList<Schema>();
		Schema s1 = new Schema();
		s1.setId(1);
		createCharts(s1);
		Schema s2 = new Schema();
		s2.setId(2);
		createCharts(s2);
		schemas.add(s1);
		schemas.add(s2);
		model = new ChartPrivilegesTreeTableModel(schemas);
	}

	private void createCharts(Schema s){
		Chart c1 = new Chart();
		c1.setId(s.getId() * 2);
		Chart c2 = new Chart();
		c2.setId(s.getId() * 3);

		List<Chart> charts = new ArrayList<Chart>();
		charts.add(c1);
		charts.add(c2);
		s.setCharts(charts);
	}

	public void testGetChild(){
		Object child = model.getChild(null, 34);
		Schema s0 = schemas.get(0);
		Chart c1 = s0.getCharts().get(1);
		assertNull(child);
		child = model.getChild(s0, 1);
		assertEquals(child, c1);
		child = model.getChild(new ACRootNode(), 1);
		assertEquals(child, schemas.get(1));
		child = model.getChild(c1, 0);
		assertNull(child);
	}

	public void testGetChildCount(){
		int cc = model.getChildCount(null);
		assertEquals(cc, 0);
		cc = model.getChildCount(schemas.get(0));
		assertEquals(cc, 2);
		schemas.remove(0);
		cc = model.getChildCount(new ACRootNode());
		assertEquals(cc, 1);
	}

	public void testGetIndexOfChild(){
		int i = model.getIndexOfChild(null, null);
		assertEquals(i, -1);
		i = model.getIndexOfChild(new ACRootNode(), schemas.get(1));
		assertEquals(i, 1);
		i = model.getIndexOfChild(new ACRootNode(), new Schema());
		assertEquals(i, -1);
	}

	public void testIsLeaf(){
		boolean isLeaf = model.isLeaf(null);
		assertTrue(isLeaf);
		isLeaf = model.isLeaf(schemas.get(0));
		assertFalse(isLeaf);
		schemas.get(0).setCharts(null);
		isLeaf = model.isLeaf(schemas.get(0));
		assertTrue(isLeaf);
	}
}
