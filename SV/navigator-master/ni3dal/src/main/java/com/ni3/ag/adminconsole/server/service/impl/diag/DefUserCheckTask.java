/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DefUserCheckTask extends HibernateDaoSupport implements DiagnosticTask{

	private static final String DESCRIPTION = "Checking if default user is still active";
	private static final String ERROR_DESCRIPTION = "User `def` is active";

	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		User user = userDAO.getUser(User.DEFAULT_USER_NAME);
		String className = getClass().getName();
		if (user != null && user.getActive()){
			return new DiagnoseTaskResult(className, DESCRIPTION, true, DiagnoseTaskStatus.Warning, ERROR_DESCRIPTION, null);
		}
		return new DiagnoseTaskResult(className, DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		User user = userDAO.getUser(User.DEFAULT_USER_NAME);
		String className = getClass().getName();
		if (user != null && user.getActive()){
			user.setActive(false);
			user = userDAO.saveOrUpdate(user);
		}
		return new DiagnoseTaskResult(className, DESCRIPTION, true, DiagnoseTaskStatus.Ok, null, null);
	}

}
