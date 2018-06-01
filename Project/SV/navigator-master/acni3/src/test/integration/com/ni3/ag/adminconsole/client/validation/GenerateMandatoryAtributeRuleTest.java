/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;

public class GenerateMandatoryAtributeRuleTest extends ACTestCase{
	ObjectDefinition object;
	GenerateMandatoryAttributeRule rule;

	@Override
	protected void setUp() throws Exception{
		object = new ObjectDefinition();
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.setObjectType(new ObjectType(4));
		rule = new GenerateMandatoryAttributeRule(object);
	}

	public void testCreateBooleanPredefinedAttributes(){
		ObjectAttribute attr = new ObjectAttribute();
		rule.createBooleanPredefinedAttributes(attr);
		assertEquals(2, attr.getPredefinedAttributes().size());
	}

	public void testCreateAttribute(){
		rule.createAttribute("Cmnt", object);
		assertEquals(1, object.getObjectAttributes().size());
		assertEquals("Cmnt", object.getObjectAttributes().get(0).getName());
	}

	public void testCreateAttributeInPath(){
		rule.createAttribute("InPath", object);
		assertEquals(1, object.getObjectAttributes().size());
		assertEquals("InPath", object.getObjectAttributes().get(0).getName());
		assertEquals(2, object.getObjectAttributes().get(0).getPredefinedAttributes().size());
	}

	public void testPerformCheck(){
		rule.performCheck();
		assertEquals(6, object.getObjectAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(1).getPredefinedAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(3).getPredefinedAttributes().size());
	}

	public void testPerformCheckExistSomeAttr(){
		ObjectAttribute inPathAttr = new ObjectAttribute();
		inPathAttr.setName("InPath");
		ObjectAttribute connTypeAttr = new ObjectAttribute();
		connTypeAttr.setName("ConnectionType");
		object.getObjectAttributes().add(inPathAttr);
		object.getObjectAttributes().add(connTypeAttr);
		rule.performCheck();
		assertEquals(6, object.getObjectAttributes().size());
		assertEquals(2, object.getObjectAttributes().get(3).getPredefinedAttributes().size());
	}

	public void testGetDataType(){

		DataType dt = rule.getDataType("Cmnt");
		assertEquals(1, dt.getId().intValue());

		dt = rule.getDataType("Directed");
		assertEquals(2, dt.getId().intValue());

		dt = rule.getDataType("Strength");
		assertEquals(2, dt.getId().intValue());

		dt = rule.getDataType("InPath");
		assertEquals(2, dt.getId().intValue());

		dt = rule.getDataType("ConnectionType");
		assertEquals(2, dt.getId().intValue());

	}
}
