/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.dto.ErrorContainer;

public class LanguagePropertyValidationRuleTest extends ACTestCase{

	public void testPerformCheck(){
		Language lang = generateNormalLang();
		LanguagePropertyValidationRule rule = new LanguagePropertyValidationRule(lang);
		ErrorContainer ec = rule.performCheck();
		assertNotNull(ec);
		assertNotNull(ec.getErrors());
		assertEquals(0, ec.getErrors().size());
		
		lang = generateDuplicateItemLang();
		rule = new LanguagePropertyValidationRule(lang);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertNotNull(ec.getErrors());
		assertEquals(1, ec.getErrors().size());
		
		lang = generate2DuplicateNames();
		rule = new LanguagePropertyValidationRule(lang);
		ec = rule.performCheck();
		assertNotNull(ec);
		assertNotNull(ec.getErrors());
		assertEquals(2, ec.getErrors().size());
	}

	private Language generate2DuplicateNames(){
		Language l = generateDuplicateItemLang();
		UserLanguageProperty ulp = new UserLanguageProperty(l);
		ulp.setProperty("prop" + 1);
		l.getProperties().add(ulp);
		return l;
    }

	private Language generateDuplicateItemLang(){
		Language l = generateNormalLang();
		UserLanguageProperty ulp = new UserLanguageProperty(l);
		ulp.setProperty("prop" + 0);
		l.getProperties().add(ulp);
		return l;
    }

	private Language generateNormalLang(){
		Language l = new Language();
		l.setProperties(new ArrayList<UserLanguageProperty>());
		for(int i = 0; i < 3; i++){
			UserLanguageProperty ulp = new UserLanguageProperty(l);
			ulp.setProperty("prop" + i);
			l.getProperties().add(ulp);
		}
		return l;
    }

	public void testGetErrors(){
		Language lang = generateNormalLang();
		LanguagePropertyValidationRule rule = new LanguagePropertyValidationRule(lang);
		ErrorContainer ec = rule.performCheck();
		assertNotNull(ec);
		assertNotNull(ec.getErrors());
		assertEquals(0, ec.getErrors().size());
	}

}
