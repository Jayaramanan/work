/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.shared.service.def.NavigatorLicenseService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserLicenseCheckTask implements DiagnosticTask{

	private final static String DESCRIPTION = "Checking that every user has assigned license";
	private final static String TOOLTIP = "No licenses (base module) assigned to user(s): ";
	private final static String ACTION_DESCRIPTION = "Go to Licenses->Navigator tab and assign `Base` module for users: ";

	private UserDAO userDAO;
	private NavigatorLicenseService licenseService;

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setLicenseService(NavigatorLicenseService licenseService){
		this.licenseService = licenseService;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		licenseService.checkExpiringLicenseModules();

		List<User> users = userDAO.getUsers();
		return getTaskResult(users);
	}

	DiagnoseTaskResult getTaskResult(List<User> users){
		String uNames = "";
		String baseModule = NavigatorModule.BaseModule.getValue();
		for (User user : users){
			if (!user.getActive())
				continue;
			boolean found = false;
			for (UserEdition ed : user.getUserEditions()){
				if (baseModule.equals(ed.getEdition())){
					found = true;
					break;
				}
			}
			if (!found){
				uNames += user.getUserName();
				uNames += ", ";
			}
		}

		DiagnoseTaskStatus status = DiagnoseTaskStatus.Ok;
		String tooltip = null;
		String action = null;
		if (!uNames.isEmpty()){
			if (uNames.endsWith(", ")){
				uNames = uNames.substring(0, uNames.length() - 2);
			}
			status = DiagnoseTaskStatus.Warning;
			tooltip = TOOLTIP + uNames;
			action = ACTION_DESCRIPTION + uNames;
		}

		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, status, tooltip, action);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}
}
