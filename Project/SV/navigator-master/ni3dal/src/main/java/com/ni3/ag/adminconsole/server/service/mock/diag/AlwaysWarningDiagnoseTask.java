/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock.diag;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class AlwaysWarningDiagnoseTask implements DiagnosticTask{

	private static final String DESCRIPTION = "Fake test Always warning";

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, DiagnoseTaskStatus.Warning, null, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException{
		Logger.getLogger(this.getClass()).info("Make fake correction of Warning task");
		taskResult.setStatus(DiagnoseTaskStatus.Ok);
		return taskResult;
	}

}
