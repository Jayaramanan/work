/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Favorites;

public class ChartFavoriteReferenceRuleTest extends TestCase{
	private String xml = "<NI3 version='3.00' SchemaID='25' ChartID='5' MapID='2' ThematicDataSetID='0' Mode='2' MetaphorSet='Default' ChartType='0,0,0,0,0' >"
	        + "<Query SchemaID='25' Name='' Type='1' CopyNToGraph='0' MaxResults='5000' TextQuery='null' GeoSearch='null'>"
	        + "<Section EntityID='26' Name=''></Section>"
	        + "<Section EntityID='28' Name=''>"
	        + "<Condition AttrID='336' Operation='=' Value='415'/>" + "</Section></Query></NI3>";

	public void testGetReferencedFavoritesNoReferences(){
		Favorites fav = new Favorites();
		fav.setName("Favorite 1");
		fav.setData(xml);
		List<Favorites> favorites = new ArrayList<Favorites>();
		favorites.add(fav);

		ChartFavoriteReferenceRule rule = new ChartFavoriteReferenceRule();
		assertEquals(0, rule.getReferencedFavorites("1", favorites).size());
	}

	public void testGetReferencedFavoritesExistReferences(){
		Favorites fav = new Favorites();
		fav.setName("Favorite 1");
		fav.setData(xml);
		List<Favorites> favorites = new ArrayList<Favorites>();
		favorites.add(fav);

		ChartFavoriteReferenceRule rule = new ChartFavoriteReferenceRule();
		assertEquals(1, rule.getReferencedFavorites("5", favorites).size());
	}
}
