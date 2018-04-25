/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.server.dao.UserLanguagePropertyDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;

public class UserLanguageServiceImpl implements UserLanguageService{

	private Logger log = Logger.getLogger(UserLanguageServiceImpl.class);

	private UserLanguagePropertyDAO userLanguagePropertyDAO;

	public UserLanguagePropertyDAO getUserLanguagePropertyDAO(){
		return userLanguagePropertyDAO;
	}

	public void setUserLanguagePropertyDAO(UserLanguagePropertyDAO userLanguagePropertyDAO){
		this.userLanguagePropertyDAO = userLanguagePropertyDAO;
	}

	public List<UserLanguageProperty> getProperties(Language language){
		return userLanguagePropertyDAO.getPropertiesByLanguage(language);
	}

	public String getLabelById(TextID id, Language language){
		String key = id.getKey();
		UserLanguageProperty prop = userLanguagePropertyDAO.getProperty(key, language);
		if (prop == null){
			log.warn("No property with id: " + key + " found");
			return key;
		}
		return prop.getValue();
	}

	@Override
	public void refresh(){

	}

}
