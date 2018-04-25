/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ModuleDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

// action : check if at least one module of each type is uploaded to the system (exception: DB dump is optional
// module, do not check it)
public class ModuleAmountCheckTask implements DiagnosticTask{
	private static final String MY_DESCRIPTION = "Checking modules for offline client";
	private static final String TOOLTIP = "Modules: `{1}` are not present in the system.";
	private static final String ACTION_DESCRIPTION = "Go to Offline client->Versions tab and upload missing modules: ";

	private ModuleDAO moduleDAO;

	public void setModuleDAO(ModuleDAO moduleDAO){
		this.moduleDAO = moduleDAO;
	}

	@Override
	public String getTaskDescription(){
		return MY_DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<String> systemModules = moduleDAO.getModuleNames();
		String missingModules = "";
		DiagnoseTaskStatus status = null;
		if (systemModules == null){
			for (String module : Module.NAMES){
				if (!missingModules.isEmpty())
					missingModules += ", ";
				missingModules += module;
			}

		} else
			for (String module : Module.NAMES){
				if (Module.DB_DUMP.equals(module))
					continue;
				if (!systemModules.contains(module)){
					if (!missingModules.isEmpty())
						missingModules += ", ";
					missingModules += module;
					if (!isGisModule(module))
						status = DiagnoseTaskStatus.Error;
				}
			}
		if (missingModules.isEmpty())
			status = DiagnoseTaskStatus.Ok;
		else if (status == null)
			status = DiagnoseTaskStatus.Warning;

		boolean error = status != DiagnoseTaskStatus.Ok;
		String toolTip = error ? TOOLTIP.replaceAll("\\{1\\}", missingModules) : null;
		String action = error ? ACTION_DESCRIPTION + missingModules : null;

		return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, status, toolTip, action);
	}

	private boolean isGisModule(String module){
		return Module.GIS_CONFIG.equals(module) || Module.GIS_EXECUTABLE.equals(module) || Module.MAPS.equals(module);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

}
