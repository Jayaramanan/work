package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.FavoritesFolder;

public interface FavoritesFolderDAO{

	Integer create(FavoritesFolder folder);

	void delete(FavoritesFolder folder);

	void delete(Integer id);

	FavoritesFolder get(Integer id);

	List<FavoritesFolder> getSubfolders(int id);

	FavoritesFolder save(FavoritesFolder folder);

	long getCount();

	List<FavoritesFolder> findByName(String name);

	List<FavoritesFolder> getFolders(int schemaId, int userId);

	List<Integer> getTraverseListParentFirst(List<FavoritesFolder> folders, Integer folderId);

	List<FavoritesFolder> getFolders();
}