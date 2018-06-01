package com.ni3.ag.navigator.client.controller.graph;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.util.PrivateAccesor;
import junit.framework.TestCase;

public class GraphControllerTest extends TestCase{
	private Entity node;
	private Entity edge;
	private Attribute nodeAttr1;
	private Attribute edgeAttr1;
	private Attribute nodeAttr2;
	private Attribute edgeAttr2;
	private Attribute nodeAttr3;
	private Attribute edgeAttr3;
	private Value nodeAttr1Value1;
	private Value edgeAttr1Value1;
	private Value nodeAttr1Value2;
	private Value edgeAttr1Value2;
	private Value nodeAttr2Value1;
	private Value edgeAttr2Value1;
	private Value nodeAttr3Value1;
	private Value edgeAttr3Value1;
	private Value nodeAttr3Value2;
	private Value edgeAttr3Value2;
	private Value nodeAttr3Value3;
	private Value edgeAttr3Value3;

	@Override
	public void setUp() throws Exception{
		super.setUp();
		node = new Entity(10);
		PrivateAccesor.setPrivateField(node, "objectTypeID", 2);
		List<Attribute> attributes = new ArrayList<Attribute>();
		PrivateAccesor.setPrivateField(node, "attributesReadable", attributes);
		PrivateAccesor.setPrivateField(node, "attributesAll", attributes);

		nodeAttr1 = new Attribute();
		PrivateAccesor.setPrivateField(nodeAttr1, "values", new ArrayList<Value>());
		nodeAttr1.ID = 100;
		nodeAttr1.predefined = true;
		nodeAttr1.inFilter = true;

		nodeAttr2 = new Attribute();
		PrivateAccesor.setPrivateField(nodeAttr2, "values", new ArrayList<Value>());
		nodeAttr2.ID = 101;
		nodeAttr2.predefined = true;
		nodeAttr2.inFilter = true;

		nodeAttr3 = new Attribute();
		PrivateAccesor.setPrivateField(nodeAttr3, "values", new ArrayList<Value>());
		nodeAttr3.ID = 102;
		nodeAttr3.inFilter = true;

		attributes.add(nodeAttr1);
		attributes.add(nodeAttr2);
		attributes.add(nodeAttr3);

		nodeAttr1Value1 = new Value(10, 0, "nodeAttr1Value1", "nodeAttr1Value1");
		nodeAttr1Value2 = new Value(10, 0, "nodeAttr1Value2", "nodeAttr1Value2");
		nodeAttr1.getValues().add(nodeAttr1Value1);
		nodeAttr1.getValues().add(nodeAttr1Value2);
		nodeAttr2Value1 = new Value(10, 0, "nodeAttr2Value1", "nodeAttr2Value1");
		nodeAttr2.getValues().add(nodeAttr2Value1);
		nodeAttr3Value1 = new Value(10, 0, "nodeAttr3Value1", "nodeAttr3Value1");
		nodeAttr3Value2 = new Value(10, 0, "nodeAttr3Value2", "nodeAttr3Value2");
		nodeAttr3Value3 = new Value(10, 0, "nodeAttr3Value3", "nodeAttr3Value3");
		nodeAttr3.getValues().add(nodeAttr3Value1);
		nodeAttr3.getValues().add(nodeAttr3Value2);
		nodeAttr3.getValues().add(nodeAttr3Value3);

		edge = new Entity(100);
		attributes = new ArrayList<Attribute>();
		PrivateAccesor.setPrivateField(edge, "objectTypeID", 4);
		PrivateAccesor.setPrivateField(edge, "attributesReadable", attributes);
		PrivateAccesor.setPrivateField(edge, "attributesAll", attributes);

		edgeAttr1 = new Attribute();
		PrivateAccesor.setPrivateField(edgeAttr1, "values", new ArrayList<Value>());
		edgeAttr1.ID = 200;
		edgeAttr1.predefined = true;
		edgeAttr1.inFilter = true;

		edgeAttr2 = new Attribute();
		PrivateAccesor.setPrivateField(edgeAttr2, "values", new ArrayList<Value>());
		edgeAttr2.ID = 201;
		edgeAttr2.predefined = true;
		edgeAttr2.inFilter = true;

		edgeAttr3 = new Attribute();
		PrivateAccesor.setPrivateField(edgeAttr3, "values", new ArrayList<Value>());
		edgeAttr3.ID = 202;
		edgeAttr3.predefined = true;
		edgeAttr3.inFilter = true;

		attributes.add(edgeAttr1);
		attributes.add(edgeAttr2);
		attributes.add(edgeAttr3);

		edgeAttr1Value1 = new Value(10, 0, "edgeAttr1Value1", "edgeAttr1Value1");
		edgeAttr1Value2 = new Value(10, 0, "edgeAttr1Value2", "edgeAttr1Value2");
		edgeAttr1.getValues().add(edgeAttr1Value1);
		edgeAttr1.getValues().add(edgeAttr1Value2);
		edgeAttr2Value1 = new Value(10, 0, "edgeAttr2Value1", "edgeAttr2Value1");
		edgeAttr2.getValues().add(edgeAttr2Value1);
		edgeAttr3Value1 = new Value(10, 0, "edgeAttr3Value1", "edgeAttr3Value1");
		edgeAttr3Value2 = new Value(10, 0, "edgeAttr3Value2", "edgeAttr3Value2");
		edgeAttr3Value3 = new Value(10, 0, "edgeAttr3Value3", "edgeAttr3Value3");
		edgeAttr3.getValues().add(edgeAttr3Value1);
		edgeAttr3.getValues().add(edgeAttr3Value2);
		edgeAttr3.getValues().add(edgeAttr3Value3);
	}

	public void testCalculateStatistics() throws Exception{
		ValueUsageStatistics expected = new ValueUsageStatistics();
		List<GraphObject> objects = new ArrayList<GraphObject>();
		ValueUsageStatistics result = GraphController.calculateStatistics(objects, false);
		assertEquals(expected, result);

		final int ENTITY_INDEX = 0;
		final int VALUE_1_INDEX = 1;
		final int VALUE_2_INDEX = 2;
		final int VALUE_3_INDEX = 3;
		Object[][] nodesData = new Object[][] { { node, nodeAttr1Value1, nodeAttr2Value1, nodeAttr3Value1 },
				{ node, nodeAttr1Value2, nodeAttr2Value1, nodeAttr3Value2 }, { node, null, null, nodeAttr3Value3 },
				{ node, null, null, nodeAttr3Value3 } };
		for (Object[] nodeData : nodesData){
			Node n = new Node(0, 0);
			n.Obj = new DBObject((Entity) nodeData[ENTITY_INDEX]);
			n.Obj.getData().put(100, nodeData[VALUE_1_INDEX]);
			n.Obj.getData().put(101, nodeData[VALUE_2_INDEX]);
			n.Obj.getData().put(102, nodeData[VALUE_3_INDEX]);
			objects.add(n);
		}
		expected.increment(nodeAttr1Value1);
		expected.increment(nodeAttr1Value2);
		expected.increment(nodeAttr2Value1);
		expected.increment(nodeAttr2Value1);
		expected.incrementDisplayed(nodeAttr1Value1);
		expected.incrementDisplayed(nodeAttr1Value2);
		expected.incrementDisplayed(nodeAttr2Value1);
		expected.incrementDisplayed(nodeAttr2Value1);
		expected.increment(edgeAttr1Value1);
		expected.increment(edgeAttr1Value2);
		expected.increment(edgeAttr2Value1);
		expected.increment(edgeAttr2Value1);
		expected.incrementDisplayed(edgeAttr1Value1);
		expected.incrementDisplayed(edgeAttr1Value2);
		expected.incrementDisplayed(edgeAttr2Value1);
		expected.incrementDisplayed(edgeAttr2Value1);
		expected.increment(edgeAttr3Value1);
		expected.increment(edgeAttr3Value2);
		expected.increment(edgeAttr3Value3);
		expected.increment(edgeAttr3Value3);
		expected.incrementDisplayed(edgeAttr3Value1);
		expected.incrementDisplayed(edgeAttr3Value2);
		expected.incrementDisplayed(edgeAttr3Value3);
		expected.incrementDisplayed(edgeAttr3Value3);

		Object[][] edgesData = new Object[][] { { edge, edgeAttr1Value1, edgeAttr2Value1, edgeAttr3Value1 },
				{ edge, edgeAttr1Value2, edgeAttr2Value1, edgeAttr3Value2 }, { edge, null, null, edgeAttr3Value3 },
				{ edge, null, null, edgeAttr3Value3 } };
		for (Object[] edgeData : edgesData){
			Edge e = new Edge();
			e.Obj = new DBObject((Entity) edgeData[ENTITY_INDEX]);
			e.Obj.getData().put(200, edgeData[VALUE_1_INDEX]);
			e.Obj.getData().put(201, edgeData[VALUE_2_INDEX]);
			e.Obj.getData().put(202, edgeData[VALUE_3_INDEX]);
			objects.add(e);
		}

		result = GraphController.calculateStatistics(objects, false);
		assertEquals(expected, result);

		expected.increment(nodeAttr3Value1);
		expected.increment(nodeAttr3Value2);
		expected.increment(nodeAttr3Value3);
		expected.increment(nodeAttr3Value3);
		expected.incrementDisplayed(nodeAttr3Value1);
		expected.incrementDisplayed(nodeAttr3Value2);
		expected.incrementDisplayed(nodeAttr3Value3);
		expected.incrementDisplayed(nodeAttr3Value3);
		nodeAttr3.predefined = true;
		nodeAttr3.inFilter = true;
		result = GraphController.calculateStatistics(objects, false);
		assertEquals(expected, result);

		result = GraphController.calculateStatistics(objects, false);
		assertEquals(expected, result);

		expected = new ValueUsageStatistics();
		expected.increment(edgeAttr1Value1);
		expected.increment(edgeAttr1Value2);
		expected.increment(edgeAttr2Value1);
		expected.increment(edgeAttr2Value1);
		expected.incrementDisplayed(edgeAttr1Value1);
		expected.incrementDisplayed(edgeAttr1Value2);
		expected.incrementDisplayed(edgeAttr2Value1);
		expected.incrementDisplayed(edgeAttr2Value1);
		expected.increment(edgeAttr3Value1);
		expected.increment(edgeAttr3Value2);
		expected.increment(edgeAttr3Value3);
		expected.increment(edgeAttr3Value3);
		expected.incrementDisplayed(edgeAttr3Value1);
		expected.incrementDisplayed(edgeAttr3Value2);
		expected.incrementDisplayed(edgeAttr3Value3);
		expected.incrementDisplayed(edgeAttr3Value3);
		result = GraphController.calculateStatistics(objects, true);
		assertEquals(expected, result);
	}
}
