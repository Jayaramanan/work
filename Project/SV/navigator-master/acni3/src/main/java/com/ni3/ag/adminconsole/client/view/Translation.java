/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view;

import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;

public class Translation{
	public static String get(TextID id){
		ACSpringFactory serviceFactory = ACSpringFactory.getInstance();
		UserLanguageService userLanguageService = serviceFactory.getUserLanguageService();
		SessionData sessionData = SessionData.getInstance();
		return userLanguageService.getLabelById(id, sessionData.getUserLanguage());
	}

	public static String get(TextID id, Object[] params){
		return getParsedMessage(get(id), params);
	}

	public static String getParsedMessage(String text, Object[] params){
		if (text != null && params != null && params.length > 0){
			for (int i = 0; i < params.length; i++){
				text = text.replace("{" + (i + 1) + "}", (params[i] == null ? "" : params[i].toString()));
			}
		}
		return text;
	}

	public static String get(TextID id, List<String> params){
		return getParsedMessage(get(id), params);
	}

	public static String getParsedMessage(String text, List<String> params){
		if (text != null && params != null && !params.isEmpty()){
			for (int i = 0; i < params.size(); i++){
				String prm = params.get(i);
				text = text.replace("{" + (i + 1) + "}", (prm == null ? "" : prm));
			}
		}
		return text;
	}
}
