/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class SchemaAdminServiceImplTest extends TestCase{
	SchemaAdminServiceImpl impl;

	@Override
	protected void setUp() throws Exception{
		impl = new SchemaAdminServiceImpl();
	}

	public void testGetNewObjectAttribute(){
		ObjectAttribute origAttr = new ObjectAttribute();
		origAttr.setName("name");

		ObjectDefinition newObject = new ObjectDefinition();
		ObjectAttribute attr1 = new ObjectAttribute();
		attr1.setName("name1");
		ObjectAttribute attr2 = new ObjectAttribute();
		attr2.setName("name2");
		newObject.setObjectAttributes(new ArrayList<ObjectAttribute>());
		newObject.getObjectAttributes().add(attr1);
		newObject.getObjectAttributes().add(attr2);

		SchemaAdminServiceImpl impl = new SchemaAdminServiceImpl();
		ObjectAttribute newAttribute = impl.getNewObjectAttribute(origAttr, newObject);
		assertNull(newAttribute);

		attr2.setName("name");
		newAttribute = impl.getNewObjectAttribute(origAttr, newObject);
		assertSame(attr2, newAttribute);
	}

}
