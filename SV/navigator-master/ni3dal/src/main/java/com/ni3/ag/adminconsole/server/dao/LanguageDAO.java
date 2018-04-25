/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;

public interface LanguageDAO{

	public List<Language> getLanguages();

	public Language saveOrUpdate(Language language);

	public Language getLanguage(int languageID);

	public void deleteLanguage(Language language);

	public Language getByName(String name);

	public void saveOrUpdateAll(List<Language> languages);

}
