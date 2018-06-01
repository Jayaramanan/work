/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class AttributeNameValidationRuleTest extends TestCase{
	public static String[] validNames = { "attr1", "attr2" };

	public void testRule(){
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute oa = new ObjectAttribute();
		od.getObjectAttributes().add(oa);
		SchemaAdminModel model = new SchemaAdminModel();
		model.setCurrentObjectDefinition(od);
		AttributeNameValidationRule rule = new AttributeNameValidationRule();
		for (String s : validNames){
			oa.setName(s);
			assertTrue(rule.performCheck(model));
		}
		for (String s : AttributeNameValidationRule.restrictedNames){
			oa.setName(s);
			assertFalse(rule.performCheck(model));
		}
	}
}
