/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock.diag;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class AlwaysThrowACExceptionDiagnoseTask implements DiagnosticTask{
	private static final String name = "AlwaysThrowACExceptionDiagnoseTask";

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		return new DiagnoseTaskResult(getClass().getName(), name, true, DiagnoseTaskStatus.Warning, null, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		throw new ACException(TextID.MsgEmpty, new String[] { name });
	}

	@Override
	public String getTaskDescription(){
		return name;
	}

}
