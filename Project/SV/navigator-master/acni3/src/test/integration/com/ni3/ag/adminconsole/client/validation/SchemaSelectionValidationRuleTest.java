/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.List;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.model.SchemaAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorContainer;

public class SchemaSelectionValidationRuleTest extends ACTestCase{
	private SchemaAdminController controller;

	private static final String ERROR_MESSAGE = "MsgSelectSchema";

	public void setUp(){
		controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean("schemaAdminController"); 
	}

	public void testSchemaSelectionValidationRuleHappyPath(){
		ObjectDefinition currentSchemaDefinition = new ObjectDefinition();
		SchemaAdminModel model = controller.getModel();
		model.setCurrentSchemaDefinition(currentSchemaDefinition);

		SchemaSelectionValidationRule rule = new SchemaSelectionValidationRule(model);
		ErrorContainer result = rule.performCheck();
		assertNotNull(result);
		assertTrue(result.getErrors().isEmpty());
	}

	public void testSchemaAdminSelectionValidationRuleError(){
		controller.getModel().setCurrentSchemaDefinition(null);
		SchemaSelectionValidationRule rule = new SchemaSelectionValidationRule(controller.getModel());		
		ErrorContainer result = rule.performCheck();
		assertNotNull(result);

		List<String> errors = result.getErrors();
		assertFalse(errors.isEmpty());

		String errorMessage = errors.get(0);
		assertEquals(ERROR_MESSAGE, errorMessage);
	}
}
