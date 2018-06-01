/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;

public interface GraphGateway{

	Node reloadNode(int nodeId, int schemaId, DataFilter dataFilter);

	List<Node> getNodes(List<Integer> nodeIds, int schemaId, DataFilter dataFilter);

	List<Edge> getEdges(List<Integer> edgeIds, int schemaId, DataFilter dataFilter);

	List<Edge> getEdgesByFavorite(int favoriteId, int schemaId, DataFilter dataFilter);

	List<GraphObject> getNodesAndEdges(List<Integer> nodeIds, int schemaId, DataFilter dataFilter, int maxNodeCount);

	List<GraphObject> findPathObjects(int fromNodeId, int toNodeId, int schemaId, DataFilter dataFilter, int maxPathLength,
			int pathLengthOverrun);

}
