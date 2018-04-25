/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import junit.framework.TestCase;

public class ActivityStreamServiceImplTest extends TestCase{
	private ActivityStreamServiceImpl service;
	private DeltaType[] deltaTypes;
	private boolean isGroup = false;

	@Override
	protected void setUp() throws Exception{
		service = new ActivityStreamServiceImpl();
		deltaTypes = new DeltaType[] { DeltaType.EDGE_CREATE, DeltaType.NODE_CREATE, DeltaType.EDGE_UPDATE,
				DeltaType.NODE_UPDATE, DeltaType.NODE_MERGE, DeltaType.OBJECT_DELETE, DeltaType.FAVORITE_COPY,
				DeltaType.FAVORITE_FOLDER_CREATE, DeltaType.FAVORITE_FOLDER_UPDATE, DeltaType.FAVORITE_FOLDER_DELETE,
				DeltaType.FAVORITE_UPDATE, DeltaType.FAVORITE_CREATE, DeltaType.FAVORITE_DELETE };
	}

	public void testGetSchemaID(){
		DeltaParamIdentifier[] identifiers = { DeltaParamIdentifier.CreateEdgeSchemaId,
				DeltaParamIdentifier.CreateNodeSchemaId, DeltaParamIdentifier.UpdateEdgeSchemaId,
				DeltaParamIdentifier.UpdateNodeSchemaId, DeltaParamIdentifier.MergeNodeSchemaId,
				DeltaParamIdentifier.DeleteObjectSchemaId, DeltaParamIdentifier.CopyFavoriteSchemaId,
				DeltaParamIdentifier.CreateFavoriteFolderSchemaId, DeltaParamIdentifier.UpdateFavoriteFolderSchemaId,
				DeltaParamIdentifier.DeleteFavoriteFolderSchemaId, DeltaParamIdentifier.UpdateFavoriteSchemaId,
				DeltaParamIdentifier.CreateFavoriteSchemaId, DeltaParamIdentifier.DeleteFavoriteSchemaId };

		final Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		for (int i = 0; i < identifiers.length; i++){
			params.put(identifiers[i], new DeltaParam(identifiers[i], i + 11 + ""));
		}
		DeltaHeader dh = new DeltaHeader(null, null, params);

		for (int i = 0; i < deltaTypes.length; i++){
			DeltaType dt = deltaTypes[i];
			dh.setDeltaType(dt);
			Integer schemaId = service.getSchemaId(dh);
			assertEquals(new Integer(i + 11), schemaId);
		}
	}

	public void testGetObjectID(){
		DeltaParamIdentifier[] identifiers = { DeltaParamIdentifier.CreateEdgeNewId, DeltaParamIdentifier.CreateNodeNewId,
				DeltaParamIdentifier.UpdateEdgeObjectId, DeltaParamIdentifier.UpdateNodeObjectId,
				DeltaParamIdentifier.MergeNodeToId, DeltaParamIdentifier.DeleteObjectObjectId,
				DeltaParamIdentifier.CopyFavoriteNewId, DeltaParamIdentifier.CreateFavoriteFolderNewId,
				DeltaParamIdentifier.UpdateFavoriteFolderFolderId, DeltaParamIdentifier.DeleteFavoriteFolderObjId,
				DeltaParamIdentifier.UpdateFavoriteId, DeltaParamIdentifier.CreateFavoriteNewId,
				DeltaParamIdentifier.DeleteFavoriteId };

		final Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		for (int i = 0; i < identifiers.length; i++){
			params.put(identifiers[i], new DeltaParam(identifiers[i], i + 111 + ""));
		}
		DeltaHeader dh = new DeltaHeader(null, null, params);

		for (int i = 0; i < deltaTypes.length; i++){
			DeltaType dt = deltaTypes[i];
			dh.setDeltaType(dt);
			Integer objectId = service.getObjectId(dh, dh.getDeltaParameters());
			assertEquals(new Integer(i + 111), objectId);
		}
	}

	public void testGetObjectDefinitionId(){
		DeltaParamIdentifier[] identifiers = { DeltaParamIdentifier.CreateEdgeObjectDefinitionId,
				DeltaParamIdentifier.CreateNodeObjectDefinitionId, DeltaParamIdentifier.UpdateEdgeObjectDefinitionId,
				DeltaParamIdentifier.UpdateNodeObjectDefinitionId, DeltaParamIdentifier.MergeNodeObjectDefinitionId,
				DeltaParamIdentifier.DeleteObjectObjectDefinitionId };

		final Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		for (int i = 0; i < identifiers.length; i++){
			params.put(identifiers[i], new DeltaParam(identifiers[i], i + 111 + ""));
		}
		DeltaHeader dh = new DeltaHeader(null, null, params);

		for (int i = 0; i < deltaTypes.length; i++){
			DeltaType dt = deltaTypes[i];
			dh.setDeltaType(dt);
			Integer odId = service.getObjectDefinitionId(dh);
			if (i < 6){
				assertEquals(new Integer(i + 111), odId);
			} else{
				assertNull(odId);
			}
		}
	}

	public void testHasAccessToObjectDefinition(){
		List<ObjectDefinitionGroup> objectGroups = new ArrayList<ObjectDefinitionGroup>();
		ObjectDefinitionGroup readableOg = new ObjectDefinitionGroup();
		readableOg.setObject(new ObjectDefinition(11));
		readableOg.setGroupId(1);
		readableOg.setCanRead(true);

		ObjectDefinitionGroup notReadableOg = new ObjectDefinitionGroup();
		notReadableOg.setObject(new ObjectDefinition(12));
		notReadableOg.setGroupId(1);
		notReadableOg.setCanRead(false);

		objectGroups.add(readableOg);
		objectGroups.add(notReadableOg);

		assertTrue(service.hasAccessToObjectDefinition(11, objectGroups));
		assertFalse(service.hasAccessToObjectDefinition(12, objectGroups));
		assertFalse(service.hasAccessToObjectDefinition(13, objectGroups));
	}

	public void testIsAppropriateDelta(){
		DeltaParamIdentifier[] objectIdentifiers = { DeltaParamIdentifier.CreateEdgeNewId,
				DeltaParamIdentifier.CreateNodeNewId, DeltaParamIdentifier.UpdateEdgeObjectId,
				DeltaParamIdentifier.UpdateNodeObjectId, DeltaParamIdentifier.MergeNodeToId,
				DeltaParamIdentifier.DeleteObjectObjectId, DeltaParamIdentifier.CopyFavoriteNewId,
				DeltaParamIdentifier.CreateFavoriteFolderNewId, DeltaParamIdentifier.UpdateFavoriteFolderFolderId,
				DeltaParamIdentifier.DeleteFavoriteFolderObjId, DeltaParamIdentifier.UpdateFavoriteId,
				DeltaParamIdentifier.CreateFavoriteNewId, DeltaParamIdentifier.DeleteFavoriteId };
		DeltaParamIdentifier[] odIdentifiers = { DeltaParamIdentifier.CreateEdgeObjectDefinitionId,
				DeltaParamIdentifier.CreateNodeObjectDefinitionId, DeltaParamIdentifier.UpdateEdgeObjectDefinitionId,
				DeltaParamIdentifier.UpdateNodeObjectDefinitionId, DeltaParamIdentifier.MergeNodeObjectDefinitionId,
				DeltaParamIdentifier.DeleteObjectObjectDefinitionId };
		DeltaParamIdentifier[] schemaIdentifiers = { DeltaParamIdentifier.CreateEdgeSchemaId,
				DeltaParamIdentifier.CreateNodeSchemaId, DeltaParamIdentifier.UpdateEdgeSchemaId,
				DeltaParamIdentifier.UpdateNodeSchemaId, DeltaParamIdentifier.MergeNodeSchemaId,
				DeltaParamIdentifier.DeleteObjectSchemaId, DeltaParamIdentifier.CopyFavoriteSchemaId,
				DeltaParamIdentifier.CreateFavoriteFolderSchemaId, DeltaParamIdentifier.UpdateFavoriteFolderSchemaId,
				DeltaParamIdentifier.DeleteFavoriteFolderSchemaId, DeltaParamIdentifier.UpdateFavoriteSchemaId,
				DeltaParamIdentifier.CreateFavoriteSchemaId, DeltaParamIdentifier.DeleteFavoriteSchemaId };

		final Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		for (int i = 0; i < objectIdentifiers.length; i++){
			params.put(objectIdentifiers[i], new DeltaParam(objectIdentifiers[i], i + 111 + ""));
		}
		for (int i = 0; i < odIdentifiers.length; i++){
			params.put(odIdentifiers[i], new DeltaParam(schemaIdentifiers[i], i + 11 + ""));
		}

		for (int i = 0; i < schemaIdentifiers.length; i++){
			params.put(schemaIdentifiers[i], new DeltaParam(schemaIdentifiers[i], 2 + ""));
		}

		List<ObjectDefinitionGroup> objectGroups = new ArrayList<ObjectDefinitionGroup>();
		for (int i = 0; i < 6; i++){
			ObjectDefinitionGroup readableOg = new ObjectDefinitionGroup();
			readableOg.setObject(new ObjectDefinition(i + 11));
			readableOg.setGroupId(1);
			readableOg.setCanRead(true);
			objectGroups.add(readableOg);
		}

		DeltaHeader dh = new DeltaHeader(null, null, params);
		isGroup = true;
		service.setFavoriteDAO(favoriteDao);
		service.setFavoritesFolderDAO(folderDAO);

		for (int i = 0; i < deltaTypes.length; i++){
			DeltaType dt = deltaTypes[i];
			dh.setDeltaType(dt);
			boolean result = service.isAppropriateDelta(dh, 2, objectGroups);
			assertTrue(result);
		}

		isGroup = false;
		for (ObjectDefinitionGroup og : objectGroups){
			og.setCanRead(false);
		}
		for (int i = 0; i < deltaTypes.length; i++){
			DeltaType dt = deltaTypes[i];
			dh.setDeltaType(dt);
			boolean result = service.isAppropriateDelta(dh, 2, objectGroups);
			if (dt == DeltaType.FAVORITE_DELETE || dt == DeltaType.FAVORITE_FOLDER_DELETE){
				assertTrue(result);
			} else{
				assertFalse(result);
			}
		}

		isGroup = false;
		objectGroups.clear();
		for (int i = 0; i < deltaTypes.length; i++){
			DeltaType dt = deltaTypes[i];
			dh.setDeltaType(dt);
			boolean result = service.isAppropriateDelta(dh, 2, objectGroups);
			if (dt == DeltaType.FAVORITE_DELETE || dt == DeltaType.FAVORITE_FOLDER_DELETE){
				assertTrue(result);
			} else{
				assertFalse(result);
			}
		}
	}

	private FavoriteDAO favoriteDao = new FavoriteDAO(){

		@Override
		public Favorite save(Favorite favorite){
			return null;
		}

		@Override
		public List<Favorite> getFavorites(){
			return null;
		}

		@Override
		public List<Integer> getFavoriteIdsByFolder(Integer id){
			return null;
		}

		@Override
		public long getCount(){
			return 0;
		}

		@Override
		public List<Favorite> getBySchema(int schemaId, int userId){
			return null;
		}

		@Override
		public List<Favorite> getBySchema(int schemaId){
			return null;
		}

		@Override
		public Favorite get(Integer id){
			Favorite favorite = new Favorite();
			favorite.setId(id);
			favorite.setGroupFavorite(isGroup);
			return favorite;
		}

		@Override
		public void deleteByFolder(Integer folderId){

		}

		@Override
		public void delete(Integer id){

		}

		@Override
		public void delete(Favorite favorite){
		}

		@Override
		public Favorite create(Favorite favorite){
			return null;
		}
	};

	private FavoritesFolderDAO folderDAO = new FavoritesFolderDAO(){

		@Override
		public FavoritesFolder save(FavoritesFolder folder){
			return null;
		}

		@Override
		public List<Integer> getTraverseListParentFirst(List<FavoritesFolder> folders, Integer folderId){
			return null;
		}

		@Override
		public List<FavoritesFolder> getSubfolders(int id){
			return null;
		}

		@Override
		public List<FavoritesFolder> getFolders(){
			return null;
		}

		@Override
		public List<FavoritesFolder> getFolders(int schemaId, int userId){
			return null;
		}

		@Override
		public long getCount(){
			return 0;
		}

		@Override
		public FavoritesFolder get(Integer id){
			FavoritesFolder folder = new FavoritesFolder(id, null);
			folder.setGroupFolder(isGroup);
			return folder;
		}

		@Override
		public List<FavoritesFolder> findByName(String name){
			return null;
		}

		@Override
		public void delete(Integer id){
		}

		@Override
		public void delete(FavoritesFolder folder){
		}

		@Override
		public Integer create(FavoritesFolder folder){
			return null;
		}
	};
}
