package com.ni3.ag.navigator.server.services.impl;

import java.util.*;

import com.ni3.ag.navigator.server.cache.GraphCache;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.calc.FormulaExecutor;
import com.ni3.ag.navigator.server.dao.*;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.dictionary.DBObject;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.services.GraphEngineFactory;
import com.ni3.ag.navigator.server.services.ObjectManagementService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.services.UserDataService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ObjectManagementServiceImpl extends JdbcDaoSupport implements ObjectManagementService{

	private static final Logger log = Logger.getLogger(ObjectManagementServiceImpl.class);

	private UserDataService userDataService;
	private EdgeDAO edgeDAO;
	private ContextDAO contextDAO;
	private NodeDAO nodeDAO;
	private ObjectDAO objectDAO;
	private ThreadLocalStorage threadLocalStorage;
	private SchemaLoaderService schemaLoaderService;
	private GraphEngineFactory graphEngineFactory;
	private GeoCacheDAO geoCacheDAO;
	private GroupDAO groupDAO;
	private Map<String, AttributeDataSource> attributeDataSources;

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setAttributeDataSources(Map<String, AttributeDataSource> attributeDataSources){
		this.attributeDataSources = attributeDataSources;
	}

	public void setGraphEngineFactory(GraphEngineFactory graphEngineFactory){
		this.graphEngineFactory = graphEngineFactory;
	}

	public void setNodeDAO(NodeDAO nodeDAO){
		this.nodeDAO = nodeDAO;
	}

	public void setObjectDAO(ObjectDAO objectDAO){
		this.objectDAO = objectDAO;
	}

	public void setUserDataService(UserDataService userDataService){
		this.userDataService = userDataService;
	}

	public void setThreadLocalStorage(ThreadLocalStorage threadLocalStorage){
		this.threadLocalStorage = threadLocalStorage;
	}

	public void setEdgeDAO(EdgeDAO edgeDAO){
		this.edgeDAO = edgeDAO;
	}

	public void setContextDAO(ContextDAO contextDAO){
		this.contextDAO = contextDAO;
	}

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	public void setGeoCacheDAO(GeoCacheDAO geoCacheDAO){
		this.geoCacheDAO = geoCacheDAO;
	}

	@Override
	public ObjectDefinition getEntityById(int entityId){
		ObjectDefinition entity = null;
		List<Schema> schemas = schemaLoaderService.getAllSchemas();
		for (Schema sch : schemas)
			for (ObjectDefinition ent : sch.getDefinitions())
				if (ent.getId() == entityId){
					entity = ent;
					break;
				}
		return entity;
	}

	private Schema getSchemaByEntityId(int entityId){
		Schema schema = null;
		List<Schema> schemas = schemaLoaderService.getAllSchemas();
		for (Schema sch : schemas)
			for (ObjectDefinition ent : sch.getDefinitions())
				if (ent.getId() == entityId){
					schema = sch;
					break;
				}
		return schema;
	}

	@Override
	public int insertNode(int entityId, Map<Attribute, String> attributeIdToValueMap){
		ObjectDefinition entity = getEntityById(entityId);
		return insertNode(null, entity, attributeIdToValueMap);
	}

	@Override
	public int insertNode(Integer newObjectId, ObjectDefinition entity, Map<Attribute, String> attributeToValueMap){
		log.info("inserting new object");
		Integer userId = threadLocalStorage.getCurrentUser().getId();

		DBObject obj = prepareObject(entity, attributeToValueMap);
		createNode(newObjectId, userId, entity, obj);
		saveUserData(obj, true);

		final GraphNi3Engine graph = getGraph(entity.getSchema().getId());
		// TODO create and call newNode with parameters like newEdge
		// new node gets data from DB but data is already here
		graph.newNode(obj.ID, entity);

		return obj.ID;
	}

	private void createNode(Integer newObjectId, int userId, ObjectDefinition entity, DBObject obj){
		boolean found = false;
		for (Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			if (!attributeDataSource.isPrimary())
				continue;
			found = true;
			obj.ID = attributeDataSource.createNode(newObjectId, entity.getId(), userId, attribute, obj
					.getAttributeValue(attribute));
			break;
		}
		if (!found)
			throw new RuntimeException("Cannot find primary dataSource for entity: " + entity.getName()
					+ ".\nCannot create object");
	}

	@Override
	public int insertEdge(int entityId, Map<Attribute, String> attributeToValueMap, int favoritesId, int fromId, int toId){
		ObjectDefinition entity = getEntityById(entityId);
		return insertEdge(null, entity, attributeToValueMap, favoritesId, fromId, toId);
	}

	@Override
	public int insertEdge(Integer id, ObjectDefinition entity, Map<Attribute, String> attributeToValueMap, int favoritesId,
			int fromId, int toId){
		Integer userId = threadLocalStorage.getCurrentUser().getId();

		log.info("inserting new edge");
		if (!validateFromToNodes(fromId, toId, entity.getSchema(), userId))
			return -1;

		DBObject obj = prepareObject(entity, attributeToValueMap);
		createEdge(id, fromId, toId, entity, userId, obj);
		saveUserData(obj, true);
		final GraphNi3Engine graph = getGraph(entity.getSchema().getId());
		// TODO call version with more params
		graph.newEdge(obj.ID);

		return obj.ID;
	}

	private void createEdge(Integer newObjectId, int fromId, int toId, ObjectDefinition entity, int userId, DBObject obj){
		boolean found = false;
		for (Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			if (!attributeDataSource.isPrimary())
				continue;
			found = true;
			obj.ID = attributeDataSource.createEdge(newObjectId, fromId, toId, entity.getId(), userId, attribute, obj
					.getAttributeValue(attribute));
			break;
		}
		if (!found)
			throw new RuntimeException("Cannot find primary dataSource for entity: " + entity.getName()
					+ ".\nCannot create object");
	}

	private void saveUserData(DBObject obj, boolean create){
		Map<String, Map<Attribute, String>> dataSourceAttributeValues = new HashMap<String, Map<Attribute, String>>();
		for (Attribute attribute : obj.getAttributeToValueMap().keySet()){
			if (attribute.isInContext())
				continue;
			if (!dataSourceAttributeValues.containsKey(attribute.getDataSource()))
				dataSourceAttributeValues.put(attribute.getDataSource(), new HashMap<Attribute, String>());
			dataSourceAttributeValues.get(attribute.getDataSource()).put(attribute, obj.getAttributeValue(attribute));
		}
		for (String dataSource : dataSourceAttributeValues.keySet()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(dataSource);
			if (attributeDataSource.isPrimary() && create)
				continue;
			attributeDataSource.saveOrUpdate(obj.ID, dataSourceAttributeValues.get(dataSource));
		}
	}

	private DBObject prepareObject(ObjectDefinition entity, Map<Attribute, String> attributeToValueMap){
		DBObject obj = new DBObject(entity, attributeToValueMap);
		FormulaExecutor.recalcObjectFields(obj);
		if(entity.isEdge()){
			addInPathValue(entity, obj);
		}
		return obj;
	}

	private void addInPathValue(ObjectDefinition entity, DBObject obj){
		Attribute inPathAttribute = entity.getAttribute("inpath");
		if(inPathAttribute == null){
			log.error("Cannot find inPath attribute for entity: " + entity.getName());
			return;
		}
		String inPathValue = obj.getAttributeValue(inPathAttribute);
		if(inPathValue != null){
			log.debug("Edge already has inPath value: " + inPathValue);
			return;
		}
		PredefinedAttribute trueInPathValue = null;
		for(PredefinedAttribute pa : inPathAttribute.getValues()){
			if(pa.getValue().equals("1")){
				trueInPathValue = pa;
				break;
			}
		}
		if(trueInPathValue == null){
			log.error("Cannot find `1` value for " + entity.getName() + "." + inPathAttribute.getName());
			log.error(inPathAttribute.getValues());
			return;
		}
		log.debug("InPath value: " + trueInPathValue);
		obj.setAttributeValue(inPathAttribute, "" + trueInPathValue.getId());
	}

	// TODO make this using graph?
	private boolean validateFromToNodes(int fromId, int toId, Schema schema, Integer userId){
		GraphNi3Engine graph = getGraph(schema.getId());
		Group group = groupDAO.getByUser(userId);
		Node toNode = graph.getNode(fromId, group.getId(), new DataFilter(schema, new ArrayList<Integer>()));
		Node fromNode = graph.getNode(toId, group.getId(), new DataFilter(schema, new ArrayList<Integer>()));

		if (fromNode == null || fromNode.getStatus() == ObjectStatus.Deleted.toInt()
				|| fromNode.getStatus() == ObjectStatus.Merged.toInt()){
			log.error("Try to create edge from deleted/merged node " + fromId + "->" + (fromNode != null ? ObjectStatus.fromInt(fromNode.getStatus()) : ""));
			return false;
		}
		if (toNode == null || toNode.getStatus() == ObjectStatus.Deleted.toInt()
				|| toNode.getStatus() == ObjectStatus.Merged.toInt()){
			log.error("Try to create edge to deleted/merged node " + toId + "->" + (toNode != null ? ObjectStatus.fromInt(toNode.getStatus()) : ""));
			return false;
		}
		return true;
	}

	private synchronized GraphNi3Engine getGraph(int schemaId){
		log.debug("getGraph for schema " + schemaId);
		GraphNi3Engine graph = GraphCache.getInstance().getGraph(schemaId);
		log.debug("graph=" + graph);
		if (graph == null){
			log.debug("graph is not inited yet - creating one");
			graph = graphEngineFactory.newGraph(schemaId);
		}
		GraphCache.getInstance().setGraph(graph);
		if (!graph.isGraphLoaded()){
			log.error("Graph is not loaded probably due to an error");
		}
		return graph;
	}

	@Override
	public int updateNode(int nodeId, final int entityId, Map<Attribute, String> attributeToValueMap){
		ObjectDefinition entity = getEntityById(entityId);
		return updateNode(nodeId, entity, attributeToValueMap);
	}

	@Override
	public int updateNode(int nodeId, final ObjectDefinition ent, Map<Attribute, String> attributeToValueMap){
		log.debug("updating node");
		DBObject obj = prepareObject(ent, attributeToValueMap);
		obj.ID = nodeId;
		saveUserData(obj, false);
		return obj.ID;
	}

	@Override
	public int updateEdge(int edgeId, final int entityId, Map<Attribute, String> attributeToValueMap, int favoritesId){
		ObjectDefinition entity = getEntityById(entityId);
		return updateEdge(edgeId, entity, attributeToValueMap, favoritesId);
	}

	@Override
	public int updateEdge(int edgeId, final ObjectDefinition entity, Map<Attribute, String> attributeToValueMap,
			int favoritesId){
		log.debug("updating edge");
		DBObject obj = prepareObject(entity, attributeToValueMap);
		obj.ID = edgeId;
		saveUserData(obj, false);

		final GraphNi3Engine graph = getGraph(entity.getSchema().getId());
		graph.updateEdge(obj.ID);
		return obj.ID;
	}

	@Override
	public List<Integer> delete(int entityId, int objectId){
		ObjectDefinition entity = getEntityById(entityId);
		log.debug("Deleting object " + objectId + ", entity " + entity.getName());
		return delete(entity, objectId);
	}

	private List<Integer> delete(final ObjectDefinition entity, int objectId){
		List<Integer> res = new ArrayList<Integer>();
		GraphNi3Engine graph = getGraph(entity.getSchema().getId());

		if (entity.isEdge()){
			Edge edge = graph.getEdge(objectId);
			if (edge != null) {
				Node fromNode = edge.getFromNode();
				if (fromNode != null){
					objectDAO.setChanged(fromNode.getID());
				}

				Node toNode = edge.getToNode();
				if (toNode != null){
					objectDAO.setChanged(toNode.getID());
				}
			}
		}

		final boolean isNode = entity.isNode();
		if (isNode){// This is needed for the rare case of OFC sync, when additional edges were created to the
			// object that is being deleted
			Map<Integer, Integer> edges = graph.getAllEdgesWithType(objectId);
			for (Integer edgeId : edges.keySet()){
				res.addAll(delete(edges.get(edgeId), edgeId));
			}
		}
		deleteObject(entity, objectId);
		res.add(objectId);

		if (entity.isNode()){
			graph.deleteNode(objectId);
		} else
			graph.deleteEdge(objectId);
		return res;
	}

	@Override
	public int merge(int objectFromId, int objectToId, int entityId, List<Integer> attributeIds, List<Integer> edgeIds){
		if (log.isDebugEnabled()){
			log.debug("merging 2 nodes: from " + objectFromId + " to " + objectToId);
			log.debug("attributes: " + attributeIds);
			log.debug("connections: " + edgeIds);
		}

		final ObjectDefinition entity = getEntityById(entityId);

		// reload objects from database
		final DBObject objTo = userDataService.loadObject(entity, objectToId, attributeIds);
		final DBObject objFrom = userDataService.loadObject(entity, objectFromId, attributeIds);

		if (objTo == null || objFrom == null){
			log.error("Object not found for merge, fromObject: " + objFrom + ", toObject: " + objTo);
			return -1;
		}

		// merge
		merge(objFrom, objTo);

		// save merged node
		updateNode(objTo.ID, entity, objTo.getAttributeToValueMap());

		// relink edges
		relinkEdges(objFrom, objTo, edgeIds);

		// remove unneeded edges
		cleanupEdges(objTo, edgeIds);

		// delete source node
		mergeObject(entity, objFrom.ID);

		return objTo.ID;
	}

	private void mergeObject(ObjectDefinition entity, int id){
		boolean found = false;
		for (Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			if (!attributeDataSource.isPrimary())
				continue;
			found = true;
			attributeDataSource.merge(id, entity);
		}
		if (!found)
			throw new RuntimeException("Cannot merge object " + id + "\nNo primary datasource found for entity: "
					+ entity.getName());
	}

	@Override
	public boolean updateNodeGeoCoords(int nodeId, double lon, double lat){
		CisObject object = objectDAO.get(nodeId);
		Schema schema = getSchemaByEntityId(object.getTypeId());
		boolean result = nodeDAO.updateNodeGeoCoords(nodeId, lon, lat);

		fillGeoMappingTable();
		return result && reloadNode(nodeId, schema.getId());
	}

	private boolean reloadNode(int nodeId, int schemaId){
		GraphNi3Engine graph = getGraph(schemaId);
		graph.reloadNode(nodeId);
		return true;
	}

	/**
	 * remove edges, that shouldn't remain after merge
	 * 
	 * @param node
	 *            - node
	 * @param eIds
	 *            - ids of the resulting edges
	 */
	private void cleanupEdges(DBObject node, List<Integer> eIds){
		// TODO hack
		Schema schema = node.ent.getSchema();
		GraphNi3Engine graph = getGraph(schema.getId());
		Map<Integer, Integer> edges = graph.getAllEdgesWithType(node.ID);

		for (Integer id : edges.keySet()){
			if (!eIds.contains(id)){
				int entityId = edges.get(id);
				ObjectDefinition entity = schema.getEntity(entityId);
				mergeObject(entity, id);
			}
		}
	}

	/**
	 * @param objFrom
	 *            - source node
	 * @param objTo
	 *            - destination node
	 * @param eIds
	 *            - ids of the resulting edges
	 */
	private void relinkEdges(DBObject objFrom, DBObject objTo, List<Integer> eIds){
		Schema schema = objFrom.ent.getSchema();
		GraphNi3Engine graph = getGraph(schema.getId());
		Collection<Integer> ids = graph.getAllEdges(objFrom.ID);
		ids.removeAll(eIds);

		if (ids.isEmpty())
			return;
		userDataService.relinkEdges(objFrom.ID, objTo.ID, ids);

		for (Integer edge : ids){
			graph.updateEdge(edge);
		}
	}

	private void merge(DBObject from, DBObject to){
		for (Attribute attr : from.getAttributeToValueMap().keySet()){
			final String fromValue = from.getAttributeValue(attr);
			to.setAttributeValue(attr, fromValue);
		}
	}

	@Override
	public void clearContext(final Integer favoriteId, final Integer schemaId){

		Schema schema = schemaLoaderService.getSchema(schemaId);
		for (ObjectDefinition entity : schema.getDefinitions()){
			if (entity.getObjectTypeId() == ObjectDefinition.CONTEXT_EDGE_OBJECT_TYPE_ID)
				deleteObjectsByFavorite(entity, favoriteId);
		}

		final List<Context> contexts = contextDAO.findByName("Favorites");

		for (final Context context : contexts){
			Attribute pkAttribute = context.getPkAttribute();
			for (Attribute attribute : context.getAttributes()){
				if (attribute.equals(pkAttribute))
					continue;
				AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
				attributeDataSource.deleteContextDataByFavorite(pkAttribute, favoriteId);
			}
			AttributeDataSource pkAttributeDataSource = attributeDataSources.get(pkAttribute.getDataSource());
			pkAttributeDataSource.deleteContextDataByFavorite(pkAttribute, favoriteId);
		}

		GraphNi3Engine graph = getGraph(schemaId);
		graph.removeTopicEdges(favoriteId);
	}

	private void deleteObjectsByFavorite(ObjectDefinition entity, Integer favoriteId){
		AttributeDataSource primaryAttributeDataSource = null;
		for (Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			if (attributeDataSource.isPrimary()){
				primaryAttributeDataSource = attributeDataSource;
				break;
			}
		}
		if (primaryAttributeDataSource == null)
			throw new RuntimeException("Cannot find primary dataSource for entity: " + entity.getName());
		List<Integer> ids = primaryAttributeDataSource.getContextEdges(entity.getId(), favoriteId);
		if (ids != null && !ids.isEmpty()){
			deleteObjects(entity, ids);
		}
	}

	private void deleteObject(ObjectDefinition entity, int objectId){
		AttributeDataSource primaryAttributeDataSource = null;
		Set<AttributeDataSource> dataSources = new HashSet<AttributeDataSource>();
		for (Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			if (attributeDataSource.isPrimary())
				primaryAttributeDataSource = attributeDataSource;
			else
				dataSources.add(attributeDataSource);
		}
		if (primaryAttributeDataSource == null)
			throw new RuntimeException("Cannot delete object\nPrimary datasource for entity: " + entity.getName()
					+ " not found");
		for (AttributeDataSource attributeDataSource : dataSources)
			attributeDataSource.delete(objectId, entity);
		primaryAttributeDataSource.delete(objectId, entity);
	}

	private void deleteObjects(ObjectDefinition entity, List<Integer> ids){
		AttributeDataSource primaryAttributeDataSource = null;
		for (Attribute attribute : entity.getAttributes()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			if (attributeDataSource.isPrimary())
				primaryAttributeDataSource = attributeDataSource;
			else
				attributeDataSource.delete(ids, entity);
		}
		if (primaryAttributeDataSource == null)
			throw new RuntimeException("Cannot delete object\nPrimary datasource for entity: " + entity.getName()
					+ " not found");
		primaryAttributeDataSource.delete(ids, entity);
	}

	/**
	 * Clone context data - used for SaveAs/Copy favorites
	 */
	// todo this should probably be moved to favoriteManagementService
	@Override
	public void cloneContext(final int schemaId, final int contextID, final int fromTopicId, final int toTopicId){
		final Schema schema = schemaLoaderService.getSchema(schemaId);
		for (final ObjectDefinition ent : schema.getDefinitions()){
			Context context = ent.getContext(contextID);
			if (context == null)
				continue;
			cloneContextData(context, fromTopicId, toTopicId);
		}
	}

	private void cloneContextData(Context context, int fromTopicId, int toTopicId){
		Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> data = getContextData(context, fromTopicId);
		log.debug("got top " + fromTopicId + " data " + data.size());
		saveContextData(context, toTopicId, data);
	}

	private void saveContextData(Context context, int toTopicId,
			Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> data){
		Attribute pkAttribute = context.getPkAttribute();
		AttributeDataSource pkAttributeDataSource = attributeDataSources.get(pkAttribute.getDataSource());
		for (com.ni3.ag.navigator.shared.domain.DBObject object : data.values()){
			DBObject serverObject = makeServerObject(object, context.getObjectDefinition());
			pkAttributeDataSource.saveOrUpdateContext(object.getId(), pkAttribute, toTopicId);
			// TODO merge calls by attributeDataSource
			for (Attribute attribute : context.getAttributes()){
				if (attribute.equals(pkAttribute))
					continue;
				AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
				attributeDataSource.saveOrUpdateContextData(object.getId(), pkAttribute, toTopicId, serverObject
						.getAttributeToValueMap());
			}
		}
	}

	private DBObject makeServerObject(com.ni3.ag.navigator.shared.domain.DBObject object, ObjectDefinition od){
		DBObject result = new DBObject(object.getId(), od, new HashMap<Attribute, String>());
		for (Integer id : object.getData().keySet()){
			Attribute a = od.getAttribute(id);
			result.setAttributeValue(a, object.getData().get(id));
		}
		return result;
	}

	private Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> getContextData(Context c, int fromTopicId){
		HashMap<Integer, com.ni3.ag.navigator.shared.domain.DBObject> result = new HashMap<Integer, com.ni3.ag.navigator.shared.domain.DBObject>();
		Attribute pkAttribute = c.getPkAttribute();
		AttributeDataSource pkAttributeDataSource = attributeDataSources.get(pkAttribute.getDataSource());
		Collection<Integer> ids = pkAttributeDataSource.search(pkAttribute, new AdvancedCriteria.Section.Condition(
				pkAttribute.getId(), "=", "" + fromTopicId, false));
		for (Attribute attribute : c.getAttributes()){
			if (attribute.equals(pkAttribute))
				continue;
			AttributeDataSource attributeDataSource = attributeDataSources.get(attribute.getDataSource());
			attributeDataSource.getContext(ids, c.getPkAttribute(), "" + fromTopicId, Arrays.asList(attribute), result);
		}
		return result;
	}

	@Override
	public int setContext(int nodeId, final int entityId, Map<Attribute, String> attributeToValueMap, final int contextID,
			final int topicId){
		final ObjectDefinition entity = getEntityById(entityId);
		final DBObject obj = prepareObject(entity, attributeToValueMap);
		obj.ID = nodeId;

		final Context c = entity.getContext(contextID);
		AttributeDataSource pkAttributeDataSource = attributeDataSources.get(c.getPkAttribute().getDataSource());
		pkAttributeDataSource.saveOrUpdateContext(nodeId, c.getPkAttribute(), topicId);
		saveContextData(obj, c, topicId);
		return nodeId;
	}

	private void saveContextData(DBObject obj, Context c, int favoriteId){
		Map<String, Map<Attribute, String>> dataSourceAttributeValues = new HashMap<String, Map<Attribute, String>>();
		for (Attribute attribute : c.getAttributes()){
			if (attribute.equals(c.getPkAttribute()))
				continue;
			if (!dataSourceAttributeValues.containsKey(attribute.getDataSource()))
				dataSourceAttributeValues.put(attribute.getDataSource(), new HashMap<Attribute, String>());
			dataSourceAttributeValues.get(attribute.getDataSource()).put(attribute, obj.getAttributeValue(attribute));
		}
		for (String dataSource : dataSourceAttributeValues.keySet()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(dataSource);
			attributeDataSource.saveOrUpdateContextData(obj.ID, c.getPkAttribute(), favoriteId, dataSourceAttributeValues
					.get(dataSource));
		}
	}

	@Override
	public boolean canDeleteObject(final User user, final int objId){
		final List<GroupObjectPermissions> list = edgeDAO.getEdgePermissionsForUser(user, objId);
		boolean ret = true;
		if (log.isDebugEnabled()){
			log.debug("CHECK_CAN_DELETE_NODE: User: " + user + ", objectId: " + objId + "edges: " + list);
		}
		for (GroupObjectPermissions permissions : list){
			if (!permissions.isCanDelete()){
				ret = false;
			}
		}

		log.debug("CHECK_CAN_DELETE_NODE result: " + ret);

		return ret;
	}

	@Override
	public Map<Attribute, String> getAttributeIdToValueMap(int entityId, List<Integer> attributeIds, List<String> values,
			boolean generateSrcId){
		Map<Attribute, String> res = new HashMap<Attribute, String>();
		ObjectDefinition entity = getEntityById(entityId);

		for (int i = 0; i < attributeIds.size(); i++){
			Integer attrId = attributeIds.get(i);
			Attribute attr = entity.getAttribute(attrId);
			if (attr != null){
				String value = values.get(i);
				res.put(attr, value);
			}
		}
		if (generateSrcId){
			addGeneratedSrcId(res, entity);
		}
		return res;
	}

	private void addGeneratedSrcId(Map<Attribute, String> map, ObjectDefinition entity){
		Attribute srcIdAttr = entity.getAttribute(Attribute.SRCID_ATTRIBUTE_NAME);
		if (srcIdAttr != null){
			String sourceId = String.format("NAV_%d", new Date().getTime());
			map.put(srcIdAttr, sourceId);
			if (log.isDebugEnabled()){
				log.debug("Generated src id: " + sourceId);
			}
		}
	}

	private void fillGeoMappingTable(){
		final Set<String> cacheTables = geoCacheDAO.getCacheTables();
		if (cacheTables != null && !cacheTables.isEmpty()){
			geoCacheDAO.updateCache(new ArrayList<String>(cacheTables));
		}
	}

}