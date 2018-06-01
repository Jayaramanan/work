/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.validation;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;

public class AttributeDeleteValidationRuleTest extends ACTestCase{
	public void testPerformtCheckNotEdgeObject(){

		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(new ObjectType(2));
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("InPath");
		AttributeDeleteValidationRule rule = new AttributeDeleteValidationRule(oa);
		assertNull(rule.performCheck());
	}

	public void testPerformCheckNotSystemAttribute(){
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(new ObjectType(4));
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("Test");
		AttributeDeleteValidationRule rule = new AttributeDeleteValidationRule(oa);
		assertNull(rule.performCheck());
	}

	public void testPerformCheck(){
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(new ObjectType(4));
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("Cmnt");
		AttributeDeleteValidationRule rule = new AttributeDeleteValidationRule(oa);
		assertNotNull(rule.performCheck());

		oa.setName("Directed");
		rule = new AttributeDeleteValidationRule(oa);
		assertNotNull(rule.performCheck());

		oa.setName("Strength");
		rule = new AttributeDeleteValidationRule(oa);
		assertNotNull(rule.performCheck());

		oa.setName("InPath");
		rule = new AttributeDeleteValidationRule(oa);
		assertNotNull(rule.performCheck());

		oa.setName("ConnectionType");
		rule = new AttributeDeleteValidationRule(oa);
		assertNotNull(rule.performCheck());
	}
}
