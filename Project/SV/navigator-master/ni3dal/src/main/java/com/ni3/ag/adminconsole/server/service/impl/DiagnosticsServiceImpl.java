/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.server.service.DiagnosticTasksHolder;
import com.ni3.ag.adminconsole.shared.service.def.DiagnosticsService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DiagnosticsServiceImpl implements DiagnosticsService{
	private SchemaDAO schemaDAO;
	private DiagnosticTasksHolder taskHolder;

	public DiagnosticTasksHolder getTaskHolder(){
		return taskHolder;
	}

	public void setTaskHolder(DiagnosticTasksHolder taskHolder){
		this.taskHolder = taskHolder;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	@Override
	public List<Schema> getSchemas(){
		return schemaDAO.getSchemas();
	}

	@Override
	public List<DiagnoseTaskResult> makeDiagnostic(Schema sch){
		return taskHolder.makeDiagnostic(sch);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult result) throws ACFixTaskException, ACException{
		return taskHolder.makeCorrection(result);
	}

	@Override
	public List<DiagnoseTaskResult> getInitialTaskResults(Schema schema){
		List<DiagnosticTask> tasks = taskHolder.getTasks();
		List<DiagnoseTaskResult> result = new ArrayList<DiagnoseTaskResult>();
		for (DiagnosticTask task : tasks){
			DiagnoseTaskResult r = new DiagnoseTaskResult(task.getClass().getName(), task.getTaskDescription(), false,
			        DiagnoseTaskStatus.NotChecked, null, null);
			result.add(r);
		}
		return result;
	}

	@Override
	public DiagnoseTaskResult makeDiagnostic(DiagnoseTaskResult r, Schema schema){
		Schema loadedSchema = schemaDAO.getSchema(schema.getId());
		return taskHolder.makeDiagnostic(r, loadedSchema);
	}
}
