/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class PredefAttributeValidationRuleTest extends TestCase{
	PredefAttributeValidationRule rule;
	PredefinedAttribute pa1;
	PredefinedAttribute pa2;
	PredefinedAttributeEditModel model;
	private List<PredefinedAttribute> predefinedAttributes;

	@Override
	protected void setUp() throws Exception{
		predefinedAttributes = new ArrayList<PredefinedAttribute>();
		pa1 = new PredefinedAttribute();
		pa1.setValue("value1");
		pa1.setLabel("label1");
		pa2 = new PredefinedAttribute();
		pa2.setValue("value2");
		pa2.setLabel("label2");

		predefinedAttributes.add(pa1);
		predefinedAttributes.add(pa2);

		ObjectAttribute oa = new ObjectAttribute();
		oa.setPredefinedAttributes(predefinedAttributes);

		rule = new PredefAttributeValidationRule();

		model = new PredefinedAttributeEditModel();
		model.setCurrentAttribute(oa);
	}

	public void testNotNull(){
		rule.performCheck(null);
		assertNotNull(rule.getErrorEntries());
	}

	public void testPerformCheckSuccess(){
		rule.performCheck(null);
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckValueEmpty(){
		pa2.setValue(null);
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());

		pa2.setValue("");
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}

	public void testPerformCheckLabelEmpty(){
		pa2.setLabel(null);
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());

		pa2.setLabel("");
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}

	public void testContainsDuplicateValueOrLabelSuccess(){
		assertFalse(rule.containsDuplicateValueOrLabel(predefinedAttributes));
	}

	public void testContainsDuplicateValueOrLabelFail1(){
		pa2.setValue("value1");
		assertFalse(rule.containsDuplicateValueOrLabel(predefinedAttributes));

		pa2.setLabel("label1");
		assertTrue(rule.containsDuplicateValueOrLabel(predefinedAttributes));
	}

	public void testContainsDuplicateValueOrLabelFail2(){
		pa2.setLabel("label1");
		assertFalse(rule.containsDuplicateValueOrLabel(predefinedAttributes));

		pa2.setValue("value1");
		assertTrue(rule.containsDuplicateValueOrLabel(predefinedAttributes));

	}

}
