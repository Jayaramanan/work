/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.language.TextID;

public interface UserLanguageService{
	List<UserLanguageProperty> getProperties(Language language);

	String getLabelById(TextID id, Language language);

	void refresh();
}
