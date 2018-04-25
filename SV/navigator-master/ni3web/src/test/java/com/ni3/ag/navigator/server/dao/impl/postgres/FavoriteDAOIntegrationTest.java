package com.ni3.ag.navigator.server.dao.impl.postgres;

import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import junit.framework.TestCase;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.FavoriteDAO;
import com.ni3.ag.navigator.server.domain.Favorite;

public class FavoriteDAOIntegrationTest extends TestCase{
	private FavoriteDAO favoriteDAO = NSpringFactory.getInstance().getFavoritesDao();

	public void testSave(){
		Favorite favorite = new Favorite("desc", 2, "data", "layout", "fav_name1", 1, null, false, FavoriteMode.FAVORITE);
		Favorite afterSave = favoriteDAO.save(favorite);
		assertEquals(favorite.getMode(), afterSave.getMode());
	}
}
