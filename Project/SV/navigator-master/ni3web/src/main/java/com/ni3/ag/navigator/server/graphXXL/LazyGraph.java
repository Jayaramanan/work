package com.ni3.ag.navigator.server.graphXXL;

import java.util.*;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.CISObjectProviderService;
import com.ni3.ag.navigator.server.services.EdgeLoader;
import com.ni3.ag.navigator.server.services.NodeLoader;
import org.apache.log4j.Logger;

public class LazyGraph extends Graph implements NodeLoader, EdgeLoader{
	private static final Logger log = Logger.getLogger(LazyGraph.class);
	private CISObjectProviderService objectProviderService;
	private LazyGroupScope groupScope;
	private LazyFilter objectFilter;

	private Map<Integer, Integer> inPathTrueValues;
	private Map<Integer, Integer> directedTrueValues;
	private Map<Integer, Float> strengthValues;
	private Map<Integer, Boolean> strengthPredefinedFlag;

	public LazyGraph(int schemaId){
		this.schemaId = schemaId;
		lastUpdateTime = new Date();
		objectProviderService = NSpringFactory.getInstance().getCISObjectProviderService();
		nodeMap = new HashMap<Integer, Node>();
		edgeMap = new HashMap<Integer, Edge>();
		Schema schema = NSpringFactory.getInstance().getSchemaLoaderService().getSchema(schemaId);
		groupScope = new LazyGroupScope(schema);
		objectFilter = new LazyFilter(schema);
		if (schema == null){
			log.error("Error load graph for schema for id " + schemaId);
			setGraphLoaded(false);
			return;
		}
		nodeTypes = new ArrayList<ObjectDefinition>();
		edgeTypes = new ArrayList<ObjectDefinition>();
		inPathTrueValues = new HashMap<Integer, Integer>();
		directedTrueValues = new HashMap<Integer, Integer>();
		strengthValues = new HashMap<Integer, Float>();
		strengthPredefinedFlag = new HashMap<Integer, Boolean>();
		for (ObjectDefinition od : schema.getDefinitions()){
			if (od.isNode())
				nodeTypes.add(od);
			else if (od.isEdge()){
				edgeTypes.add(od);
				initPredefinedSubstituteValues(od);
			} else
				log.warn("Invalid entity type " + od.getObjectTypeId());
		}
		final GroupDAO groupDao = NSpringFactory.getInstance().getGroupDao();
		initSysFilters(groupDao.getGroups(), schema);
		setGraphLoaded(true);
	}

	private void initPredefinedSubstituteValues(ObjectDefinition od){
		Attribute inPathAttribute = od.getAttribute("inpath");
		log.debug("Found inPath attribute for object " + od.getName() + " -> " + inPathAttribute);
		int trueValue = 1;
		if (inPathAttribute.isPredefined()){
			for (PredefinedAttribute pa : inPathAttribute.getValues()){
				if ("true".equals(pa.getValue().toLowerCase()) || "1".equals(pa.getValue())){
					trueValue = pa.getId();
					log.debug("True value for inPath: " + pa.getLabel() + " -> " + trueValue);
					break;
				}
			}
		}
		log.debug(od.getName() + "." + inPathAttribute.getName() + " - (true value) " + trueValue);
		inPathTrueValues.put(od.getId(), trueValue);

		Attribute directedAttribute = od.getAttribute("directed");
		trueValue = 1;
		if (directedAttribute.isPredefined()){
			for (PredefinedAttribute pa : directedAttribute.getValues()){
				if ("true".equals(pa.getValue().toLowerCase()) || "1".equals(pa.getValue())){
					trueValue = pa.getId();
					log.debug("True value for directed: " + pa.getLabel() + " -> " + trueValue);
					break;
				}
			}
		}
		log.debug(od.getName() + "." + directedAttribute.getName() + " - (true value) " + trueValue);
		directedTrueValues.put(od.getId(), trueValue);

		Attribute strengthAttribute = od.getAttribute("strength");
		strengthPredefinedFlag.put(strengthAttribute.getEntity().getId(), strengthAttribute.isPredefined());
		if(strengthAttribute.isPredefined())
		{
			for(PredefinedAttribute pa : strengthAttribute.getValues()){
				strengthValues.put(pa.getId(), Float.parseFloat(pa.getValue()));
				log.debug(strengthAttribute.getEntity().getName() + "." + strengthAttribute.getName() + ":" + pa.getId() + "->" + pa.getValue());
			}
		}
	}

	LazyGraph(){
		// test
	}

	@Override
	public Node getNode(int id, int groupId, DataFilter dataFilter){
		Node n = getNode(id);
		Node result = null;
		if (groupScope.isVisible(n, groupId) && !objectFilter.isFiltered(n, dataFilter, getSystemFilter(groupId))){
			result = new Node();
			n.copyDataTo(result);
			final List<Edge> childEdges = getChildEdges(n, groupId, dataFilter);
			final List<Edge> parentEdges = getParentEdges(n, groupId, dataFilter);
			result.setOutEdges(childEdges);
			result.setInEdges(parentEdges);
		}

		return result;
	}

	@Override
	public void deleteNode(int nodeID){
		objectFilter.removeNode(getNode(nodeID));
		super.deleteNode(nodeID);
	}

	@Override
	public Edge getEdge(int edgeID, int groupId, DataFilter dataFilter){
		Edge e = getEdge(edgeID);
		Edge result = null;
		if (groupScope.isVisible(e, groupId) && !objectFilter.isFiltered(e, dataFilter, getSystemFilter(groupId))
				&& e.getFromNode() != null && e.getToNode() != null){
			Node fromNode = getNode(e.getFromNode().getID(), groupId, dataFilter);
			Node toNode = getNode(e.getToNode().getID(), groupId, dataFilter);
			if (fromNode != null && toNode != null){
				result = new Edge();
				e.copyTo(result);
				result.setFromNode(fromNode);
				result.setToNode(toNode);
			}
		}
		return result;
	}

	@Override
	public List<Edge> getEdgesByFavorite(int favoriteId, int groupId, DataFilter dataFilter){
		List<Integer> ids = objectProviderService.getEdgeListByFavorite(favoriteId);
		List<Edge> edges = new ArrayList<Edge>();
		for (Integer id : ids){
			Edge e = getEdge(id, groupId, dataFilter);
			if (e != null)
				edges.add(e);
		}
		return edges;
	}

	class PathSearchData{
		public Node targetNode;
		public int maxPathLength;
		public int overrun;
		public int groupId;
		public DataFilter dataFilter;

		public int shortest = -1;
		public List<List<Object>> result = new ArrayList<List<Object>>();
	}

	@Override
	public List<Object> findPath(int fromNodeId, int toNodeId, int maxPathLength, int pathLengthOverrun, int groupId,
								 DataFilter dataFilter){
		log.debug("Find path requested");
		log.debug("FromId: " + fromNodeId);
		log.debug("ToId: " + toNodeId);
		log.debug("Max Length: " + maxPathLength);
		log.debug("Overrun: " + pathLengthOverrun);
		log.debug("GroupId: " + groupId);

		Node fromNode = getNode(fromNodeId, groupId, dataFilter);
		if (fromNode == null){
			log.warn("From node not visible for user");
			return Collections.emptyList();
		}
		Node toNode = getNode(toNodeId, groupId, dataFilter);
		if (toNode == null){
			log.warn("To node not visible for user");
			return Collections.emptyList();
		}

		PathSearchData pathSearchData = new PathSearchData();
		pathSearchData.targetNode = toNode;
		pathSearchData.maxPathLength = maxPathLength;
		pathSearchData.overrun = pathLengthOverrun;
		pathSearchData.groupId = groupId;
		pathSearchData.dataFilter = dataFilter;

		Map<Integer, Integer> nodeMarks = new HashMap<Integer, Integer>();
		nodeMarks.put(fromNodeId, 0);

		//make deep search
		searchDepth(fromNode, nodeMarks, 1, pathSearchData);
//		searchBreadth(Arrays.asList(fromNode), nodeMarks, 1, pathSearchData);
		log.debug("Shortest found: " + pathSearchData.shortest);
		List<Object> foundResults = new ArrayList<Object>();
		//filtering results
		for (List<Object> foundPath : pathSearchData.result){
			if (pathSearchData.shortest == -1 || (pathSearchData.shortest + pathLengthOverrun >= foundPath.size() - 1)){
				foundResults.addAll(foundPath);
			} else
				log.debug("Filtering path with size: " + (foundPath.size() - 1) + " because too long, overrun allowed:" + pathLengthOverrun);
		}
		return foundResults;
	}

//	private void searchBreadth(List<Node> fromNodes, Map<Integer, Integer> nodeMarks, int level, PathSearchData data){
//		log.debug("Level: " + level);
//		if(level > data.maxPathLength + data.overrun)
//			return;
//		List<Node> children = new ArrayList<Node>();
//		for(Node n : fromNodes)
//			 children.addAll(getChildren(n, data.groupId, data.dataFilter));
//		for(Node n : children){
//			if(nodeMarks.containsKey(n.getID()))
//				continue;
//			nodeMarks.put(n.getID(), level);
//			if(n.getID() == data.targetNode.getID()){
//				data.result.add(makePath(nodeMarks, level, data));
//				if(data.shortest == -1)
//					data.shortest = level;
//				return;
//			}
//		}
//		searchBreadth(children, nodeMarks, level + 1, data);
//	}

	private void searchDepth(Node fromNode, Map<Integer, Integer> nodeMarks, int level, PathSearchData data){
		if (level > data.maxPathLength + data.overrun)//is too deep?
			return;
		if (data.shortest != -1 && level > data.shortest + data.overrun)//deeper then shortest + overrun?
			return;
		List<Node> children = getChildren(fromNode);
		if (children.isEmpty())
			return;
		for (Node n : children){
			if (!nodeMarks.containsKey(n.getID())){//prevents reverse movement
				if (n.getID() == data.targetNode.getID()){//target found
					log.debug("Found path on level: " + level);
					data.result.add(makePath(nodeMarks, level, data));
					if (data.shortest == -1 || data.shortest > level){
						data.shortest = level;
						log.debug("Setting shortest: " + data.shortest);
					}
					return;
				}
				nodeMarks.put(n.getID(), level);
				searchDepth(n, nodeMarks, level + 1, data);
				nodeMarks.remove(n.getID());
			}
		}
	}

	private List<Object> makePath(Map<Integer, Integer> vertexMarks, int currentLength, PathSearchData data){
		List<Object> results = new ArrayList<Object>();
		log.debug("\tFound path with length: " + currentLength);
		Set<Node> current = new HashSet<Node>();
		current.add(data.targetNode);
		for (int i = currentLength - 1; i >= 0; i--){
			boolean wasOne = false;
			Set<Node> newCurrent = new HashSet<Node>();
			for (Node n : current){
				if(getNode(n.getID(), data.groupId, data.dataFilter) == null)
					continue;
				for (Edge e : n.getOutEdges()){
					if(getEdge(e.getID(), data.groupId, data.dataFilter) == null)
						continue;
					if(getNode(e.getToNode().getID(), data.groupId, data.dataFilter) == null)
						continue;
					if (addEdgeToPathResults(vertexMarks, results, i, e, e.getToNode())){
						newCurrent.add(e.getToNode());
						wasOne = true;
					}
				}
				for (Edge e : n.getInEdges()){
					if (getEdge(e.getID(), data.groupId, data.dataFilter) == null)
						continue;
					if (getNode(e.getFromNode().getID(), data.groupId, data.dataFilter) == null)
						continue;
					if (addEdgeToPathResults(vertexMarks, results, i, e, e.getFromNode())){
						newCurrent.add(e.getFromNode());
						wasOne = true;
					}
				}
			}
			current = newCurrent;
			if(!wasOne){
				log.debug("Nothing visible found on level for path, excluding path from results");
				return Collections.emptyList();
			}
		}
		results.add(data.targetNode);
		Collections.reverse(results);
		return results;
	}

	private boolean addEdgeToPathResults(Map<Integer, Integer> vertexMarks, List<Object> results, int mark, Edge e, Node to){
		if (!vertexMarks.containsKey(to.getID()))
			return false;
		if (vertexMarks.get(to.getID()) == mark){
			results.add(e);
			return true;
		}
		return false;
	}

	private List<Node> getChildren(Node currentVertex){
		List<Node> children = new ArrayList<Node>();
		for (Edge e : currentVertex.getOutEdges()){
			children.add(e.getToNode());
		}
		for (Edge e : currentVertex.getInEdges()){
			children.add(e.getFromNode());
		}
		return children;
	}

//	protected boolean isInPath(Edge e){
//		return e.getInPath() != 0;
//	}

	@Override
	public boolean newNode(int newNodeID, ObjectDefinition objectDefinition){
		if (nodeMap.containsKey(newNodeID)){
			log.warn("Graph already contains node: " + newNodeID);
			return true;
		}
		Node n = getNode(newNodeID);
		objectFilter.updateObject(n);
		return n != null;
	}

	@Override
	public Node reloadNode(int nodeID, int groupId, DataFilter dataFilter){
		reloadNode(nodeID);
		return getNode(nodeID, groupId, dataFilter);
	}

	@Override
	public Node reloadNode(int NodeID){
		Node node = objectProviderService.getNode(NodeID);
		if (node == null){
			log.error("Node with id is not found in db: " + NodeID);
			return null;
		}
		ProxyNode current = (ProxyNode) nodeMap.get(NodeID);
		if (current == null){
			log.error("Cannot reload node: " + NodeID + " - not in map");
			return null;
		}
		node.copyDataTo(current);
		objectFilter.updateObject(current);
		return current;
	}

	@Override
	public boolean reloadEdge(int id){
		Edge edge = edgeMap.get(id);
		if (edge == null){
			log.error("Error reload edge - not in graph: " + id);
			return false;
		}
		Edge dbEdge = objectProviderService.getEdge(id);
		if (dbEdge == null){
			log.warn("Error load edge from DB: " + id);
			return false;
		}
		substitutePredefinedValues(dbEdge);
		dbEdge.copyTo(edge);
		objectFilter.updateObject(edge);
		return true;
	}

	@Override
	public boolean newEdge(int EdgeID, int FromID, int ToID, int ObjectType, int Type, float Strength, int InPath,
						   int status, int Directed, int favoritesID, int userID, int groupID, boolean contextEdge){
		Edge e = getEdge(EdgeID);
		objectFilter.updateObject(e);
		return e != null;
	}

	@Override
	public boolean newEdge(int newEdgeID){
		if (edgeMap.containsKey(newEdgeID)){
			log.error("Edge map already contains edge : " + newEdgeID);
			return false;
		}
		Edge e = getEdge(newEdgeID);
		objectFilter.updateObject(e);
		return e != null;
	}

	@Override
	public boolean updateEdge(int EdgeID){
		if (!edgeMap.containsKey(EdgeID)){
			log.warn("UPDATE: edge " + EdgeID + " not in map");
			return false;
		}
		return reloadEdge(EdgeID);
	}

	@Override
	public boolean updateEdge(int EdgeID, int FromID, int ToID, int connectionType, float strength, int inPath, int status,
							  int Directed, int favoritesID, int objectType){
		return updateEdge(EdgeID);
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
		objectFilter.removeObject(edge);
		edgeMap.remove(EdgeID);
		log.debug("Edge deleted: " + EdgeID);
	}

	protected void touchUpdatedObjects(){
		List<ObjectDefinition> types = new ArrayList<ObjectDefinition>();
		types.addAll(nodeTypes);
		types.addAll(edgeTypes);
		objectProviderService.fillLastModified(lastUpdateTime, types);
	}

	protected List<CisObject> getUpdatedObjects(){
		List<ObjectDefinition> types = new ArrayList<ObjectDefinition>();
		types.addAll(nodeTypes);
		types.addAll(edgeTypes);
		return objectProviderService.getUpdatedObjects(lastUpdateTime, types);
	}

	@Override
	public Edge getEdge(int edgeID){
		if (!edgeMap.containsKey(edgeID)){
			log.debug("Adding new edge to the graph:" + edgeID);
			edgeMap.put(edgeID, new ProxyEdge(edgeID, this));
		}
		return edgeMap.get(edgeID);
	}

	@Override
	public Node getNode(int id){
		if (!nodeMap.containsKey(id)){
			log.debug("adding node " + id + " to the graph");
			nodeMap.put(id, new ProxyNode(id, this));
		}
		return nodeMap.get(id);
	}

	@Override
	// TODO very slow on big data - needs optimization
	public List<Integer> filterEdgesByFromTo(List<Integer> edgeIds, List<Integer> fromIds, List<Integer> toIds, int limit){
		return objectProviderService.getEdgeList(fromIds, toIds, edgeIds, limit);
	}

	@Override
	public List<Integer> filterEdgesByNodes(List<Integer> searchedEdgeIds, List<Integer> searchedNodeIds, int limit){
		return objectProviderService.getEdgeList(searchedNodeIds, searchedEdgeIds, limit);
	}

	@Override
	public List<Integer> getAllConnectedNodes(Integer id){
		return objectProviderService.getConnectedNodesForNode(id);
	}

	@Override
	public Collection<Integer> getAllEdges(Integer id){
		return getAllEdgesWithType(id).keySet();
	}

	@Override
	public Map<Integer, Integer> getAllEdgesWithType(int objectId){
		return objectProviderService.getEdgesWithTypesForNode(objectId);
	}

	@Override
	public void loadNode(int id, ProxyNode target){
		Node n = objectProviderService.getNode(id);
		if (n == null){
			log.warn("Requested node does not exists: " + id);
			nodeMap.remove(id);
			return;
		}
		n.copyDataTo(target);
		List<Edge> inEdges = objectProviderService.getNodeInEdges(id);
		List<Edge> outEdges = objectProviderService.getNodeOutEdges(id);
		List<ProxyEdge> pInEdges = new ArrayList<ProxyEdge>();
		List<ProxyEdge> pOutEdges = new ArrayList<ProxyEdge>();
		for (Edge e : inEdges){
			ProxyEdge pEdge;
			if (edgeMap.containsKey(e.getID()))
				pEdge = (ProxyEdge) edgeMap.get(e.getID());
			else{
				pEdge = new ProxyEdge(e.getID(), this);
				edgeMap.put(pEdge.getID(), pEdge);
			}
			pInEdges.add(pEdge);
		}
		for (Edge e : outEdges){
			ProxyEdge pEdge;
			if (edgeMap.containsKey(e.getID()))
				pEdge = (ProxyEdge) edgeMap.get(e.getID());
			else{
				pEdge = new ProxyEdge(e.getID(), this);
				edgeMap.put(pEdge.getID(), pEdge);
			}
			pOutEdges.add(pEdge);
		}
		target.setInEdges(pInEdges);
		target.setOutEdges(pOutEdges);
	}

	@Override
	public void loadEdge(int id, ProxyEdge proxyEdge){
		Edge e = objectProviderService.getEdge(id);
		if (e == null){
			log.warn("Request not existing edge: " + id);
			edgeMap.remove(id);
			return;
		}
		substitutePredefinedValues(e);
		e.copyTo(proxyEdge);
		Node fromNode = objectProviderService.getFromNode(id);
		Node toNode = objectProviderService.getToNode(id);
		ProxyNode pFromNode;
		ProxyNode pToNode;
		if (nodeMap.containsKey(fromNode.getID()))
			pFromNode = (ProxyNode) nodeMap.get(fromNode.getID());
		else{
			pFromNode = new ProxyNode(fromNode.getID(), this);
			nodeMap.put(pFromNode.getID(), pFromNode);
		}
		if (nodeMap.containsKey(toNode.getID()))
			pToNode = (ProxyNode) nodeMap.get(toNode.getID());
		else{
			pToNode = new ProxyNode(toNode.getID(), this);
			nodeMap.put(pToNode.getID(), pToNode);
		}
		proxyEdge.setFromNode(pFromNode);
		proxyEdge.setToNode(pToNode);
		if (!pFromNode.getOutEdges().contains(proxyEdge))
			pFromNode.addOutEdge(proxyEdge);
		if (!pToNode.getInEdges().contains(proxyEdge))
			pToNode.addInEdge(proxyEdge);
	}

	private void substitutePredefinedValues(Edge srcEdge){
		int inPath = inPathTrueValues.get(srcEdge.getType()) == srcEdge.getInPath() ? 1 : 0;
		srcEdge.setInPath(inPath);
		int directed = directedTrueValues.get(srcEdge.getType()) == srcEdge.getDirected() ? 1 : 0;
		srcEdge.setDirected(directed);
		if(strengthPredefinedFlag.get(srcEdge.getType())){
			Float strength = strengthValues.get((int)srcEdge.getStrength());
			if(strength == null){
				log.error("Edge Id strength is predefined, but value is not ID of predefined: " + srcEdge.getID()  + " -> strength=" + srcEdge.getStrength());
				strength = 0.f;
			}
			srcEdge.setStrength(strength);
		}
	}

	private List<Edge> getChildEdges(Node node, int groupId, DataFilter dataFilter){
		List<Edge> edges = new ArrayList<Edge>();
		for (final Edge edge : node.getOutEdges()){
			if (groupScope.isVisible(edge, groupId) && !objectFilter.isFiltered(edge, dataFilter, getSystemFilter(groupId))
					&& groupScope.isVisible(edge.getToNode(), groupId)
					&& !objectFilter.isFiltered(edge.getToNode(), dataFilter, getSystemFilter(groupId))){
				edges.add(edge);
			}
		}
		return edges;
	}

	private List<Edge> getParentEdges(Node node, int groupId, DataFilter dataFilter){
		List<Edge> edges = new ArrayList<Edge>();
		for (final Edge edge : node.getInEdges()){
			if (groupScope.isVisible(edge, groupId) && !objectFilter.isFiltered(edge, dataFilter, getSystemFilter(groupId))
					&& groupScope.isVisible(edge.getFromNode(), groupId)
					&& !objectFilter.isFiltered(edge.getFromNode(), dataFilter, getSystemFilter(groupId))){
				edges.add(edge);
			}
		}

		return edges;
	}

	@Override
	public boolean containsNode(int id){
		return nodeMap.containsKey(id);
	}
}
