/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class NavigatorCacheValidityTask implements DiagnosticTask{

	private final static String DESCRIPTION = "Checking Navigator's cache validity";
	private final static String TOOLTIP = "Navigator's cache needs to be refreshed";

	private SchemaAdminService schemaAdminService;

	public void setSchemaAdminService(SchemaAdminService schemaAdminService){
		this.schemaAdminService = schemaAdminService;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		boolean required = schemaAdminService.isAnyInvalidationRequired();
		DiagnoseTaskStatus status = required ? DiagnoseTaskStatus.Warning : DiagnoseTaskStatus.Ok;
		String tooltip = required ? TOOLTIP : null;
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, status, tooltip, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		final Object[] params = taskResult.getFixParams();
		User user = (User) params[0];
		String navHost = (String) params[1];

		schemaAdminService.updateCache(navHost, user, null);
		schemaAdminService.resetAnyInvalidationRequired();

		taskResult.setStatus(DiagnoseTaskStatus.Ok);
		return taskResult;
	}
}
