package com.ni3.ag.navigator.server.services;

import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.shared.domain.User;

/**
 * This is the class to handle CRUD operations on dynamic (USR_*) tables
 */
public interface ObjectManagementService{
	/**
	 * Create new node generating id from sequence
	 * 
	 * @param entityId
	 *            - object definition
	 * @param attributeIdToValueMap
	 *            - attribute values
	 * @return new object id
	 */
	int insertNode(int entityId, Map<Attribute, String> attributeIdToValueMap);

	/**
	 * Creates new node using id passed as first param
	 * 
	 * @param id
	 *            - id for new node
	 * @param entity
	 *            - object definition
	 * @param attributeIdToValueMap
	 *            - attribute values
	 * @return id of new node
	 */
	int insertNode(Integer id, ObjectDefinition entity, Map<Attribute, String> attributeIdToValueMap);

	int insertEdge(Integer id, ObjectDefinition entity, Map<Attribute, String> attributeToValueMap, int favoritesId,
			int fromId, int toId);

	int insertEdge(int entityId, Map<Attribute, String> attributeToValueMap, int favoritesId, int fromId, int toId);

	int updateNode(int nodeId, final int entityId, Map<Attribute, String> attributeToValueMap);

	int updateNode(int nodeId, final ObjectDefinition entity, Map<Attribute, String> attributeToValueMap);

	int updateEdge(int objectId, int entityId, Map<Attribute, String> attributeToValueMap, int favoriteId);

	int updateEdge(int edgeId, final ObjectDefinition ent, Map<Attribute, String> attributeToValueMap, int favoritesId);

	/**
	 * @param schemaId
	 *            - schema id
	 * @param entityId
	 *            - entity id
	 * @param id
	 *            object id
	 * @return list of object ids (both nodes and edges) that were deleted
	 */
	List<Integer> delete(int entityId, int id);

	/**
	 * @param fromNodeId
	 *            - id of the node, from which data is merged; node is removed at the end
	 * @param toNodeId
	 *            - id of the node, to whick data is merged
	 * @param entity
	 *            - entity
	 * @param attributeIds
	 *            - ids of the attributes, that should be merged
	 * @param edgeIds
	 *            - ids of the edges, that should be relinked
	 */
	int merge(int fromNodeId, int toNodeId, int entityId, List<Integer> attributeIds, List<Integer> edgeIds);

	boolean updateNodeGeoCoords(int nodeId, double lon, double lat);

	void clearContext(Integer favoriteId, Integer schemaId);

	int setContext(int nodeId, int entityId, Map<Attribute, String> attributeToValueMap, int contextID, int topicId);

	void cloneContext(int schemaId, int contextID, int fromTopicId, int toTopicId);

	boolean canDeleteObject(User user, int objId);

	Map<Attribute, String> getAttributeIdToValueMap(int entityId, List<Integer> attributeIds, List<String> values, boolean b);

	ObjectDefinition getEntityById(int entityId);

}
