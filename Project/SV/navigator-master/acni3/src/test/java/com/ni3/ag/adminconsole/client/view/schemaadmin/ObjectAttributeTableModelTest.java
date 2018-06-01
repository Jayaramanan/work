/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;

public class ObjectAttributeTableModelTest extends ACTestCase{

	public void testGetSelectedAttribute(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		ObjectAttribute attr2 = new ObjectAttribute(new ObjectDefinition());
		ObjectAttribute attr3 = new ObjectAttribute(new ObjectDefinition());

		List<ObjectAttribute> objectAttributes = new ArrayList<ObjectAttribute>();
		objectAttributes.add(attr1);
		objectAttributes.add(attr2);
		objectAttributes.add(attr3);

		ObjectAttributeTableModel model = new ObjectAttributeTableModel(objectAttributes, null);
		assertSame(attr1, model.getSelectedAttribute(0));
		assertSame(attr2, model.getSelectedAttribute(1));
		assertSame(attr3, model.getSelectedAttribute(2));

		assertNull(model.getSelectedAttribute(3));
		assertNull(model.getSelectedAttribute(-1));
	}

	public void testIsMandatoryAttributeNotEdgeObject(){
		ObjectAttributeTableModel model = new ObjectAttributeTableModel(null, null);
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.NODE);
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("InPath");
		assertFalse(model.isMandatoryAttribute(oa));
	}

	public void testIsMandatoryAttributeEdgeObjectNotMandatory(){
		ObjectAttributeTableModel model = new ObjectAttributeTableModel(null, null);
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.EDGE);
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("Test");
		assertFalse(model.isMandatoryAttribute(oa));
	}

	public void testIsMandatoryAttribute(){
		ObjectAttributeTableModel model = new ObjectAttributeTableModel(null, true, null);
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectType(ObjectType.EDGE);
		ObjectAttribute oa = new ObjectAttribute(od);
		oa.setName("Cmnt");
		assertTrue(model.isMandatoryAttribute(oa));

		oa.setName("Directed");
		assertTrue(model.isMandatoryAttribute(oa));

		oa.setName("Strength");
		assertTrue(model.isMandatoryAttribute(oa));

		oa.setName("InPath");
		assertTrue(model.isMandatoryAttribute(oa));

		oa.setName("ConnectionType");
		assertTrue(model.isMandatoryAttribute(oa));
	}

}
