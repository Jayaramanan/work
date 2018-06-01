/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class SchemaAdminControllerTest extends TestCase{

	public void testSetNextSorts(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		SchemaAdminModel model = controller.getModel();

		ObjectDefinition object = new ObjectDefinition();
		model.setCurrentObjectDefinition(object);
		ObjectAttribute newAttribute = new ObjectAttribute();
		controller.setNextSorts(newAttribute);
		assertEquals(1, newAttribute.getSort().intValue());
		assertEquals(1, newAttribute.getLabelSort().intValue());
		assertEquals(1, newAttribute.getFilterSort().intValue());
		assertEquals(1, newAttribute.getSearchSort().intValue());

		ObjectAttribute attr1 = new ObjectAttribute();
		attr1.setSort(1);
		attr1.setLabelSort(3);
		attr1.setFilterSort(5);
		attr1.setSearchSort(2);
		object.setObjectAttributes(new ArrayList<ObjectAttribute>());
		object.getObjectAttributes().add(attr1);
		newAttribute = new ObjectAttribute();
		controller.setNextSorts(newAttribute);
		assertEquals(2, newAttribute.getSort().intValue());
		assertEquals(4, newAttribute.getLabelSort().intValue());
		assertEquals(6, newAttribute.getFilterSort().intValue());
		assertEquals(3, newAttribute.getSearchSort().intValue());

		ObjectAttribute attr2 = new ObjectAttribute();
		ObjectAttribute attr3 = new ObjectAttribute();
		attr2.setSort(5);
		attr2.setLabelSort(5);
		attr2.setFilterSort(5);
		attr2.setSearchSort(5);
		attr3.setSort(3);
		attr3.setLabelSort(3);
		attr3.setFilterSort(3);
		attr3.setSearchSort(3);
		object.getObjectAttributes().add(attr2);
		object.getObjectAttributes().add(attr3);
		newAttribute = new ObjectAttribute();
		controller.setNextSorts(newAttribute);
		assertEquals(6, newAttribute.getSort().intValue());
		assertEquals(6, newAttribute.getLabelSort().intValue());
		assertEquals(6, newAttribute.getFilterSort().intValue());
		assertEquals(6, newAttribute.getSearchSort().intValue());

		model.setCurrentObjectDefinition(null);
	}
}
