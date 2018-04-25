package com.ni3.ag.navigator.server.dao;

import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Edge;
import com.ni3.ag.navigator.server.domain.GroupObjectPermissions;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.shared.domain.User;

public interface EdgeDAO{

	List<GroupObjectPermissions> getEdgePermissionsForUser(User user, Integer nodeId);

	Map<Integer, Edge> getEdges(List<ObjectDefinition> edgeTypes);

	Edge get(int edgeID);

	Map<Attribute, Object> getEdgeData(int edgeId, ObjectDefinition entity);

	List<Integer> getEdgeIdsByFavorite(int favoriteId);
}
