/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.languageadmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;

public class LanguageTableModelTest extends ACTestCase{

	public void testGetRowCount(){
		List<Language> languages = new ArrayList<Language>();
		LanguageTableModel model = new LanguageTableModel(null);
		assertEquals(0, model.getRowCount());

		Language l1 = new Language();
		languages.add(l1);
		l1.setProperties(new ArrayList<UserLanguageProperty>());
		UserLanguageProperty ulp = new UserLanguageProperty(l1);
		ulp.setProperty("pr1");
		ulp.setValue("val1");
		l1.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals(1, model.getRowCount());

		Language l2 = new Language();
		languages.add(l2);
		l2.setProperties(new ArrayList<UserLanguageProperty>());
		ulp = new UserLanguageProperty(l2);
		ulp.setProperty("pr2");
		ulp.setValue("val2");
		l2.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals(2, model.getRowCount());

		ulp = new UserLanguageProperty(l2);
		ulp.setProperty("pr1");
		ulp.setValue("val11");
		l2.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals(2, model.getRowCount());

		Language l3 = new Language();
		languages.add(l3);
		l3.setProperties(new ArrayList<UserLanguageProperty>());
		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr3");
		ulp.setValue("val3");
		l3.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals(3, model.getRowCount());

		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr1");
		ulp.setValue("val12");
		l3.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals(3, model.getRowCount());
	}

	public void testGetValueAt(){
		List<Language> languages = new ArrayList<Language>();
		LanguageTableModel model = new LanguageTableModel(null);
		assertEquals(0, model.getRowCount());

		Language l1 = new Language();
		l1.setId(1);
		languages.add(l1);
		l1.setProperties(new ArrayList<UserLanguageProperty>());
		UserLanguageProperty ulp = new UserLanguageProperty(l1);
		ulp.setProperty("pr1");
		ulp.setValue("val11");
		l1.getProperties().add(ulp);

		Language l2 = new Language();
		l2.setId(2);
		languages.add(l2);
		l2.setProperties(new ArrayList<UserLanguageProperty>());
		ulp = new UserLanguageProperty(l2);
		ulp.setProperty("pr2");
		ulp.setValue("val22");
		l2.getProperties().add(ulp);

		ulp = new UserLanguageProperty(l2);
		ulp.setProperty("pr1");
		ulp.setValue("val12");
		l2.getProperties().add(ulp);

		Language l3 = new Language();
		l3.setId(3);
		languages.add(l3);
		l3.setProperties(new ArrayList<UserLanguageProperty>());
		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr3");
		ulp.setValue("val3");
		l3.getProperties().add(ulp);

		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr1");
		ulp.setValue("val13");
		l3.getProperties().add(ulp);

		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr2");
		ulp.setValue("val23");
		l3.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals("pr1", model.getValueAt(0, 0));
		assertEquals("val11", model.getValueAt(0, 1));
		assertEquals("val12", model.getValueAt(0, 2));
		assertEquals("val13", model.getValueAt(0, 3));

		assertEquals("pr2", model.getValueAt(1, 0));
		assertEquals(null, model.getValueAt(1, 1));
		assertEquals("val22", model.getValueAt(1, 2));
		assertEquals("val23", model.getValueAt(1, 3));

		assertEquals("pr3", model.getValueAt(2, 0));
		assertEquals(null, model.getValueAt(2, 1));
		assertEquals(null, model.getValueAt(2, 2));
		assertEquals("val3", model.getValueAt(2, 3));
	}

	public void testSetValueAt(){
		List<Language> languages = new ArrayList<Language>();
		LanguageTableModel model = new LanguageTableModel(null);

		Language l1 = new Language();
		l1.setId(1);
		languages.add(l1);
		l1.setProperties(new ArrayList<UserLanguageProperty>());
		UserLanguageProperty ulp = new UserLanguageProperty(l1);
		ulp.setProperty("pr1");
		ulp.setValue("val11");
		l1.getProperties().add(ulp);

		Language l2 = new Language();
		l2.setId(2);
		languages.add(l2);
		l2.setProperties(new ArrayList<UserLanguageProperty>());
		ulp = new UserLanguageProperty(l2);
		ulp.setProperty("pr2");
		ulp.setValue("val22");
		l2.getProperties().add(ulp);

		ulp = new UserLanguageProperty(l2);
		ulp.setProperty("pr1");
		ulp.setValue("val12");
		l2.getProperties().add(ulp);

		Language l3 = new Language();
		l3.setId(3);
		languages.add(l3);
		l3.setProperties(new ArrayList<UserLanguageProperty>());
		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr3");
		ulp.setValue("val3");
		l3.getProperties().add(ulp);

		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr1");
		ulp.setValue("val13");
		l3.getProperties().add(ulp);

		ulp = new UserLanguageProperty(l3);
		ulp.setProperty("pr2");
		ulp.setValue("val23");
		l3.getProperties().add(ulp);

		model = new LanguageTableModel(languages);
		assertEquals("pr1", model.getValueAt(0, 0));
		assertEquals("val11", model.getValueAt(0, 1));
		assertEquals("val12", model.getValueAt(0, 2));
		assertEquals("val13", model.getValueAt(0, 3));

		assertEquals("pr2", model.getValueAt(1, 0));
		assertEquals(null, model.getValueAt(1, 1));
		assertEquals("val22", model.getValueAt(1, 2));
		assertEquals("val23", model.getValueAt(1, 3));

		assertEquals("pr3", model.getValueAt(2, 0));
		assertEquals(null, model.getValueAt(2, 1));
		assertEquals(null, model.getValueAt(2, 2));
		assertEquals("val3", model.getValueAt(2, 3));

		model.setValueAt("pr1mod", 0, 0);
		assertEquals("pr1mod", model.getValueAt(0, 0));
		assertEquals("pr1mod", l1.getProperties().get(0).getProperty());
		assertEquals("pr1mod", l2.getProperties().get(1).getProperty());
		assertEquals("pr1mod", l3.getProperties().get(1).getProperty());

		model.setValueAt("pr2mod", 1, 0);
		assertEquals("pr2mod", model.getValueAt(1, 0));
		assertEquals("pr2mod", l2.getProperties().get(0).getProperty());
		assertEquals("pr2mod", l3.getProperties().get(2).getProperty());

		model.setValueAt("val21", 1, 1);
		assertEquals("pr2mod", l1.getProperties().get(1).getProperty());
		assertEquals("val21", l1.getProperties().get(1).getValue());

		model.setValueAt("val21", 2, 2);
		assertEquals("val21", l2.getProperties().get(2).getValue());
	}
}
