/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.server.service.DiagnosticTasksHolder;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class DiagnosticTasksHolderImpl implements DiagnosticTasksHolder{
	private List<DiagnosticTask> tasks;

	public List<DiagnosticTask> getTasks(){
		return tasks;
	}

	public void setTasks(List<DiagnosticTask> tasks){
		this.tasks = tasks;
	}

	@Override
	public List<DiagnoseTaskResult> makeDiagnostic(Schema sch){
		List<DiagnoseTaskResult> results = new ArrayList<DiagnoseTaskResult>();
		for (DiagnosticTask task : tasks){
			results.add(task.makeDiagnose(sch));
		}
		return results;
	}

	@Override
	public DiagnoseTaskResult makeCorrection(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		DiagnosticTask task = getTaskForName(taskResult.getTaskClass());
		if (task == null)
			throw new ACException(TextID.MsgNoTaskForName, new String[] { taskResult.getTaskClass() });
		return task.makeFix(taskResult);
	}

	private DiagnosticTask getTaskForName(String clazz){
		for (DiagnosticTask task : tasks){
			if (task.getClass().getName().equals(clazz))
				return task;
		}
		return null;
	}

	@Override
	public DiagnoseTaskResult makeDiagnostic(DiagnoseTaskResult taskResult, Schema sch){
		DiagnosticTask task = getTaskForName(taskResult.getTaskClass());
		if (task == null)
			return null;
		return task.makeDiagnose(sch);
	}

}
