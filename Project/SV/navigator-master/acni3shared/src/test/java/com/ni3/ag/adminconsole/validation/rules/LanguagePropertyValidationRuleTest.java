/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class LanguagePropertyValidationRuleTest extends TestCase{

	ACValidationRule rule;
	LanguageModel model;

	public void setUp(){
		rule = new LanguagePropertyValidationRule();
		model = new LanguageModel();
	}

	public void testNotNull(){
		rule.performCheck(null);
		assertNotNull(rule.getErrorEntries());
	}

	public void testPerformCheck(){
		Language lang = generateNormalLang();
		model.setCurrentLanguage(lang);
		List<Language> langs = new ArrayList<Language>();
		langs.add(lang);
		model.setLanguages(langs);
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(0, errors.size());

		lang = generateDuplicateItemLang();
		model.setCurrentLanguage(lang);
		langs.clear();
		langs.add(lang);
		assertFalse(rule.performCheck(model));
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(1, errors.size());

		lang = generate2DuplicateNames();
		model.setCurrentLanguage(lang);
		langs.clear();
		langs.add(lang);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(2, errors.size());
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
		for (int i = 0; i < 3; i++){
			UserLanguageProperty ulp = new UserLanguageProperty(l);
			ulp.setProperty("prop" + i);
			l.getProperties().add(ulp);
		}
		return l;
	}

	public void testGetErrors(){
		Language lang = generateNormalLang();
		model.setCurrentLanguage(lang);
		List<Language> langs = new ArrayList<Language>();
		langs.add(lang);
		model.setLanguages(langs);
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertEquals(0, errors.size());
	}

}
