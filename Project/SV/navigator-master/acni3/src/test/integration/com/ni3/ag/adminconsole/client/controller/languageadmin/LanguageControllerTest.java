/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.model.LanguageModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageTableModel;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;

public class LanguageControllerTest extends ACTestCase{

	LanguageView view;
	LanguageModel model;
	LanguageController controller;
	Language lang;
	UserLanguageProperty prop1;
	UserLanguageProperty prop2;
	UserLanguageProperty prop3;

	@Override
	protected void setUp() throws Exception{
		controller = (LanguageController) ACSpringFactory.getInstance().getBean("languageController");
		view = controller.getView();
		view.initializeComponents();
		model = controller.getModel();
		lang = new Language();
		lang.setProperties(new ArrayList<UserLanguageProperty>());
		prop1 = getProperty(lang, "property 1", "value 1");
		prop2 = getProperty(lang, "property 1", "value 2");
		prop3 = getProperty(lang, "property 2", "value 21");
		lang.getProperties().add(prop1);
		lang.getProperties().add(prop2);
		lang.getProperties().add(prop3);

		model.setCurrentLanguage(lang);
		model.setDeletedProperties(new ArrayList<UserLanguageProperty>());
	}

	public void testAddNewProperty(){
		view.setTableModel(new LanguageTableModel(lang.getProperties()));
		controller.addNewProperty();
		assertEquals(4, model.getCurrentLanguage().getProperties().size());
		assertSame(lang, model.getCurrentLanguage().getProperties().get(3).getLanguage());
	}

	public void testDeleteExistingProperty(){
		view.setTableModel(new LanguageTableModel(lang.getProperties()));
		prop2.setNew(false);
		controller.deleteProperty(prop2);
		assertEquals(2, model.getCurrentLanguage().getProperties().size());
		assertSame(prop1, model.getCurrentLanguage().getProperties().get(0));
		assertSame(prop3, model.getCurrentLanguage().getProperties().get(1));
	}

	public void testDeleteNewProperty(){
		view.setTableModel(new LanguageTableModel(lang.getProperties()));
		prop2.setNew(true);
		controller.deleteProperty(prop2);
		assertEquals(2, model.getCurrentLanguage().getProperties().size());
		assertSame(prop1, model.getCurrentLanguage().getProperties().get(0));
		assertSame(prop3, model.getCurrentLanguage().getProperties().get(1));
		assertEquals(0, model.getDeletedProperties().size());
	}

	private UserLanguageProperty getProperty(Language lang, String property, String value){
		UserLanguageProperty prop = new UserLanguageProperty(lang);
		prop.setProperty(property);
		prop.setValue(value);
		return prop;
	}
}
