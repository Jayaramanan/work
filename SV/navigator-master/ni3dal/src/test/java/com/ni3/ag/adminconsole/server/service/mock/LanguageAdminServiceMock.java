/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.service.def.LanguageAdminService;
import com.ni3.ag.adminconsole.validation.ACException;

public class LanguageAdminServiceMock implements LanguageAdminService{

	public List<Language> getLanguages(){
		List<Language> languages = new ArrayList<Language>();
		Language lang1 = new Language();
		lang1.setLanguage("English");
		lang1.setProperties(new ArrayList<UserLanguageProperty>());
		lang1.getProperties().add(getProperty(lang1, "property 1", "value 1"));
		lang1.getProperties().add(getProperty(lang1, "property 1", "value 2"));
		lang1.getProperties().add(getProperty(lang1, "property 2", "value 21"));

		Language lang2 = new Language();
		lang2.setLanguage("Russian");
		lang2.setProperties(new ArrayList<UserLanguageProperty>());
		lang2.getProperties().add(getProperty(lang2, "property 1", "value 1 ru"));
		lang2.getProperties().add(getProperty(lang2, "property 1", "value 2 ru"));

		languages.add(lang1);
		languages.add(lang2);
		return languages;
	}

	private UserLanguageProperty getProperty(Language lang, String property, String value){
		UserLanguageProperty prop = new UserLanguageProperty(lang);
		prop.setProperty(property);
		prop.setValue(value);
		return prop;
	}

	public Language saveOrUpdateLanguage(Language currentLanguage){
		return null;
	}

	public Language getLanguage(int languageID){
		Language lang1 = new Language();
		lang1.setLanguage("English");
		lang1.setProperties(new ArrayList<UserLanguageProperty>());
		lang1.getProperties().add(getProperty(lang1, "property 1", "value 1"));
		lang1.getProperties().add(getProperty(lang1, "property 1", "value 2"));
		lang1.getProperties().add(getProperty(lang1, "property 2", "value 21"));
		return lang1;
	}

	public void deleteLanguage(Language language){
	}

	@Override
	public Language reloadLanguage(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateLanguages(List<Language> currentLanguage){
		// TODO Auto-generated method stub

	}

	@Override
	public Language getLanguage(User currentUser) throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

}
