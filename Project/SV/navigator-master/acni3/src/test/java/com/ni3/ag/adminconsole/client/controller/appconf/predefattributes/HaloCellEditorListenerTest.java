/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class HaloCellEditorListenerTest extends ACTestCase{

	private HaloCellEditorListener listener;
	private List<PredefinedAttribute> search;

	public void setUp(){
		listener = new HaloCellEditorListener(null);
		search = new ArrayList<PredefinedAttribute>();
		PredefinedAttribute pa1 = new PredefinedAttribute();
		pa1.setId(1);
		pa1.setLevel(0);
		PredefinedAttribute pa2 = new PredefinedAttribute();
		pa2.setId(2);
		pa2.setLevel(0);
		PredefinedAttribute pa3 = new PredefinedAttribute();
		pa3.setId(3);
		pa3.setLevel(0);
		PredefinedAttribute pa4 = new PredefinedAttribute();
		pa4.setId(4);
		pa4.setLevel(0);
		PredefinedAttribute pa5 = new PredefinedAttribute();
		pa5.setId(5);
		pa5.setLevel(0);
		PredefinedAttribute pa6 = new PredefinedAttribute();
		pa6.setId(6);
		pa6.setLevel(0);
		PredefinedAttribute pa7 = new PredefinedAttribute();
		pa7.setId(7);
		pa7.setLevel(0);
		PredefinedAttribute pa8 = new PredefinedAttribute();
		pa8.setId(8);
		pa8.setLevel(0);
		pa1.setParent(pa2);
		pa2.setParent(pa3);
		pa4.setParent(pa5);
		pa8.setParent(pa3);
		pa3.setChildren(new ArrayList<PredefinedAttribute>());
		pa2.setChildren(new ArrayList<PredefinedAttribute>());
		pa5.setChildren(new ArrayList<PredefinedAttribute>());
		pa3.getChildren().add(pa2);
		pa3.getChildren().add(pa8);
		pa2.getChildren().add(pa1);
		pa5.getChildren().add(pa4);

		search.add(0, pa1);
		search.add(1, pa2);
		search.add(2, pa3);
		search.add(3, pa4);
		search.add(4, pa5);
		search.add(5, pa6);
		search.add(6, pa7);
		search.add(7, pa8);
	}

	public void testFindRootParent(){
		PredefinedAttribute pa = listener.findRootParent(search.get(0));
		assertEquals(3, pa.getId().intValue());
		assertEquals(2, search.get(0).getLevel().intValue());

		pa = listener.findRootParent(search.get(1));
		assertEquals(3, pa.getId().intValue());
		assertEquals(1, search.get(1).getLevel().intValue());

		pa = listener.findRootParent(search.get(2));
		assertEquals(3, pa.getId().intValue());
		assertEquals(0, search.get(2).getLevel().intValue());

		pa = listener.findRootParent(search.get(3));
		assertEquals(5, pa.getId().intValue());
		assertEquals(1, search.get(3).getLevel().intValue());

		pa = listener.findRootParent(search.get(4));
		assertEquals(5, pa.getId().intValue());
		assertEquals(0, search.get(4).getLevel().intValue());

		pa = listener.findRootParent(search.get(5));
		assertEquals(6, pa.getId().intValue());
		assertEquals(0, search.get(5).getLevel().intValue());

		pa = listener.findRootParent(search.get(6));
		assertEquals(7, pa.getId().intValue());
		assertEquals(0, search.get(6).getLevel().intValue());

		pa = listener.findRootParent(search.get(7));
		assertEquals(3, pa.getId().intValue());
		assertEquals(1, search.get(7).getLevel().intValue());
	}

	public void testSearchNestedPredefineds(){
		List<PredefinedAttribute> result = new ArrayList<PredefinedAttribute>();
		listener.searchNestedPredefineds(search.get(0), result, 2, 2);
		assertEquals(0, result.size());
		listener.searchNestedPredefineds(search.get(1), result, 1, 1);
		assertEquals(1, result.size());
		assertEquals(search.get(0), result.get(0));
		result.clear();
		listener.searchNestedPredefineds(search.get(2), result, 0, 0);
		assertEquals(3, result.size());
		assertTrue(result.contains(search.get(0)));
		assertTrue(result.contains(search.get(1)));
		assertTrue(result.contains(search.get(7)));
		result.clear();
		listener.searchNestedPredefineds(search.get(3), result, 1, 1);
		assertEquals(0, result.size());
		listener.searchNestedPredefineds(search.get(4), result, 0, 0);
		assertEquals(1, result.size());
		result.clear();
		listener.searchNestedPredefineds(search.get(5), result, 0, 0);
		assertEquals(0, result.size());
		listener.searchNestedPredefineds(search.get(6), result, 0, 0);
		assertEquals(0, result.size());
		listener.searchNestedPredefineds(search.get(7), result, 1, 1);
		assertEquals(0, result.size());
	}

}
