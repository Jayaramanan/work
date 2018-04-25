/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.model.favorites;

import com.ni3.ag.navigator.client.model.FavoritesModel;
import junit.framework.TestCase;

import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;

public class FavoritesModelTest extends TestCase{

	private FavoritesModel model;

	@Override
	protected void setUp() throws Exception{
		model = new FavoritesModel(1, false);
	}

	public void testFavoritesModel(){
		assertEquals(3, model.getFolders().size());
		assertEquals(0, model.getFolders().get(0).getId());
		assertEquals(-3, model.getFolders().get(1).getId());
		assertEquals(-2, model.getFolders().get(2).getId());
	}

	public void testGetFavoritesPath(){
		Favorite fav = createFavorite(111, model.getMyFolder());
		Object[] path = model.getFavoritesPath(fav);
		assertEquals(3, path.length);
		assertEquals(model.getRootFolder(), path[0]);
		assertEquals(model.getMyFolder(), path[1]);
		assertEquals(fav, path[2]);

		fav.setFolder(model.getGroupFolder());
		path = model.getFavoritesPath(fav);
		assertEquals(3, path.length);
		assertEquals(model.getRootFolder(), path[0]);
		assertEquals(model.getGroupFolder(), path[1]);
		assertEquals(fav, path[2]);

		Folder folder = createFolder(100, model.getMyFolder());
		fav.setFolder(folder);
		path = model.getFavoritesPath(fav);
		assertEquals(4, path.length);
		assertEquals(model.getRootFolder(), path[0]);
		assertEquals(model.getMyFolder(), path[1]);
		assertEquals(folder, path[2]);
		assertEquals(fav, path[3]);
	}

	public void testIsFavoriteInFolder(){
		Favorite fav = createFavorite(111, model.getMyFolder());
		model.getFavorites().add(fav);

		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		assertFalse(model.isFavoriteInFolder(fav.getId(), folder.getId()));

		fav.setFolder(folder);
		assertTrue(model.isFavoriteInFolder(fav.getId(), folder.getId()));

		Folder folder2 = createFolder(101, folder);
		model.getFolders().add(folder2);

		fav.setFolder(folder2);
		assertTrue(model.isFavoriteInFolder(fav.getId(), folder.getId()));
		assertTrue(model.isFavoriteInFolder(fav.getId(), folder2.getId()));
		assertTrue(model.isFavoriteInFolder(fav.getId(), -3));
	}

	public void testGetFavoriteById(){
		Favorite fav = createFavorite(111, model.getMyFolder());
		model.getFavorites().add(fav);

		Favorite fav2 = createFavorite(112, model.getGroupFolder());

		model.getFavorites().add(fav2);

		assertEquals(fav, model.getFavoriteByID(111));
		assertEquals(fav2, model.getFavoriteByID(112));
		assertNull(model.getFavoriteByID(113));
	}

	public void testGetFavoriteByName(){
		Favorite fav = new Favorite();
		fav.setId(111);
		fav.setName("name111");
		fav.setFolder(model.getMyFolder());

		model.getFavorites().add(fav);

		Favorite fav2 = new Favorite();
		fav2.setId(112);
		fav2.setName("name112");
		fav2.setFolder(model.getGroupFolder());

		model.getFavorites().add(fav2);

		assertEquals(fav, model.getFavoriteByName("name111", -3));
		assertEquals(fav2, model.getFavoriteByName("name112", -2));
		assertNull(model.getFavoriteByName("name112", -3));
		assertNull(model.getFavoriteByName("name111", 11));
	}

	public void testRemoveFavorite(){
		Favorite fav = createFavorite(111, model.getMyFolder());
		model.getFavorites().add(fav);

		Favorite fav2 = createFavorite(112, model.getGroupFolder());
		model.getFavorites().add(fav2);

		assertEquals(2, model.getFavorites().size());

		model.removeFavorite(fav);
		assertEquals(1, model.getFavorites().size());
		assertEquals(fav2, model.getFavorites().get(0));

		model.removeFavorite(fav2);
		assertTrue(model.getFavorites().isEmpty());
	}

	public void testRemoveFolderWithIncludedFolders(){
		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, folder);
		model.getFolders().add(folder2);

		Favorite fav = createFavorite(111, folder);
		model.getFavorites().add(fav);

		Favorite fav2 = createFavorite(112, folder2);
		model.getFavorites().add(fav2);

		Favorite fav3 = createFavorite(113, model.getGroupFolder());
		model.getFavorites().add(fav3);

		assertEquals(3, model.getFavorites().size());
		assertEquals(5, model.getFolders().size());

		model.removeFolder(folder);

		assertEquals(1, model.getFavorites().size());
		assertEquals(fav3, model.getFavorites().get(0));

		assertEquals(3, model.getFolders().size());
		assertEquals(0, model.getFolders().get(0).getId());
		assertEquals(-3, model.getFolders().get(1).getId());
		assertEquals(-2, model.getFolders().get(2).getId());
	}

	public void testRemoveFolder(){
		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, model.getMyFolder());
		model.getFolders().add(folder2);

		Favorite fav = createFavorite(111, folder);
		model.getFavorites().add(fav);

		Favorite fav2 = createFavorite(112, folder2);
		model.getFavorites().add(fav2);

		Favorite fav3 = createFavorite(113, model.getGroupFolder());
		model.getFavorites().add(fav3);

		assertEquals(3, model.getFavorites().size());
		assertEquals(5, model.getFolders().size());

		model.removeFolder(folder2);

		assertEquals(2, model.getFavorites().size());
		assertEquals(fav, model.getFavorites().get(0));
		assertEquals(fav3, model.getFavorites().get(1));

		assertEquals(4, model.getFolders().size());
		assertEquals(0, model.getFolders().get(0).getId());
		assertEquals(-3, model.getFolders().get(1).getId());
		assertEquals(-2, model.getFolders().get(2).getId());
		assertEquals(folder, model.getFolders().get(3));
	}

	public void testIsUniqueFavoriteFolderName(){
		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, model.getMyFolder());
		model.getFolders().add(folder2);

		Folder folder3 = createFolder(102, model.getMyFolder());
		model.getFolders().add(folder3);

		assertTrue(model.isUniqueFavoriteFolderName("name102", 102, -3));
		assertFalse(model.isUniqueFavoriteFolderName("name101", 102, -3));
	}

	public void testIsUniqueFavoriteName(){
		Favorite fav = createFavorite(111, model.getMyFolder());
		model.getFavorites().add(fav);

		Favorite fav2 = createFavorite(112, model.getMyFolder());
		model.getFavorites().add(fav2);

		Favorite fav3 = createFavorite(113, model.getMyFolder());
		model.getFavorites().add(fav3);

		assertTrue(model.isUniqueFavoriteName("name112", 112, -3));
		assertFalse(model.isUniqueFavoriteName("name113", 112, -3));
	}

	public void testGetNewSort(){
		assertEquals(1, model.getNewSort(-3));

		Folder folder = createFolder(100, model.getMyFolder());
		folder.setSort(7);
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, model.getGroupFolder());
		folder2.setSort(20);
		model.getFolders().add(folder2);

		assertEquals(8, model.getNewSort(-3));

		Folder folder3 = createFolder(102, model.getMyFolder());
		folder3.setSort(9);
		model.getFolders().add(folder3);

		assertEquals(10, model.getNewSort(-3));
	}

	public void testIsFolderChildOfFolder(){

		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, folder);
		model.getFolders().add(folder2);

		Folder folder3 = createFolder(102, folder2);
		model.getFolders().add(folder3);

		Folder folder4 = createFolder(103, model.getMyFolder());
		model.getFolders().add(folder4);

		assertFalse(model.isFolderChildOfFolder(folder, model.getMyFolder()));
		assertFalse(model.isFolderChildOfFolder(folder2, model.getMyFolder()));
		assertFalse(model.isFolderChildOfFolder(folder3, model.getMyFolder()));
		assertFalse(model.isFolderChildOfFolder(folder4, model.getMyFolder()));
		assertTrue(model.isFolderChildOfFolder(folder2, folder));
		assertTrue(model.isFolderChildOfFolder(folder3, folder));
		assertTrue(model.isFolderChildOfFolder(folder3, folder2));

		assertFalse(model.isFolderChildOfFolder(folder, model.getGroupFolder()));
		assertFalse(model.isFolderChildOfFolder(folder2, model.getGroupFolder()));
		assertFalse(model.isFolderChildOfFolder(folder3, model.getGroupFolder()));
		assertFalse(model.isFolderChildOfFolder(folder4, model.getGroupFolder()));
	}

	public void testGetCurrentIndexOfFolder(){
		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, model.getMyFolder());
		model.getFolders().add(folder2);

		Folder folder3 = createFolder(102, folder2);
		model.getFolders().add(folder3);

		Folder folder4 = createFolder(103, model.getMyFolder());
		model.getFolders().add(folder4);

		assertEquals(0, model.getCurrentIndexOfFolder(folder));
		assertEquals(1, model.getCurrentIndexOfFolder(folder2));
		assertEquals(0, model.getCurrentIndexOfFolder(folder3));
		assertEquals(2, model.getCurrentIndexOfFolder(folder4));
	}

	public void testGetByIndex(){
		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, model.getMyFolder());
		model.getFolders().add(folder2);

		Folder folder3 = createFolder(102, folder2);
		model.getFolders().add(folder3);

		Folder folder4 = createFolder(103, model.getMyFolder());
		model.getFolders().add(folder4);

		assertEquals(folder, model.getByIndex(model.getMyFolder(), 0));
		assertEquals(folder2, model.getByIndex(model.getMyFolder(), 1));
		assertEquals(folder4, model.getByIndex(model.getMyFolder(), 2));

		assertEquals(folder3, model.getByIndex(folder2, 0));
		assertNull(model.getByIndex(model.getMyFolder(), 4));
		assertNull(model.getByIndex(folder3, 0));
	}

	public void testLastChildForFolder(){
		Folder folder = createFolder(100, model.getMyFolder());
		model.getFolders().add(folder);

		Folder folder2 = createFolder(101, model.getMyFolder());
		model.getFolders().add(folder2);

		Folder folder3 = createFolder(102, folder2);
		model.getFolders().add(folder3);

		Folder folder4 = createFolder(103, model.getMyFolder());
		model.getFolders().add(folder4);

		assertEquals(folder4, model.lastChildForFolder(model.getMyFolder(), folder));
		assertEquals(folder4, model.lastChildForFolder(model.getMyFolder(), folder2));
		assertEquals(folder2, model.lastChildForFolder(model.getMyFolder(), folder4));
		assertNull(model.lastChildForFolder(folder, folder2));
	}

	private Favorite createFavorite(int id, Folder folder){
		Favorite fav = new Favorite();
		fav.setId(id);
		fav.setName("name" + id);
		fav.setFolder(folder);
		return fav;
	}

	private Folder createFolder(int id, Folder parentFolder){
		Folder folder = new Folder();
		folder.setName("name" + id);
		folder.setId(id);
		folder.setParentFolder(parentFolder);
		return folder;
	}
}
