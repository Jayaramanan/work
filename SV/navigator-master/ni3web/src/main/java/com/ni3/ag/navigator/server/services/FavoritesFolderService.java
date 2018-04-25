package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.FavoritesFolder;

import java.util.List;

public interface FavoritesFolderService{
	List<FavoritesFolder> getAllFolders(int schemaId);

	FavoritesFolder createFolder(FavoritesFolder folder);

	FavoritesFolder updateFolder(FavoritesFolder folder);

	void deleteFolder(int id);
}
