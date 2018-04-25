package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.Favorite;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;

public interface FavoritesService{

	static final String OLD_FAVORITE_WARNING = "1";
	static final String OLD_FAVORITE_ERROR = "2";
	static final String RESULT_ERROR = "-1";

	String checkVersion(int favoritesId);

	Favorite copyFavorite(Integer originalId, Integer favoriteId, Integer creatorId, Integer destinationFolderId,
			String newName, Boolean isGroupFavorite);

	Favorite createFavorite(Integer favoriteId, Integer userId, Integer schemaId, String name, String data,
			String description, Integer folderId, Boolean isGroupFavorite, String layout, FavoriteMode mode);

	void deleteFavorite(Integer favoriteId);

	boolean updateFavorite(Integer favoriteId, Integer folderId, final String name, String data, String description,
			String layout, FavoriteMode mode, Boolean group);

	void convertFavoriteToGroup(Integer favoriteId);

	Favorite createFavorite(Favorite favorite);

}
