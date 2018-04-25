/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphCache;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.cache.UserGroupCache;
import com.ni3.ag.navigator.server.domain.DataFilter;
import com.ni3.ag.navigator.server.domain.Edge;
import com.ni3.ag.navigator.server.domain.Node;
import com.ni3.ag.navigator.server.services.GraphEngineFactory;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.Graph;
import com.ni3.ag.navigator.shared.proto.NResponse.Graph.Builder;

public class GraphServlet extends Ni3Servlet{
	private static final long serialVersionUID = -3761617399078435724L;
	private static final Logger log = Logger.getLogger(GraphServlet.class);

	@Override
	protected void doInternalPost(HttpServletRequest httpRequest, HttpServletResponse response) throws ServletException,
			IOException{
		final InputStream io = getInputStream(httpRequest);
		final NRequest.Graph request = NRequest.Graph.parseFrom(io);
		final NResponse.Graph.Builder graphResponse = NResponse.Graph.newBuilder();

		final int schemaId = request.getSchemaId();
		final GraphNi3Engine graph = getGraph(schemaId);
		final DataFilter dataFilter = graph.createDataFilter(request.getDataFilter().getValueIdList());

		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final UserGroupCache userGroupCache = NSpringFactory.getInstance().getUserGroupCache();
		final int groupId = userGroupCache.getGroup(storage.getCurrentUser().getId());

		graphResponse.setResult(NResponse.Graph.Result.OK);
		switch (request.getAction()){
			case GET_NODES:
				handleGetNodes(request, graphResponse, graph, groupId, dataFilter);
				break;
			case GET_EDGES:
				handleGetEdges(request, graphResponse, graph, groupId, dataFilter);
				break;
			case GET_NODES_WITH_EDGES:
				handleGetNodesWithEdges(request, graphResponse, graph, groupId, dataFilter);
				break;
			case RELOAD_NODE:
				handleReloadNode(request, graphResponse, graph, groupId, dataFilter);
				break;
			case GET_FAVORITES_EDGES:
				handleGetEdgesByFavorite(request, graphResponse, graph, groupId, dataFilter);
				break;
			case FIND_PATH:
				handleFindPath(request, graphResponse, graph, groupId, dataFilter);
				break;
			case GET_NODES_BY_EDGES:
				handleGetNodesByEdges(request, graphResponse, graph, groupId, dataFilter);
				break;
		}

		final ByteString payload = graphResponse.build().toByteString();
		final NResponse.Envelope.Builder envelope = NResponse.Envelope.newBuilder();
		envelope.setStatus(NResponse.Envelope.Status.SUCCESS);
		envelope.setPayload(payload);
		sendResponse(httpRequest, response, envelope);
	}

	private void handleGetNodesWithEdges(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId,
			DataFilter dataFilter){
		log.debug("Get nodes with edges");
		List<Integer> nodeIds = request.getObjectIdsList();
		int maxNodeCount = request.getMaxNodeCount();

		int edgeCount = 0;
		final Set<Integer> nodeSet = new HashSet<Integer>();
		for (Integer nodeId : nodeIds){
			List<Object> objects = graph.getNodeWithEdges(nodeId, groupId, dataFilter);
			for (Object object : objects){
				if (object instanceof Node){
					NResponse.Node.Builder protoNode = createProtoNode((Node) object);
					graphResponse.addNodes(protoNode);
					nodeSet.add(((Node) object).getID());
				} else{
					edgeCount++;
					NResponse.Edge.Builder protoEdge = createProtoEdge((Edge) object);
					graphResponse.addEdges(protoEdge);
					nodeSet.add(((Edge) object).getFromNode().getID());
					nodeSet.add(((Edge) object).getToNode().getID());
				}

				if (maxNodeCount > 0 && maxNodeCount < nodeSet.size()){
					graphResponse.setResult(NResponse.Graph.Result.TOO_MUCH_NODES);
					graphResponse.clearEdges();
					graphResponse.clearNodes();
					return;
				}
			}
		}
		log.debug("total node count = " + nodeSet.size() + ", edge count = " + edgeCount);
	}

	private void handleFindPath(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId,
			DataFilter dataFilter){
		int fromNodeId = request.getNodeId();
		int toNodeId = request.getNodeToId();
		int maxPathLength = request.getMaxPathLenght();
		if (maxPathLength == 0){
			maxPathLength = 10;
		}
		int pathLengthOverrun = request.getPathLengthOverrun();

		List<Object> pathObjects = graph.findPath(fromNodeId, toNodeId, maxPathLength, pathLengthOverrun, groupId,
				dataFilter);
		for (Object pathObject : pathObjects){
			if (pathObject instanceof Node){
				NResponse.Node.Builder protoNode = createProtoNode((Node) pathObject);
				graphResponse.addNodes(protoNode);
			} else{
				NResponse.Edge.Builder protoEdge = createProtoEdge((Edge) pathObject);
				graphResponse.addEdges(protoEdge);
			}
		}
	}

	private void handleGetEdgesByFavorite(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId,
			DataFilter dataFilter){
		int favoriteId = request.getFavoriteId();
		List<Edge> edges = graph.getEdgesByFavorite(favoriteId, groupId, dataFilter);
		for (Edge edge : edges){
			NResponse.Edge.Builder protoEdge = createProtoEdge(edge);
			graphResponse.addEdges(protoEdge);
		}

	}

	private void handleGetEdges(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId,
			DataFilter dataFilter){
		final List<Integer> edgeIds = request.getObjectIdsList();
		for (Integer edgeId : edgeIds){
			Edge edge = graph.getEdge(edgeId, groupId, dataFilter);
			if (edge != null){
				NResponse.Edge.Builder protoEdge = createProtoEdge(edge);
				graphResponse.addEdges(protoEdge);
			}
		}
	}

	private void handleGetNodesByEdges(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId, DataFilter dataFilter){
		final List<Integer> edgeIds = request.getObjectIdsList();
		for (Integer edgeId : edgeIds){
			Edge edge = graph.getEdge(edgeId, groupId, dataFilter);
			if(edge == null)
				continue;
			Node fromNode = edge.getFromNode();
			Node toNode = edge.getToNode();
			fromNode = graph.getNode(fromNode.getID(), groupId, dataFilter);
			toNode = graph.getNode(toNode.getID(), groupId, dataFilter);
			if(fromNode == null || toNode == null)
				continue;
			NResponse.Node.Builder protoNode = createProtoNode(fromNode);
			graphResponse.addNodes(protoNode);
			protoNode = createProtoNode(toNode);
			graphResponse.addNodes(protoNode);
			graphResponse.addEdges(createProtoEdge(edge));
		}
	}

	private void handleGetNodes(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId,
			DataFilter dataFilter){
		final List<Integer> nodeIds = request.getObjectIdsList();
		for (Integer nodeId : nodeIds){
			Node node = graph.getNode(nodeId, groupId, dataFilter);
			if (node != null){
				NResponse.Node.Builder protoNode = createProtoNode(node);
				graphResponse.addNodes(protoNode);
			}
		}
	}

	private void handleReloadNode(Graph request, Builder graphResponse, GraphNi3Engine graph, int groupId,
			DataFilter dataFilter){
		final int nodeId = request.getNodeId();
		Node node = graph.reloadNode(nodeId, groupId, dataFilter);
		if (node != null){
			NResponse.Node.Builder protoNode = createProtoNode(node);
			graphResponse.addNodes(protoNode);
		}
	}

	private NResponse.Node.Builder createProtoNode(Node node){
		NResponse.Node.Builder protoNode = NResponse.Node.newBuilder();
		protoNode.setId(node.getID());
		protoNode.setObjectDefinitionId(node.getType());
		protoNode.setChildrenCount(node.getOutEdges().size());
		protoNode.setParentCount(node.getInEdges().size());
		protoNode.setCreatorId(node.getCreatorUser());
		protoNode.setCreatorGroupId(node.getCreatorGroup());
		return protoNode;
	}

	private NResponse.Edge.Builder createProtoEdge(Edge edge){
		NResponse.Edge.Builder protoEdge = NResponse.Edge.newBuilder();
		protoEdge.setId(edge.getID());
		protoEdge.setObjectDefinitionId(edge.getType());
		protoEdge.setDirected(edge.getDirected());
		protoEdge.setStrength(edge.getStrength());
		protoEdge.setInPath(edge.getInPath());
		protoEdge.setConnectionType(edge.getConnectionType());
		protoEdge.setStatus(edge.getStatus());
		protoEdge.setCreatorId(edge.getCreatorUser());
		protoEdge.setCreatorGroupId(edge.getCreatorGroup());
		protoEdge.setFavoriteId(edge.getFavoriteId());
		protoEdge.setFromNode(createProtoNode(edge.getFromNode()));
		protoEdge.setToNode(createProtoNode(edge.getToNode()));
		return protoEdge;
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}

	public static synchronized GraphNi3Engine getGraph(int schemaId){
		log.debug("Requested graph for schema " + schemaId);
		GraphNi3Engine graph = GraphCache.getInstance().getGraph(schemaId);
		log.debug("found one " + graph);
		if (graph == null){
			log.debug("graph for schema " + schemaId + " is not loaded yet");
			log.debug("initializing graph");
			GraphEngineFactory graphEngineFactory = NSpringFactory.getInstance().getGraphEngineFactory();
			graph = graphEngineFactory.newGraph(schemaId);
			GraphCache.getInstance().setGraph(graph);
		}

		if (!graph.isGraphLoaded()){
			log.error("Graph is not loaded probably due to an error");
		}
		return graph;
	}
}
