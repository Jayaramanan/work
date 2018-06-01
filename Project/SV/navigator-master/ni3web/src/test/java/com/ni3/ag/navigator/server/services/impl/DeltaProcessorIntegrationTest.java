package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.server.domain.FavoritesFolder;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.shared.domain.*;
import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.dao.UserSettingsDAO;
import com.ni3.ag.navigator.server.services.DeltaProcessor;

public class DeltaProcessorIntegrationTest extends TestCase{

	private static final String FOLDER_NAME = "integration-test-folder";

	private static final String FOLDER_NAME_UPDATED = FOLDER_NAME + "-updated";

	private static final String FAVORITE_NAME = "integration-test-favorite";
	private static final String FAVORITE_NAME_UPDATED = FAVORITE_NAME + "-updated";
	private static final String FAVORITE_DESCRIPTION = "integration-test-favorite-description";
	private static final String FAVORITE_DESCRIPTION_UPDATED = FAVORITE_DESCRIPTION + "-updated";

	private static final String SETTINGS_KEY = "integration-test-property";

	private static final Logger log = Logger.getLogger(DeltaProcessorIntegrationTest.class);

	private static final NSpringFactory daoFactory = NSpringFactory.getInstance();
	private static final UserSettingsDAO settingsDao = daoFactory.getUserSettingsDao();
	private static final UserDAO userDao = daoFactory.getUserDao();
	private static final FavoritesFolderDAO favoritsFolderDao = daoFactory.getFavoritesFolderDao();
	private static final FavoriteDAO favoritesDao = daoFactory.getFavoritesDao();
	private static final User user = userDao.get(1);

	private final List<String> settingsCleanupList = new ArrayList<String>();
	private final List<String> folderCleanupList = new ArrayList<String>();
	private final List<Integer> favoriteCleanupList = new ArrayList<Integer>();

	@Override
	protected void tearDown() throws Exception{
		for (final String property : settingsCleanupList){
			try{
				final UserSetting record = settingsDao.get(user.getId(), property);
				if (record != null){
					settingsDao.delete(record);
				}
			} catch (final RuntimeException e){
				log.error("Can't cleanup", e);
			}
		}

		for (final String name : folderCleanupList){
			final List<FavoritesFolder> list = favoritsFolderDao.findByName(name);
			try{
				for (final FavoritesFolder folder : list){
					favoritsFolderDao.delete(folder);
				}
			} catch (final RuntimeException e){
				log.error("Can't cleanup", e);
			}
		}

		for (final Integer id : favoriteCleanupList){
			try{
				favoritesDao.delete(id);
			} catch (final RuntimeException e){
				log.error("Can't cleanup", e);
			}
		}
	}

	public void testFavorite(){
		final DeltaProcessor processor = new DeltaProcessorImpl();
		final ArrayList<DeltaHeader> deltas = new ArrayList<DeltaHeader>();

		favoriteCleanupList.add(9999);

		final List<FavoritesFolder> preList = favoritsFolderDao.findByName(FOLDER_NAME);
		assertEquals(0, preList.size());

		final Map<DeltaParamIdentifier, DeltaParam> parameters = new HashMap<DeltaParamIdentifier, DeltaParam>();

		// create
		parameters.put(DeltaParamIdentifier.CreateFavoriteDocument, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteDocument, "document data"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteName, new DeltaParam(DeltaParamIdentifier.CreateFavoriteName,
		        FAVORITE_NAME));
		parameters.put(DeltaParamIdentifier.CreateFavoriteDescription, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteDescription, FAVORITE_DESCRIPTION));
		parameters
		        .put(DeltaParamIdentifier.CreateFavoriteMode, new DeltaParam(DeltaParamIdentifier.CreateFavoriteMode, "1"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteGroupFolder, "0"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderId, null));
		parameters.put(DeltaParamIdentifier.CreateFavoriteLayout, new DeltaParam(DeltaParamIdentifier.CreateFavoriteLayout,
		        "layout"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteSchemaId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteSchemaId, "2"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteNewId, new DeltaParam(DeltaParamIdentifier.CreateFavoriteNewId,
		        "9999"));
		deltas.add(new DeltaHeader(DeltaType.FAVORITE_CREATE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final Favorite favorite = favoritesDao.get(9999);
		assertNotNull(favorite);
		assertEquals("document data", favorite.getData());
		assertEquals(FAVORITE_NAME, favorite.getName());
		assertEquals(FAVORITE_DESCRIPTION, favorite.getDescription());

		// update
		deltas.clear();
		parameters.clear();
		parameters
		        .put(DeltaParamIdentifier.UpdateFavoriteMode, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteMode, "1"));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteDescription, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteDescription, FAVORITE_DESCRIPTION_UPDATED));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteName, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteName,
		        FAVORITE_NAME_UPDATED));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteFolderId, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderId, null));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteDocument, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteDocument, "updated document data"));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteId, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteId, "9999"));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteLayout, new DeltaParam(DeltaParamIdentifier.UpdateFavoriteLayout,
		        "updated layout"));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteGroupFolder, "0"));

		deltas.add(new DeltaHeader(DeltaType.FAVORITE_UPDATE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final Favorite favoritePostUpdate = favoritesDao.get(9999);
		assertNotNull(favoritePostUpdate);
		assertEquals("updated document data", favoritePostUpdate.getData());
		assertEquals(FAVORITE_NAME_UPDATED, favoritePostUpdate.getName());
		assertEquals(FAVORITE_DESCRIPTION_UPDATED, favoritePostUpdate.getDescription());

		// delete
		deltas.clear();
		parameters.clear();
		parameters.put(DeltaParamIdentifier.DeleteFavoriteFolderObjId, new DeltaParam(
		        DeltaParamIdentifier.DeleteFavoriteFolderObjId, "9999"));

		deltas.add(new DeltaHeader(DeltaType.FAVORITE_DELETE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final Favorite favoritePostDelete = favoritesDao.get(9999);
		assertNull(favoritePostDelete);
	}

	/**
	 * 
	 */
	public void testFavoritesFolder(){
		final DeltaProcessor processor = new DeltaProcessorImpl();
		final ArrayList<DeltaHeader> deltas = new ArrayList<DeltaHeader>();

		folderCleanupList.add(FOLDER_NAME);
		folderCleanupList.add(FOLDER_NAME_UPDATED);

		final List<FavoritesFolder> preList = favoritsFolderDao.findByName(FOLDER_NAME);
		assertEquals(0, preList.size());

		final Map<DeltaParamIdentifier, DeltaParam> parameters = new HashMap<DeltaParamIdentifier, DeltaParam>();

		// create
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderNewId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderNewId, "9999"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderName, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderName, FOLDER_NAME));
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderSchemaId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderSchemaId, "2"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderParentFolderId, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderParentFolderId, null));
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderGroupFolder, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderGroupFolder, "0"));
		parameters.put(DeltaParamIdentifier.CreateFavoriteFolderSort, new DeltaParam(
		        DeltaParamIdentifier.CreateFavoriteFolderSort, "10"));
		deltas.add(new DeltaHeader(DeltaType.FAVORITE_FOLDER_CREATE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final List<FavoritesFolder> postCreateList = favoritsFolderDao.findByName(FOLDER_NAME);
		assertNotNull(postCreateList);
		assertEquals(1, postCreateList.size());

		final FavoritesFolder postCreateRecord = postCreateList.get(0);
		assertNotNull(postCreateRecord);
		assertEquals(9999, postCreateRecord.getId());
		assertEquals(FOLDER_NAME, postCreateRecord.getFolderName());
		assertEquals(2, postCreateRecord.getSchemaId());
		assertEquals(0, postCreateRecord.getParentId());
		assertEquals(Boolean.FALSE, postCreateRecord.getGroupFolder());
		assertEquals(10, postCreateRecord.getSortOrder());

		// update
		deltas.clear();
		parameters.clear();
		parameters.put(DeltaParamIdentifier.UpdateFavoriteFolderId, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderId, "9999"));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteFolderName, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderName, FOLDER_NAME_UPDATED));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteFolderFolderId, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderFolderId, null));
		parameters.put(DeltaParamIdentifier.UpdateFavoriteFolderSort, new DeltaParam(
		        DeltaParamIdentifier.UpdateFavoriteFolderSort, "15"));
		deltas.add(new DeltaHeader(DeltaType.FAVORITE_FOLDER_UPDATE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final List<FavoritesFolder> postUpdateList = favoritsFolderDao.findByName(FOLDER_NAME_UPDATED);
		assertNotNull(postUpdateList);
		assertEquals(1, postUpdateList.size());

		final FavoritesFolder postUpdateRecord = postUpdateList.get(0);
		assertNotNull(postUpdateRecord);
		assertEquals(9999, postUpdateRecord.getId());
		assertEquals(FOLDER_NAME_UPDATED, postUpdateRecord.getFolderName());
		assertEquals(2, postUpdateRecord.getSchemaId());
		assertEquals(0, postUpdateRecord.getParentId());
		assertEquals(Boolean.FALSE, postUpdateRecord.getGroupFolder());
		assertEquals(15, postUpdateRecord.getSortOrder());

		// delete
		deltas.clear();
		parameters.clear();
		parameters.put(DeltaParamIdentifier.DeleteFavoriteFolderObjId, new DeltaParam(
		        DeltaParamIdentifier.DeleteFavoriteFolderObjId, "9999"));
		deltas.add(new DeltaHeader(DeltaType.FAVORITE_FOLDER_DELETE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final List<FavoritesFolder> postDeleteList = favoritsFolderDao.findByName(FOLDER_NAME);
		assertNotNull(postDeleteList);
		assertEquals(0, postDeleteList.size());
	}

	public void testSettings(){
		final DeltaProcessor processor = new DeltaProcessorImpl();
		final ArrayList<DeltaHeader> deltas = new ArrayList<DeltaHeader>();

		settingsCleanupList.add(SETTINGS_KEY);

		final UserSetting preRecord = settingsDao.get(user.getId(), SETTINGS_KEY);
		assertNull(preRecord);

		final Map<DeltaParamIdentifier, DeltaParam> parameters = new HashMap<DeltaParamIdentifier, DeltaParam>();
		parameters.put(DeltaParamIdentifier.UpdateSettingsPropertyName, new DeltaParam(
		        DeltaParamIdentifier.UpdateSettingsPropertyName, SETTINGS_KEY));
		parameters.put(DeltaParamIdentifier.UpdateSettingsPropertyValue, new DeltaParam(
		        DeltaParamIdentifier.UpdateSettingsPropertyValue, "value1"));
		deltas.add(new DeltaHeader(DeltaType.SETTING_UPDATE, user, parameters));
		processor.processDeltas(deltas, false);
		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final UserSetting postRecord = settingsDao.get(user.getId(), SETTINGS_KEY);
		assertNotNull(postRecord);
		assertEquals("value1", postRecord.getValue());

		deltas.clear();
		parameters.clear();
		parameters.put(DeltaParamIdentifier.UpdateSettingsPropertyName, new DeltaParam(
		        DeltaParamIdentifier.UpdateSettingsPropertyName, SETTINGS_KEY));
		parameters.put(DeltaParamIdentifier.UpdateSettingsPropertyValue, new DeltaParam(
		        DeltaParamIdentifier.UpdateSettingsPropertyValue, "value2"));
		deltas.add(new DeltaHeader(DeltaType.SETTING_UPDATE, user, parameters));

		processor.processDeltas(deltas, false);

		for (DeltaHeader dh : deltas)
			assertEquals(SyncStatus.Processed, dh.getSyncStatus());

		final UserSetting postUpdateRecord = settingsDao.get(user.getId(), SETTINGS_KEY);
		assertNotNull(postUpdateRecord);
		assertEquals("value2", postUpdateRecord.getValue());
	}
}
