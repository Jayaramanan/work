/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ChartDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class ChartPrivilegesCheckTask implements DiagnosticTask{

	private final static String DESCRIPTION = "Checking that chart is accessible at least to one user";
	private final static String TOOLTIP = "No accesses to chart(s): ";
	private final static String ACTION_DESCRIPTION = "Go to Users tab and set 'Has access' for at least one group to charts: % ";

	private ChartDAO chartDAO;

	public void setChartDAO(ChartDAO chartDAO){
		this.chartDAO = chartDAO;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<Chart> charts = chartDAO.getChartsBySchema(sch);
		return getTaskResult(charts);
	}

	DiagnoseTaskResult getTaskResult(List<Chart> charts){
		String chNames = "";
		for (Chart chart : charts){
			if (chart.getChartGroups().isEmpty()){
				chNames += chart.getName();
				chNames += ", ";
			}
		}

		DiagnoseTaskStatus status = DiagnoseTaskStatus.Ok;
		String tooltip = null;
		String action = null;
		if (!chNames.isEmpty()){
			if (chNames.endsWith(", ")){
				chNames = chNames.substring(0, chNames.length() - 2);
			}
			status = DiagnoseTaskStatus.Warning;
			tooltip = TOOLTIP + chNames;
			action = ACTION_DESCRIPTION + chNames;
		}

		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, status, tooltip, action);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}
}
