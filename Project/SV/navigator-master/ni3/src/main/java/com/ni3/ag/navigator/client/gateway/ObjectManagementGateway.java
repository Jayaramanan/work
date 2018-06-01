package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.Context;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Favorite;

public interface ObjectManagementGateway{
	void updateNodeMetaphor(int nodeId, String iconName);

	void updateNodeCoords(int id, double lon, double lat);

	void delete(DBObject obj);

	void insertNode(DBObject n);

	void insertEdge(DBObject edge, int favoritesID, int FromID, int ToID);

	void updateEdge(DBObject obj, int favoritesID, boolean locked);

	void updateNode(DBObject obj, boolean locked);

	void merge(DBObject toNode, DBObject fromNode, List<Integer> attributes, List<Integer> connections);

	void setContext(DBObject obj, Context c, int favoriteId, boolean locked);

	void clearContext(Favorite favorite);

	void cloneContext(int schemaId, int contextId, int oldFavoriteId, int newFavoriteId);

	boolean checkUserObjectPermissions(int nodeId, int schemaId);

}
