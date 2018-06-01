package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DaoException;
import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.dao.NodeDAO;
import com.ni3.ag.navigator.server.dao.UncommittedDeltasDAO;
import com.ni3.ag.navigator.server.dao.UserActivityDAO;
import com.ni3.ag.navigator.server.dao.UserSettingsDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.server.domain.FavoritesFolder;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.server.services.DeltaProcessor;
import com.ni3.ag.navigator.server.services.FavoritesFolderService;
import com.ni3.ag.navigator.server.services.FavoritesService;
import com.ni3.ag.navigator.server.services.GeoAnalyticsService;
import com.ni3.ag.navigator.server.services.ObjectManagementService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.session.UserSessionStore;
import com.ni3.ag.navigator.server.sync.SyncException;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import com.ni3.ag.navigator.shared.domain.ThematicCluster;
import com.ni3.ag.navigator.shared.domain.ThematicMap;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.domain.UserSetting;

public class DeltaProcessorImpl implements DeltaProcessor{

	private static final Logger log = Logger.getLogger(DeltaProcessorImpl.class);
	private static final String SYNC_IDENTIFIER = "sync=1";
	private static final String ID_LOG_PARAM = "Id";
	private static final String ICONNAME_LOG_PARAM = "Iconname";
	private static final String FROMID_LOG_PARAM = "FromId";
	private static final String TOID_LOG_PARAM = "ToId";
	private ObjectManagementService objectManagementService;
	private FavoritesService favoritesService;
	private FavoritesFolderService favoritesFolderService;
	private GeoAnalyticsService geoAnalyticsService;
	private SchemaLoaderService schemaLoaderService;
	private UncommittedDeltasDAO uncommittedDeltasDAO;
	private DeltaHeaderDAO deltaHeaderDAO;
	private NodeDAO nodeDAO;
	private FavoriteDAO favoriteDAO;
	private FavoritesFolderDAO favoritesFolderDAO;
	private UserSettingsDAO userSettingsDAO;

	public void setObjectManagementService(ObjectManagementService objectManagementService){
		this.objectManagementService = objectManagementService;
	}

	public void setGeoAnalyticsService(GeoAnalyticsService geoAnalyticsService){
		this.geoAnalyticsService = geoAnalyticsService;
	}

	public void setFavoritesService(FavoritesService favoritesService){
		this.favoritesService = favoritesService;
	}

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	public void setUncommittedDeltasDAO(UncommittedDeltasDAO uncommittedDeltasDAO){
		this.uncommittedDeltasDAO = uncommittedDeltasDAO;
	}

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
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

	public void setUserSettingsDAO(UserSettingsDAO userSettingsDAO){
		this.userSettingsDAO = userSettingsDAO;
	}

	// TODO adjust description
	/*
	 * @param deltas - DeltaHeader array to process
	 * 
	 * @return true if all deltas process without errors, false otherwise deltas array should be cleared and filled only
	 * with successfully processed deltas
	 */
	@Override
	public void processDeltas(final List<DeltaHeader> deltas, boolean onOffline){
		for (final DeltaHeader deltaHeader : deltas){
			if (deltaHeader.getSyncStatus() != SyncStatus.New){
				log.warn("Received delta `" + deltaHeader.getDeltaType() + "`status " + deltaHeader.getSyncStatus());
				log.warn("\t" + deltaHeader);
			} else
				processDelta(deltaHeader);

			if (!onOffline){
				saveDelta(deltaHeader);
			} else{
				saveRolledOnDelta(deltaHeader);
			}
			log.debug("Delta processed result: " + deltaHeader.getSyncStatus());
		}
	}

	private void saveRolledOnDelta(DeltaHeader deltaHeader){
		uncommittedDeltasDAO.save(deltaHeader);
	}

	private void saveDelta(DeltaHeader deltaHeader){
		SyncStatus current = deltaHeader.getSyncStatus();
		// if delta processed normally we should save it as new to routing queue
		// otherwise delta threatened as invalid and should not be routed
		if (SyncStatus.Processed.equals(current))
			deltaHeader.setSyncStatus(SyncStatus.New);
		deltaHeaderDAO.save(deltaHeader);
		deltaHeader.setSyncStatus(current);
	}

	private void processDelta(DeltaHeader deltaHeader){
		dumpDeltaHeader(deltaHeader);
		try{
			switch (deltaHeader.getDeltaType()){
				case SETTING_UPDATE:
					processSettingsUpdate(deltaHeader);
					break;
				case FAVORITE_FOLDER_CREATE:
					processFolderCreate(deltaHeader);
					break;
				case FAVORITE_FOLDER_DELETE:
					processFolderDelete(deltaHeader);
					break;
				case FAVORITE_CREATE:
					processFavoriteCreate(deltaHeader);
					break;
				case FAVORITE_FOLDER_UPDATE:
					processFolderUpdate(deltaHeader);
					break;
				case FAVORITE_DELETE:
					processFavoriteDelete(deltaHeader);
					break;
				case FAVORITE_UPDATE:
					processFavoriteUpdate(deltaHeader);
					break;
				case NODE_CREATE:
					processNodeCreate(deltaHeader);
					break;
				case NODE_UPDATE:
					processNodeUpdate(deltaHeader);
					break;
				case EDGE_CREATE:
					processEdgeCreate(deltaHeader);
					break;
				case EDGE_UPDATE:
					processUpdateEdge(deltaHeader);
					break;
				case OBJECT_DELETE:
					processObjectDelete(deltaHeader);
					break;
				case NODE_MERGE:
					processNodeMerge(deltaHeader);
					break;
				case NODE_UPDATE_METAPHOR:
					processNodeUpdateMetaphor(deltaHeader);
					break;
				case NODE_UPDATE_COORDS:
					processNodeUpdateGeoCoords(deltaHeader);
					break;
				case GEO_ANALYTICS_SAVE:
					processGeoAnalyticsSave(deltaHeader);
					break;
				case GEO_ANALYTICS_DELETE:
					processGeoAnalyticsDelete(deltaHeader);
					break;
				default:
					deltaHeader.setSyncStatus(SyncStatus.Error);
					break;
			}
		} catch (Exception ex){
			deltaHeader.setSyncStatus(SyncStatus.Error);
			log.error("Failed to handle delta", ex);
		}
	}

	private void processGeoAnalyticsDelete(DeltaHeader deltaHeader){
		log.debug("handling geo analytics delete");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();
		int id = Integer.parseInt(params.get(DeltaParamIdentifier.DeleteGeoAnalyticsId).getValue());
		log.debug("GeoAnalytics to delete: " + id);
		geoAnalyticsService.deleteThematicMap(id);
		deltaHeader.setSyncStatus(SyncStatus.Processed);
	}

	private void processGeoAnalyticsSave(DeltaHeader deltaHeader){
		log.debug("Handling geo analytics create");

		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();
		int id = Integer.parseInt(params.get(DeltaParamIdentifier.SaveGeoAnalyticsId).getValue());
		String name = params.get(DeltaParamIdentifier.SaveGeoAnalyticsName).getValue();
		int folderId = Integer.parseInt(params.get(DeltaParamIdentifier.SaveGeoAnalyticsFolderId).getValue());
		int groupId = Integer.parseInt(params.get(DeltaParamIdentifier.SaveGeoAnalyticsGroupId).getValue());
		int layerId = Integer.parseInt(params.get(DeltaParamIdentifier.SaveGeoAnalyticsLayerId).getValue());
		String attribute = params.get(DeltaParamIdentifier.SaveGeoAnalyticsAttribute).getValue();
		log.debug("id: " + id);
		log.debug("name: " + name);
		log.debug("folderId: " + folderId);
		log.debug("groupId: " + groupId);
		log.debug("layerId: " + layerId);
		log.debug("attribute: " + attribute);
		ThematicMap tm = new ThematicMap();
		tm.setId(id);
		tm.setAttribute(attribute);
		tm.setFolderId(folderId);
		tm.setGroupId(groupId);
		tm.setLayerId(layerId);
		tm.setName(name);
		tm.setClusters(parseClusters(params, id));
		ThematicMap existingTM = geoAnalyticsService.getThematicMap(id);
		int savedId;
		if (existingTM == null)
			savedId = geoAnalyticsService.createThematicMapWithClusters(tm);
		else
			savedId = geoAnalyticsService.saveThematicMapWithClustersWithIds(tm);
		log.debug("saved id: " + savedId);
		deltaHeader.setSyncStatus(id == savedId ? SyncStatus.Processed : SyncStatus.Error);
	}

	private List<ThematicCluster> parseClusters(Map<DeltaParamIdentifier, DeltaParam> params, int thematicMapId){
		List<ThematicCluster> result = new ArrayList<ThematicCluster>();
		for (DeltaParamIdentifier di : params.keySet()){
			if (di.isFixedParam())
				continue;
			String value = params.get(di).getValue();
			log.debug("Cluster value: " + value);
			String[] values = value.split("\\|");

			int clusterId = Integer.parseInt(di.getIdentifier());
			double fromValue = Double.parseDouble(values[0]);
			double toValue = Double.parseDouble(values[1]);
			String color = values[2];
			String gsId = values[3];
			String description = values[4];

			log.debug("fromValue:" + fromValue);
			log.debug("toValue: " + toValue);
			log.debug("color: " + color);
			log.debug("gsId: " + gsId);
			log.debug("description: " + description);
			if ("null".equals(description))
				description = null;
			ThematicCluster tc = new ThematicCluster();
			tc.setFromValue(fromValue);
			tc.setToValue(toValue);
			tc.setColor(color);
			tc.setGisIds(gsId);
			tc.setDescription(description);
			tc.setThematicMapId(thematicMapId);
			tc.setId(clusterId);
			result.add(tc);
		}
		return result;
	}

	private void dumpDeltaHeader(DeltaHeader deltaHeader){
		if (!log.isDebugEnabled())
			return;
		log.debug("---DeltaHeader---");
		log.debug("\tType: " + deltaHeader.getDeltaType());
		log.debug("\tParams: ");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();
		for (DeltaParamIdentifier dpi : params.keySet())
			log.debug("\t\t" + dpi.getIdentifier() + " -> " + params.get(dpi).getValue());
	}

	private void processNodeUpdateGeoCoords(DeltaHeader deltaHeader){
		log.debug("Handling node coords update");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();
		int nodeId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeCoordsObjectId).getValue());
		double lon = Double.parseDouble(params.get(DeltaParamIdentifier.UpdateNodeCoordsLon).getValue());
		double lat = Double.parseDouble(params.get(DeltaParamIdentifier.UpdateNodeCoordsLat).getValue());

		log.debug("Node to update: " + nodeId);
		log.debug("New lon: " + lon);
		log.debug("New lat: " + lat);

		boolean result = objectManagementService.updateNodeGeoCoords(nodeId, lon, lat);
		deltaHeader.setSyncStatus(result ? SyncStatus.Processed : SyncStatus.Error);
	}

	private void processNodeUpdateMetaphor(DeltaHeader deltaHeader){
		log.debug("Handling metaphor update");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();
		int nodeId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeMetaphorObjectId).getValue());
		String iconName = params.get(DeltaParamIdentifier.UpdateNodeMetaphorNewMetaphor).getValue();

		log.debug("Node to update: " + nodeId);
		log.debug("New metaphor name: " + iconName);

		boolean result = nodeDAO.updateNodeMetaphor(nodeId, iconName);
		deltaHeader.setSyncStatus(result ? SyncStatus.Processed : SyncStatus.Error);

		createUserActivityLog(UserActivityType.UpdateNodeMetaphor, deltaHeader, new LogParam(ID_LOG_PARAM, nodeId),
				new LogParam(ICONNAME_LOG_PARAM, iconName));
	}

	private void processNodeMerge(DeltaHeader deltaHeader){
		log.debug("processing node merge");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();

		int objectDefId = Integer.parseInt(params.get(DeltaParamIdentifier.MergeNodeObjectDefinitionId).getValue());
		int nodeFromId = Integer.parseInt(params.get(DeltaParamIdentifier.MergeNodeFromId).getValue());
		int nodeToId = Integer.parseInt(params.get(DeltaParamIdentifier.MergeNodeToId).getValue());
		String attributeIds = listParam(params.get(DeltaParamIdentifier.MergeNodeAttributeIDs).getValue());
		String edgeIds = listParam(params.get(DeltaParamIdentifier.MergeNodeEdgeIDs).getValue());

		if (log.isDebugEnabled()){
			log.debug("Object definition id: " + objectDefId);
			log.debug("node from Id: " + nodeFromId);
			log.debug("node to Id: " + nodeToId);
		}

		int processedId = objectManagementService.merge(nodeFromId, nodeToId, objectDefId, Utility
				.stringToIntegerList(attributeIds), Utility.stringToIntegerList(edgeIds));
		log.debug("ProcessedId: " + processedId);
		deltaHeader.setSyncStatus(processedId != nodeToId ? SyncStatus.ProcessedWithWarning : SyncStatus.Processed);

		createUserActivityLog(UserActivityType.MergeNode, deltaHeader, new LogParam(FROMID_LOG_PARAM, nodeFromId),
				new LogParam(TOID_LOG_PARAM, nodeToId));
	}

	private void processObjectDelete(DeltaHeader deltaHeader){
		log.debug("processing object delete");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();

		int objectId = Integer.parseInt(params.get(DeltaParamIdentifier.DeleteObjectObjectId).getValue());
		int schemaId = Integer.parseInt(params.get(DeltaParamIdentifier.DeleteObjectSchemaId).getValue());
		int objectDefId = Integer.parseInt(params.get(DeltaParamIdentifier.DeleteObjectObjectDefinitionId).getValue());

		if (log.isDebugEnabled()){
			log.debug("Object def id: " + objectDefId);
			log.debug("objectId: " + objectId);
			log.debug("schema key: " + schemaId);
		}
		List<Integer> result = objectManagementService.delete(objectDefId, objectId);
		if (!result.contains(objectId)){
			deltaHeader.setSyncStatus(SyncStatus.ProcessedWithWarning);
			log.warn("Failed to delete object with id: " + objectId + "... ignoring");
		} else
			deltaHeader.setSyncStatus(SyncStatus.Processed);

		final ObjectDefinition entity = objectManagementService.getEntityById(objectDefId);
		if (entity != null){
			UserActivityType activity = entity.isNode() ? UserActivityType.DeleteNode : UserActivityType.DeleteEdge;
			createUserActivityLog(activity, deltaHeader, new LogParam(ID_LOG_PARAM, objectId));
		}

	}

	private void processUpdateEdge(DeltaHeader deltaHeader){
		log.debug("processing edge update");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();

		int objectDefId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateEdgeObjectDefinitionId).getValue());
		int edgeId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateEdgeObjectId).getValue());
		int schemaKey = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateEdgeSchemaId).getValue());
		int favoriteId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateEdgeFavoriteId).getValue());

		if (log.isDebugEnabled()){
			log.debug("Object def id: " + objectDefId);
			log.debug("edgeId: " + edgeId);
			log.debug("schema key: " + schemaKey);
			log.debug("FavoriteId: " + favoriteId);
		}

		log.debug("Retrieving schema from cache");
		Schema sch = schemaLoaderService.getSchema(schemaKey);
		log.debug("Schema resolved by id: " + sch);

		ObjectDefinition entity = sch.getEntity(objectDefId);
		// log.debug("ObjectDefinition: " + entity);

		int processedId = objectManagementService.updateEdge(edgeId, entity, filterAttributeParams(params,
				makeAttributeMap(entity.getAttributes())), favoriteId);
		log.debug("ProcessedId: " + processedId);
		deltaHeader.setSyncStatus(processedId != edgeId ? SyncStatus.ProcessedWithWarning : SyncStatus.Processed);

		createUserActivityLog(UserActivityType.UpdateEdge, deltaHeader, new LogParam(ID_LOG_PARAM, edgeId));
	}

	private void processNodeUpdate(DeltaHeader deltaHeader){
		log.debug("processing node update");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();

		int objectDefId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeObjectDefinitionId).getValue());
		int nodeId = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeObjectId).getValue());
		int schemaKey = Integer.parseInt(params.get(DeltaParamIdentifier.UpdateNodeSchemaId).getValue());

		if (log.isDebugEnabled()){
			log.debug("Object def id: " + objectDefId);
			log.debug("nodeId: " + nodeId);
			log.debug("schema key: " + schemaKey);
		}

		log.debug("Retrieving schema from cache");
		Schema sch = schemaLoaderService.getSchema(schemaKey);
		log.debug("Schema resolved by id: " + sch);

		ObjectDefinition entity = sch.getEntity(objectDefId);

		int processedId = objectManagementService.updateNode(nodeId, entity, filterAttributeParams(params,
				makeAttributeMap(entity.getAttributes())));
		log.debug("ProcessedId: " + processedId);
		deltaHeader.setSyncStatus(processedId != nodeId ? SyncStatus.ProcessedWithWarning : SyncStatus.Processed);

		createUserActivityLog(UserActivityType.UpdateNode, deltaHeader, new LogParam(ID_LOG_PARAM, nodeId));
	}

	private void processEdgeCreate(DeltaHeader deltaHeader){
		log.debug("processing edge create");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();

		int objectDefId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeObjectDefinitionId).getValue());
		int newEdgeId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeNewId).getValue());
		int schemaKey = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeSchemaId).getValue());
		int fromId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeFromId).getValue());
		int toId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeToId).getValue());
		int favId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateEdgeFavoriteId).getValue());

		if (log.isDebugEnabled()){
			log.debug("Object def id: " + objectDefId);
			log.debug("newEdgeId: " + newEdgeId);
			log.debug("schema key: " + schemaKey);
			log.debug("fromId: " + fromId);
			log.debug("toId: " + toId);
			log.debug("favId: " + favId);
		}

		log.debug("Retrieving schema from cache");
		Schema sch = schemaLoaderService.getSchema(schemaKey);
		log.debug("Schema resolved by id: " + sch);

		ObjectDefinition entity = sch.getEntity(objectDefId);
		log.debug("ObjectDefinition: " + entity);

		log.debug("Call create edge");
		int newId = objectManagementService.insertEdge(newEdgeId, entity, filterAttributeParams(params,
				makeAttributeMap(entity.getAttributes())), favId, fromId, toId);
		log.debug("ProcessedId: " + newId);
		deltaHeader.setSyncStatus(newId == newEdgeId ? SyncStatus.Processed : SyncStatus.ProcessedWithWarning);

		createUserActivityLog(UserActivityType.CreateEdge, deltaHeader, new LogParam(ID_LOG_PARAM, newId));
	}

	private void processNodeCreate(DeltaHeader deltaHeader){
		log.debug("processing node create");
		Map<DeltaParamIdentifier, DeltaParam> params = deltaHeader.getDeltaParameters();
		int objectDefId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateNodeObjectDefinitionId).getValue());
		int newNodeId = Integer.parseInt(params.get(DeltaParamIdentifier.CreateNodeNewId).getValue());
		int schemaKey = Integer.parseInt(params.get(DeltaParamIdentifier.CreateNodeSchemaId).getValue());
		log.debug("Object def id: " + objectDefId);
		log.debug("newNodeId: " + newNodeId);
		log.debug("schema key: " + schemaKey);

		log.debug("Retrieving schema from cache");
		Schema sch = schemaLoaderService.getSchema(schemaKey);

		log.debug("Schema resolved by id: " + sch);
		ObjectDefinition entity = sch.getEntity(objectDefId);

		log.debug("Call create node");
		int newId = objectManagementService.insertNode(newNodeId, entity, filterAttributeParams(params,
				makeAttributeMap(entity.getAttributes())));
		log.info("Just created node id: " + newId);
		deltaHeader.setSyncStatus(newId != -1 ? SyncStatus.Processed : SyncStatus.Error);

		createUserActivityLog(UserActivityType.CreateNode, deltaHeader, new LogParam(ID_LOG_PARAM, newId));
	}

	private Map<Integer, Attribute> makeAttributeMap(List<Attribute> attributes){
		Map<Integer, Attribute> result = new HashMap<Integer, Attribute>();
		for (Attribute a : attributes)
			result.put(a.getId(), a);
		return result;
	}

	private Map<Attribute, String> filterAttributeParams(Map<DeltaParamIdentifier, DeltaParam> params,
			Map<Integer, Attribute> attributes){
		Map<Attribute, String> result = new HashMap<Attribute, String>();
		for (DeltaParamIdentifier dpi : params.keySet()){
			log.debug("Param=" + dpi + " | Value=" + params.get(dpi));
			if (!dpi.isFixedParam()){
				String aid = dpi.getIdentifier();
				Attribute a = attributes.get(Integer.parseInt(aid));
				result.put(a, params.get(dpi).getValue());
			}
		}
		return result;
	}

	private void processFavoriteCreate(final DeltaHeader deltaHeader){
		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam documentParam = parameters.get(DeltaParamIdentifier.CreateFavoriteDocument);
		final DeltaParam nameParam = parameters.get(DeltaParamIdentifier.CreateFavoriteName);
		final DeltaParam descriptionParam = parameters.get(DeltaParamIdentifier.CreateFavoriteDescription);
		final DeltaParam modeParam = parameters.get(DeltaParamIdentifier.CreateFavoriteMode);
		final DeltaParam groupFolderParam = parameters.get(DeltaParamIdentifier.CreateFavoriteGroupFolder);
		final DeltaParam folderIdParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderId);
		final DeltaParam layoutParam = parameters.get(DeltaParamIdentifier.CreateFavoriteLayout);
		final DeltaParam schemaIdParam = parameters.get(DeltaParamIdentifier.CreateFavoriteSchemaId);
		final DeltaParam favoriteIdParam = parameters.get(DeltaParamIdentifier.CreateFavoriteNewId);
		final DeltaParam dbVersion = parameters.get(DeltaParamIdentifier.CreateFavoriteDBVersion);

		if (log.isDebugEnabled()){
			log.debug("Document: " + documentParam);
			log.debug("Name: " + nameParam);
			log.debug("Descr: " + descriptionParam);
			log.debug("Mode: " + modeParam);
			log.debug("GroupFolder: " + groupFolderParam);
			log.debug("Folder: " + folderIdParam);
			log.debug("Layout: " + layoutParam);
			log.debug("Schema: " + schemaIdParam);
			log.debug("FavId: " + favoriteIdParam);
			log.debug("DBVers: " + dbVersion);
		}

		final Favorite favorite = new Favorite();
		favorite.setCreatorId(deltaHeader.getCreator().getId());
		favorite.setData(documentParam.getValue());
		favorite.setName(nameParam.getValue());
		favorite.setDescription(descriptionParam.getValue());
		favorite.setMode(FavoriteMode.getByValue(modeParam.getValueAsInteger()));
		favorite.setGroupFavorite(groupFolderParam.getValueAsBoolean());
		favorite.setFolderId(folderIdParam.getValueAsInteger());
		favorite.setLayout(layoutParam.getValue());
		favorite.setSchemaId(schemaIdParam.getValueAsInteger());
		favorite.setId(favoriteIdParam.getValueAsInteger());
		favorite.setDbVersion(dbVersion.getValue());

		boolean processed = false;
		try{
			favoriteDAO.create(favorite);
			processed = true;
		} catch (final DaoException e){
			log.error("Can't process Favorites create delta", e);
		}

		deltaHeader.setSyncStatus(processed ? SyncStatus.Processed : SyncStatus.Error);

		createUserActivityLog(UserActivityType.CreateFavorite, deltaHeader, new LogParam(ID_LOG_PARAM, favorite.getId()));
	}

	private void processFavoriteDelete(final DeltaHeader deltaHeader){
		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam favoriteIdParam = parameters.get(DeltaParamIdentifier.DeleteFavoriteId);

		boolean processed = false;
		Integer id = favoriteIdParam.getValueAsInteger();
		try{
			if (id == null)
				throw new SyncException("Unexpected null favorite id, deltaParamValue=" + favoriteIdParam.getValue());
			favoritesService.deleteFavorite(id);
			processed = true;
		} catch (final DaoException e){
			log.error("Can't process Favorites delete delta", e);
		} catch (SyncException ex){
			log.error("Can't process Favorites delete delta", ex);
		}

		deltaHeader.setSyncStatus(processed ? SyncStatus.Processed : SyncStatus.ProcessedWithWarning);

		createUserActivityLog(UserActivityType.DeleteFavorite, deltaHeader, new LogParam(ID_LOG_PARAM, id));
	}

	private void processFavoriteUpdate(final DeltaHeader deltaHeader){
		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam favoriteIdParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteId);
		final DeltaParam modeParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteMode);
		final DeltaParam descriptionParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteDescription);
		final DeltaParam nameParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteName);
		final DeltaParam folderIdParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteFolderId);
		final DeltaParam documentParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteDocument);
		final DeltaParam layoutParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteLayout);
		final DeltaParam groupFolderParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteGroupFolder);

		final Integer favoriteId = favoriteIdParam.getValueAsInteger();
		boolean processed = favoritesService.updateFavorite(favoriteId, folderIdParam == null ? null : folderIdParam
				.getValueAsInteger(), nameParam == null ? null : nameParam.getValue(), documentParam == null ? null
				: documentParam.getValue(), descriptionParam == null ? null : descriptionParam.getValue(),
				layoutParam == null ? null : layoutParam.getValue(), modeParam == null ? null : FavoriteMode
						.getByValue(modeParam.getValueAsInteger()), groupFolderParam == null ? null : groupFolderParam
						.getValueAsBoolean());
		deltaHeader.setSyncStatus(processed ? SyncStatus.Processed : SyncStatus.ProcessedWithWarning);

		createUserActivityLog(UserActivityType.UpdateFavorite, deltaHeader, new LogParam(ID_LOG_PARAM, favoriteId));
	}

	private void processFolderCreate(final DeltaHeader deltaHeader){
		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam newObjectIdParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderNewId);
		final DeltaParam nameParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderName);
		final DeltaParam schemaIdParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderSchemaId);
		final DeltaParam parentFolderIdParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderParentFolderId);
		final DeltaParam groupFolderParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderGroupFolder);
		final DeltaParam sortParam = parameters.get(DeltaParamIdentifier.CreateFavoriteFolderSort);

		final FavoritesFolder folder = new FavoritesFolder();
		folder.setCreatorId(deltaHeader.getCreator().getId());
		folder.setFolderName(nameParam.getValue());
		folder.setGroupFolder(groupFolderParam.getValueAsBoolean());
		folder.setId(newObjectIdParam.getValueAsInteger());
		folder.setParentId(parentFolderIdParam.getValueAsInteger());
		folder.setSchemaId(schemaIdParam.getValueAsInteger());
		folder.setSortOrder(sortParam.getValueAsInteger());

		boolean processed = false;
		try{
			favoritesFolderDAO.create(folder);
			processed = true;
		} catch (final DaoException e){
			log.error("Can't process FavoritesFolder create delta", e);
		}

		deltaHeader.setSyncStatus(processed ? SyncStatus.Processed : SyncStatus.Error);

		createUserActivityLog(UserActivityType.CreateFolder, deltaHeader, new LogParam(ID_LOG_PARAM, folder.getId()));
	}

	private void processFolderDelete(final DeltaHeader deltaHeader){
		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam folderObjectIdParam = parameters.get(DeltaParamIdentifier.DeleteFavoriteFolderObjId);

		boolean processed = false;
		final Integer id = folderObjectIdParam.getValueAsInteger();
		try{
			favoritesFolderService.deleteFolder(id);
			processed = true;
		} catch (final DaoException e){
			log.error("Can't process FavoritesFolder delete delta", e);
		}

		deltaHeader.setSyncStatus(processed ? SyncStatus.Processed : SyncStatus.ProcessedWithWarning);

		createUserActivityLog(UserActivityType.DeleteFolder, deltaHeader, new LogParam(ID_LOG_PARAM, id));
	}

	private void processFolderUpdate(final DeltaHeader deltaHeader){
		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam newObjectIdParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteFolderFolderId);
		final DeltaParam nameParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteFolderName);
		final DeltaParam parentFolderIdParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteFolderParentFolderId);
		final DeltaParam sortParam = parameters.get(DeltaParamIdentifier.UpdateFavoriteFolderSort);

		boolean processed = false;
		final Integer folderId = newObjectIdParam.getValueAsInteger();
		final FavoritesFolder folder = favoritesFolderDAO.get(folderId);
		if (folder != null){
			folder.setFolderName(nameParam.getValue());
			folder.setId(folderId);
			folder.setParentId(parentFolderIdParam.getValueAsInteger());
			folder.setSortOrder(sortParam.getValueAsInteger());

			try{
				favoritesFolderDAO.save(folder);
				processed = true;
			} catch (final DaoException e){
				log.error("Can't process FavoritesFolder create delta", e);
			}
		} else{
			log.warn("Can't process FavoritesFolder create delta: object not found");
		}

		deltaHeader.setSyncStatus(processed ? SyncStatus.Processed : SyncStatus.ProcessedWithWarning);

		createUserActivityLog(UserActivityType.UpdateFolder, deltaHeader, new LogParam(ID_LOG_PARAM, folderId));
	}

	private void processSettingsUpdate(final DeltaHeader deltaHeader){
		boolean processed = false;

		final Map<DeltaParamIdentifier, DeltaParam> parameters = deltaHeader.getDeltaParameters();
		final DeltaParam nameParam = parameters.get(DeltaParamIdentifier.UpdateSettingsPropertyName);
		final DeltaParam valueParam = parameters.get(DeltaParamIdentifier.UpdateSettingsPropertyValue);

		final String name = nameParam.getValue();
		final String newValue = valueParam.getValue();
		final User user = deltaHeader.getCreator();
		final Integer userId = user.getId();

		boolean doUpdate = false;
		UserSetting setting = userSettingsDAO.get(userId, name);
		if (setting != null){
			final String oldValue = setting.getValue();
			if (!oldValue.equals(newValue)){
				setting.setValue(newValue);
				doUpdate = true;
			}
		} else{
			setting = new UserSetting();
			setting.setId(userId);
			setting.setProperty(name);
			setting.setValue(newValue);
			// TODO: section?
			setting.setSection("Applet");
			doUpdate = true;
		}

		if (doUpdate){
			try{
				userSettingsDAO.save(setting);
				processed = true;
			} catch (final DaoException e){
				log.error("Can't process UserSettings delta", e);
			}
		}

		deltaHeader.setSyncStatus(!doUpdate || processed ? SyncStatus.Processed : SyncStatus.ProcessedWithWarning);
	}

	private String listParam(String param){
		return param == null ? null : param.replaceAll("[^0-9,-]", "");
	}

	private void createUserActivityLog(UserActivityType activityType, DeltaHeader header, LogParam... logParams){
		User user = header.getCreator();
		final UserActivityDAO uaDao = NSpringFactory.getInstance().getUserActivityDao();
		UserSessionStore store = UserSessionStore.getInstance();
		final String sessionId = store.getSessionId(user.getId());
		String message = "SessionId=" + sessionId + ";";
		for (LogParam param : logParams){
			message += param.name + "=" + param.value + ";";
		}
		message += SYNC_IDENTIFIER + ";";
		uaDao.save(user.getId(), activityType.getValueText(), message, "", "");
	}

	private class LogParam{
		private String name;
		private Object value;

		public LogParam(String name, Object value){
			super();
			this.name = name;
			this.value = value;
		}

	}
}
