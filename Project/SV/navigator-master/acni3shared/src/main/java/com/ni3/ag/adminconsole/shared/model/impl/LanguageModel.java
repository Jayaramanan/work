/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class LanguageModel extends AbstractModel{
	private Map<DatabaseInstance, List<Language>> languageMap = new HashMap<DatabaseInstance, List<Language>>();
	private Language currentLanguage;

	public List<Language> getLanguages(){
		return languageMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Language>> getLanguageMap(){
		return languageMap;
	}

	public void setLanguages(List<Language> languages){
		languageMap.put(currentDatabaseInstance, languages);
	}

	public Language getCurrentLanguage(){
		return currentLanguage;
	}

	public void setCurrentLanguage(Language currentLanguage){
		this.currentLanguage = currentLanguage;
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return languageMap.containsKey(instance);
	}
}
