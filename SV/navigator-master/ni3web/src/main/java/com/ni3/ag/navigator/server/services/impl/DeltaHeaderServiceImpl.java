package com.ni3.ag.navigator.server.services.impl;

import java.util.*;

import com.ni3.ag.navigator.server.cache.GraphCache;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.dao.*;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.DeltaHeaderService;
import com.ni3.ag.navigator.server.services.GeoAnalyticsService;
import com.ni3.ag.navigator.server.services.GraphEngineFactory;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.ThematicCluster;
import com.ni3.ag.navigator.shared.domain.ThematicMap;
import com.ni3.ag.navigator.shared.domain.UserSetting;
import org.apache.log4j.Logger;

public class DeltaHeaderServiceImpl implements DeltaHeaderService{
	private static final Logger log = Logger.getLogger(DeltaHeaderServiceImpl.class);
	private FavoriteDAO favoriteDAO;
	private FavoritesFolderDAO favoritesFolderDAO;
	private EdgeDAO edgeDAO;
	private NodeDAO nodeDAO;
	private ThematicMapDAO thematicMapDAO;
	private GeoAnalyticsService geoAnalyticsService;
	private GraphEngineFactory graphEngineFactory;
	private SchemaLoaderService schemaLoaderService;
	private DeltaHeaderDAO deltaHeaderDAO;
	private DeltaHeaderUserDAO deltaHeaderUserDAO;
	private UserSettingsDAO userSettingsDAO;

	public void setThematicMapDAO(ThematicMapDAO thematicMapDAO){
		this.thematicMapDAO = thematicMapDAO;
	}

	public void setGraphEngineFactory(GraphEngineFactory graphEngineFactory){
		this.graphEngineFactory = graphEngineFactory;
	}

	public void setGeoAnalyticsService(GeoAnalyticsService geoAnalyticsService){
		this.geoAnalyticsService = geoAnalyticsService;
	}

	public void setEdgeDAO(EdgeDAO edgeDAO){
		this.edgeDAO = edgeDAO;
	}

	public void setNodeDAO(NodeDAO nodeDAO){
		this.nodeDAO = nodeDAO;
	}

	public void setFavoriteDAO(FavoriteDAO favoriteDAO){
		this.favoriteDAO = favoriteDAO;
	}

	public void setFavoritesFolderDAO(FavoritesFolderDAO favoritesFolderDAO){
		this.favoritesFolderDAO = favoritesFolderDAO;
	}

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
	}

	public void setDeltaHeaderUserDAO(DeltaHeaderUserDAO deltaHeaderUserDAO){
		this.deltaHeaderUserDAO = deltaHeaderUserDAO;
	}

	public void setUserSettingsDAO(UserSettingsDAO userSettingsDAO){
		this.userSettingsDAO = userSettingsDAO;
	}

	@Override
	public List<DeltaHeader> getUnprocessedForUser(Integer userId, int limit){
		List<DeltaHeader> result = deltaHeaderUserDAO.getUnprocessedForUser(userId, 100);
		result = fillDeltasData(result);
		return result;
	}

	@Override
	public void prepareDataForUser(Integer id){
		List<DeltaHeader> unprocessedDeltas = deltaHeaderUserDAO.getUnprocessedForUser(id, -1);
		List<DeltaHeader> deltasToMarkProcessed = getDeltasToMark(unprocessedDeltas);
		deltaHeaderUserDAO.markUserDeltasAsProcessed(deltasToMarkProcessed);
	}

	class ObsoleteObjects{
		public Map<Integer, DeltaHeader> deletedFavorites = new HashMap<Integer, DeltaHeader>();
		public Map<Integer, DeltaHeader> deletedFolders = new HashMap<Integer, DeltaHeader>();
		public Map<Integer, DeltaHeader> deletedObjects = new HashMap<Integer, DeltaHeader>();
		public Map<Integer, DeltaHeader> deletedThematicMaps = new HashMap<Integer, DeltaHeader>();

		public Set<Integer> existingFavorites = getExistingFavorites();
		public Set<Integer> existingFavoriteFolders = getExistingFavoriteFolders();
		public Set<Integer> existingThematicMaps = getExistingThematicMaps();

		public Set<DeltaHeader> deltasToMarkProcessed = new HashSet<DeltaHeader>();
	}

	private List<DeltaHeader> getDeltasToMark(List<DeltaHeader> unprocessedDeltas){
		log.debug("resolving out-of-date deltas to mark as processed " + unprocessedDeltas.size());
		Collections.reverse(unprocessedDeltas);

		ObsoleteObjects obsoleteObjects = new ObsoleteObjects();
		for (DeltaHeader dh : unprocessedDeltas){
			switch (dh.getDeltaType()){
				case FAVORITE_DELETE:
				case FAVORITE_FOLDER_DELETE:
				case OBJECT_DELETE:
				case GEO_ANALYTICS_DELETE:
					rememberDelete(obsoleteObjects, dh);
					break;
				case FAVORITE_CREATE:
				case FAVORITE_FOLDER_CREATE:
				case NODE_CREATE:
				case EDGE_CREATE:
					if (isObsoleteDelta(obsoleteObjects, dh)){
						markCreate(obsoleteObjects, dh);
						markCorrespondingDelete(obsoleteObjects, dh);
					}
					break;
				case FAVORITE_UPDATE:
				case FAVORITE_FOLDER_UPDATE:
				case NODE_UPDATE:
				case EDGE_UPDATE:
				case GEO_ANALYTICS_SAVE:
					if (isObsoleteDelta(obsoleteObjects, dh))
						markUpdate(obsoleteObjects, dh);
					break;
			}
		}
		log.debug("Marking deltas as processed: " + obsoleteObjects.deltasToMarkProcessed.toString());
		List<DeltaHeader> result = new ArrayList<DeltaHeader>();
		for (DeltaHeader dh : obsoleteObjects.deltasToMarkProcessed){
			dh.setSyncStatus(SyncStatus.Processed);
			result.add(dh);
		}
		return result;
	}

	private void markUpdate(ObsoleteObjects obsoleteObjects, DeltaHeader dh){
		log.debug("marking update delta " + dh.getId() + " as processed");
		obsoleteObjects.deltasToMarkProcessed.add(dh);
	}

	// some magic here
	// if we have create some object ... blablabla ... delete for same object then
	// we mark create as obsolete and also mark delete as obsolete too
	private void markCorrespondingDelete(ObsoleteObjects obsoleteObjects, DeltaHeader dh){
		int objectId = getObjectId(dh);
		switch (dh.getDeltaType()){
			case FAVORITE_CREATE:
				if (obsoleteObjects.deletedFavorites.containsKey(objectId))
					obsoleteObjects.deltasToMarkProcessed.add(obsoleteObjects.deletedFavorites.get(objectId));
				break;
			case FAVORITE_FOLDER_CREATE:
				if (obsoleteObjects.deletedFolders.containsKey(objectId))
					obsoleteObjects.deltasToMarkProcessed.add(obsoleteObjects.deletedFolders.get(objectId));
				break;
			case NODE_CREATE:
			case EDGE_CREATE:
				if (obsoleteObjects.deletedObjects.containsKey(objectId)){
					obsoleteObjects.deltasToMarkProcessed.add(obsoleteObjects.deletedObjects.get(objectId));
				}
				break;
		}
	}

	private void markCreate(ObsoleteObjects obsoleteObjects, DeltaHeader dh){
		log.debug("marking create delta " + dh.getId() + " as processed");
		obsoleteObjects.deltasToMarkProcessed.add(dh);
	}

	private boolean isObsoleteDelta(ObsoleteObjects obsoleteObjects, DeltaHeader dh){
		int objectId = getObjectId(dh);
		boolean result = false;
		switch (dh.getDeltaType()){
			case FAVORITE_CREATE:
			case FAVORITE_UPDATE:
				result = obsoleteObjects.deletedFavorites.containsKey(objectId)
				        || !obsoleteObjects.existingFavorites.contains(objectId);
				break;
			case FAVORITE_FOLDER_CREATE:
			case FAVORITE_FOLDER_UPDATE:
				result = obsoleteObjects.deletedFolders.containsKey(objectId)
				        || !obsoleteObjects.existingFavoriteFolders.contains(objectId);
				break;
			case NODE_CREATE:
			case NODE_UPDATE:
				result = obsoleteObjects.deletedObjects.containsKey(objectId)
				        || getGraph(getSchemaId(dh)).getNode(objectId) == null;
				break;
			case EDGE_CREATE:
			case EDGE_UPDATE:
				result = obsoleteObjects.deletedObjects.containsKey(objectId)
				        || getGraph(getSchemaId(dh)).getEdge(objectId) == null;
				break;
			case GEO_ANALYTICS_SAVE:
				result = obsoleteObjects.deletedThematicMaps.containsKey(objectId)
				        || !obsoleteObjects.existingThematicMaps.contains(objectId);
				break;
		}
		log.debug("Delta " + dh.getDeltaType() + " is obsolete: " + result);
		return result;
	}

	private void rememberDelete(ObsoleteObjects obsoleteObjects, DeltaHeader dh){
		int objectId = getObjectId(dh);
		log.debug("Remembering delta " + dh.getDeltaType() + " as delete delta for object: " + objectId);
		switch (dh.getDeltaType()){
			case FAVORITE_DELETE:
				obsoleteObjects.deletedFavorites.put(objectId, dh);
				break;
			case FAVORITE_FOLDER_DELETE:
				obsoleteObjects.deletedFolders.put(objectId, dh);
				break;
			case OBJECT_DELETE:
				obsoleteObjects.deletedObjects.put(objectId, dh);
				break;
			case GEO_ANALYTICS_DELETE:
				obsoleteObjects.deletedThematicMaps.put(objectId, dh);
				break;
		}
	}

	private int getSchemaId(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		DeltaParamIdentifier schemaParam;
		switch (dh.getDeltaType()){
			case NODE_CREATE:
				schemaParam = DeltaParamIdentifier.CreateNodeSchemaId;
				break;
			case NODE_UPDATE:
				schemaParam = DeltaParamIdentifier.UpdateNodeSchemaId;
				break;
			case EDGE_CREATE:
				schemaParam = DeltaParamIdentifier.CreateEdgeSchemaId;
				break;
			case EDGE_UPDATE:
				schemaParam = DeltaParamIdentifier.UpdateEdgeSchemaId;
				break;
			default:
				log.error("Cannot resolve Schema id for delta " + dh);
				return -1;
		}
		return Integer.parseInt(params.get(schemaParam).getValue());
	}

	private Set<Integer> getExistingThematicMaps(){
		Set<Integer> existing = new HashSet<Integer>();
		List<ThematicMap> thematicMaps = thematicMapDAO.getThematicMaps();
		for (ThematicMap tm : thematicMaps)
			existing.add(tm.getId());
		return existing;
	}

	private Set<Integer> getExistingFavoriteFolders(){
		Set<Integer> existing = new HashSet<Integer>();
		List<FavoritesFolder> folders = favoritesFolderDAO.getFolders();
		for (FavoritesFolder ff : folders)
			existing.add(ff.getId());
		return existing;
	}

	private Set<Integer> getExistingFavorites(){
		Set<Integer> existing = new HashSet<Integer>();
		List<Favorite> favorites = favoriteDAO.getFavorites();
		for (Favorite f : favorites)
			existing.add(f.getId());
		return existing;
	}

	@Override
	public List<DeltaHeader> getUnprocessedForMaster(int count){
		List<DeltaHeader> deltas = deltaHeaderDAO.getUnprocessedDeltas(count);
		deltas = fillDeltasData(deltas);
		return deltas;
	}

	@Override
	public void prepareDataForMaster(){
		List<DeltaHeader> deltas = deltaHeaderDAO.getUnprocessedDeltas(-1);
		List<DeltaHeader> toMark = getDeltasToMark(deltas);
		deltaHeaderDAO.markProcessed(toMark);
	}

	public synchronized GraphNi3Engine getGraph(int schemaId){
		log.debug("get graph for schema " + schemaId);
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

	private Integer getObjectId(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		switch (dh.getDeltaType()){
			case FAVORITE_DELETE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.DeleteFavoriteId).getValue());
			case FAVORITE_CREATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.CreateFavoriteNewId).getValue());
			case FAVORITE_UPDATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.UpdateFavoriteId).getValue());
			case FAVORITE_FOLDER_CREATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.CreateFavoriteFolderNewId).getValue());
			case FAVORITE_FOLDER_UPDATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.UpdateFavoriteFolderFolderId).getValue());
			case FAVORITE_FOLDER_DELETE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.DeleteFavoriteFolderObjId).getValue());
			case NODE_CREATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.CreateNodeNewId).getValue());
			case EDGE_CREATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeNewId).getValue());
			case NODE_UPDATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeObjectId).getValue());
			case EDGE_UPDATE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.UpdateEdgeObjectId).getValue());
			case OBJECT_DELETE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.DeleteObjectObjectId).getValue());
			case GEO_ANALYTICS_DELETE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.DeleteGeoAnalyticsId).getValue());
			case GEO_ANALYTICS_SAVE:
				return Integer.parseInt(params.get(DeltaParamIdentifier.SaveGeoAnalyticsId).getValue());
			default: {
				log.error("Cannot resolve object id for delta type " + dh.getDeltaType());
				return 0;
			}
		}
	}

	private List<DeltaHeader> fillDeltasData(List<DeltaHeader> result){
		List<DeltaHeader> deltas = new ArrayList<DeltaHeader>();
		for (DeltaHeader dh : result){
			boolean fillResult;
			switch (dh.getDeltaType()){
				case FAVORITE_CREATE:
					fillResult = fillFavoriteCreateData(dh);
					break;
				case FAVORITE_UPDATE:
					fillResult = fillFavoriteUpdateData(dh);
					break;
				case FAVORITE_COPY:
					fillResult = fillFavoriteCopyData(dh);
					break;
				case FAVORITE_FOLDER_CREATE:
					fillResult = fillFolderCreateData(dh);
					break;
				case FAVORITE_FOLDER_UPDATE:
					fillResult = fillFolderUpdateData(dh);
					break;
				case SETTING_UPDATE:
					fillResult = fillSettingsUpdateData(dh);
					break;
				case NODE_CREATE:
					fillResult = fillNodeCreateData(dh);
					break;
				case EDGE_CREATE:
					fillResult = fillEdgeCreateData(dh);
					break;
				case NODE_UPDATE:
					fillResult = fillNodeUpdateData(dh);
					break;
				case EDGE_UPDATE:
					fillResult = fillEdgeUpdateData(dh);
					break;
				case GEO_ANALYTICS_SAVE:
					fillResult = fillGeoAnalyticsSave(dh);
					break;
				case GEO_ANALYTICS_DELETE:
					fillResult = true;
					break;
				default:
					fillResult = true;
					break;

			}
			if (fillResult){
				dumpResultDelta(dh);
			} else{
				dh.setSyncStatus(SyncStatus.ProcessedWithWarning);
				log.warn("Error fill data for " + dh.getDeltaType());
			}
			deltas.add(dh);
		}
		return deltas;
	}

	private boolean fillGeoAnalyticsSave(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		int id = Integer.parseInt(params.get(DeltaParamIdentifier.SaveGeoAnalyticsId).getValue());
		ThematicMap tm = geoAnalyticsService.getThematicMapWithClusters(id);
		if (tm == null){
			log.error("Error get thematic map with id " + id);
			return false;
		}
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsName, new DeltaParam(DeltaParamIdentifier.SaveGeoAnalyticsName, tm
		        .getName()));
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsFolderId, new DeltaParam(
		        DeltaParamIdentifier.SaveGeoAnalyticsFolderId, "" + tm.getFolderId()));
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsGroupId, new DeltaParam(
		        DeltaParamIdentifier.SaveGeoAnalyticsGroupId, "" + tm.getGroupId()));
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsLayerId, new DeltaParam(
		        DeltaParamIdentifier.SaveGeoAnalyticsLayerId, "" + tm.getLayerId()));
		params.put(DeltaParamIdentifier.SaveGeoAnalyticsAttribute, new DeltaParam(
		        DeltaParamIdentifier.SaveGeoAnalyticsAttribute, tm.getAttribute()));
		if (tm.getClusters() == null || tm.getClusters().isEmpty()){
			log.warn("Thematic map with empty or null clusters container");
			return true;
		}
		for (ThematicCluster tc : tm.getClusters()){
			DeltaParamIdentifier di = new DeltaParamIdentifier("" + tc.getId());
			params.put(di, new DeltaParam(di, makeClusterString(tc)));
		}
		return true;
	}

	private String makeClusterString(ThematicCluster tc){
		StringBuilder sb = new StringBuilder();
		sb.append(tc.getFromValue()).append("|").append(tc.getToValue()).append("|").append(tc.getColor()).append("|")
		        .append(tc.getGisIds()).append("|");
		if (tc.getDescription() != null)
			sb.append(tc.getDescription().replace("|", "\\x07c"));
		else
			sb.append("null");
		return sb.toString();
	}

	private boolean fillEdgeUpdateData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		int edgeId = getObjectId(dh);
		int schemaId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateEdgeSchemaId).getValue());

		Edge edge;
		try{
			edge = edgeDAO.get(edgeId);
		} catch (DaoException ex){
			log.error("Cannot find edge with id " + edgeId + " already deleted by node", ex);
			return false;
		}
		Schema schema = schemaLoaderService.getSchema(schemaId);
		ObjectDefinition entity = schema.getEntity(edge.getType());
		Map<Attribute, Object> data = edgeDAO.getEdgeData(edgeId, entity);

		params.put(DeltaParamIdentifier.UpdateEdgeFavoriteId, new DeltaParam(DeltaParamIdentifier.UpdateEdgeFavoriteId, ""
		        + edge.getFavoriteId()));
		params.put(DeltaParamIdentifier.UpdateEdgeObjectDefinitionId, new DeltaParam(
		        DeltaParamIdentifier.UpdateEdgeObjectDefinitionId, "" + entity.getId()));

		for (DeltaParamIdentifier dpi : params.keySet()){
			if (dpi.isFixedParam())
				continue;
			int attrId = Integer.parseInt(dpi.getIdentifier());
			Attribute attr = entity.getAttribute(attrId);
			Object o = data.get(attr);
			if (o != null)
				params.put(dpi, new DeltaParam(dpi, o.toString()));
		}
		return true;
	}

	private boolean fillNodeUpdateData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		int nodeId = getObjectId(dh);
		int schemaId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeSchemaId).getValue());

		Node node;
		try{
			node = nodeDAO.get(nodeId);
		} catch (DaoException ex){
			log.error("Error get node with id " + nodeId, ex);
			return false;
		}
		Schema schema = schemaLoaderService.getSchema(schemaId);
		ObjectDefinition entity = schema.getEntity(node.getType());
		Map<Attribute, Object> data = nodeDAO.getNodeData(nodeId, entity);

		params.put(DeltaParamIdentifier.UpdateNodeObjectDefinitionId, new DeltaParam(
		        DeltaParamIdentifier.UpdateNodeObjectDefinitionId, "" + entity.getId()));

		for (DeltaParamIdentifier dpi : params.keySet()){
			if (dpi.isFixedParam())
				continue;
			int attrId = Integer.parseInt(dpi.getIdentifier());
			Attribute attr = entity.getAttribute(attrId);
			Object o = data.get(attr);
			if (o != null)
				params.put(dpi, new DeltaParam(dpi, o.toString()));
		}
		return true;
	}

	private boolean fillEdgeCreateData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		int edgeId = getObjectId(dh);
		int schemaId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeSchemaId).getValue());

		Edge edge;
		try{
			edge = edgeDAO.get(edgeId);
		} catch (DaoException ex){
			log.error("Cannot find edge with id " + edgeId + " already deleted by node", ex);
			return false;
		}
		Schema schema = schemaLoaderService.getSchema(schemaId);
		ObjectDefinition entity = schema.getEntity(edge.getType());
		Map<Attribute, Object> data = edgeDAO.getEdgeData(edgeId, entity);

		params.put(DeltaParamIdentifier.CreateEdgeObjectDefinitionId, new DeltaParam(
		        DeltaParamIdentifier.CreateEdgeObjectDefinitionId, "" + entity.getId()));
		params.put(DeltaParamIdentifier.CreateEdgeFromId, new DeltaParam(DeltaParamIdentifier.CreateEdgeFromId, ""
		        + edge.getFromNode().getID()));
		params.put(DeltaParamIdentifier.CreateEdgeToId, new DeltaParam(DeltaParamIdentifier.CreateEdgeToId, ""
		        + edge.getToNode().getID()));
		params.put(DeltaParamIdentifier.CreateEdgeFavoriteId, new DeltaParam(DeltaParamIdentifier.CreateEdgeFavoriteId, ""
		        + edge.getFavoriteId()));

		for (DeltaParamIdentifier dpi : params.keySet()){
			if (dpi.isFixedParam())
				continue;
			int attrId = Integer.parseInt(dpi.getIdentifier());
			Attribute attr = entity.getAttribute(attrId);
			Object o = data.get(attr);
			if (o != null)
				params.put(dpi, new DeltaParam(dpi, o.toString()));
		}
		return true;
	}

	private void dumpResultDelta(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		Set<DeltaParamIdentifier> keys = params.keySet();
		log.debug("DUMP delta header " + dh);
		for (DeltaParamIdentifier dpi : keys)
			log.debug("\t\tName " + dpi + " -> " + params.get(dpi));
	}

	private boolean fillNodeCreateData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		int nodeId = getObjectId(dh);
		int schemaId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateNodeSchemaId).getValue());

		Node node;
		try{
			node = nodeDAO.get(nodeId);
		} catch (DaoException ex){
			log.error("Error get node with id " + nodeId, ex);
			return false;
		}
		Schema schema = schemaLoaderService.getSchema(schemaId);
		ObjectDefinition entity = schema.getEntity(node.getType());
		Map<Attribute, Object> data = nodeDAO.getNodeData(nodeId, entity);

		params.put(DeltaParamIdentifier.CreateNodeObjectDefinitionId, new DeltaParam(
		        DeltaParamIdentifier.CreateNodeObjectDefinitionId, "" + node.getType()));

		for (DeltaParamIdentifier dpi : params.keySet()){
			if (dpi.isFixedParam())
				continue;
			int attrId = Integer.parseInt(dpi.getIdentifier());
			Attribute attr = entity.getAttribute(attrId);
			Object o = data.get(attr);
			if (o != null)
				params.put(dpi, new DeltaParam(dpi, o.toString()));
		}
		return true;
	}

	private boolean fillSettingsUpdateData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		String propName = params.get(DeltaParamIdentifier.UpdateSettingsPropertyName).getValue();
		UserSetting value = userSettingsDAO.get(dh.getCreator().getId(), propName);
		if (value == null){
			log.error("Error get property `" + propName + "` value for user " + dh.getCreator());
			return false;
		}
		params.put(DeltaParamIdentifier.UpdateSettingsPropertyValue, new DeltaParam(
		        DeltaParamIdentifier.UpdateSettingsPropertyValue, value.getValue()));
		return true;
	}

	private boolean fillFolderUpdateData(DeltaHeader dh){
		int folderId = getObjectId(dh);
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		FavoritesFolder folder = favoritesFolderDAO.get(folderId);
		if (folder == null){
			log.error("Error get favorite folder for id " + folderId);
			return false;
		}
		params.put(DeltaParamIdentifier.UpdateFavoriteFolderName, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderName, folder.getFolderName()));
		params.put(DeltaParamIdentifier.UpdateFavoriteFolderParentFolderId, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderParentFolderId, "" + folder.getParentId()));
		params.put(DeltaParamIdentifier.UpdateFavoriteFolderSort, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderSort, "" + folder.getSortOrder()));
		return true;
	}

	private boolean fillFolderCreateData(DeltaHeader dh){
		int folderId = getObjectId(dh);
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		FavoritesFolder folder = favoritesFolderDAO.get(folderId);
		if (folder == null){
			log.error("Error get folder for id " + folderId);
			return false;
		}
		params.put(DeltaParamIdentifier.CreateFavoriteFolderName, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderName, folder.getFolderName()));
		params.put(DeltaParamIdentifier.CreateFavoriteFolderParentFolderId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderParentFolderId, "" + folder.getParentId()));
		params.put(DeltaParamIdentifier.CreateFavoriteFolderGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderGroupFolder, folder.getGroupFolder() ? "1" : "0"));
		params.put(DeltaParamIdentifier.CreateFavoriteFolderSort, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderSort, "" + folder.getSortOrder()));
		return true;
	}

	private boolean fillFavoriteCopyData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		dh.setDeltaType(DeltaType.FAVORITE_CREATE);

		DeltaParam id = params.get(DeltaParamIdentifier.CopyFavoriteNewId);
		DeltaParam schemaId = params.get(DeltaParamIdentifier.CopyFavoriteSchemaId);
		params.clear();

		id.setName(DeltaParamIdentifier.CreateFavoriteNewId);
		schemaId.setName(DeltaParamIdentifier.CreateFavoriteSchemaId);

		params.put(id.getName(), id);
		params.put(schemaId.getName(), schemaId);

		return fillFavoriteCreateData(dh);
	}

	private boolean fillFavoriteUpdateData(DeltaHeader dh){
		final Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		final int favId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateFavoriteId).getValue());
		final Favorite favorite = favoriteDAO.get(favId);
		if (favorite == null){
			log.error("Error get favorite by id: " + favId);
			return false;
		}
		params.put(DeltaParamIdentifier.UpdateFavoriteMode, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteMode, ""
		        + favorite.getMode().getValue()));
		params.put(DeltaParamIdentifier.UpdateFavoriteDescription, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteDescription, favorite.getDescription()));
		params.put(DeltaParamIdentifier.UpdateFavoriteName, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteName, favorite
		        .getName()));
		params.put(DeltaParamIdentifier.UpdateFavoriteFolderId, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteFolderId,
		        "" + favorite.getFolderId()));
		params.put(DeltaParamIdentifier.UpdateFavoriteDocument, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteDocument,
		        favorite.getData()));
		params.put(DeltaParamIdentifier.UpdateFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteSchemaId,
		        "" + favorite.getSchemaId()));
		params.put(DeltaParamIdentifier.UpdateFavoriteLayout, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteLayout,
		        favorite.getLayout()));
		params.put(DeltaParamIdentifier.UpdateFavoriteGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteGroupFolder, favorite.getGroupFavorite() ? "1" : "0"));
		return true;
	}

	private boolean fillFavoriteCreateData(DeltaHeader dh){
		Map<DeltaParamIdentifier, DeltaParam> params = dh.getDeltaParameters();
		final int newId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateFavoriteNewId).getValue());
		log.debug("Filling Create favorite delta with actual data");
		log.debug("Favorite id: " + newId);
		final Favorite favorite = favoriteDAO.get(newId);
		if (favorite == null){
			log.error("Error get favorite for id " + newId);
			return false;
		}
		params.put(DeltaParamIdentifier.CreateFavoriteDocument, new DeltaParam(DeltaParamIdentifier.CreateFavoriteDocument,
		        favorite.getData()));
		params.put(DeltaParamIdentifier.CreateFavoriteName, new DeltaParam(DeltaParamIdentifier.CreateFavoriteName, favorite
		        .getName()));
		params.put(DeltaParamIdentifier.CreateFavoriteDescription, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteDescription, favorite.getDescription()));
		params.put(DeltaParamIdentifier.CreateFavoriteMode, new DeltaParam(DeltaParamIdentifier.CreateFavoriteMode, ""
		        + favorite.getMode().getValue()));
		params.put(DeltaParamIdentifier.CreateFavoriteGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteGroupFolder, favorite.getGroupFavorite() ? "1" : "0"));
		params.put(DeltaParamIdentifier.CreateFavoriteFolderId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteFolderId,
		        "" + favorite.getFolderId()));
		params.put(DeltaParamIdentifier.CreateFavoriteLayout, new DeltaParam(DeltaParamIdentifier.CreateFavoriteLayout,
		        favorite.getLayout()));
		params.put(DeltaParamIdentifier.CreateFavoriteSchemaId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteSchemaId,
		        "" + favorite.getSchemaId()));
		params.put(DeltaParamIdentifier.CreateFavoriteDBVersion, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteDBVersion, favorite.getDbVersion()));
		return true;
	}
}
