/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserTableNameCheckTask implements DiagnosticTask{

	private final static String DESCRIPTION = "Checking user table names";
	private final static String ACTION_DESCRIPTION = "Go to Schemas tab, select object `%` in the tree and press update";
	private final static String USER_TABLE_PREFIX = "usr_";

	private SchemaDAO schemaDAO;

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		Schema schema = schemaDAO.getSchema(sch.getId());
		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		for (int i = 0; i < objects.size(); i++){
			ObjectDefinition obj = objects.get(i);
			String tableName = obj.getTableName().toLowerCase();
			if (!tableName.startsWith(USER_TABLE_PREFIX)){
				String action = ACTION_DESCRIPTION.replaceAll("%", obj.getName());
				return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error, null,
				        action);
			}
		}
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

}
