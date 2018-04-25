package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class OfflineSetupValidationTask implements DiagnosticTask{

	private static final String DESCRIPTION = "Check if all users with offline clients have assigned mandatory modules";
	private static final String ERROR = "Following users don't have all mandatory modules assigned:";
	private static final String[] MANDATORY_MODULES = { Module.CLIENT_JAR, Module.START_SCRIPTS, Module.JETTY,
	        Module.JETTY_CONFIG, Module.JRE, Module.CLIENT_JAR, Module.RDBMS_ENGINE, Module.SERVER_SIDE_WAR,
	        Module.SERVER_CACHE, Module.DB_DATA, Module.DB_DUMP };
	private static final String ACTION_DESCRIPTION = "Go Offline client->Versions tab and assign modules for all users";

	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<User> users = userDAO.getUsers();
		List<User> invalidUsers = new ArrayList<User>();
		for (User u : users){
			if (!u.getHasOfflineClient())
				continue;
			if (!userHasMandatoryModules(u)){
				invalidUsers.add(u);
			}
		}
		if (!invalidUsers.isEmpty())
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
			        makeErrorString(invalidUsers), ACTION_DESCRIPTION);
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	private String makeErrorString(List<User> invalidUsers){
		StringBuilder sb = new StringBuilder();
		sb.append(ERROR);
		boolean first = true;
		for (User u : invalidUsers){
			if (!first)
				sb.append(",");
			else
				first = false;
			sb.append(u.getUserName());
		}
		return sb.toString();
	}

	private boolean userHasMandatoryModules(User u){
		List<ModuleUser> userModules = u.getUserModules();
		for (String s : MANDATORY_MODULES){
			boolean found = false;
			for (ModuleUser mu : userModules){
				if (s.equals(mu.getTarget().getName())){
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}
}
