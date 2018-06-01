/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.LanguageDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class PasswordResetTextCheckTask implements DiagnosticTask{
	private static final String DESCRIPTION = "Checking if translation is set for password reset email text";
	private static final String ERROR_STRING_NO_PROPERTY = "Property `MsgEMailPasswordResetText` does not exist";
	private static final String ACTION_ADD_PROPERTY = "Go to Languages tab and add property `MsgEMailPasswordResetText` with translation for the language `%`";
	private UserDAO userDAO;
	private LanguageDAO languageDAO;
	private UserLanguageService userLangService;

	public void setLanguageDAO(LanguageDAO languageDAO){
		this.languageDAO = languageDAO;
	}

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public UserLanguageService getUserLangService(){
		return userLangService;
	}

	public void setUserLangService(UserLanguageService userLangService){
		this.userLangService = userLangService;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		DiagnoseTaskResult result = new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok,
		        null, null);
		List<User> users = userDAO.getUsers();
		if (users == null || users.isEmpty())
			return result;
		List<Language> languages = languageDAO.getLanguages();
		for (Language l : languages){
			String s = userLangService.getLabelById(TextID.MsgEMailPasswordResetText, l);
			if (s == null || TextID.MsgEMailPasswordResetText.getKey().equals(s)){
				result.setErrorDescription(ERROR_STRING_NO_PROPERTY);
				result.setActionDescription(ACTION_ADD_PROPERTY.replaceAll("%", l.getLanguage()));
				result.setStatus(DiagnoseTaskStatus.Warning);
				return result;
			}
		}
		return result;
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
