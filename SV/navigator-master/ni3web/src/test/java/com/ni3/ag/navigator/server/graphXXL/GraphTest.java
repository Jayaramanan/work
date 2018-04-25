/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.graphXXL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.domain.DataFilter;
import com.ni3.ag.navigator.server.domain.Edge;
import com.ni3.ag.navigator.server.domain.Node;
import com.ni3.ag.navigator.server.type.Scope;
import com.ni3.ag.navigator.server.util.PrivateAccessor;
import com.ni3.ag.navigator.shared.domain.Prefilter;

public class GraphTest extends TestCase{

	private Graph graph;
	private GroupScope groupScope;
	private DataFilter dataFilter;
	private DataFilter sysFilter;

	@Override
	protected void setUp() throws Exception{
		graph = new Graph();
		groupScope = new GroupScope();
		groupScope.getAllowedEntities().add(11);
		groupScope.getAllowedEntities().add(21);
		Map<Integer, GroupScope> groupScopes = new HashMap<Integer, GroupScope>();
		groupScopes.put(1, groupScope);
		PrivateAccessor.setPrivateField(graph, "groupScopes", groupScopes);
		PreFilterData pf = new PreFilterData();
		PrivateAccessor.setPrivateField(graph, "filterData", pf);
		Map<Integer, DataFilter> sysFilters = new HashMap<Integer, DataFilter>();
		sysFilter = new DataFilter(new ArrayList<Prefilter>());
		sysFilters.put(1, sysFilter);
		PrivateAccessor.setPrivateField(graph, "sysFilters", sysFilters);
		dataFilter = new DataFilter(new ArrayList<Prefilter>());
	}

	public void testHasGroupScope(){
		GroupScope gs = new GroupScope();
		assertFalse((Boolean) PrivateAccessor.invokePrivateMethod(gs, "hasGroupScope", ""));
		assertFalse((Boolean) PrivateAccessor.invokePrivateMethod(gs, "hasGroupScope", "A"));
		assertFalse((Boolean) PrivateAccessor.invokePrivateMethod(gs, "hasGroupScope", "X"));

		assertTrue((Boolean) PrivateAccessor.invokePrivateMethod(gs, "hasGroupScope", "S"));
	}

	public void testGetScope(){
		assertEquals(Scope.Denied, PrivateAccessor.invokePrivateMethod(graph, "getScope", new Object[] { null }));
		assertEquals(Scope.Denied, PrivateAccessor.invokePrivateMethod(graph, "getScope", ""));
		assertEquals(Scope.Denied, PrivateAccessor.invokePrivateMethod(graph, "getScope", "X"));

		assertEquals(Scope.External, PrivateAccessor.invokePrivateMethod(graph, "getScope", "E"));
		assertEquals(Scope.Allow, PrivateAccessor.invokePrivateMethod(graph, "getScope", "N"));
	}

	public void testGetMaxNodeScopeAllow(){
		Node node = new Node(111);
		node.setType(11);
		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxNodeScope", node, 1, dataFilter);
		assertEquals(Scope.Allow, scope);

		groupScope.getAllowedNodes().add(111);
		groupScope.getDeniedNodes().add(112);
		groupScope.setHasNodeScope("S");

		assertEquals(Scope.Allow, PrivateAccessor.invokePrivateMethod(graph, "getMaxNodeScope", node, 1, dataFilter));
	}

	public void testGetMaxNodeScopeDenied(){
		Node node = new Node(111);
		node.setType(12);
		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxNodeScope", node, 1, dataFilter);
		assertEquals(Scope.Denied, scope);

		node.setID(112);
		node.setType(11);
		groupScope.getAllowedNodes().add(111);
		groupScope.getDeniedNodes().add(112);
		groupScope.setHasNodeScope("S");

		assertEquals(Scope.Denied, PrivateAccessor.invokePrivateMethod(graph, "getMaxNodeScope", node, 1, dataFilter));
	}

	public void testGetMaxNodeScopeDeniedBySysFilter(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return true;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);
		Node node = new Node(111);
		node.setType(11);

		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxNodeScope", node, 1, dataFilter);
		assertEquals(Scope.Denied, scope);
	}

	public void testGetMaxNodeScopeDeniedByFilter(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return dataFilter == GraphTest.this.dataFilter;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);
		Node node = new Node(111);
		node.setType(11);

		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxNodeScope", node, 1, dataFilter);
		assertEquals(Scope.DeniedByPrefilter, scope);
	}

	public void testGetMaxEdgeScopeAllow(){
		Edge edge = new Edge();
		edge.setID(111);
		edge.setType(11);
		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxEdgeScope", edge, 1, dataFilter);
		assertEquals(Scope.Allow, scope);

		groupScope.getAllowedEdges().add(111);
		groupScope.getDeniedEdges().add(112);
		groupScope.setHasEdgeScope("S");

		assertEquals(Scope.Allow, PrivateAccessor.invokePrivateMethod(graph, "getMaxEdgeScope", edge, 1, dataFilter));
	}

	public void testGetMaxEdgeScopeDenied(){
		Edge edge = new Edge();
		edge.setID(111);
		edge.setType(12);
		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxEdgeScope", edge, 1, dataFilter);
		assertEquals(Scope.Denied, scope);

		edge.setID(112);
		edge.setType(11);
		groupScope.getAllowedEdges().add(111);
		groupScope.getDeniedEdges().add(112);
		groupScope.setHasEdgeScope("S");

		assertEquals(Scope.Denied, PrivateAccessor.invokePrivateMethod(graph, "getMaxEdgeScope", edge, 1, dataFilter));
	}

	public void testGetMaxEdgeScopeDeniedByFilter(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return dataFilter == GraphTest.this.dataFilter;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);

		Edge edge = new Edge();
		edge.setID(111);
		edge.setType(11);
		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxEdgeScope", edge, 1, dataFilter);
		assertEquals(Scope.DeniedByPrefilter, scope);
	}

	public void testGetMaxEdgeScopeDeniedBySysFilter(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return dataFilter == sysFilter;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);

		Edge edge = new Edge();
		edge.setID(111);
		edge.setType(11);
		Scope scope = (Scope) PrivateAccessor.invokePrivateMethod(graph, "getMaxEdgeScope", edge, 1, dataFilter);
		assertEquals(Scope.Denied, scope);
	}

	public void testGetChildEdges(){
		Node root = initNodeTree();
		List<Edge> edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getChildEdges", root, 1, dataFilter);
		assertEquals(2, edges.size());
		assertEquals(211, edges.get(0).getID());
		assertEquals(212, edges.get(1).getID());

		root.getOutEdges().get(0).setType(22);
		edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getChildEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(212, edges.get(0).getID());

		root.getOutEdges().get(0).setType(21);
		root.getOutEdges().get(0).getToNode().setType(12);
		edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getChildEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(212, edges.get(0).getID());
	}

	public void testGetChildEdgesFiltered(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return objectId == 211;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);

		Node root = initNodeTree();
		List<Edge> edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getChildEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(212, edges.get(0).getID());
	}

	public void testGetChildEdgesSysFiltered(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return objectId == 211 && dataFilter == sysFilter;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);

		Node root = initNodeTree();
		List<Edge> edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getChildEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(212, edges.get(0).getID());
	}

	public void testGetParentEdges(){
		Node root = initNodeTree();
		List<Edge> edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getParentEdges", root, 1, dataFilter);
		assertEquals(2, edges.size());
		assertEquals(213, edges.get(0).getID());
		assertEquals(214, edges.get(1).getID());

		root.getInEdges().get(0).setType(22);
		edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getParentEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(214, edges.get(0).getID());

		root.getInEdges().get(0).setType(21);
		root.getInEdges().get(0).getFromNode().setType(12);
		edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getParentEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(214, edges.get(0).getID());
	}

	public void testGetParentEdgesFiltered(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return objectId == 213;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);

		Node root = initNodeTree();
		List<Edge> edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getParentEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(214, edges.get(0).getID());
	}

	public void testGetParentEdgesSysFiltered(){
		PreFilterData pf = new PreFilterData(){
			@Override
			public boolean checkObject(int objectId, int entityId, DataFilter dataFilter){
				return objectId == 213 && dataFilter == sysFilter;
			}
		};

		PrivateAccessor.setPrivateField(graph, "filterData", pf);

		Node root = initNodeTree();
		List<Edge> edges = (List<Edge>) PrivateAccessor.invokePrivateMethod(graph, "getParentEdges", root, 1, dataFilter);
		assertEquals(1, edges.size());
		assertEquals(214, edges.get(0).getID());
	}

	public void testGetNode(){
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		Node root = initNodeTree();
		nodeMap.put(root.getID(), root);
		PrivateAccessor.setPrivateField(graph, "nodeMap", nodeMap);

		final Node node = graph.getNode(111, 1, new DataFilter(new ArrayList<Prefilter>()));
		assertNotNull(node);
		assertNotSame(root, node);
		assertEquals(2, node.getInEdges().size());
		assertEquals(2, node.getOutEdges().size());
		assertEquals(root.getID(), node.getID());
		assertEquals(root.getType(), node.getType());
	}

	public void testGetEdge(){
		Map<Integer, Edge> edgeMap = new HashMap<Integer, Edge>();
		Node root = initNodeTree();
		final Edge edge = root.getInEdges().get(0);
		edgeMap.put(edge.getID(), edge);
		PrivateAccessor.setPrivateField(graph, "edgeMap", edgeMap);

		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		nodeMap.put(edge.getFromNode().getID(), edge.getFromNode());
		nodeMap.put(edge.getToNode().getID(), edge.getToNode());
		PrivateAccessor.setPrivateField(graph, "nodeMap", nodeMap);

		final Edge resEdge = graph.getEdge(213, 1, new DataFilter(new ArrayList<Prefilter>()));
		assertNotNull(resEdge);
		assertNotSame(edge, resEdge);
		assertEquals(edge.getFromNode().getID(), resEdge.getFromNode().getID());
		assertEquals(edge.getToNode().getID(), resEdge.getToNode().getID());
		assertEquals(edge.getID(), resEdge.getID());
		assertEquals(edge.getType(), resEdge.getType());
		Node from = resEdge.getFromNode();
		Node to = resEdge.getToNode();
		assertEquals(0, from.getInEdges().size());
		assertEquals(1, from.getOutEdges().size());
		assertEquals(2, to.getInEdges().size());
		assertEquals(2, to.getOutEdges().size());
	}

	private Node initNodeTree(){
		Node root = new Node(111);
		root.setType(11);
		Node chNode1 = new Node(112);
		chNode1.setType(11);
		Node chNode2 = new Node(113);
		chNode2.setType(11);
		Node pNode1 = new Node(114);
		pNode1.setType(11);
		Node pNode2 = new Node(115);
		pNode2.setType(11);

		Edge chEdge1 = new Edge();
		chEdge1.setID(211);
		chEdge1.setType(21);
		chEdge1.setFromNode(root);
		chEdge1.setToNode(chNode1);

		Edge chEdge2 = new Edge();
		chEdge2.setID(212);
		chEdge2.setType(21);
		chEdge2.setFromNode(root);
		chEdge2.setToNode(chNode2);

		Edge pEdge1 = new Edge();
		pEdge1.setID(213);
		pEdge1.setType(21);
		pEdge1.setFromNode(pNode1);
		pEdge1.setToNode(root);

		Edge pEdge2 = new Edge();
		pEdge2.setID(214);
		pEdge2.setType(21);
		pEdge2.setFromNode(pNode2);
		pEdge2.setToNode(root);

		root.addInEdge(pEdge1);
		root.addInEdge(pEdge2);

		root.addOutEdge(chEdge1);
		root.addOutEdge(chEdge2);

		chNode1.addInEdge(pEdge1);
		chNode2.addInEdge(pEdge2);

		pNode1.addOutEdge(chEdge1);
		pNode2.addOutEdge(chEdge2);

		return root;
	}

	public void testGetAllConnectedNodes(){
		List<Integer> expected = new ArrayList<Integer>();
		Graph graph = makeGraph();
		List<Integer> result = graph.getAllConnectedNodes(1);
		assertEquals(expected, result);

		result = graph.getAllConnectedNodes(2);
		expected.add(3);
		expected.add(4);
		expected.add(5);
		Collections.sort(result);
		assertEquals(expected, result);
	}

	private Graph makeGraph(){
		Graph graph = new Graph();
		Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
		Map<Integer, Edge> edgeMap = new HashMap<Integer, Edge>();
		PrivateAccessor.setPrivateField(graph, "nodeMap", nodeMap);
		PrivateAccessor.setPrivateField(graph, "edgeMap", edgeMap);

		nodeMap.put(1, new Node(1));
		Node node2 = new Node(2);
		nodeMap.put(2, node2);
		node2.addOutEdge(new Edge(5));
		node2.addOutEdge(new Edge(6));
		node2.addInEdge(new Edge(7));

		node2.getOutEdges().get(0).setFromNode(node2);
		node2.getOutEdges().get(0).setToNode(new Node(3));
		node2.getOutEdges().get(1).setFromNode(node2);
		node2.getOutEdges().get(1).setToNode(new Node(4));
		node2.getInEdges().get(0).setToNode(node2);
		node2.getInEdges().get(0).setFromNode(new Node(5));

		return graph;
	}

	public void testGetAllEdges(){
		Set<Integer> expected = new HashSet<Integer>();
		graph = makeGraph();
		Collection<Integer> result = graph.getAllEdges(1);
		assertEquals(expected, result);

		expected.add(5);
		expected.add(6);
		expected.add(7);
		result = graph.getAllEdges(2);
		assertEquals(expected, result);
	}
}
