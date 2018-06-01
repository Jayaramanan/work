package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.dao.FavoritesFolderDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.server.domain.FavoritesFolder;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import com.ni3.ag.navigator.shared.domain.User;

public class FavoritesFolderDAOIntegrationTest extends TestCase{
	private FavoritesFolderDAO folderDAO = NSpringFactory.getInstance().getFavoritesFolderDao();
	private UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
	private FavoriteDAO favoriteDAO = NSpringFactory.getInstance().getFavoritesDao();

	public void testDelete_TopOfTheTree(){
		Integer id1 = null;
		Integer id2 = null;
		Integer id3 = null;
		Favorite fav1 = null;
		Favorite fav2 = null;
		Favorite fav3 = null;

		try{
			long initialFolderCount = folderDAO.getCount();
			long initialFavoriteCount = favoriteDAO.getCount();

			User user = userDAO.get(1);

			// TODO extract general test data constants to base test class
			final int SCHEMA_ID = 2;
			final int CREATOR_ID = 1;

			FavoritesFolder folder1 = new FavoritesFolder("folder1", null, user, SCHEMA_ID, false, 111);
			id1 = folderDAO.save(folder1).getId();
			folder1.setId(id1);
			fav1 = new Favorite("desc", SCHEMA_ID, "data", "layout", "fav_name1", CREATOR_ID, id1, false,
			        FavoriteMode.FAVORITE);
			fav1 = favoriteDAO.save(fav1);

			FavoritesFolder folder2 = new FavoritesFolder("folder2", folder1, user, SCHEMA_ID, false, 222);
			id2 = folderDAO.save(folder2).getId();
			folder2.setId(id2);
			fav2 = new Favorite("desc", SCHEMA_ID, "data", "layout", "fav_name2", CREATOR_ID, id2, false,
			        FavoriteMode.FAVORITE);
			fav2 = favoriteDAO.save(fav2);

			FavoritesFolder folder3 = new FavoritesFolder("folder3", folder2, user, SCHEMA_ID, false, 333);
			id3 = folderDAO.save(folder3).getId();
			folder3.setId(id3);
			fav3 = new Favorite("desc", SCHEMA_ID, "data", "layout", "fav_name3", CREATOR_ID, id3, false,
			        FavoriteMode.FAVORITE);
			fav3 = favoriteDAO.save(fav3);

			long folderCountAfterSave = folderDAO.getCount();
			long favoriteCountAfterSave = favoriteDAO.getCount();

			assertEquals(initialFolderCount + 3, folderCountAfterSave);
			assertEquals(initialFavoriteCount + 3, favoriteCountAfterSave);

			folderDAO.delete(folder1);
			long countAfterDelete = folderDAO.getCount();
			long favoriteCountAfterDelete = favoriteDAO.getCount();

			assertEquals(initialFolderCount, countAfterDelete);
			assertEquals(initialFavoriteCount, favoriteCountAfterDelete);
		} finally{
			favoriteDAO.delete(fav1);
			favoriteDAO.delete(fav2);
			favoriteDAO.delete(fav3);
			folderDAO.delete(id3);
			folderDAO.delete(id2);
			folderDAO.delete(id1);
		}
	}

	public void testDelete_MiddleOfTheTree(){
		Integer id1 = null;
		Integer id2 = null;
		Integer id3 = null;
		Favorite fav1 = null;
		Favorite fav2 = null;
		Favorite fav3 = null;

		try{
			long initialFolderCount = folderDAO.getCount();
			long initialFavoriteCount = favoriteDAO.getCount();

			User user = userDAO.get(1);
			final int SCHEMA_ID = 2;
			final int CREATOR_ID = 1;

			FavoritesFolder folder1 = new FavoritesFolder("folder1", null, user, SCHEMA_ID, false, 111);
			id1 = folderDAO.save(folder1).getId();
			folder1.setId(id1);
			fav1 = new Favorite("desc", SCHEMA_ID, "data", "layout", "fav_name1", CREATOR_ID, id1, false,
			        FavoriteMode.FAVORITE);
			fav1 = favoriteDAO.save(fav1);

			FavoritesFolder folder2 = new FavoritesFolder("folder2", folder1, user, SCHEMA_ID, false, 222);
			id2 = folderDAO.save(folder2).getId();
			folder2.setId(id2);
			fav2 = new Favorite("desc", SCHEMA_ID, "data", "layout", "fav_name2", CREATOR_ID, id2, false,
			        FavoriteMode.FAVORITE);
			fav2 = favoriteDAO.save(fav2);

			FavoritesFolder folder3 = new FavoritesFolder("folder3", folder2, user, SCHEMA_ID, false, 333);
			id3 = folderDAO.save(folder3).getId();
			folder3.setId(id3);
			fav3 = new Favorite("desc", SCHEMA_ID, "data", "layout", "fav_name3", CREATOR_ID, id3, false,
			        FavoriteMode.FAVORITE);
			fav3 = favoriteDAO.save(fav3);

			long folderCountAfterSave = folderDAO.getCount();
			long favoriteCountAfterSave = favoriteDAO.getCount();

			assertEquals(initialFolderCount + 3, folderCountAfterSave);
			assertEquals(initialFavoriteCount + 3, favoriteCountAfterSave);

			folderDAO.delete(folder2);
			long countAfterDelete = folderDAO.getCount();
			long favoriteCountAfterDelete = favoriteDAO.getCount();

			assertEquals(initialFolderCount + 1, countAfterDelete);
			assertEquals(initialFavoriteCount + 1, favoriteCountAfterDelete);
		} finally{
			favoriteDAO.delete(fav1);
			favoriteDAO.delete(fav2);
			favoriteDAO.delete(fav3);
			folderDAO.delete(id3);
			folderDAO.delete(id2);
			folderDAO.delete(id1);
		}
	}

	public void testFindByName(){
		final List<FavoritesFolder> emptyList = folderDAO.findByName("Non-Existing-Name");
		assertNotNull(emptyList);
		assertEquals(0, emptyList.size());

		final List<FavoritesFolder> list = folderDAO.findByName("New folder");
		assertNotNull(list);
		assertEquals(2, list.size());
		final FavoritesFolder folder = list.get(0);
		assertNotNull(folder);
		assertEquals(73, folder.getId());
		assertEquals(0, folder.getParentId());
	}
}
