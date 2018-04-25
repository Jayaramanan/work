/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class SchemaAdminNameValidationRuleTest extends TestCase{

	private ACValidationRule rule;
	private SchemaAdminModel model;

	public void setUp(){
		rule = new SchemaAdminNameValidationRule();
		model = new SchemaAdminModel();
	}

	public void testPerformCheckFail(){
		List<Schema> schemas = generateSchemas();
		model.setSchemaList(schemas);
		Schema schema = new Schema();
		schema.setName("123");
		model.setCurrentSchema(schema);

		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());

		schema.setName("-1 2-3 ");

		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
		assertEquals(1, rule.getErrorEntries().size());
	}

	public void testPerformCheckSuccess(){
		List<Schema> schemas = generateSchemas();
		model.setSchemaList(schemas);
		Schema schema = new Schema();
		schema.setName("345");
		model.setCurrentSchema(schema);

		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
		assertEquals(0, rule.getErrorEntries().size());
	}

	private List<Schema> generateSchemas(){
		List<Schema> schemas = new ArrayList<Schema>();
		Schema schema = new Schema();
		schema.setName("012");
		schemas.add(schema);
		schema = new Schema();
		schema.setName("123");
		schemas.add(schema);
		schema = new Schema();
		schema.setName("234");
		schemas.add(schema);
		return schemas;
	}
}
