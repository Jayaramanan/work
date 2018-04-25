/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.graphXXL;

import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.dao.*;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.GroupScopeProvider;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.type.Scope;
import com.ni3.ag.navigator.server.util.IntHashMap;
import com.ni3.ag.navigator.shared.domain.Prefilter;
import org.apache.log4j.Logger;

public class Graph extends GraphNi3Engine{
	private static Logger log = Logger.getLogger(Graph.class);

	private Map<Integer, GroupScope> groupScopes;

	private PreFilterData filterData;
	private Map<Integer, DataFilter> sysFilters;

	private Map<Integer, Boolean> edgeTypeToStrengthMap;
	private Map<Integer, Integer> predefinedValues;
	protected Date lastUpdateTime;
	protected List<ObjectDefinition> nodeTypes;
	protected List<ObjectDefinition> edgeTypes;

	// for testing purposes
	Graph(){
		super(0);
	}

	public Graph(int schema){
		super(schema);
		final long current = System.currentTimeMillis();
		log.debug("Loading graph...");
		filterData = new PreFilterData(schema);
		loadGraph();
		log.info("Graph loaded: " + ((System.currentTimeMillis() - current) / 1000) + " sec");
	}

	private List<Edge> getChildEdges(Node node, int groupId, DataFilter dataFilter){
		List<Edge> edges = new ArrayList<Edge>();
		Scope nodeScope, edgeScope;
		for (final Edge edge : node.getOutEdges()){
			edgeScope = getMaxEdgeScope(edge, groupId, dataFilter);

			if (edgeScope.getValue() > Scope.DeniedByPrefilter.getValue()){
				final Node toNode = edge.getToNode();
				nodeScope = getMaxNodeScope(toNode, groupId, dataFilter);
				if (nodeScope.getValue() > Scope.DeniedByPrefilter.getValue())
					edges.add(edge);
			}
		}

		return edges;
	}

	private List<Edge> getParentEdges(Node node, int groupId, DataFilter dataFilter){
		List<Edge> edges = new ArrayList<Edge>();
		Scope nodeScope, edgeScope;
		for (final Edge edge : node.getInEdges()){
			edgeScope = getMaxEdgeScope(edge, groupId, dataFilter);

			if (edgeScope.getValue() > Scope.DeniedByPrefilter.getValue()){
				final Node fromNode = edge.getFromNode();
				nodeScope = getMaxNodeScope(fromNode, groupId, dataFilter);
				if (nodeScope.getValue() > Scope.DeniedByPrefilter.getValue())
					edges.add(edge);
			}
		}

		return edges;
	}

	@Override
	public void deleteEdge(int EdgeID){
		if (!edgeMap.containsKey(EdgeID)){
			log.warn("DELETE edge not in map" + EdgeID);
			return;
		}
		final Edge edge = edgeMap.get(EdgeID);

		unlinkFromID(edge);
		unlinkToID(edge);

		edgeMap.remove(EdgeID);
		log.debug("Edge deleted: " + EdgeID);
	}

	@Override
	public void deleteNode(int nodeID){
		if (!nodeMap.containsKey(nodeID)){
			log.warn("DELETE node not in map" + nodeID);
			return;
		}
		final Node node = nodeMap.get(nodeID);
		if (!node.getInEdges().isEmpty())
			log.warn("DELETE: not all IN edges are deleted from node");
		if (!node.getOutEdges().isEmpty())
			log.warn("DELETE: not all OUT edges are deleted from node");
		nodeMap.remove(nodeID);
		log.debug("DELETE node " + nodeID);
	}

	@Override
	public List<Edge> getEdgesByFavorite(int favoriteId, int groupId, DataFilter dataFilter){
		List<Edge> edges = new ArrayList<Edge>();
		EdgeDAO edgeDAO = NSpringFactory.getInstance().getEdgeDao();
		List<Integer> edgeIds = edgeDAO.getEdgeIdsByFavorite(favoriteId);
		for (Integer edgeId : edgeIds){
			Edge edge = getEdge(edgeId, groupId, dataFilter);
			if (edge != null){
				edges.add(edge);
			}
		}
		return edges;
	}

	@Override
	public List<Object> findPath(int fromNodeId, int toNodeId, int maxPathLength, int pathLengthOverrun, int groupId,
			DataFilter dataFilter){
		List<Object> pathObjects = new ArrayList<Object>();
		log.debug("Find path between " + fromNodeId + " and " + toNodeId + " MaxPath=" + maxPathLength + " Overrun="
				+ pathLengthOverrun);
		if (!nodeMap.containsKey(fromNodeId)){
			log.warn("Node " + fromNodeId + " not found in graph");
			return pathObjects;
		}

		if (!nodeMap.containsKey(toNodeId)){
			log.warn("Node " + toNodeId + " not found in graph");
			return pathObjects;
		}
		final IntHashMap degree = new IntHashMap(100000);
		final IntHashMap degreeSecond = new IntHashMap(100000);

		markDegree(nodeMap.get(fromNodeId), 0, maxPathLength, degree, groupId, dataFilter);
		markDegree(nodeMap.get(toNodeId), 0, maxPathLength, degreeSecond, groupId, dataFilter);

		if (log.isDebugEnabled())
			log.debug("Index2 " + toNodeId + " Degree " + degree.get(toNodeId) + " DegreeSecond "
					+ degreeSecond.get(toNodeId));

		if (degree.get(toNodeId) + degreeSecond.get(toNodeId) <= maxPathLength){
			final Node node = getNode(toNodeId, groupId, dataFilter);
			pathObjects.add(node);
			final int maxDegree = Math.min(degree.get(toNodeId) + pathLengthOverrun, maxPathLength);
			final List<Edge> edges = showPath(maxDegree, groupId, degree, degreeSecond, dataFilter);
			pathObjects.addAll(edges);
		} else{
			final Node fromNode = getNode(fromNodeId, groupId, dataFilter);
			final Node toNode = getNode(toNodeId, groupId, dataFilter);
			pathObjects.add(fromNode);
			pathObjects.add(toNode);
		}
		return pathObjects;
	}

	@Override
	public Edge getEdge(int edgeID, int groupId, DataFilter dataFilter){
		if (!edgeMap.containsKey(edgeID)){
			log.warn("Edge " + edgeID + " not found in graph");
			return null;
		}
		final Edge edge = edgeMap.get(edgeID);

		final Scope edgeScope = getMaxEdgeScope(edge, groupId, dataFilter);

		if (edgeScope.getValue() < Scope.External.getValue())
			return null;

		final Node firstNode = edge.getFromNode();
		final Node secondNode = edge.getToNode();

		if (firstNode == null || secondNode == null)
			return null;

		Node fromNode = getNode(firstNode.getID(), groupId, dataFilter);
		Node toNode = getNode(secondNode.getID(), groupId, dataFilter);

		if (fromNode == null || toNode == null){
			return null;
		}
		Edge resultEdge = new Edge();
		edge.copyTo(resultEdge);
		resultEdge.setFromNode(fromNode);
		resultEdge.setToNode(toNode);

		return resultEdge;
	}

	private Map<Integer, Boolean> getEdgeTypeToStrengthMap(){
		log.debug("Getting edge type to strength map");
		final Map<Integer, Boolean> res = new HashMap<Integer, Boolean>();
		final SchemaLoaderService service = NSpringFactory.getInstance().getSchemaLoaderService();
		final List<Schema> allSchemas = service.getAllSchemas();
		for (final Schema s : allSchemas){
			log.debug("schema id " + s.getId());
			final List<ObjectDefinition> definitions = s.getDefinitions();
			for (final ObjectDefinition e : definitions)
				if (e.isEdge()){
					final Attribute strength = e.getAttribute(Attribute.STRENGTH_ATTRIBUTE_NAME);
					final boolean predefined = strength.isPredefined();
					res.put(e.getId(), predefined);
					log.trace("Found " + e.getId() + " -> " + predefined);
				}
		}
		return res;
	}

	private Scope getMaxEdgeScope(Edge edge, int groupId, DataFilter dataFilter){
		GroupScope gs = groupScopes.get(groupId);
		Scope scope = gs.getEdgeScope(edge.getID(), edge.getType());
		if (scope.getValue() > Scope.Denied.getValue()){
			if (filterData.checkObject(edge.getID(), edge.getType(), getSystemFilter(groupId))){
				log.trace("Edge " + edge.getID() + " SysFiltered out");
				return Scope.Denied;
			}

			if (filterData.checkObject(edge.getID(), edge.getType(), dataFilter)){
				log.trace("Edge " + edge.getID() + " filtered out");
				return Scope.DeniedByPrefilter;
			}
		}

		return scope;
	}

	private Scope getMaxNodeScope(Node node, int groupId, DataFilter dataFilter){
		GroupScope gs = groupScopes.get(groupId);
		Scope scope = gs.getNodeScope(node.getID(), node.getType());

		if (scope.getValue() > Scope.Denied.getValue()){
			if (filterData.checkObject(node.getID(), node.getType(), getSystemFilter(groupId))){
				log.trace("Node " + node.getID() + " SysFiltered out");
				return Scope.Denied;
			}

			if (filterData.checkObject(node.getID(), node.getType(), dataFilter)){
				log.trace("Node " + node.getID() + " filtered out");
				return Scope.DeniedByPrefilter;
			}
		}

		return scope;
	}

	@Override
	public Node getNode(int nodeId, int groupId, DataFilter dataFilter){
		final Node node = getNode(nodeId);
		if (node == null){
			log.warn("Node " + nodeId + " not in graph");
			return null;
		}

		Node result = null;
		final Scope scope = getMaxNodeScope(node, groupId, dataFilter);
		if (scope == Scope.Allow){
			result = new Node();
			node.copyDataTo(result);
			final List<Edge> childEdges = getChildEdges(node, groupId, dataFilter);
			final List<Edge> parentEdges = getParentEdges(node, groupId, dataFilter);
			result.setOutEdges(childEdges);
			result.setInEdges(parentEdges);
		}

		return result;
	}

	private float getPredefinedStrength(int objectType, float strength){
		Boolean isStrengthPredefined = edgeTypeToStrengthMap.get(objectType);
		if (isStrengthPredefined == null){
			isStrengthPredefined = false;
			log.warn("strange edge type " + objectType + ". Setting strength to default");
		}
		if (isStrengthPredefined){
			final Integer predefinedValue = predefinedValues.get((int) strength);
			if (predefinedValue == null){
				log.warn("Predefined attribute with id = " + strength
						+ " was not found. Setting strength to default. Please check database!");
				return 0;
			} else
				return predefinedValue;
		}
		return strength;
	}

	@Override
	public List<Object> getNodeWithEdges(int rootID, int groupId, DataFilter dataFilter){
		List<Object> results = new ArrayList<Object>();
		if (!nodeMap.containsKey(rootID)){
			log.warn("Node " + rootID + " not in graph");
			return results;
		}

		final Node root = getNode(rootID, groupId, dataFilter);
		if (root != null){
			results.add(root);
			if (!root.getInEdges().isEmpty() || !root.getOutEdges().isEmpty()){
				for (Edge edge : root.getInEdges()){
					final Edge e = getEdge(edge.getID(), groupId, dataFilter);
					if (e != null){
						results.add(e);
					}
				}
				for (Edge edge : root.getOutEdges()){
					final Edge e = getEdge(edge.getID(), groupId, dataFilter);
					if (e != null){
						results.add(e);
					}
				}
			}
		}
		return results;
	}

	private Scope getScope(final String sFlag){
		if (sFlag == null || sFlag.isEmpty())
			return Scope.Denied;
		else
			switch (sFlag.charAt(0)){
				case 'N':
					return Scope.Allow;
				case 'E':
					return Scope.External;
				default:
					return Scope.Denied;
			}
	}

	private void initEdges(){
		edgeTypeToStrengthMap = getEdgeTypeToStrengthMap();
		final EdgeDAO edgeDAO = NSpringFactory.getInstance().getEdgeDao();
		edgeMap = edgeDAO.getEdges(edgeTypes);
		List<Integer> incorrectEdges = new ArrayList<Integer>();
		for (final Edge e : edgeMap.values()){
			// replace mock nodes with actual references
			Node from = nodeMap.get(e.getFromNode().getID());
			if (from == null){
				log.error("Cannot find node (fromid=" + e.getFromNode().getID() + ") for edge with id=" + e.getID());
				incorrectEdges.add(e.getID());
				continue;
			}
			e.setFromNode(from);

			Node to = nodeMap.get(e.getToNode().getID());
			if (to == null){
				log.error("Cannot find node (toid=" + e.getToNode().getID() + ") for edge with id=" + e.getID());
				incorrectEdges.add(e.getID());
				continue;
			}
			e.setToNode(to);

			if (e.getStatus() > 1)
				e.setStatus(predefinedValues.get(e.getStatus()));
			if (e.getDirected() > 1)
				e.setDirected(predefinedValues.get(e.getDirected()));
			Boolean isStrengthPredefined = edgeTypeToStrengthMap.get(e.getType());
			if (isStrengthPredefined == null){
				log.warn("Edge type with id " + e.getType() + " was not found. Setting edge strength to default");
				isStrengthPredefined = Boolean.FALSE;
			}
			// TODO use here function getPredefinedStrength()
			if (isStrengthPredefined){
				final Integer predefinedValue = predefinedValues.get((int) e.getStrength());
				if (predefinedValue != null)
					e.setStrength(predefinedValue);
			}
			if (e.getInPath() > 1)
				e.setInPath(predefinedValues.get(e.getInPath()));
			linkEdge(e);
		}

		for (Integer id : incorrectEdges){
			edgeMap.remove(id);
		}
	}

	private void initNodeScopes(Integer nodeId, List<ObjectDefinition> nodeTypes){
		GroupScopeProvider groupScopeProvider = NSpringFactory.getInstance().getGroupScopeProvider();
		for (Integer groupId : groupScopes.keySet()){
			GroupScope gs = groupScopes.get(groupId);
			if (gs.hasNodeScope()){
				List<Integer> scopedNodes = groupScopeProvider.getNodeScope(groupId);
				if (scopedNodes != null && !scopedNodes.isEmpty()){
					gs.getAllowedNodes().addAll(scopedNodes);
				}
			}
		}

		ObjectScopeDAO objectScopeDAO = NSpringFactory.getInstance().getObjectScopeDAO();
		List<NodeScope> nodeScopes = objectScopeDAO.getNodeScopes(nodeTypes, nodeId);
		for (NodeScope ns : nodeScopes){
			final Node node = nodeMap.get(ns.getNodeId());
			if (node == null){
				log.warn("Scoped node cannot be found: " + ns.getNodeId());
				continue;
			}
			final GroupScope gs = groupScopes.get(ns.getGroupId());
			if (gs == null){
				log.warn("Scoped group cannot be found: " + ns.getGroupId());
				continue;
			}

			Scope scope = getScope(ns.getFlag());
			switch (scope){
				case External:
				case Allow:
					gs.getAllowedNodes().add(node.getID());
					break;
				case Denied:
				default:
					gs.getDeniedNodes().add(node.getID());
					break;
			}
		}
	}

	private void initEdgeScopes(Integer edgeId, List<ObjectDefinition> edgeTypes){
		GroupScopeProvider groupScopeProvider = NSpringFactory.getInstance().getGroupScopeProvider();
		for (Integer groupId : groupScopes.keySet()){
			GroupScope gs = groupScopes.get(groupId);
			if (gs.hasEdgeScope()){
				List<Integer> scopedEdges = groupScopeProvider.getEdgeScope(groupId);
				if (scopedEdges != null && !scopedEdges.isEmpty()){
					gs.getAllowedEdges().addAll(scopedEdges);
				}
			}
		}

		ObjectScopeDAO objectScopeDAO = NSpringFactory.getInstance().getObjectScopeDAO();
		List<EdgeScope> edgeScopes = objectScopeDAO.getEdgeScopes(edgeTypes, edgeId);
		for (EdgeScope es : edgeScopes){
			final Edge edge = edgeMap.get(es.getEdgeId());
			if (edge == null){
				log.warn("Scoped edge cannot be found: " + es.getEdgeId());
				continue;
			}
			final GroupScope gs = groupScopes.get(es.getGroupId());
			if (gs == null){
				log.warn("Scoped group cannot be found: " + es.getGroupId());
				continue;
			}

			final Scope scope = getScope(es.getFlag());
			switch (scope){
				case External:
				case Allow:
					gs.getAllowedNodes().add(edge.getID());
					break;
				case Denied:
				default:
					gs.getDeniedNodes().add(edge.getID());
					break;
			}
		}
	}

	private void initGroupScopes(Schema schema, List<Group> groups){
		log.debug("init group scopes");
		groupScopes = new HashMap<Integer, GroupScope>();
		for (final Group g : groups){
			GroupScope gs = new GroupScope();
			gs.setHasNodeScope(g.getNodeScope());
			gs.setHasEdgeScope(g.getEdgeScope());
			groupScopes.put(g.getId(), gs);
		}

		for (ObjectDefinition od : schema.getDefinitions()){
			for (ObjectDefinitionGroup odg : od.getObjectPermissions()){
				GroupScope gs = groupScopes.get(odg.getGroupId());
				if (gs == null){
					log.warn("Error cannot find group with id: " + odg.getGroupId());
					continue;
				}
				if (odg.isCanRead()){
					gs.getAllowedEntities().add(odg.getObject().getId());
				}
			}
		}
	}

	private void recalculateNodeScope(Node node){
		for (GroupScope gs : groupScopes.values()){
			gs.clearNode(node.getID());
		}
		ObjectDefinition entity = null;
		for (ObjectDefinition ent : nodeTypes){
			if (ent.getId() == node.getType()){
				entity = ent;
			}
		}
		initEdgeScopes(node.getID(), Arrays.asList(entity));
	}

	private void recalculateEdgeScope(Edge edge){
		for (GroupScope gs : groupScopes.values()){
			gs.clearEdge(edge.getID());
		}
		ObjectDefinition entity = null;
		for (ObjectDefinition ent : edgeTypes){
			if (ent.getId() == edge.getType()){
				entity = ent;
			}
		}
		initEdgeScopes(edge.getID(), Arrays.asList(entity));
	}

	private void initNodes(){
		final NodeDAO nodeDAO = NSpringFactory.getInstance().getNodeDAO();
		nodeMap = nodeDAO.getNodes(nodeTypes);
		for (final Node n : nodeMap.values())
			if (n.getStatus() > 1)
				n.setStatus(predefinedValues.get(n.getStatus()));
	}

	private void initPredefinedAttributeMap(){
		final PredefinedAttributesDAO dao = NSpringFactory.getInstance().getPredefinedAttributesDao();
		predefinedValues = dao.getNumericPredefinedValues(schemaId);
	}

	@Override
	public DataFilter createDataFilter(List<Integer> filteredValueIds){
		SchemaLoaderService schemaLoader = NSpringFactory.getInstance().getSchemaLoaderService();
		Schema schema = schemaLoader.getSchema(schemaId);
		DataFilter df = new DataFilter(schema, filteredValueIds);
		return df;
	}

	protected DataFilter getSystemFilter(int groupId){
		return sysFilters.get(groupId);
	}

	private void linkEdge(final Edge edge){
		edge.getFromNode().addOutEdge(edge);
		edge.getToNode().addInEdge(edge);
	}

	private void linkFromID(final Edge edge, final Node fromNode){
		edge.setFromNode(fromNode);
		fromNode.addOutEdge(edge);
	}

	private void linkToID(final Edge edge, final Node toNode){
		edge.setToNode(toNode);
		toNode.addInEdge(edge);
	}

	private void loadGraph(){
		try{
			lastUpdateTime = new Date();
			log.info("Loading graph from database");

			final SchemaLoaderService service = NSpringFactory.getInstance().getSchemaLoaderService();
			Schema schema = service.getSchema(schemaId);
			if (schema == null){
				log.error("Error load graph for schema for id " + schemaId);
				setGraphLoaded(false);
				return;
			}
			nodeTypes = new ArrayList<ObjectDefinition>();
			edgeTypes = new ArrayList<ObjectDefinition>();
			for (ObjectDefinition od : schema.getDefinitions()){
				if (od.isNode())
					nodeTypes.add(od);
				else if (od.isEdge())
					edgeTypes.add(od);
				else
					log.warn("Invalid entity type " + od.getObjectTypeId());
			}
			initPredefinedAttributeMap();
			final GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
			final List<Group> groups = groupDAO.getGroups();

			initGroupScopes(schema, groups);
			initNodes();
			initEdges();

			initNodeScopes(null, nodeTypes);
			initEdgeScopes(null, edgeTypes);

			initSysFilters(groups, schema);

			setGraphLoaded(true);
		} catch (final Exception e){
			log.error("Error loading graph", e);
			setGraphLoaded(false);
		}
		if (log.isInfoEnabled()){
			log.info("Graph loaded successfully: " + isGraphLoaded());
			log.info("Graph loaded " + nodeMap.size() + " nodes, " + edgeMap.size() + " edges");
		}
	}

	protected void initSysFilters(List<Group> groups, Schema schema){
		sysFilters = new HashMap<Integer, DataFilter>();
		final PrefilterDAO prefilterDAO = NSpringFactory.getInstance().getPrefilterDAO();
		for (Group group : groups){
			final List<Prefilter> prefilters = prefilterDAO.getPrefilter(group.getId(), schema.getId());
			final DataFilter filter = new DataFilter(prefilters);
			sysFilters.put(group.getId(), filter);
		}
	}

	private void markDegree(Node rootNode, int degree, int MaxDegree, IntHashMap DegreeToMark, int groupId,
			DataFilter dataFilter){
		if (degree <= MaxDegree && DegreeToMark.get(rootNode.getID()) > degree){
			DegreeToMark.put(rootNode.getID(), degree);

			for (final Edge edge : rootNode.getOutEdges()){
				final Node secondNode = edge.getToNode();
				if (secondNode == rootNode)
					continue;
				if (getMaxNodeScope(secondNode, groupId, dataFilter).getValue() < Scope.External.getValue())
					continue;
				if (getMaxEdgeScope(edge, groupId, dataFilter).getValue() < Scope.External.getValue())
					continue;
				if (DegreeToMark.get(secondNode.getID()) > degree + 1)
					if (edge.getInPath() == 1)
						markDegree(secondNode, degree + 1, MaxDegree, DegreeToMark, groupId, dataFilter);
			}

			for (final Edge edge : rootNode.getInEdges()){
				final Node secondNode = edge.getFromNode();

				if (secondNode == rootNode)
					continue;
				if (getMaxNodeScope(secondNode, groupId, dataFilter).getValue() < Scope.External.getValue())
					continue;
				if (getMaxEdgeScope(edge, groupId, dataFilter).getValue() < Scope.External.getValue())
					continue;
				if (DegreeToMark.get(secondNode.getID()) > degree + 1)
					if (edge.getInPath() == 1)
						markDegree(secondNode, degree + 1, MaxDegree, DegreeToMark, groupId, dataFilter);
			}
		}
	}

	@Override
	public boolean newEdge(int EdgeID){
		if (edgeMap.containsKey(EdgeID)){
			log.warn("ADD: edge " + EdgeID + " already in map");
			return true;
		}
		EdgeDAO edgeDAO = NSpringFactory.getInstance().getEdgeDao();
		Edge newEdge = edgeDAO.get(EdgeID);
		return newEdge(newEdge.getID(), newEdge.getFromNode().getID(), newEdge.getToNode().getID(), newEdge.getType(),
				newEdge.getConnectionType(), newEdge.getStrength(), newEdge.getInPath(), newEdge.getStatus(), newEdge
						.getDirected(), newEdge.getFavoriteId(), newEdge.getCreatorUser(), newEdge.getCreatorGroup(),
				newEdge.isContextEdge());
	}

	@Override
	public boolean newEdge(int EdgeID, int FromID, int ToID, int objectType, int connectionType, float strength, int inPath,
			int status, int Directed, int favoritesID, int userID, int groupID, boolean contextEdge){
		if (edgeMap.containsKey(EdgeID)){
			log.warn("ADD: edge " + EdgeID + " already in map");
			return true;
		}

		final Edge newEdge = new Edge();
		newEdge.setFromNode(nodeMap.get(FromID));
		newEdge.setToNode(nodeMap.get(ToID));
		if (newEdge.getFromNode() == null || newEdge.getToNode() == null){
			log.warn("Cannot add new edge to graph: fromnode=" + newEdge.getFromNode() + " toedge=" + newEdge.getToNode()
					+ "(" + FromID + ", " + ToID + ")");
			return false;
		}

		strength = getPredefinedStrength(objectType, strength);

		if (inPath > 1)
			inPath = predefinedValues.get(inPath);

		if (status > 1)
			status = predefinedValues.get(status);

		if (Directed > 1)
			Directed = predefinedValues.get(Directed);

		newEdge.setID(EdgeID);
		newEdge.setStatus(status);
		newEdge.setDirected(Directed);
		newEdge.setConnectionType(connectionType);
		newEdge.setType(objectType);
		newEdge.setStrength(strength);
		newEdge.setInPath(inPath);
		newEdge.setFavoriteId(favoritesID);
		newEdge.setCreatorUser(userID);
		newEdge.setCreatorGroup(groupID);
		newEdge.setContextEdge(contextEdge);

		edgeMap.put(EdgeID, newEdge);

		linkEdge(newEdge);

		recalculateEdgeScope(newEdge);
		return true;
	}

	@Override
	public boolean newNode(int newNodeID, ObjectDefinition entity){
		if (nodeMap.containsKey(newNodeID)){
			log.warn("ADD: node " + newNodeID + " already in map");
			return true;
		}

		final NodeDAO nodeDAO = NSpringFactory.getInstance().getNodeDAO();
		final Node newNode = nodeDAO.get(newNodeID, entity);
		if (newNode == null){
			log.error("Error get node from DB");
			return false;
		}
		nodeMap.put(newNode.getID(), newNode);
		if (newNode.getStatus() > 1)
			newNode.setStatus(predefinedValues.get(newNode.getStatus()));
		filterData.updateObject(newNodeID, newNode.getType());

		recalculateNodeScope(newNode);
		return true;
	}

	private void relinkFromID(Edge edge, Node fromNode){
		unlinkFromID(edge);
		linkFromID(edge, fromNode);
	}

	private void relinkToID(Edge edge, Node toNode){
		unlinkToID(edge);
		linkToID(edge, toNode);
	}

	@Override
	public boolean reloadEdge(int id){
		Edge edge = edgeMap.get(id);
		EdgeDAO edgeDAO = NSpringFactory.getInstance().getEdgeDao();
		Edge dbEdge = edgeDAO.get(id);
		if (dbEdge == null){
			log.warn("Error load edge from DB: " + id);
			return false;
		}
		dbEdge.copyTo(edge);

		edge.setStrength(getPredefinedStrength(edge.getType(), edge.getStrength()));

		if (edge.getInPath() > 1)
			edge.setInPath(predefinedValues.get(edge.getInPath()));

		if (edge.getStatus() > 1)
			edge.setStatus(predefinedValues.get(edge.getStatus()));

		if (edge.getDirected() > 1)
			edge.setDirected(predefinedValues.get(edge.getDirected()));

		recalculateEdgeScope(edge);
		return true;
	}

	@Override
	public Node reloadNode(int newNodeID){
		Node node = nodeMap.get(newNodeID);
		final NodeDAO nodeDAO = NSpringFactory.getInstance().getNodeDAO();
		final Node dbNode = nodeDAO.get(newNodeID);
		if (dbNode == null){
			log.warn("Error load node from DB " + newNodeID);
			return null;
		}
		dbNode.copyDataTo(node);
		if (node.getStatus() > 1)
			node.setStatus(predefinedValues.get(node.getStatus()));
		filterData.updateObject(newNodeID, node.getType());
		recalculateNodeScope(node);
		return node;
	}

	@Override
	public Node reloadNode(int newNodeID, int groupId, DataFilter dataFilter){
		Node node = reloadNode(newNodeID);
		Node result = null;
		if (node != null){
			result = getNode(newNodeID, groupId, dataFilter);
		}
		return result;
	}

	@Override
	public void removeTopicEdges(int TopicID){
		final Set<Integer> keySet = edgeMap.keySet();
		final Integer[] edgeIds = keySet.toArray(new Integer[keySet.size()]);
		for (final int id : edgeIds){
			final Edge e = edgeMap.get(id);
			if (e.getFavoriteId() == TopicID && e.isContextEdge())
				deleteEdge(e.getID());
		}
	}

	private List<Edge> showPath(int maxDegree, int groupId, IntHashMap degree, final IntHashMap degreeSecond,
			DataFilter dataFilter){
		final List<Edge> edges = new ArrayList<Edge>();
		int degree1, degree2;
		for (final Integer eID : edgeMap.keySet()){
			final Edge edge = edgeMap.get(eID);
			final int fromNodeID = edge.getFromNode().getID();
			final int toNodeID = edge.getToNode().getID();

			degree1 = degree.get(fromNodeID) + degreeSecond.get(fromNodeID);
			degree2 = degree.get(toNodeID) + degreeSecond.get(toNodeID);

			if ((degree1 <= maxDegree) && (degree2 <= maxDegree))
				if (((edge.getFromNode().getEdgeCount() > 1) && (edge.getToNode().getEdgeCount() > 1))
						|| degree.get(fromNodeID) == 0 || degree.get(toNodeID) == 0 || degreeSecond.get(fromNodeID) == 0
						|| degreeSecond.get(toNodeID) == 0){
					log.debug(" Showpath edge " + eID + ": " + degree.get(fromNodeID) + "," + degreeSecond.get(fromNodeID)
							+ "," + degree.get(toNodeID) + "," + degreeSecond.get(toNodeID));
					if (!degree.get(fromNodeID).equals(degree.get(toNodeID))
							|| !degreeSecond.get(fromNodeID).equals(degreeSecond.get(toNodeID))){
						final Edge pEdge = getEdge(eID, groupId, dataFilter);
						edges.add(pEdge);
					}
				}
		}

		return edges;
	}

	@Override
	public void syncGraphWithDB(boolean background){
		List<CisObject> updatedObjects = getUpdatedObjects();
		if (updatedObjects.isEmpty()){
			lastUpdateTime = new Date();
			return;
		}

		final SchemaLoaderService service = NSpringFactory.getInstance().getSchemaLoaderService();
		List<Schema> schemas = service.getAllSchemas();
		Map<Integer, ObjectDefinition> entities = new HashMap<Integer, ObjectDefinition>();
		for (Schema sch : schemas)
			for (ObjectDefinition e : sch.getDefinitions())
				entities.put(e.getId(), e);
		log.info("Synchronizing graph with DB");
		if (log.isDebugEnabled())
			log.debug("Last sync was " + lastUpdateTime);

		for (CisObject co : updatedObjects){
			ObjectDefinition e = entities.get(co.getTypeId());
			if (log.isDebugEnabled())
				log.debug("Object: " + co.getId() + " changed since last sync");
			switch (co.getStatus()){
				case Normal:
				case Locked: {
					if (e.isNode()){
						if (!nodeMap.containsKey(co.getId()))
							if (!newNode(co.getId(), e))
								log.warn("Error sync graph - failed add new node: " + co.getId());
						// else
						// user cannot change data of cis_nodes or cis_objects stored in cache
						// so we don't need to reload node event if it is changed
					} else if (e.isEdge()){
						if (edgeMap.containsKey(co.getId()))
							if (!reloadEdge(co.getId()))
								log.warn("Error sync graph - failed reload edge: " + co.getId());
							else if (!newEdge(co.getId()))
								log.warn("Error sync graph - failed add new edge: " + co.getId());
					} else
						log.error("Unknown type " + co.getId());
				}
					break;
				case Deleted:
				case Merged: {
					if (e.isNode())
						deleteNode(co.getId());
					else if (e.isEdge())
						deleteEdge(co.getId());
					else
						log.error("Unknown type " + co.getId());
				}
					break;
			}
		}
		lastUpdateTime = new Date();

		touchUpdatedObjects();
	}

	protected void touchUpdatedObjects(){
		ObjectDAO objectDAO = NSpringFactory.getInstance().getObjectDAO();
		List<ObjectDefinition> types = new ArrayList<ObjectDefinition>();
		types.addAll(nodeTypes);
		types.addAll(edgeTypes);
		objectDAO.fillLastModified(lastUpdateTime, types);
	}

	protected List<CisObject> getUpdatedObjects(){
		ObjectDAO objectDAO = NSpringFactory.getInstance().getObjectDAO();
		List<ObjectDefinition> types = new ArrayList<ObjectDefinition>();
		types.addAll(nodeTypes);
		types.addAll(edgeTypes);
		return objectDAO.getUpdatedObjects(lastUpdateTime, types);
	}

	@Override
	public Edge getEdge(int edgeId){
		return edgeMap.get(edgeId);
	}

	@Override
	public Node getNode(int nodeId){
		return nodeMap.get(nodeId);
	}

	@Override
	public List<Integer> filterEdgesByFromTo(List<Integer> edgeIds, List<Integer> fromIds, List<Integer> toIds, int limit){
		Set<Integer> fromSet = new HashSet<Integer>();
		Set<Integer> toSet = new HashSet<Integer>();
		fromSet.addAll(fromIds);
		toSet.addAll(toIds);
		List<Integer> result = new ArrayList<Integer>();
		for (Integer edgeId : edgeIds){
			Edge e = edgeMap.get(edgeId);
			if (fromSet.contains(e.getFromNode().getID()) && toSet.contains(e.getToNode().getID())){
				result.add(edgeId);
				if (result.size() >= limit)
					break;
			}
		}
		return result;
	}

	/**
	 * for each node in searchedNodeIds node ok if it has connection mentioned in searchedEdgeIds
	 * 
	 * @param searchedEdgeIds
	 *            array with edge ids meets search criteria
	 * @param searchedNodeIds
	 *            array with node ids meets search criteria
	 * @param limit
	 *            count of nodes to limit result
	 * @return array with filtered edge (!) ids
	 */
	@Override
	public List<Integer> filterEdgesByNodes(List<Integer> searchedEdgeIds, List<Integer> searchedNodeIds, int limit){
		List<Integer> result = new ArrayList<Integer>();
		Set<Integer> edgeSet = new HashSet<Integer>();
		Set<Integer> nodeSet = new HashSet<Integer>();
		edgeSet.addAll(searchedEdgeIds);
		for (Integer nodeId : searchedNodeIds){
			Node n = nodeMap.get(nodeId);
			for (Edge e : n.getOutEdges()){
				if (edgeSet.contains(e.getID())){
					result.add(e.getID());
					nodeSet.add(n.getID());
					if (nodeSet.size() >= limit)
						return result;
				}
			}
			for (Edge e : n.getInEdges()){
				if (edgeSet.contains(e.getID())){
					result.add(e.getID());
					nodeSet.add(n.getID());
					if (nodeSet.size() >= limit)
						return result;
				}
			}
		}
		return result;
	}

	@Override
	public List<Integer> getAllConnectedNodes(Integer id){
		List<Integer> result = new ArrayList<Integer>();
		Node node = nodeMap.get(id);
		if (node == null){
			log.warn("No node with id " + id + " found in graph");
			return result;
		}
		for (Edge e : node.getInEdges()){
			result.add(e.getFromNode().getID());
		}
		for (Edge e : node.getOutEdges()){
			result.add(e.getToNode().getID());
		}
		return result;
	}

	@Override
	public Collection<Integer> getAllEdges(Integer id){
		return getAllEdgesWithType(id).keySet();
	}

	@Override
	public Map<Integer, Integer> getAllEdgesWithType(int objectId){
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		Node node = nodeMap.get(objectId);
		if (node == null){
			log.warn("No node with id " + objectId + " found in graph");
			return result;
		}
		for (Edge e : node.getInEdges()){
			result.put(e.getID(), e.getType());
		}
		for (Edge e : node.getOutEdges()){
			result.put(e.getID(), e.getType());
		}
		return result;
	}

	@Override
	public void retainVisibleNodes(Collection<Integer> ids, Group group, DataFilter dataFilter){
		List<Integer> nodeIds = new ArrayList<Integer>();
		nodeIds.addAll(ids);
		for (Integer id : nodeIds){
			if (getNode(id, group.getId(), dataFilter) == null)
				ids.remove(id);
		}
	}

	protected void unlinkFromID(Edge edge){
		edge.getFromNode().removeOutEdge(edge);
	}

	protected void unlinkToID(Edge edge){
		edge.getToNode().removeInEdge(edge);
	}

	@Override
	public boolean updateEdge(int EdgeID){
		if (!edgeMap.containsKey(EdgeID)){
			log.warn("UPDATE: edge " + EdgeID + " not in map");
			return true;
		}
		EdgeDAO edgeDAO = NSpringFactory.getInstance().getEdgeDao();
		final Edge edge = edgeDAO.get(EdgeID);
		return updateEdge(edge.getID(), edge.getFromNode().getID(), edge.getToNode().getID(), edge.getConnectionType(), edge
				.getStrength(), edge.getInPath(), edge.getStatus(), edge.getDirected(), edge.getFavoriteId(), edge.getType());
	}

	@Override
	public boolean updateEdge(int EdgeID, int FromID, int ToID, int connectionType, float strength, int inPath, int status,
			int directed, int favoritesID, int objectType){
		if (!edgeMap.containsKey(EdgeID)){
			log.warn("UPDATE: edge " + EdgeID + " not in map");
			return false;
		}
		final Edge edge = edgeMap.get(EdgeID);
		final Node fromNode = nodeMap.get(FromID);
		final Node toNode = nodeMap.get(ToID);

		edge.setConnectionType(connectionType);
		edge.setStatus(status);
		edge.setDirected(directed);
		edge.setInPath(inPath);
		edge.setStrength(getPredefinedStrength(objectType, strength));
		edge.setFavoriteId(favoritesID);

		if (edge.getDirected() > 1)
			edge.setDirected(predefinedValues.get(edge.getDirected()));
		if (edge.getStatus() > 1)
			edge.setStatus(predefinedValues.get(edge.getStatus()));
		if (edge.getInPath() > 1)
			edge.setInPath(predefinedValues.get(edge.getInPath()));

		log.debug("Update Edge " + EdgeID);

		if (fromNode != edge.getFromNode())
			relinkFromID(edge, fromNode);

		if (toNode != edge.getToNode())
			relinkToID(edge, toNode);

		recalculateEdgeScope(edge);
		return true;
	}

	@Override
	public boolean containsNode(int id){
		return true;
	}
}
