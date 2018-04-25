/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;

public class UserLanguageServiceMock implements UserLanguageService{
	Logger log = Logger.getLogger(UserLanguageServiceMock.class);

	public String getLabelById(TextID id, Language language){
		log.debug("getLabelById mock value: " + id.toString());
		return id.toString();
	}

	public List<UserLanguageProperty> getProperties(Language language){
		return null;
	}

}
