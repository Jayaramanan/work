/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class ChartPrivilegesCheckTaskTest extends TestCase{
	private List<Chart> charts;
	private ChartPrivilegesCheckTask task;

	@Override
	protected void setUp() throws Exception{
		charts = new ArrayList<Chart>();
		for (int i = 0; i < 3; i++){
			Chart chart = new Chart();
			chart.setName("chart" + (i + 1));
			chart.setChartGroups(new ArrayList<ChartGroup>());
			charts.add(chart);
		}
		task = new ChartPrivilegesCheckTask();
	}

	public void testGetTaskResultNoAccessToAll(){
		DiagnoseTaskResult result = task.getTaskResult(charts);
		assertEquals(DiagnoseTaskStatus.Warning, result.getStatus());
		assertEquals("No accesses to chart(s): chart1, chart2, chart3", result.getErrorDescription());
	}

	public void testGetTaskResultNoAccessToOne(){
		for (int i = 0; i < 2; i++){
			charts.get(i).getChartGroups().add(new ChartGroup(new Group(), charts.get(i)));
		}
		DiagnoseTaskResult result = task.getTaskResult(charts);
		assertEquals(DiagnoseTaskStatus.Warning, result.getStatus());
		assertEquals("No accesses to chart(s): chart3", result.getErrorDescription());
	}

	public void testGetTaskResultAllAccessible(){
		for (int i = 0; i < 3; i++){
			charts.get(i).getChartGroups().add(new ChartGroup(new Group(), charts.get(i)));
		}
		DiagnoseTaskResult result = task.getTaskResult(charts);
		assertEquals(DiagnoseTaskStatus.Ok, result.getStatus());
		assertNull(result.getErrorDescription());
	}
}
