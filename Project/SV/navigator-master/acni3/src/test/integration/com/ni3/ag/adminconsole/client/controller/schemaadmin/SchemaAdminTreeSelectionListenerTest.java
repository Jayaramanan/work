/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.SchemaAdminModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.schemaadmin.ObjectAttributeTableModel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminTreeModel;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;

public class SchemaAdminTreeSelectionListenerTest extends ACTestCase{
	public void testAddAttribute(){
		ACMain.ScreenWidth = 500.;
		SessionData.getInstance().setDbName("NI3");
		SchemaAdminController controller = (SchemaAdminController) ACSpringFactory.getInstance().getBean("schemaAdminController");
		SchemaAdminModel model = controller.getModel();
		SchemaAdminView view = controller.getView();
		//SchemaAdminView view = new SchemaAdminView();
		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();
		ObjectDefinition def1 = getObjectDefinition(1);
		def1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		def1.setTableName("TEST_TABLE_NAME");
		objDefinitions.add(def1);
		view.initializeComponents();
		SchemaAdminTreeModel treeModel = new SchemaAdminTreeModel(objDefinitions);
		view.setTreeModel(treeModel);
		ObjectAttributeTableModel objectAttributeTableModel = new ObjectAttributeTableModel(def1.getObjectAttributes());
		view.setObjectAttributeTableModel(objectAttributeTableModel);
		//SchemaAdminModel model = new SchemaAdminModel();
		model.setCurrentObjectDefinition(def1);
		model.setObjectTypes(new ArrayList<ObjectType>());
		
		SchemaAdminTreeSelectionListener l = new SchemaAdminTreeSelectionListener(controller);
		TreePath tp = new TreePath(treeModel.getRoot());
		TreeSelectionEvent e = new TreeSelectionEvent(view.getLeftPanel().getSchemaTree(), tp, false, null, null);
		// just test call with null
		l.valueChanged(e);
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
}

