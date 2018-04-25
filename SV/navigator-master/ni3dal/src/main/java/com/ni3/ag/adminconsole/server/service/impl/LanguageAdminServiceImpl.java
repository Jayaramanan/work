/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.LanguageDAO;
import com.ni3.ag.adminconsole.server.dao.SettingDAO;
import com.ni3.ag.adminconsole.server.dao.UserLanguagePropertyDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;
import com.ni3.ag.adminconsole.validation.ACException;

public class LanguageAdminServiceImpl implements LanguageAdminService{
	private LanguageDAO languageDAO;
	private SettingDAO settingDAO;
	private UserLanguagePropertyDAO userLanguagePropertyDAO;

	public void setSettingDAO(SettingDAO settingDAO){
		this.settingDAO = settingDAO;
	}

	public LanguageDAO getLanguageDAO(){
		return languageDAO;
	}

	public void setLanguageDAO(LanguageDAO languageDAO){
		this.languageDAO = languageDAO;
	}

	public List<Language> getLanguages(){
		return getLanguageDAO().getLanguages();
	}

	public UserLanguagePropertyDAO getUserLanguagePropertyDAO(){
		return userLanguagePropertyDAO;
	}

	public void setUserLanguagePropertyDAO(UserLanguagePropertyDAO userLanguagePropertyDAO){
		this.userLanguagePropertyDAO = userLanguagePropertyDAO;
	}

	public Language getLanguage(int languageID) throws ACException{
		Language language = getLanguageDAO().getLanguage(languageID);
		if (language == null){
			throw new ACException(TextID.MsgLanguageNotExist, new String[] { String.valueOf(languageID) });
		}
		return language;
	}

	public void deleteLanguage(Language language){
		getLanguageDAO().deleteLanguage(language);
	}

	@Override
	public Language reloadLanguage(Integer id){
		Language language = languageDAO.getLanguage(id);
		Hibernate.initialize(language.getProperties());
		return language;
	}

	@Override
	public void saveOrUpdateLanguages(List<Language> languages){
		languageDAO.saveOrUpdateAll(languages);
	}

	@Override
	public Language saveOrUpdateLanguage(Language language){
		return languageDAO.saveOrUpdate(language);
	}

	public Language getLanguage(User currentUser) throws ACException{
		String langId = settingDAO.getSetting(currentUser.getId(), Setting.APPLET_SECTION, Setting.LANGUAGE_PROPERTY);
		if (langId == null)
			throw new ACException(TextID.MsgNoLanguageSettingForUser, new String[] { String.valueOf(currentUser
			        .getUserName()) });
		return getLanguage(Integer.parseInt(langId));
	}
}
