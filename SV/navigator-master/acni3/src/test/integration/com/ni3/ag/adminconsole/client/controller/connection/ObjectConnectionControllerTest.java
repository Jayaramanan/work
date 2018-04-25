/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import applet.ACMain;

import com.ni3.ag.adminconsole.client.model.ObjectConnectionModel;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionTableModel;
import com.ni3.ag.adminconsole.client.view.connection.ObjectConnectionView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class ObjectConnectionControllerTest extends ACTestCase{
	ObjectConnectionController controller;
	ObjectConnectionView view;
	ObjectConnectionModel model;

	ObjectConnection conn1;
	ObjectConnection conn2;
	ObjectConnection conn3;

	ObjectDefinition object;

	List<ObjectConnection> objectConnections;

	@Override
	protected void setUp() throws Exception{
		ACMain.ScreenWidth = 500.;
		controller = (ObjectConnectionController) ACSpringFactory.getInstance().getBean("objectConnectionController");
		model = controller.getModel();
		view = controller.getView();
		view.initializeComponents();		
		conn1 = new ObjectConnection();
		conn2 = new ObjectConnection();
		conn3 = new ObjectConnection();

		objectConnections = new ArrayList<ObjectConnection>();
		objectConnections.add(conn1);
		objectConnections.add(conn2);
		objectConnections.add(conn3);

		object = new ObjectDefinition();
		object.setId(10);
		Map<Integer, List<ObjectConnection>> connectionMap = new HashMap<Integer, List<ObjectConnection>>();
		connectionMap.put(object.getId(), objectConnections);

		model.setCurrentObject(object);
		model.setObjectConnections(connectionMap);
		model.setDeletedConnections(new ArrayList<ObjectConnection>());
	}

	public void testDeleteNewConnection(){
		view.setTableModel(new ObjectConnectionTableModel(objectConnections));
		controller.deleteConnection(conn2);

		assertEquals(2, model.getObjectConnections().size());
		assertEquals(0, model.getDeletedConnections().size());
		assertSame(conn1, model.getObjectConnections().get(0));
		assertSame(conn3, model.getObjectConnections().get(1));
	}

	public void testDeleteExistingConnection(){
		conn3.setId(3);
		view.setTableModel(new ObjectConnectionTableModel(objectConnections));
		controller.deleteConnection(conn3);

		assertEquals(2, model.getObjectConnections().size());
		assertEquals(1, model.getDeletedConnections().size());
		assertSame(conn1, model.getObjectConnections().get(0));
		assertSame(conn3, model.getDeletedConnections().get(0));
	}

	public void testAddNewConnection(){

		view.setTableModel(new ObjectConnectionTableModel(objectConnections));

		controller.addNewConnection();

		assertEquals(4, model.getObjectConnections().size());
	}

	public void testGetConnectionTypeReferenceData(){
		ObjectAttribute attr1 = new ObjectAttribute(object);
		attr1.setName("Attribute name");

		ObjectAttribute attr2 = new ObjectAttribute(object);
		attr2.setName("ConnectionType");

		List<ObjectAttribute> attributes = new ArrayList<ObjectAttribute>();
		attributes.add(attr1);
		attributes.add(attr2);
		object.setObjectAttributes(attributes);

		PredefinedAttribute pa1 = new PredefinedAttribute();
		PredefinedAttribute pa2 = new PredefinedAttribute();
		attr2.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		attr2.getPredefinedAttributes().add(pa1);
		attr2.getPredefinedAttributes().add(pa2);

		view.setTableModel(new ObjectConnectionTableModel(objectConnections));
		List<PredefinedAttribute> result = controller.getConnectionTypeReferenceData();
		assertEquals(2, result.size());
		assertSame(pa1, result.get(0));
		assertSame(pa2, result.get(1));
	}

	public void testGetConnectionTypeReferenceDataNoAttr(){
		ObjectAttribute attr1 = new ObjectAttribute(object);
		attr1.setName("Attribute name");

		ObjectAttribute attr2 = new ObjectAttribute(object);
		attr2.setName("Attribute name2");

		List<ObjectAttribute> attributes = new ArrayList<ObjectAttribute>();
		attributes.add(attr1);
		attributes.add(attr2);
		object.setObjectAttributes(attributes);

		view.setTableModel(new ObjectConnectionTableModel(objectConnections));
		List<PredefinedAttribute> result = controller.getConnectionTypeReferenceData();
		assertEquals(0, result.size());
	}
}
