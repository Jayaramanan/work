/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.search;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.domain.query.Section;

public class SearchControllerTest extends TestCase{

	private SearchController controller;
	private List<DBObject> dbObjects;

	@Override
	protected void setUp() throws Exception{
		controller = new SearchController(null);
		dbObjects = new ArrayList<DBObject>();
		for (int i = 0; i < 10; i++){
			DBObject dbo = new DBObject(new Entity((i % 3) + 1));
			dbo.setId(11 + i);
			dbObjects.add(dbo);
		}
	}

	public void testGetFirstNIds(){
		List<Integer> result = controller.getFirstNIds(1, dbObjects, 100);
		assertEquals(4, result.size());
		assertEquals(new Integer(11), result.get(0));
		assertEquals(new Integer(14), result.get(1));
		assertEquals(new Integer(17), result.get(2));
		assertEquals(new Integer(20), result.get(3));

		result = controller.getFirstNIds(3, dbObjects, 100);
		assertEquals(3, result.size());
		assertEquals(new Integer(13), result.get(0));
		assertEquals(new Integer(16), result.get(1));
		assertEquals(new Integer(19), result.get(2));

		result = controller.getFirstNIds(3, dbObjects, 2);
		assertEquals(2, result.size());
		assertEquals(new Integer(13), result.get(0));
		assertEquals(new Integer(16), result.get(1));
	}

	public void testGetNodeIdsToPutOnGraph(){
		Query query = new Query("", null);
		query.add(new Section("", new Entity(1)));
		query.add(new Section("", new Entity(2)));
		query.add(new Section("", new Entity(3)));
		List<Integer> result = controller.getNodeIdsToPutOnGraph(query, dbObjects, 100);
		assertEquals(10, result.size());

		assertEquals(new Integer(11), result.get(0));
		assertEquals(new Integer(14), result.get(1));
		assertEquals(new Integer(17), result.get(2));
		assertEquals(new Integer(20), result.get(3));
		assertEquals(new Integer(12), result.get(4));
		assertEquals(new Integer(15), result.get(5));
		assertEquals(new Integer(18), result.get(6));
		assertEquals(new Integer(13), result.get(7));
		assertEquals(new Integer(16), result.get(8));
		assertEquals(new Integer(19), result.get(9));

		result = controller.getNodeIdsToPutOnGraph(query, dbObjects, 2);
		assertEquals(6, result.size());

		assertEquals(new Integer(11), result.get(0));
		assertEquals(new Integer(14), result.get(1));
		assertEquals(new Integer(12), result.get(2));
		assertEquals(new Integer(15), result.get(3));
		assertEquals(new Integer(13), result.get(4));
		assertEquals(new Integer(16), result.get(5));
	}
}
