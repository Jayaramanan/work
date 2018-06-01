/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class PredefinedAttributeEditControllerTest extends ACTestCase{

	PredefinedAttributeEditController controller;
	PredefinedAttributeEditModel model;

	@Override
	protected void setUp() throws Exception{
		controller = (PredefinedAttributeEditController) ACSpringFactory.getInstance().getBean(
		        "predefinedAttributeEditController");
		model = (PredefinedAttributeEditModel) controller.getModel();
	}

	public void testFillTranslations(){
		Language language = new Language();
		language.setProperties(new ArrayList<UserLanguageProperty>());
		SessionData.getInstance().setUserLanguage(language);

		UserLanguageProperty prop1 = new UserLanguageProperty(language);
		prop1.setProperty("prop1");
		prop1.setValue("value1");

		UserLanguageProperty prop2 = new UserLanguageProperty(language);
		prop2.setProperty("prop2");
		prop2.setValue("value2");

		language.getProperties().add(prop1);
		language.getProperties().add(prop2);

		ObjectAttribute attribute = new ObjectAttribute(new ObjectDefinition());
		attribute.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());

		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setLabel("prop2");

		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setLabel("prop3");

		attribute.getPredefinedAttributes().add(pa1);
		attribute.getPredefinedAttributes().add(pa2);

		model.setCurrentAttribute(attribute);
		controller.fillTranslations();

		assertEquals("value2", model.getCurrentAttribute().getPredefinedAttributes().get(0).getTranslation());
		assertNull(model.getCurrentAttribute().getPredefinedAttributes().get(1).getTranslation());
	}
}
