package com.ni3.ag.navigator.server.services;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.CisObject;
import com.ni3.ag.navigator.server.domain.Edge;
import com.ni3.ag.navigator.server.domain.Node;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;

public interface CISObjectProviderService{
	void init();

	Node getNode(int id);

	List<Edge> getNodeInEdges(int id);

	List<Edge> getNodeOutEdges(int id);

	Node getFromNode(int id);

	Node getToNode(int id);

	Edge getEdge(int id);

	List<Integer> getEdgeList(List<Integer> fromIds, List<Integer> toIds, List<Integer> edgeIds, int limit);

	List<Integer> getEdgeListByFavorite(int favoriteId);

	List<Integer> getEdgeList(List<Integer> nodeIds, List<Integer> edgeIds, int limit);

	List<Integer> getConnectedNodesForNode(Integer id);

	Map<Integer,Integer> getEdgesWithTypesForNode(int objectId);

	void fillLastModified(Date lastUpdateTime, List<ObjectDefinition> types);

	List<CisObject> getUpdatedObjects(Date lastUpdateTime, List<ObjectDefinition> types);

	Collection<? extends Integer> getNodeIds(ObjectDefinition od);
}
