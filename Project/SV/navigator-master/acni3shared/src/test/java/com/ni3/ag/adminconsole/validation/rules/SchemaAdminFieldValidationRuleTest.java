/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import junit.framework.TestCase;

public class SchemaAdminFieldValidationRuleTest extends TestCase{
	private SchemaAdminFieldValidationRule rule;
	private SchemaAdminModel model;
	private Schema schema;
	private ObjectDefinition object1;

	@Override
	protected void setUp() throws Exception{
		rule = new SchemaAdminFieldValidationRule();
		model = new SchemaAdminModel();
		schema = new Schema();
		schema.setName("schema");
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());

		object1 = new ObjectDefinition();
		object1.setName("object1");
		object1.setSchema(schema);
		object1.setSort(1);
		schema.getObjectDefinitions().add(object1);

		ObjectDefinition object2 = new ObjectDefinition();
		object2.setName("object2");
		object2.setSchema(schema);
		object2.setSort(2);
		schema.getObjectDefinitions().add(object2);
	}

	public void testNotNull(){
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}

	public void testPerformObjectNameDuplicate(){
		model.setCurrentObjectDefinition(object1);

		object1.setName("object2");
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());

		object1.setName("o -b--j e c-t-2-");
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformAttributeNameDuplicate(){
		model.setCurrentObjectDefinition(object1);

		object1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute attr1 = new ObjectAttribute();
		attr1.setName("attr1");
		attr1.setLabel("label1");
		attr1.setDataType(DataType.TEXT);
		object1.getObjectAttributes().add(attr1);

		ObjectAttribute attr2 = new ObjectAttribute();
		attr2.setName("attr1");
		attr2.setLabel("label2");
		attr2.setDataType(DataType.TEXT);
		object1.getObjectAttributes().add(attr2);

		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckSuccess(){
		model.setCurrentObjectDefinition(object1);

		object1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute attr1 = new ObjectAttribute();
		attr1.setName("attr1");
		attr1.setLabel("label1");
		attr1.setDataType(DataType.TEXT);
		attr1.setDataSource("dataSource");
		object1.getObjectAttributes().add(attr1);

		ObjectAttribute attr2 = new ObjectAttribute();
		attr2.setName("attr2");
		attr2.setLabel("label2");
		attr2.setDataType(DataType.TEXT);
		attr2.setDataSource("dataSource");
		object1.getObjectAttributes().add(attr2);

		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());

	}
}
