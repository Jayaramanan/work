package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.EdgeScope;
import com.ni3.ag.navigator.server.domain.NodeScope;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;

public interface ObjectScopeDAO{

	List<EdgeScope> getEdgeScopes(List<ObjectDefinition> types, Integer edgeId);

	List<NodeScope> getNodeScopes(List<ObjectDefinition> nodeTypes, Integer nodeId);
}
