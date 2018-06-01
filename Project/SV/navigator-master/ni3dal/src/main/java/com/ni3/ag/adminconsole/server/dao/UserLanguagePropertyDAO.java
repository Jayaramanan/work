/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;

public interface UserLanguagePropertyDAO{
	public List<UserLanguageProperty> getPropertiesByName(String name);

	public UserLanguageProperty getProperty(String name, Language language);

	public void deleteAll(List<UserLanguageProperty> properties);

	public List<UserLanguageProperty> getPropertiesByLanguage(Language language);
}
