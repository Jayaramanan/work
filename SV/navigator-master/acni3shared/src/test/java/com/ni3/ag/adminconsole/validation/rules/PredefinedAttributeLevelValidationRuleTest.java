/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

import junit.framework.TestCase;

public class PredefinedAttributeLevelValidationRuleTest extends TestCase{

	private PredefinedAttribute value1, value2;
	private PredefinedAttributeLevelValidationRule rule;

	public void setUp(){
		ObjectAttribute oa = new ObjectAttribute();
		value1 = new PredefinedAttribute();
		value1.setId(1);
		value1.setObjectAttribute(oa);
		value2 = new PredefinedAttribute();
		value2.setId(2);
		value2.setObjectAttribute(oa);
		rule = new PredefinedAttributeLevelValidationRule();
	}

	public void testCheckForSameLevelInheritance(){
		value1.setParent(value2);
		assertFalse(rule.checkForSameLevelInheritance(value1));
		assertTrue(rule.checkForSameLevelInheritance(value2));
	}

	public void testCheckForLoop(){
		value1.setParent(value2);
		value2.setParent(value1);
		assertNotNull(rule.checkForLoop(value1));
		assertNotNull(rule.checkForLoop(value2));

		value1.setParent(null);
		assertNull(rule.checkForLoop(value1));
		assertNull(rule.checkForLoop(value2));

		value1.setParent(value1);
		assertNotNull(rule.checkForLoop(value1));
	}
}
