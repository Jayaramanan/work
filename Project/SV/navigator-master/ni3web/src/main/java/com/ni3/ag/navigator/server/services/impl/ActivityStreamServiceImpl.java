/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.*;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.ActivityStreamService;
import org.apache.log4j.Logger;

public class ActivityStreamServiceImpl implements ActivityStreamService{
	private static final Logger log = Logger.getLogger(ActivityStreamServiceImpl.class);

	private DeltaHeaderDAO deltaHeaderDAO;
	private DeltaParamDAO deltaParamDAO;
	private FavoriteDAO favoriteDAO;
	private FavoritesFolderDAO favoritesFolderDAO;
	private ObjectGroupDAO objectGroupDAO;

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
	}

	public void setDeltaParamDAO(DeltaParamDAO deltaParamDAO){
		this.deltaParamDAO = deltaParamDAO;
	}

	public void setFavoriteDAO(FavoriteDAO favoriteDAO){
		this.favoriteDAO = favoriteDAO;
	}

	public void setFavoritesFolderDAO(FavoritesFolderDAO favoritesFolderDAO){
		this.favoritesFolderDAO = favoritesFolderDAO;
	}

	public void setObjectGroupDAO(ObjectGroupDAO objectGroupDAO){
		this.objectGroupDAO = objectGroupDAO;
	}

	@Override
	public List<DeltaHeader> getLastDeltas(int count, long lastId, int schemaId, int groupId){
		log.info("getLastDeltas request count=" + count + " lastId: " + lastId);

		List<ObjectDefinitionGroup> objectGroups = objectGroupDAO.getByGroupId(groupId, schemaId);
		int offset = 0;
		int limit = 30;

		List<DeltaHeader> result = new ArrayList<DeltaHeader>();
		while (result.size() < count){
			List<DeltaHeader> deltas = deltaHeaderDAO.getLastDeltas(limit, offset, lastId);
			log.debug("Loaded deltas: " + deltas.size());
			if (deltas.isEmpty()){
				break;
			}

			for (DeltaHeader delta : deltas){
				final Map<DeltaParamIdentifier, DeltaParam> params = deltaParamDAO.getByDeltaHeader(delta);
				delta.setDeltaParameters(params);
				if (isAppropriateDelta(delta, schemaId, objectGroups)){
					result.add(delta);
					if (result.size() >= count){
						break;
					}
				}
			}
			offset += limit;
			if(offset < 0)
				break;
		}

		return result;
	}

	boolean isAppropriateDelta(DeltaHeader delta, int schemaId, List<ObjectDefinitionGroup> objectGroups){
		boolean result = true;
		final Integer deltaSchemaId = getSchemaId(delta);
		if (deltaSchemaId == null || deltaSchemaId != schemaId){
			result = false;
		} else{
			switch (delta.getDeltaType()){
				case FAVORITE_CREATE:
				case FAVORITE_UPDATE:
				case FAVORITE_COPY:
					final Favorite favorite = favoriteDAO.get(getObjectId(delta, delta.getDeltaParameters()));
					if (favorite == null || !favorite.getGroupFavorite()){
						result = false;
					}
					break;
				case FAVORITE_FOLDER_CREATE:
				case FAVORITE_FOLDER_UPDATE:
					final FavoritesFolder folder = favoritesFolderDAO.get(getObjectId(delta, delta.getDeltaParameters()));
					if (folder == null || !folder.getGroupFolder()){
						result = false;
					}
					break;
				case EDGE_CREATE:
				case EDGE_UPDATE:
				case NODE_CREATE:
				case NODE_UPDATE:
				case NODE_MERGE:
				case OBJECT_DELETE:
					Integer odId = getObjectDefinitionId(delta);
					result = (odId != null && hasAccessToObjectDefinition(odId, objectGroups));
					break;
			}
		}
		return result;
	}

	boolean hasAccessToObjectDefinition(int odId, List<ObjectDefinitionGroup> objectGroups){
		boolean hasAccess = false;
		for (ObjectDefinitionGroup og : objectGroups){
			if (og.getObject().getId() == odId){
				hasAccess = og.isCanRead();
				break;
			}
		}
		return hasAccess;
	}

	Integer getObjectDefinitionId(DeltaHeader delta){
		final Map<DeltaParamIdentifier, DeltaParam> params = delta.getDeltaParameters();
		DeltaParam odIdParam = null;
		switch (delta.getDeltaType()){
			case EDGE_CREATE:
				odIdParam = params.get(DeltaParamIdentifier.CreateEdgeObjectDefinitionId);
				break;
			case NODE_CREATE:
				odIdParam = params.get(DeltaParamIdentifier.CreateNodeObjectDefinitionId);
				break;
			case EDGE_UPDATE:
				odIdParam = params.get(DeltaParamIdentifier.UpdateEdgeObjectDefinitionId);
				break;
			case NODE_UPDATE:
				odIdParam = params.get(DeltaParamIdentifier.UpdateNodeObjectDefinitionId);
				break;
			case NODE_MERGE:
				odIdParam = params.get(DeltaParamIdentifier.MergeNodeObjectDefinitionId);
				break;
			case OBJECT_DELETE:
				odIdParam = params.get(DeltaParamIdentifier.DeleteObjectObjectDefinitionId);
				break;
		}
		return odIdParam != null ? odIdParam.getValueAsInteger() : null;
	}

	Integer getSchemaId(DeltaHeader delta){
		final Map<DeltaParamIdentifier, DeltaParam> params = delta.getDeltaParameters();
		DeltaParam idParam = null;
		switch (delta.getDeltaType()){
			case EDGE_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateEdgeSchemaId);
				break;
			case NODE_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateNodeSchemaId);
				break;
			case EDGE_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateEdgeSchemaId);
				break;
			case NODE_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateNodeSchemaId);
				break;
			case NODE_MERGE:
				idParam = params.get(DeltaParamIdentifier.MergeNodeSchemaId);
				break;
			case OBJECT_DELETE:
				idParam = params.get(DeltaParamIdentifier.DeleteObjectSchemaId);
				break;
			case FAVORITE_COPY:
				idParam = params.get(DeltaParamIdentifier.CopyFavoriteSchemaId);
				break;
			case FAVORITE_FOLDER_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateFavoriteFolderSchemaId);
				break;
			case FAVORITE_FOLDER_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateFavoriteFolderSchemaId);
				break;
			case FAVORITE_FOLDER_DELETE:
				idParam = params.get(DeltaParamIdentifier.DeleteFavoriteFolderSchemaId);
				break;
			case FAVORITE_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateFavoriteSchemaId);
				break;
			case FAVORITE_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateFavoriteSchemaId);
				break;
			case FAVORITE_DELETE:
				idParam = params.get(DeltaParamIdentifier.DeleteFavoriteSchemaId);
				break;
			default:
				break;
		}

		Integer id = null;
		if (idParam != null){
			id = idParam.getValueAsInteger();
		}
		return id;
	}

	@Override
	public Integer getObjectId(DeltaHeader delta, final Map<DeltaParamIdentifier, DeltaParam> params){
		DeltaParam idParam = null;
		switch (delta.getDeltaType()){
			case EDGE_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateEdgeNewId);
				break;
			case NODE_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateNodeNewId);
				break;
			case FAVORITE_COPY:
				idParam = params.get(DeltaParamIdentifier.CopyFavoriteNewId);
				break;
			case FAVORITE_FOLDER_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateFavoriteFolderNewId);
				break;
			case EDGE_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateEdgeObjectId);
				break;
			case NODE_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateNodeObjectId);
				break;
			case NODE_MERGE:
				idParam = params.get(DeltaParamIdentifier.MergeNodeToId);
				break;
			case OBJECT_DELETE:
				idParam = params.get(DeltaParamIdentifier.DeleteObjectObjectId);
				break;
			case FAVORITE_FOLDER_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateFavoriteFolderFolderId);
				break;
			case FAVORITE_FOLDER_DELETE:
				idParam = params.get(DeltaParamIdentifier.DeleteFavoriteFolderObjId);
				break;
			case FAVORITE_UPDATE:
				idParam = params.get(DeltaParamIdentifier.UpdateFavoriteId);
				break;
			case FAVORITE_CREATE:
				idParam = params.get(DeltaParamIdentifier.CreateFavoriteNewId);
				break;
			case FAVORITE_DELETE:
				idParam = params.get(DeltaParamIdentifier.DeleteFavoriteId);
				break;
			default:
				break;
		}

		Integer id = null;
		if (idParam != null){
			id = idParam.getValueAsInteger();
		}
		return id;
	}

	@Override
	public String getObjectName(DeltaHeader delta, int objectId){
		String name = "";
		switch (delta.getDeltaType()){
			case FAVORITE_CREATE:
			case FAVORITE_UPDATE:
			case FAVORITE_COPY:
				Favorite fav = favoriteDAO.get(objectId);
				if (fav != null){
					name = fav.getName();
				}
				break;
			case FAVORITE_FOLDER_CREATE:
			case FAVORITE_FOLDER_UPDATE:
				FavoritesFolder folder = favoritesFolderDAO.get(objectId);
				if (folder != null){
					name = folder.getFolderName();
				}
				break;
			default:
				break;
		}

		return name;
	}

}
