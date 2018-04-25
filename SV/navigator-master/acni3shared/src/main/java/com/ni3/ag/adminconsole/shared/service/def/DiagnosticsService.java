/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public interface DiagnosticsService{

	List<Schema> getSchemas();

	List<DiagnoseTaskResult> makeDiagnostic(Schema sch);

	DiagnoseTaskResult makeFix(DiagnoseTaskResult result) throws ACFixTaskException, ACException;

	List<DiagnoseTaskResult> getInitialTaskResults(Schema schema);

	DiagnoseTaskResult makeDiagnostic(DiagnoseTaskResult r, Schema schema);

}
