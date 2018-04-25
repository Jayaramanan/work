/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jobs.data;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DataValidator{
	private SchemaDAO schemaDAO;
	private List<DiagnosticTask> tasks;

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setTasks(List<DiagnosticTask> tasks){
		this.tasks = tasks;
	}

	public DiagnoseTaskResult validate(){
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema schema : schemas){
			for (DiagnosticTask task : tasks){
				DiagnoseTaskResult result = task.makeDiagnose(schema);
				if (!DiagnoseTaskStatus.Ok.equals(result.getStatus())){
					return result;
				}
			}
		}
		return new DiagnoseTaskResult(null, null, false, DiagnoseTaskStatus.Ok, null, null);
	}
}
