/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.test;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Language;

import junit.framework.TestCase;

public class ACTestCase extends TestCase{
	static{
		Language language = new Language();
		language.setId(1);
		language.setLanguage("English");
		SessionData.getInstance().setUserLanguage(language);
	}

	public void testEmpty(){
	}
}
