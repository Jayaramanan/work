/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.Favorite;

public interface FavoritesGateway{

	void deleteFavorite(Favorite favorite);

	/**
	 * 
	 * @param favoriteFromID
	 *            old favorite id
	 * @param newFavorite
	 *            - target favorite
	 * @return id of new created favorite or -1 if error
	 */
	int copyFavorite(int favoriteFromID, Favorite newFavorite);

	/**
	 * 
	 * @param favoriteID
	 *            favorite id
	 * @return -1 if error, 2 if version is obsolete
	 */
	int validateFavoriteVersion(int favoriteID);

	/**
	 * @return id of new created favorite
	 */
	int createFavorite(Favorite favorite);

	void updateFavorite(Favorite fav);

	List<Favorite> getAllFavorites(int schemaID);

	Favorite loadFavoriteData(int id);
}
