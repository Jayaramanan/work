package com.ni3.ag.navigator.server.dao;

import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Node;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.geocode.data.GeoCodeItem;

public interface NodeDAO{
	Map<Integer, Node> getNodes(List<ObjectDefinition> nodeTypes);

	Node get(int id);

	boolean updateNodeMetaphor(int nodeId, String iconName);

	Map<Attribute, Object> getNodeData(int nodeId, ObjectDefinition entity);

	List<GeoCodeItem> getAllToCode(String sql);

	boolean updateNodeGeoCoords(int nodeId, double lon, double lat);

	boolean updateNodeGeoCoords(int id, double lon, double lat, boolean touchLastModified);

	Node get(int id, ObjectDefinition entity);
}
