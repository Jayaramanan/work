/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface LanguageAdminService{
	public List<Language> getLanguages();

	public void saveOrUpdateLanguages(List<Language> currentLanguage);

	public Language getLanguage(int languageID) throws ACException;

	public void deleteLanguage(Language language);

	public Language reloadLanguage(Integer id);

	public Language saveOrUpdateLanguage(Language language);

	public Language getLanguage(User currentUser) throws ACException;
}
