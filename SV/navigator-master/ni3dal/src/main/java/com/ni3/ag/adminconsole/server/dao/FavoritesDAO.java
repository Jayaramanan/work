/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Favorites;
import com.ni3.ag.adminconsole.domain.Schema;

public interface FavoritesDAO{

	int getMaxIdForRange(int userRangeStart, int userRangeEnd);

	List<Favorites> getFavoritesWithoutCreator(Schema sch);

	List<Favorites> getMinorOutdatedFavorites(Schema sch, String version);

	List<Favorites> getMajorOutdatedFavorites(Schema sch, String version);

	List<Favorites> getFavorites(Schema schema, Integer mode);
}
