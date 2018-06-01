/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public interface DiagnosticTasksHolder{
	List<DiagnoseTaskResult> makeDiagnostic(Schema sch);

	DiagnoseTaskResult makeCorrection(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException;

	List<DiagnosticTask> getTasks();

	DiagnoseTaskResult makeDiagnostic(DiagnoseTaskResult taskResult, Schema sch);
}
