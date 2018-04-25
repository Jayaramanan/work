/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserTablePKCheckTask implements DiagnosticTask{
	private final static String DESCRIPTION = "Checking user table primary key constraints";
	private final static String TOOLTIP = "No PK constraint on `id` column found in tables: ";
	private final static String ACTION_DESCRIPTION = "Contact system administrator";

	private SchemaDAO schemaDAO;
	private UserTableStructureService userTableStructureService;

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setUserTableStructureService(UserTableStructureService userTableStructureService){
		this.userTableStructureService = userTableStructureService;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<String> tablesWithoutPK = new ArrayList<String>();
		Schema schema = schemaDAO.getSchema(sch.getId());
		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		for (int i = 0; i < objects.size(); i++){
			ObjectDefinition obj = objects.get(i);
			String tableName = obj.getTableName().toLowerCase();
			if (!userTableStructureService.isExistPKConstraint(tableName))
				tablesWithoutPK.add(tableName);
			if (obj.hasContextAttributes()){
				tableName += ObjectAttribute.CONTEXT_TABLE_SUFFIX;
				if (!userTableStructureService.isExistPKConstraint(tableName))
					tablesWithoutPK.add(tableName);
			}
		}

		DiagnoseTaskResult result = getTaskResult(tablesWithoutPK);
		return result;
	}

	private DiagnoseTaskResult getTaskResult(List<String> tablesWithoutPK){
		DiagnoseTaskResult result = null;
		if (!tablesWithoutPK.isEmpty()){
			String tooltip = TOOLTIP;
			for (int i = 0; i < tablesWithoutPK.size(); i++){
				String table = tablesWithoutPK.get(i);
				if (i > 0){
					tooltip += ", ";
				}
				tooltip += table.toLowerCase();
			}
			result = new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Warning, tooltip,
			        ACTION_DESCRIPTION);
		} else{
			result = new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
		}
		return result;
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}
}
