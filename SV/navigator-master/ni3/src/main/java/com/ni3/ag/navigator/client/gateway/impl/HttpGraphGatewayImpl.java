/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpGraphGatewayImpl extends FilterApplicableCallGateway implements GraphGateway{

	@Override
	public Node reloadNode(int nodeId, int schemaId, DataFilter dataFilter){
		NRequest.Graph.Builder request = NRequest.Graph.newBuilder();
		request.setAction(NRequest.Graph.Action.RELOAD_NODE);
		request.setSchemaId(schemaId);
		request.setNodeId(nodeId);
		request.setDataFilter(makeFilter(dataFilter));
		try{
			ByteString payload = sendRequest(ServletName.GraphServlet, request.build());
			NResponse.Graph protoGraph = NResponse.Graph.parseFrom(payload);
			final List<com.ni3.ag.navigator.shared.proto.NResponse.Node> protoNodes = protoGraph.getNodesList();
			Node node = null;
			if (!protoNodes.isEmpty()){
				final com.ni3.ag.navigator.shared.proto.NResponse.Node protoNode = protoNodes.get(0);
				node = createNodeFromProto(protoNode);
			}
			return node;
		} catch (IOException e){
			showErrorAndThrow("Error releading node", e);
			return null;
		}
	}

	@Override
	public List<Node> getNodes(List<Integer> nodeIds, int schemaId, DataFilter dataFilter){
		NRequest.Graph.Builder request = NRequest.Graph.newBuilder();
		request.setAction(NRequest.Graph.Action.GET_NODES);
		request.setSchemaId(schemaId);
		request.setDataFilter(makeFilter(dataFilter));
		request.addAllObjectIds(nodeIds);
		try{
			ByteString payload = sendRequest(ServletName.GraphServlet, request.build());
			NResponse.Graph protoGraph = NResponse.Graph.parseFrom(payload);
			final List<com.ni3.ag.navigator.shared.proto.NResponse.Node> protoNodes = protoGraph.getNodesList();
			List<Node> nodes = new ArrayList<Node>();
			for (com.ni3.ag.navigator.shared.proto.NResponse.Node protoNode : protoNodes){
				Node node = createNodeFromProto(protoNode);
				nodes.add(node);
			}
			return nodes;
		} catch (IOException e){
			showErrorAndThrow("Error getting nodes", e);
			return null;
		}
	}

	@Override
	public List<Edge> getEdges(List<Integer> edgeIds, int schemaId, DataFilter dataFilter){
		NRequest.Graph.Builder request = NRequest.Graph.newBuilder();
		request.setAction(NRequest.Graph.Action.GET_EDGES);
		request.setSchemaId(schemaId);
		request.setDataFilter(makeFilter(dataFilter));
		request.addAllObjectIds(edgeIds);
		try{
			ByteString payload = sendRequest(ServletName.GraphServlet, request.build());
			NResponse.Graph protoGraph = NResponse.Graph.parseFrom(payload);
			final List<com.ni3.ag.navigator.shared.proto.NResponse.Edge> protoEdges = protoGraph.getEdgesList();
			List<Edge> edges = new ArrayList<Edge>();
			for (com.ni3.ag.navigator.shared.proto.NResponse.Edge protoEdge : protoEdges){
				Edge edge = createEdgeFromProto(protoEdge);
				edges.add(edge);
			}
			return edges;
		} catch (IOException e){
			showErrorAndThrow("Error getting edges", e);
			return null;
		}
	}

	@Override
	public List<Edge> getEdgesByFavorite(int favoriteId, int schemaId, DataFilter dataFilter){
		NRequest.Graph.Builder request = NRequest.Graph.newBuilder();
		request.setAction(NRequest.Graph.Action.GET_FAVORITES_EDGES);
		request.setSchemaId(schemaId);
		if (dataFilter != null){
			request.setDataFilter(makeFilter(dataFilter));
		}
		request.setFavoriteId(favoriteId);
		try{
			ByteString payload = sendRequest(ServletName.GraphServlet, request.build());
			NResponse.Graph protoGraph = NResponse.Graph.parseFrom(payload);
			final List<com.ni3.ag.navigator.shared.proto.NResponse.Edge> protoEdges = protoGraph.getEdgesList();
			List<Edge> edges = new ArrayList<Edge>();
			for (com.ni3.ag.navigator.shared.proto.NResponse.Edge protoEdge : protoEdges){
				Edge edge = createEdgeFromProto(protoEdge);
				edges.add(edge);
			}
			return edges;
		} catch (IOException e){
			showErrorAndThrow("Error getting edges for topic", e);
			return null;
		}
	}

	@Override
	public List<GraphObject> getNodesAndEdges(List<Integer> rootIds, int schemaId, DataFilter dataFilter, int maxNodeCount){
		NRequest.Graph.Builder request = NRequest.Graph.newBuilder();
		request.setAction(NRequest.Graph.Action.GET_NODES_WITH_EDGES);
		request.setSchemaId(schemaId);
		request.setDataFilter(makeFilter(dataFilter));
		request.addAllObjectIds(rootIds);
		request.setMaxNodeCount(maxNodeCount);
		try{
			ByteString payload = sendRequest(ServletName.GraphServlet, request.build());
			NResponse.Graph protoGraph = NResponse.Graph.parseFrom(payload);
			if (protoGraph.getResult() == NResponse.Graph.Result.OK){
				final List<GraphObject> objects = new ArrayList<GraphObject>();
				final List<com.ni3.ag.navigator.shared.proto.NResponse.Node> protoNodes = protoGraph.getNodesList();
				for (com.ni3.ag.navigator.shared.proto.NResponse.Node protoNode : protoNodes){
					Node node = createNodeFromProto(protoNode);
					objects.add(node);
				}
				final List<com.ni3.ag.navigator.shared.proto.NResponse.Edge> protoEdges = protoGraph.getEdgesList();
				for (com.ni3.ag.navigator.shared.proto.NResponse.Edge protoEdge : protoEdges){
					Edge edge = createEdgeFromProto(protoEdge);
					objects.add(edge);
				}
				return objects;
			} else{
				showTooManyNodesError();
			}
		} catch (IOException e){
			showErrorAndThrow("Error getting network", e);
		}
		return null;
	}

	@Override
	public List<GraphObject> findPathObjects(int fromNodeId, int toNodeId, int schemaId, DataFilter dataFilter,
			int maxPathLength, int pathLengthOverrun){
		NRequest.Graph.Builder request = NRequest.Graph.newBuilder();
		request.setAction(NRequest.Graph.Action.FIND_PATH);
		request.setSchemaId(schemaId);
		request.setDataFilter(makeFilter(dataFilter));
		request.setNodeId(fromNodeId);
		request.setNodeToId(toNodeId);
		request.setMaxPathLenght(maxPathLength);
		request.setPathLengthOverrun(pathLengthOverrun);
		try{
			ByteString payload = sendRequest(ServletName.GraphServlet, request.build());
			NResponse.Graph protoGraph = NResponse.Graph.parseFrom(payload);
			final List<GraphObject> objects = new ArrayList<GraphObject>();
			final List<com.ni3.ag.navigator.shared.proto.NResponse.Node> protoNodes = protoGraph.getNodesList();
			for (com.ni3.ag.navigator.shared.proto.NResponse.Node protoNode : protoNodes){
				Node node = createNodeFromProto(protoNode);
				objects.add(node);
			}
			final List<com.ni3.ag.navigator.shared.proto.NResponse.Edge> protoEdges = protoGraph.getEdgesList();
			for (com.ni3.ag.navigator.shared.proto.NResponse.Edge protoEdge : protoEdges){
				Edge edge = createEdgeFromProto(protoEdge);
				objects.add(edge);
			}
			return objects;
		} catch (IOException e){
			showErrorAndThrow("Error getting path objects", e);
			return null;
		}
	}

	private Edge createEdgeFromProto(com.ni3.ag.navigator.shared.proto.NResponse.Edge protoEdge){
		Edge edge = new Edge();
		edge.ID = protoEdge.getId();
		edge.Type = protoEdge.getObjectDefinitionId();
		edge.setConnectionType(protoEdge.getConnectionType());
		edge.setStrength(protoEdge.getStrength());
		edge.setDirected(protoEdge.getDirected());
		edge.userID = protoEdge.getCreatorId();
		edge.groupID = protoEdge.getCreatorGroupId();
		edge.favoritesID = protoEdge.getFavoriteId();
		edge.from = createNodeFromProto(protoEdge.getFromNode());
		edge.to = createNodeFromProto(protoEdge.getToNode());
		return edge;
	}

	private Node createNodeFromProto(com.ni3.ag.navigator.shared.proto.NResponse.Node protoNode){
		final int childrenCount = protoNode.getChildrenCount();
		final int parentCount = protoNode.getParentCount();
		final Node node = new Node(childrenCount, parentCount);
		node.ID = protoNode.getId();
		node.Type = protoNode.getObjectDefinitionId();
		node.status = protoNode.getStatus();
		node.userID = protoNode.getCreatorId();
		node.groupID = protoNode.getCreatorGroupId();
		return node;
	}

	private void showTooManyNodesError(){
		// TODO show error from controller
		final MainPanel mainFrame = SystemGlobals.MainFrame;
		mainFrame.Doc.setStatus(UserSettings.getWord("Too many nodes"));
		mainFrame.showNoResultWindow(MainPanel.TOO_MANY_SEARCH_RESULT);
	}
}
