/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.connection;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;

public class ObjectConnectionControllerTest extends TestCase{

	private List<Schema> schemas;
	private ObjectDefinition currEdge;
	private ObjectConnectionController controller;
	private ObjectConnectionModel model;
	private ObjectDefinition edge;

	@Override
	protected void setUp() throws Exception{
		controller = (ObjectConnectionController) ACSpringFactory.getInstance().getBean("objectConnectionController");
		schemas = new ArrayList<Schema>();
		Schema schema = new Schema();
		schemas.add(schema);
		ObjectDefinition node = new ObjectDefinition();
		node.setId(1);
		node.setObjectType(ObjectType.NODE);
		edge = new ObjectDefinition();
		edge.setId(2);
		edge.setObjectType(ObjectType.EDGE);
		currEdge = new ObjectDefinition();
		currEdge.setId(3);
		currEdge.setObjectType(ObjectType.EDGE);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		schema.getObjectDefinitions().add(node);
		schema.getObjectDefinitions().add(edge);
		schema.getObjectDefinitions().add(currEdge);

		ObjectConnection c1 = createConnection(createPredefinedAttribute(11), edge, node);
		ObjectConnection c2 = createConnection(createPredefinedAttribute(12), currEdge, node);
		ObjectConnection c3 = createConnection(createPredefinedAttribute(13), currEdge, node);
		ObjectConnection c4 = createConnection(createPredefinedAttribute(14), currEdge, node);
		ObjectConnection c5 = createConnection(createPredefinedAttribute(14), currEdge, node);

		edge.setObjectConnections(new ArrayList<ObjectConnection>());
		edge.getObjectConnections().add(c1);

		currEdge.setObjectConnections(new ArrayList<ObjectConnection>());
		currEdge.getObjectConnections().add(c2);
		currEdge.getObjectConnections().add(c3);
		currEdge.getObjectConnections().add(c4);
		currEdge.getObjectConnections().add(c5);

		model = controller.getModel();
		model.setCurrentDatabaseInstance(new DatabaseInstance("instance"));
		model.setSchemas(schemas);
		model.setCurrentObject(currEdge);
	}

	public void testListToString(){
		List<Integer> list = new ArrayList<Integer>();
		assertEquals("", controller.listToString(list));
		list.add(1);
		assertEquals("1", controller.listToString(list));
		list.add(22);
		list.add(333);
		assertEquals("1;22;333", controller.listToString(list));
	}

	public void testParseHierarchicalEdges(){
		controller.parseHierarchicalEdges(new ApplicationSetting("", "", "123;456"));
		assertEquals(2, model.getHierarchicalEdges().size());
		assertTrue(model.isHierarchicalEdge(123));
		assertTrue(model.isHierarchicalEdge(456));
		assertFalse(model.isHierarchicalEdge(789));
	}

	public void testRefreshHierarchicalConnections(){
		List<ObjectConnection> connections = currEdge.getObjectConnections();
		controller.parseHierarchicalEdges(new ApplicationSetting("", "", "123;456"));
		controller.refreshHierarchicalConnections(connections);
		for (ObjectConnection c : connections){
			assertFalse(c.isHierarchical());
		}

		controller.parseHierarchicalEdges(new ApplicationSetting("", "", "11;13;14"));
		controller.refreshHierarchicalConnections(connections);

		assertFalse(edge.getObjectConnections().get(0).isHierarchical());

		assertFalse(connections.get(0).isHierarchical());
		assertTrue(connections.get(1).isHierarchical());
		assertTrue(connections.get(2).isHierarchical());
		assertTrue(connections.get(3).isHierarchical());
	}

	public void testAddMissingHierarchicalEdges(){
		List<ObjectConnection> connections = currEdge.getObjectConnections();
		connections.get(0).setHierarchical(true);
		connections.get(3).setHierarchical(true);
		controller.addMissingHierarchicalEdges();

		assertFalse(model.getHierarchicalEdges().contains(11));
		assertTrue(model.getHierarchicalEdges().contains(12));
		assertFalse(model.getHierarchicalEdges().contains(13));
		assertTrue(model.getHierarchicalEdges().contains(14));

		edge.getObjectConnections().get(0).setHierarchical(true);
		connections.get(1).setHierarchical(true);
		controller.addMissingHierarchicalEdges();

		assertFalse(model.getHierarchicalEdges().contains(11));
		assertTrue(model.getHierarchicalEdges().contains(12));
		assertTrue(model.getHierarchicalEdges().contains(13));
		assertTrue(model.getHierarchicalEdges().contains(14));
	}

	public void testRemoveExcessiveHierarchicalEdges(){
		List<ObjectConnection> connections = currEdge.getObjectConnections();
		controller.parseHierarchicalEdges(new ApplicationSetting("", "", "11;12;13;14"));
		connections.get(0).setHierarchical(true);
		connections.get(1).setHierarchical(true);
		connections.get(2).setHierarchical(true);
		connections.get(3).setHierarchical(true);
		controller.removeExcessiveHierarchicalEdges();

		assertTrue(model.getHierarchicalEdges().contains(11));
		assertTrue(model.getHierarchicalEdges().contains(12));
		assertTrue(model.getHierarchicalEdges().contains(13));
		assertTrue(model.getHierarchicalEdges().contains(14));

		connections.get(0).setHierarchical(false);
		connections.get(1).setHierarchical(false);
		connections.get(2).setHierarchical(false);

		controller.removeExcessiveHierarchicalEdges();

		assertTrue(model.getHierarchicalEdges().contains(11));
		assertFalse(model.getHierarchicalEdges().contains(12));
		assertFalse(model.getHierarchicalEdges().contains(13));
		assertTrue(model.getHierarchicalEdges().contains(14));
	}

	private ObjectConnection createConnection(PredefinedAttribute pa, ObjectDefinition edge, ObjectDefinition node){
		ObjectConnection c = new ObjectConnection();
		c.setObject(edge);
		c.setConnectionType(pa);
		c.setFromObject(node);
		c.setToObject(node);
		return c;
	}

	private PredefinedAttribute createPredefinedAttribute(int id){
		PredefinedAttribute attr = new PredefinedAttribute();
		attr.setId(id);
		return attr;
	}
}
