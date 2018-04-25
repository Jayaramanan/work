/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;

public class CachedUserLanguageServiceImpl implements UserLanguageService{
	private Logger log = Logger.getLogger(CachedUserLanguageServiceImpl.class);

	private UserLanguageService realService;
	private HashMap<Language, List<UserLanguageProperty>> properties = new HashMap<Language, List<UserLanguageProperty>>();

	public UserLanguageService getRealService(){
		return realService;
	}

	public void setRealService(UserLanguageService realService){
		this.realService = realService;
	}

	public List<UserLanguageProperty> getProperties(Language lang){
		if (lang == null)
			return new ArrayList<UserLanguageProperty>();
		if (properties.containsKey(lang))
			return properties.get(lang);
		else{
			List<UserLanguageProperty> props = realService.getProperties(lang);
			properties.put(lang, props);
			return props;
		}
	}

	public String getLabelById(TextID id, Language language){
		if (language == null){
			language = SessionData.getInstance().getUserLanguage();
		}
		for (UserLanguageProperty ulp : getProperties(language)){
			if (ulp.getProperty().equals(id.getKey()))
				return ulp.getValue();
		}
		log.warn("No property with id: " + id + " found");
		return id.toString();
	}

	@Override
	public void refresh(){
		properties.clear();
	}
}
