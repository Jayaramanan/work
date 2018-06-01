/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.model.SchemaAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorContainer;

public class SchemaAdminFieldValidationRuleTest extends ACTestCase{

	SchemaAdminView view;
	SchemaAdminModel model;
	SchemaAdminController controller;
	SchemaAdminFieldValidationRule rule;
	List<ObjectAttribute> attributes;

	@Override
	protected void setUp() throws Exception{
		ACMain.ScreenWidth = 500.;
		controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean("schemaAdminController");
		view = controller.getView();
		model = controller.getModel();
		view.initializeComponents();
		attributes = new ArrayList<ObjectAttribute>();
		ObjectDefinition object1 = new ObjectDefinition();
		object1.setName("ObjectOne");
		ObjectDefinition object2 = new ObjectDefinition();
		object2.setName("ObjectTwo");
		ObjectDefinition schema = new ObjectDefinition();
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		schema.getObjectDefinitions().add(object1);
		schema.getObjectDefinitions().add(object2);
		object1.setParentObject(schema);
		object2.setParentObject(schema);

		model.setCurrentObjectDefinition(object1);

		rule = new SchemaAdminFieldValidationRule(controller, attributes);
	}

	public void testPerformCheckSortEmpty(){
		view.getRightPanel().getSort().setText("");
		view.getRightPanel().getObjectName().setText("Object");
		ErrorContainer result = rule.performCheck();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgSortEmpty", result.getErrors().get(0));
	}

	public void testPerformCheckObjectNameEmpty(){
		view.getRightPanel().getSort().setText("1");
		view.getRightPanel().getObjectName().setText("");
		ErrorContainer result = rule.performCheck();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgObjectNameEmpty", result.getErrors().get(0));
	}

	public void testPerformCheckSuccess(){
		view.getRightPanel().getSort().setText("1");
		view.getRightPanel().getObjectName().setText("Object");
		ErrorContainer result = rule.performCheck();
		assertNull(result);
	}

	public void testCheckAttributesNameEmpty(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName(null);
		attr1.setLabel("Label");
		attr1.setDataType(new DataType());
		attributes.add(attr1);

		ErrorContainer result = rule.checkAttributes();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgAttributeFieldsEmpty", result.getErrors().get(0));

		rule = new SchemaAdminFieldValidationRule(controller, attributes);
		attr1.setName("");
		result = rule.checkAttributes();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgAttributeFieldsEmpty", result.getErrors().get(0));
	}

	public void testCheckAttributesLabelEmpty(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName("Name");
		attr1.setLabel(null);
		attr1.setDataType(new DataType());
		attributes.add(attr1);

		ErrorContainer result = rule.checkAttributes();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgAttributeFieldsEmpty", result.getErrors().get(0));

		rule = new SchemaAdminFieldValidationRule(controller, attributes);
		attr1.setLabel("");
		result = rule.checkAttributes();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgAttributeFieldsEmpty", result.getErrors().get(0));
	}

	public void testCheckAttributesDataTypeEmpty(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName("Name");
		attr1.setLabel("Label");
		attr1.setDataType(null);
		attributes.add(attr1);

		ErrorContainer result = rule.checkAttributes();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgAttributeFieldsEmpty", result.getErrors().get(0));
	}

	public void testCheckAttributesSuccess(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName("Name");
		attr1.setLabel("Label");
		attr1.setDataType(new DataType());
		attributes.add(attr1);

		ErrorContainer result = rule.checkAttributes();
		assertNull(result.getErrors());
	}

	public void testCheckObjectNameExistSame(){
		view.getRightPanel().getObjectName().setText("ObjectTwo");
		ErrorContainer result = rule.checkObjectName();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgDuplicateObjects", result.getErrors().get(0));
	}

	public void testCheckObjectNameSuccess(){
		view.getRightPanel().getObjectName().setText("Object3");
		ErrorContainer result = rule.checkObjectName();
		assertEquals(null, result.getErrors());
	}

	public void testCheckObjectNameSuccessSameObject(){
		view.getRightPanel().getObjectName().setText("Object1");
		ErrorContainer result = rule.checkObjectName();
		assertEquals(null, result.getErrors());
	}

	public void testCheckAttributeDuplicatesSuccess(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName("Name1");

		ObjectAttribute attr2 = new ObjectAttribute(new ObjectDefinition());
		attr2.setName("Name2");

		attributes.add(attr1);
		attributes.add(attr2);

		ErrorContainer result = rule.checkAttributeDuplicates();
		assertNull(result.getErrors());
	}

	public void testCheckAttributeDuplicatesFail(){
		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName("Name1");

		ObjectAttribute attr2 = new ObjectAttribute(new ObjectDefinition());
		attr2.setName("name1");

		attributes.add(attr1);
		attributes.add(attr2);

		ErrorContainer result = rule.checkAttributeDuplicates();
		assertEquals(1, result.getErrors().size());
		assertEquals("MsgDuplicateAttributes", result.getErrors().get(0));
	}

	public void testPerformCheckAllFail(){
		view.getRightPanel().getSort().setText("");
		view.getRightPanel().getObjectName().setText("");

		ObjectAttribute attr1 = new ObjectAttribute(new ObjectDefinition());
		attr1.setName("");
		attr1.setLabel("Label");
		attr1.setDataType(new DataType());
		attr1.setInTable("InTable");
		attributes.add(attr1);

		ErrorContainer result = rule.performCheck();
		assertEquals(4, result.getErrors().size());
	}

}
