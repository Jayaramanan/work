/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.calendar;

import java.util.Locale;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Language;

public class LocaleParser{
	public static Locale getLocaleByUserLanguage(){
		Locale[] locales = Locale.getAvailableLocales();
		Language language = SessionData.getInstance().getUserLanguage();
		Locale defaultLocale = Locale.ENGLISH;
		for (Locale locale : locales){
			if (language.getLanguage().startsWith(locale.getDisplayLanguage(defaultLocale))){
				return locale;
			}
		}
		return defaultLocale;
	}
}
