/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class BooleanAttributeValidationRuleTest extends TestCase{
	private BooleanAttributeValidationRule rule;
	private PredefinedAttributeEditModel model;
	private ObjectAttribute attr;

	@Override
	protected void setUp() throws Exception{
		rule = new BooleanAttributeValidationRule();
		model = new PredefinedAttributeEditModel();
		attr = new ObjectAttribute();
		attr.setDataType(DataType.BOOL);
		attr.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());

		model.setCurrentAttribute(attr);
	}

	public void testPerformCheckSuccess(){
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());

		attr.getPredefinedAttributes().add(new PredefinedAttribute());
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());

		attr.getPredefinedAttributes().add(new PredefinedAttribute());
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());

		attr.setDataType(DataType.TEXT);
		attr.getPredefinedAttributes().add(new PredefinedAttribute());
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckFail(){
		attr.getPredefinedAttributes().add(new PredefinedAttribute());
		attr.getPredefinedAttributes().add(new PredefinedAttribute());
		attr.getPredefinedAttributes().add(new PredefinedAttribute());
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}
}
