/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import java.util.ArrayList;
import java.util.List;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.NodeMetaphorModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorTableModel;
import com.ni3.ag.adminconsole.client.view.metaphoradmin.NodeMetaphorView;
import com.ni3.ag.adminconsole.domain.NodeMetaphor;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class NodeMetaphorControllerTest extends ACTestCase{
	public void testRefreshModel(){
		ACMain.ScreenWidth = 500.;
		NodeMetaphorController controller = (NodeMetaphorController) ACSpringFactory.getInstance().getBean("nodeMetaphorController");

		NodeMetaphorModel model = controller.getModel();

		List<ObjectDefinition> objDefinitions = new ArrayList<ObjectDefinition>();

		ObjectDefinition obj1 = new ObjectDefinition();
		obj1.setObjectAttributes(new ArrayList<ObjectAttribute>());
		ObjectAttribute attr1 = new ObjectAttribute(obj1);
		attr1.setInMetaphor(true);
		attr1.setLabel("column name 1");

		ObjectAttribute attr2 = new ObjectAttribute(obj1);
		attr2.setInMetaphor(true);
		attr2.setLabel("column name 2");

		obj1.getObjectAttributes().add(attr1);
		obj1.getObjectAttributes().add(attr2);

		ObjectDefinition obj2 = new ObjectDefinition();

		objDefinitions.add(obj1);
		objDefinitions.add(obj2);

		model.setCurrentObjectDefinition(obj1);

		controller.refreshModel();

		assertEquals(2, model.getInMetaphorAttributes().size());

		assertEquals(2, model.getColumnNames().size());
		assertEquals("column name 1", model.getColumnNames().get(0));
		assertEquals("column name 2", model.getColumnNames().get(1));
		assertSame(attr1, model.getInMetaphorAttributes().get(0));
		assertSame(attr2, model.getInMetaphorAttributes().get(1));

		model.setCurrentObjectDefinition(obj2);
		controller.refreshModel();

		assertNull(model.getInMetaphorAttributes());
		assertEquals(0, model.getColumnNames().size());
	}

	public void testDeleteNodeMetaphor(){
		NodeMetaphorController controller = (NodeMetaphorController) ACSpringFactory.getInstance().getBean("nodeMetaphorController");;
		NodeMetaphorModel model = controller.getModel();
		model.setCurrentObjectDefinition(new ObjectDefinition());
		model.setDeletedRows(new ArrayList<NodeMetaphor>());
		NodeMetaphorView view = controller.getView();
		view.initializeComponents();

		NodeMetaphor row1 = new NodeMetaphor();
		row1.setId(1);
		NodeMetaphor row2 = new NodeMetaphor();
		row2.setId(2);

		List<NodeMetaphor> tableData = new ArrayList<NodeMetaphor>();

		tableData.add(row1);
		tableData.add(row2);
		model.setTableData(tableData);
		ArrayList<String> colNames = new ArrayList<String>();
		
		view.getRightPanel().setTableModel(new NodeMetaphorTableModel(colNames, tableData));
		controller.deleteNodeMetaphor(row1);

		assertEquals(1, model.getTableData().size());
		assertEquals(row2, model.getTableData().get(0));

		assertEquals(1, model.getDeletedRows().size());
		assertSame(row1, model.getDeletedRows().get(0));
	}

	public void testAddNewNodeMetaphor(){
		NodeMetaphorController controller = (NodeMetaphorController) ACSpringFactory.getInstance().getBean("nodeMetaphorController");;
		NodeMetaphorModel model = controller.getModel();
		ObjectDefinition current = new ObjectDefinition();
		current.setId(1);
		ObjectDefinition schema = new ObjectDefinition();
		schema.setId(11);
		current.setParentObject(schema);
		model.setCurrentObjectDefinition(current);
		model.setDeletedRows(new ArrayList<NodeMetaphor>());

		List<String> columnNames = new ArrayList<String>();
		columnNames.add("Col1");
		columnNames.add("Col2");
		model.setColumnNames(columnNames);

		NodeMetaphor row1 = new NodeMetaphor();
		row1.setId(1);
		NodeMetaphor row2 = new NodeMetaphor();
		row2.setId(2);

		List<NodeMetaphor> tableData = new ArrayList<NodeMetaphor>();
		tableData.add(row1);
		tableData.add(row2);
		model.setTableData(tableData);

		NodeMetaphorView view = controller.getView();
		view.initializeComponents();
		view.getRightPanel().setTableModel(new NodeMetaphorTableModel(columnNames, tableData));

		controller.addNewNodeMetaphor();

		assertEquals(3, model.getTableData().size());
		assertEquals(2, model.getTableData().get(2).getDynamicAttributes().size());
		assertEquals(1, (int)model.getTableData().get(2).getNodeID());
		assertEquals(11, (int)model.getTableData().get(2).getSchemaID());
	}
}
