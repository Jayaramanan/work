/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.favorites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.mockito.Mockito;

import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.model.FavoritesModel;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.shared.constants.QueryType;

public class FavoritesControllerTest extends TestCase{

	private FavoritesController controller;
	private Ni3Document doc;
	private FavoritesModel model;

	@Override
	protected void setUp() throws Exception{
		doc = Mockito.mock(Ni3Document.class);
		model = Mockito.mock(FavoritesModel.class);
		controller = new FavoritesController(doc);
		Mockito.when(doc.getFavoritesModel()).thenReturn(model);
	}

	public void testGetFavoriteCopyName(){
		Folder folder = createFolder(100, null);
		Favorite fav = createFavorite(111, folder);
		Favorite fav2 = createFavorite(112, folder);
		Mockito.when(model.getFavoriteByName("Copy of name111", 100)).thenReturn(fav);
		Mockito.when(model.getFavoriteByName("Copy of name111 (1)", 100)).thenReturn(fav2);
		final String name = controller.getFavoriteCopyName("name111", folder.getId());
		assertEquals("Copy of name111 (2)", name);
	}

	public void testContainsSimpleSearchQuery(){
		List<Query> queries = new ArrayList<Query>();
		queries.add(new Query("q1", null));
		queries.add(new Query("q2", null));
		queries.add(new Query("q3", null));
		queries.get(0).setType(QueryType.NODE);
		queries.get(1).setType(QueryType.NODE_WITH_CONNECTIONS);
		queries.get(2).setType(QueryType.LINKED_NODES);
		assertFalse(controller.containsSimpleSearchQuery(new ArrayList<Query>()));
		assertFalse(controller.containsSimpleSearchQuery(queries));

		queries.get(1).setType(QueryType.SIMPLE);
		assertTrue(controller.containsSimpleSearchQuery(queries));

		assertTrue(controller.containsSimpleSearchQuery(Arrays.asList(queries.get(1))));
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
