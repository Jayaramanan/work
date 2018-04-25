/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.SchemaAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectAttributeTableModel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectDefinitionRightPanel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminTreeModel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.User;

public class SchemaAdminControllerTest extends ACTestCase{
	DataType dataType = getDataType();

	public void testPopulateDataToView(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();

		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();

		ObjectDefinition def1 = getObjectDefinition(1);
		ObjectAttribute attr11 = getObjectAttribute(def1, 1);
		ObjectAttribute attr12 = getObjectAttribute(def1, 2);
		def1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def1.getObjectAttributes().add(attr11);
		def1.getObjectAttributes().add(attr12);

		ObjectDefinition def2 = getObjectDefinition(2);
		ObjectAttribute attr21 = getObjectAttribute(def1, 3);
		ObjectAttribute attr22 = getObjectAttribute(def1, 4);
		def1.getObjectAttributes().add(attr21);
		def1.getObjectAttributes().add(attr22);

		objDefinitions.add(def1);
		objDefinitions.add(def2);

		List<DataType> dataTypes = new ArrayList<DataType>();
		dataTypes.add(dataType);

		List<ObjectType> objectTypes = new ArrayList<ObjectType>();
		objectTypes.add(getObjectType());

		model.setSchemaList(objDefinitions);
		model.setDataTypes(dataTypes);
		model.setObjectTypes(objectTypes);
		model.setCurrentObjectDefinition(def1);

		view.initializeComponents();
		view.setTreeModel(new SchemaAdminTreeModel(objDefinitions));
		view.getRightPanel().getSort().setText("2");

		controller.populateDataToModel(model, view);
	}

	public void testPopulateNewObjectDefinition(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		ACMain.ScreenWidth = 500.;
		// SchemaAdminView view = new SchemaAdminView();
		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();
		ObjectDefinition def1 = getObjectDefinition(1);
		def1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def1.setTableName("TEST_TABLE_NAME");
		def1.setCreatedBy(new User());
		objDefinitions.add(def1);
		controller.getView().initializeComponents();
		controller.getView().setTreeModel(new SchemaAdminTreeModel(objDefinitions));
		ObjectAttributeTableModel objectAttributeTableModel = new ObjectAttributeTableModel(def1.getObjectAttributes());
		controller.getView().setObjectAttributeTableModel(objectAttributeTableModel);
		// SchemaAdminModel model = new SchemaAdminModel();
		controller.getModel().setCurrentObjectDefinition(def1);
		controller.getModel().setObjectTypes(new ArrayList<ObjectType>());
		controller.populateNewObjectDefinition(def1);
		TestCase.assertNotNull(controller.getModel().getCurrentObjectDefinition());
		TestCase.assertNull(controller.getModel().getCurrentSchemaDefinition());
		TestCase.assertEquals(controller.getView().getRightPanel().getTableModel().getRowCount(),
		        def1.getObjectAttributes() != null ? def1.getObjectAttributes().size() : 0);
	}

	public void testAddAttribute(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		ACMain.ScreenWidth = 500.;
		// SchemaAdminView view = new SchemaAdminView();
		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();
		ObjectDefinition def1 = getObjectDefinition(1);
		def1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def1.setTableName("TEST_TABLE_NAME");
		objDefinitions.add(def1);
		controller.getView().initializeComponents();
		controller.getView().setTreeModel(new SchemaAdminTreeModel(objDefinitions));
		ObjectAttributeTableModel objectAttributeTableModel = new ObjectAttributeTableModel(def1.getObjectAttributes());
		controller.getView().setObjectAttributeTableModel(objectAttributeTableModel);
		// SchemaAdminModel model = new SchemaAdminModel();
		controller.getModel().setCurrentObjectDefinition(def1);
		controller.addNewAtribute();
		ObjectAttributeTableModel attrModel = controller.getView().getRightPanel().getTableModel();
		assertSame(objectAttributeTableModel, attrModel);
		assertEquals(attrModel.getRowCount(), 1);
	}

	public void testPopulateDataToModel(){
		ACMain.ScreenWidth = 500.;

		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();

		ObjectDefinition def1 = getObjectDefinition(1);
		ObjectAttribute attr11 = getObjectAttribute(def1, 1);
		ObjectAttribute attr12 = getObjectAttribute(def1, 2);
		def1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def1.getObjectAttributes().add(attr11);
		def1.getObjectAttributes().add(attr12);

		ObjectDefinition def2 = getObjectDefinition(2);
		ObjectAttribute attr21 = getObjectAttribute(def2, 3);
		ObjectAttribute attr22 = getObjectAttribute(def2, 4);
		def2.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def2.getObjectAttributes().add(attr21);
		def2.getObjectAttributes().add(attr22);

		objDefinitions.add(def1);
		objDefinitions.add(def2);

		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");

		controller.getView().initializeComponents();
		controller.getView().setTreeModel(new SchemaAdminTreeModel(objDefinitions));
		ObjectDefinitionRightPanel rightPanel = controller.getView().getRightPanel();
		rightPanel.getSort().setText("1");
		ObjectAttributeTableModel objectAttributeTableModel = new ObjectAttributeTableModel(def1.getObjectAttributes());
		controller.getView().setObjectAttributeTableModel(objectAttributeTableModel);

		controller.getModel().setCurrentObjectDefinition(def1);

		rightPanel.getDescription().setText("testdescription");
		rightPanel.getSort().setText("100");

		assertNotNull(controller.getModel().getCurrentObjectDefinition());

		controller.populateDataToModel(controller.getModel(), controller.getView());

		ObjectDefinition objDef = controller.getModel().getCurrentObjectDefinition();
		assertNotNull(objDef);
		assertEquals("testdescription", objDef.getDescription());
		assertEquals(new Integer(100), objDef.getSort());
	}

	public void testGetFilteredObjectTypesForObject(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");

		List<ObjectType> allObjectTypes = new ArrayList<ObjectType>();
		ObjectType ot1 = new ObjectType(1);
		ObjectType ot2 = new ObjectType(2);
		ObjectType ot3 = new ObjectType(4);
		allObjectTypes.add(ot1);
		allObjectTypes.add(ot2);
		allObjectTypes.add(ot3);

		controller.getModel().setCurrentObjectDefinition(new ObjectDefinition());
		controller.getModel().setObjectTypes(allObjectTypes);

		List<ObjectType> result = controller.getFilteredObjectTypes();
		assertEquals(2, result.size());
		assertSame(ot2, result.get(0));
		assertSame(ot3, result.get(1));
	}

	public void testGetFilteredObjectTypesForSchema(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");

		List<ObjectType> allObjectTypes = new ArrayList<ObjectType>();
		ObjectType ot1 = new ObjectType(1);
		ObjectType ot2 = new ObjectType(2);
		ObjectType ot3 = new ObjectType(4);
		allObjectTypes.add(ot1);
		allObjectTypes.add(ot2);
		allObjectTypes.add(ot3);

		SchemaAdminModel model = controller.getModel();
		model.setCurrentObjectDefinition(null);
		model.setObjectTypes(allObjectTypes);

		List<ObjectType> result = controller.getFilteredObjectTypes();
		assertEquals(1, result.size());
		assertSame(ot1, result.get(0));
	}

	public void testUpdateTableNames(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();

		ObjectDefinition def1 = getObjectDefinition(1);
		ObjectAttribute attr11 = getObjectAttribute(def1, 1);
		ObjectAttribute attr12 = getObjectAttribute(def1, 2);
		def1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def1.getObjectAttributes().add(attr11);
		def1.getObjectAttributes().add(attr12);
		def1.setName("ObjectName");

		controller.updateTableNames(def1);
		assertEquals("USR_OBJECTNAME", def1.getTableName());
		assertEquals("USR_OBJECTNAME", def1.getObjectAttributes().get(0).getInTable());
		assertEquals("USR_OBJECTNAME", def1.getObjectAttributes().get(1).getInTable());
	}

	public void testGetTableName(){
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean(
		        "schemaAdminController");
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();

		assertEquals("USR_OBJECTNAME", controller.getTableName("objectname"));
		assertEquals("USR_OBJECTNAME", controller.getTableName("Object name"));
		assertEquals("USR_OBJECTNAME", controller.getTableName("O B J E C T N A M E"));
	}

	private ObjectDefinition getObjectDefinition(int uniq){
		ObjectDefinition objectDefinition = new ObjectDefinition();

		// fill parameters here
		objectDefinition.setId(uniq);
		objectDefinition.setDescription("desctiption");
		objectDefinition.setName("name " + uniq);
		objectDefinition.setParentObject(null);
		objectDefinition.setSort(uniq);

		ObjectType objectType = getObjectType();

		objectDefinition.setObjectType(objectType);

		return objectDefinition;
	}

	private ObjectType getObjectType(){

		ObjectType objectType = new ObjectType();
		objectType.setId(1);
		objectType.setName("object type name");
		return objectType;
	}

	private DataType getDataType(){
		DataType dataType = new DataType();
		dataType.setId(1);
		dataType.setName("dataType_name");
		return dataType;
	}

	private ObjectAttribute getObjectAttribute(ObjectDefinition parent, int uniq){
		ObjectAttribute attr = new ObjectAttribute(parent);
		attr.setName("name " + uniq);
		attr.setLabel("label");
		attr.setInMetaphor(true);
		attr.setDataType(dataType);
		attr.setDescription("description");
		attr.setInTable("table_name");

		return attr;
	}
}
