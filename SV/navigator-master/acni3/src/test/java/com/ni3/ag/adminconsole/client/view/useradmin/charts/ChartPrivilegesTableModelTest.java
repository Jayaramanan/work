/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.charts;

import java.util.ArrayList;

import javax.swing.JTree;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartGroup;
import com.ni3.ag.adminconsole.domain.Group;

public class ChartPrivilegesTableModelTest extends TestCase{
	private ChartPrivilegesTableModel model;
	private Group group;
	private Chart chart;

	@Override
	protected void setUp() throws Exception{
		group = new Group();
		group.setId(1);

		chart = new Chart();
		chart.setChartGroups(new ArrayList<ChartGroup>());
		chart.getChartGroups().add(new ChartGroup(new Group(), chart));
		chart.getChartGroups().add(new ChartGroup(group, chart));

		model = new ChartPrivilegesTableModel(new JTree(), group);
	}

	public void testIsCanCreate(){
		assertTrue(model.hasAccess(chart));

		chart.getChartGroups().remove(1);
		assertFalse(model.hasAccess(chart));
	}

	public void testSetCanCreate(){
		assertEquals(2, chart.getChartGroups().size());
		model.setHasAccess(chart, false);
		assertEquals(1, chart.getChartGroups().size());
		assertNotSame(group, chart.getChartGroups().get(0).getGroup());

		model.setHasAccess(chart, true);
		assertEquals(2, chart.getChartGroups().size());
		assertNotSame(group, chart.getChartGroups().get(0).getGroup());
		assertSame(group, chart.getChartGroups().get(1).getGroup());
	}

}
