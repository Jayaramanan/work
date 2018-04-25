/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.mockito.Mockito;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.shared.constants.DynamicAttributeOperation;

public class GraphCollectionTest extends TestCase{
	private GraphCollection gc;

	@Override
	protected void setUp() throws Exception{
		gc = new GraphCollection();
	}

	public void testCalculateRelativeValues(){
		Entity entityNode1 = createEntity(11, null);
		Entity entityEdge = createEntity(13, null);
		final Attribute relativeAttribute = entityNode1.getAllAttributes().get(0);
		Entity entityNode2 = createEntity(12, relativeAttribute);
		final Attribute dynAttr = entityNode2.getAllAttributes().get(1);

		Node node1 = createNode(1001, entityNode1);
		Node node2 = createNode(1002, entityNode1);
		Node node3 = createNode(1003, entityNode2);

		Edge edge1 = createEdge(1011, entityEdge, node1, node2);
		Edge edge2 = createEdge(1012, entityEdge, node1, node3);
		Edge edge3 = createEdge(1013, entityEdge, node3, node2);

		node1.outEdges = Arrays.asList(new Edge[] { edge1, edge2 });
		node1.inEdges = new ArrayList<Edge>();

		node2.outEdges = new ArrayList<Edge>();
		node2.inEdges = Arrays.asList(new Edge[] { edge1, edge3 });

		node3.outEdges = Arrays.asList(new Edge[] { edge3 });
		node3.inEdges = Arrays.asList(new Edge[] { edge2 });

		gc.getNodes().addAll(Arrays.asList(node1, node2, node3));
		gc.getEdges().addAll(Arrays.asList(edge1, edge2, edge3));

		final List<Double> values = gc.calculateRelativeValues(node3, dynAttr);
		assertEquals(2, values.size());
		assertEquals(1502.0, values.get(0));
		assertEquals(1501.0, values.get(1));
	}

	public void testCalculateRelativeValuesFromEdge(){
		Entity entityNode1 = createEntity(11, null);
		Entity entityEdge = createEntity(13, null);
		final Attribute relativeAttribute = entityEdge.getAllAttributes().get(0);
		Entity entityNode2 = createEntity(12, relativeAttribute);
		final Attribute dynAttr = entityNode2.getAllAttributes().get(1);

		Node node1 = createNode(1001, entityNode1);
		Node node2 = createNode(1002, entityNode1);
		Node node3 = createNode(1003, entityNode2);

		Edge edge1 = createEdge(1011, entityEdge, node1, node2);
		Edge edge2 = createEdge(1012, entityEdge, node1, node3);
		Edge edge3 = createEdge(1013, entityEdge, node3, node2);

		node1.outEdges = Arrays.asList(new Edge[] { edge1, edge2 });
		node1.inEdges = new ArrayList<Edge>();

		node2.outEdges = new ArrayList<Edge>();
		node2.inEdges = Arrays.asList(new Edge[] { edge1, edge3 });

		node3.outEdges = Arrays.asList(new Edge[] { edge3 });
		node3.inEdges = Arrays.asList(new Edge[] { edge2 });

		gc.getNodes().addAll(Arrays.asList(node1, node2, node3));
		gc.getEdges().addAll(Arrays.asList(edge1, edge2, edge3));

		final List<Double> values = gc.calculateRelativeValues(node3, dynAttr);
		assertEquals(2, values.size());
		assertEquals(1313.0, values.get(0));
		assertEquals(1312.0, values.get(1));
	}

	public void testCalculateRelativeValuesDuplicateEdges(){
		Entity entityNode1 = createEntity(11, null);
		Entity entityEdge = createEntity(13, null);
		final Attribute relativeAttribute = entityNode1.getAllAttributes().get(0);
		Entity entityNode2 = createEntity(12, relativeAttribute);
		final Attribute dynAttr = entityNode2.getAllAttributes().get(1);

		Node node1 = createNode(1001, entityNode1);
		Node node2 = createNode(1002, entityNode1);
		Node node3 = createNode(1003, entityNode2);

		Edge edge1 = createEdge(1011, entityEdge, node1, node2);
		Edge edge2 = createEdge(1012, entityEdge, node2, node3);
		Edge edge3 = createEdge(1013, entityEdge, node3, node2);

		node1.outEdges = Arrays.asList(new Edge[] { edge1, edge2 });
		node1.inEdges = new ArrayList<Edge>();

		node2.outEdges = Arrays.asList(new Edge[] { edge2 });
		node2.inEdges = Arrays.asList(new Edge[] { edge3 });

		node3.outEdges = Arrays.asList(new Edge[] { edge3 });
		node3.inEdges = Arrays.asList(new Edge[] { edge2 });

		gc.getNodes().addAll(Arrays.asList(node1, node2, node3));
		gc.getEdges().addAll(Arrays.asList(edge1, edge2, edge3));

		final List<Double> values = gc.calculateRelativeValues(node3, dynAttr);
		assertEquals(1, values.size());
		assertEquals(1502.0, values.get(0));
	}

	public void testCalculateDynamicValue(){
		assertEquals(0.0, gc.calculateDynamicValue(new ArrayList<Double>(), DynamicAttributeOperation.Sum));
		assertEquals(0.0, gc.calculateDynamicValue(new ArrayList<Double>(), DynamicAttributeOperation.Avg));
		assertEquals(0.0, gc.calculateDynamicValue(new ArrayList<Double>(), DynamicAttributeOperation.Min));
		assertEquals(0.0, gc.calculateDynamicValue(new ArrayList<Double>(), DynamicAttributeOperation.Max));

		List<Double> dynamicValues = Arrays.asList(new Double[] { 10.0, 20.6, 30.0 });
		assertEquals(60.6, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Sum));
		assertEquals(20.2, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Avg));
		assertEquals(10.0, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Min));
		assertEquals(30.0, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Max));

		dynamicValues = Arrays.asList(new Double[] { 10.0, -10.6, 0.0 });
		assertEquals(-0.6, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Sum), 0.001);
		assertEquals(-0.2, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Avg), 0.001);
		assertEquals(-10.6, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Min));
		assertEquals(10.0, gc.calculateDynamicValue(dynamicValues, DynamicAttributeOperation.Max));
	}

	public void testRecalculateDynamicValues(){
		Entity entityNode1 = createEntity(11, null);
		Entity entityEdge = createEntity(13, null);
		final Attribute relativeAttribute = entityNode1.getAllAttributes().get(0);
		Entity entityNode2 = createEntity(12, relativeAttribute);
		final Attribute dynAttr = entityNode2.getAllAttributes().get(1);

		Node node1 = createNode(1001, entityNode1);
		Node node2 = createNode(1002, entityNode1);
		Node node3 = createNode(1003, entityNode2);

		Edge edge1 = createEdge(1011, entityEdge, node1, node2);
		Edge edge2 = createEdge(1012, entityEdge, node1, node3);
		Edge edge3 = createEdge(1013, entityEdge, node3, node2);

		node1.outEdges = Arrays.asList(new Edge[] { edge1, edge2 });
		node1.inEdges = new ArrayList<Edge>();

		node2.outEdges = new ArrayList<Edge>();
		node2.inEdges = Arrays.asList(new Edge[] { edge1, edge3 });

		node3.outEdges = Arrays.asList(new Edge[] { edge3 });
		node3.inEdges = Arrays.asList(new Edge[] { edge2 });

		gc.getNodes().addAll(Arrays.asList(node1, node2, node3));
		gc.getEdges().addAll(Arrays.asList(edge1, edge2, edge3));

		Mockito.when(dynAttr.getDynamicOperation()).thenReturn(DynamicAttributeOperation.Sum);
		gc.recalculateDynamicValues();

		assertEquals(1503.0, node3.Obj.getValue(112));
		assertEquals(3003.0, node3.Obj.getValue(113));

		Mockito.when(dynAttr.getDynamicOperation()).thenReturn(DynamicAttributeOperation.Avg);
		gc.recalculateDynamicValues();
		assertEquals(1503.0, node3.Obj.getValue(112));
		assertEquals(1501.5, node3.Obj.getValue(113));

		Mockito.when(dynAttr.getDynamicOperation()).thenReturn(DynamicAttributeOperation.Min);
		gc.recalculateDynamicValues();
		assertEquals(1503.0, node3.Obj.getValue(112));
		assertEquals(1501.0, node3.Obj.getValue(113));

		Mockito.when(dynAttr.getDynamicOperation()).thenReturn(DynamicAttributeOperation.Max);
		gc.recalculateDynamicValues();
		assertEquals(1503.0, node3.Obj.getValue(112));
		assertEquals(1502.0, node3.Obj.getValue(113));
	}

	private Entity createEntity(int id, Attribute relativeAttr){
		Entity entity = Mockito.mock(Entity.class);
		entity.ID = id;
		Attribute attr1 = createAttribute(id + 100, entity, false, 0, null);
		Attribute attr2 = createAttribute(id + 101, entity, relativeAttr != null, 1, relativeAttr);
		Mockito.when(entity.getAllAttributes()).thenReturn(Arrays.asList(new Attribute[] { attr1, attr2 }));
		if (relativeAttr != null){
			Mockito.when(entity.getGraphDynamicAttributes()).thenReturn(Arrays.asList(new Attribute[] { attr2 }));
			Mockito.when(entity.hasDynamicAttributes()).thenReturn(true);
		}
		return entity;
	}

	private Attribute createAttribute(int id, Entity entity, boolean dynamic, int sort, Attribute relativeAttr){
		Attribute attribute = Mockito.mock(Attribute.class);
		attribute.ID = id;
		attribute.setSort(sort);
		attribute.ent = entity;
		Mockito.when(attribute.getDynamicFromAttribute()).thenReturn(relativeAttr);
		Mockito.when(attribute.getDynamicFromEntity()).thenReturn(relativeAttr != null ? relativeAttr.ent : null);
		Mockito.when(attribute.isDynamic()).thenReturn(dynamic);
		return attribute;
	}

	private Node createNode(int id, Entity entity){
		Node node = Mockito.mock(Node.class);
		node.ID = id;
		Mockito.when(node.isActive()).thenReturn(true);
		DBObject obj = new DBObject(entity);
		obj.setId(id);
		obj.setValue(entity.getAllAttributes().get(0).ID, id + 500.0);
		obj.setValue(entity.getAllAttributes().get(1).ID, id + 500.0);
		node.Obj = obj;
		return node;
	}

	private Edge createEdge(int id, Entity entity, Node fromNode, Node toNode){
		Edge edge = new Edge();
		edge.ID = id;
		edge.from = fromNode;
		edge.to = toNode;

		DBObject obj = new DBObject(entity);
		obj.setId(id);
		obj.setValue(entity.getAllAttributes().get(0).ID, id + 300);
		obj.setValue(entity.getAllAttributes().get(1).ID, id + 300);
		edge.Obj = obj;

		return edge;
	}

}
