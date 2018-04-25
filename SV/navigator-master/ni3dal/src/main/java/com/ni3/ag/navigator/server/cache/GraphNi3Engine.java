/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.*;

public abstract class GraphNi3Engine{

	protected Map<Integer, Node> nodeMap;
	protected Map<Integer, Edge> edgeMap;

	protected int schemaId;

	protected GraphNi3Engine(int schema){
		this.schemaId = schema;
	}

	private boolean graphLoaded;

	abstract public List<Object> getNodeWithEdges(int rootID, int groupId, DataFilter dataFilter);

	abstract public Node getNode(int RootID, int groupId, DataFilter dataFilter);

	abstract public Edge getEdge(int edgeID, int groupId, DataFilter dataFilter);

	abstract public List<Edge> getEdgesByFavorite(int favoriteId, int groupId, DataFilter dataFilter);

	abstract public List<Object> findPath(int fromNodeId, int toNodeId, int maxPathLength, int pathLengthOverrun,
			int groupId, DataFilter dataFilter);

	abstract public void deleteNode(int ID);

	abstract public boolean newNode(int newNodeID, ObjectDefinition objectDefinition);

	abstract public Node reloadNode(int NodeID, int groupId, DataFilter dataFilter);

	abstract public Node reloadNode(int NodeID);

	abstract public boolean reloadEdge(int id);

	abstract public boolean newEdge(int EdgeID, int FromID, int ToID, int ObjectType, int Type, float Strength, int InPath,
			int status, int Directed, int favoritesID, int userID, int groupID, boolean contextEdge);

	abstract public boolean newEdge(int newEdgeID);

	abstract public boolean updateEdge(int EdgeID);

	abstract public boolean updateEdge(int EdgeID, int FromID, int ToID, int connectionType, float strength, int inPath,
			int status, int Directed, int favoritesID, int objectType);

	abstract public void deleteEdge(int EdgeID);

	abstract public void removeTopicEdges(int TopicID);

	abstract public void syncGraphWithDB(boolean background);

	public boolean isGraphLoaded(){
		return graphLoaded;
	}

	public void setGraphLoaded(boolean graphLoaded){
		this.graphLoaded = graphLoaded;
	}

	public abstract Edge getEdge(int edgeId);

	public abstract Node getNode(int nodeId);

	public Integer getSchemaId(){
		return schemaId;
	}

	public abstract List<Integer> filterEdgesByFromTo(List<Integer> edgeIds, List<Integer> fromIds, List<Integer> toIds,
			int limit);

	public abstract List<Integer> filterEdgesByNodes(List<Integer> searchedEdgeIds, List<Integer> searchedNodeIds, int limit);

	public abstract List<Integer> getAllConnectedNodes(Integer id);

	public abstract Collection<Integer> getAllEdges(Integer id);

	public abstract Map<Integer, Integer> getAllEdgesWithType(int objectId);

	public abstract void retainVisibleNodes(Collection<Integer> ids, Group group, DataFilter dataFilter);

	public abstract DataFilter createDataFilter(List<Integer> filteredValueIds);

	public abstract boolean containsNode(int id);
}
