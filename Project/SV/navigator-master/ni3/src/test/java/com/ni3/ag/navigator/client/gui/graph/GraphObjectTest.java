/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph;

import java.util.List;

import junit.framework.TestCase;

public class GraphObjectTest extends TestCase{

	public void testWrapLabelOneLine(){
		GraphObject obj = new GraphObject();
		List<String> result = obj.wrapLabel("12(34567)_890123", 5);
		assertEquals(1, result.size());
		assertEquals("12(34567)_890123", result.get(0));

		result = obj.wrapLabel("12(34567)_890123", 15);
		assertEquals(1, result.size());
		assertEquals("12(34567)_890123", result.get(0));

		result = obj.wrapLabel("12 3456 78", 10);
		assertEquals(1, result.size());
		assertEquals("12 3456 78", result.get(0));

		result = obj.wrapLabel("12  3456 ", 10);
		assertEquals(1, result.size());
		assertEquals("12  3456 ", result.get(0));
	}

	public void testWrapLabelTwoLines(){
		GraphObject obj = new GraphObject();
		List<String> result = obj.wrapLabel("123456 789 0123", 10);
		assertEquals(2, result.size());
		assertEquals("123456 789", result.get(0));
		assertEquals("0123", result.get(1));

		result = obj.wrapLabel("12  3456 ", 5);
		assertEquals(2, result.size());
		assertEquals("12", result.get(0));
		assertEquals("3456", result.get(1));

		result = obj.wrapLabel("12 3456 789 0123", 10);
		assertEquals(2, result.size());
		assertEquals("12 3456", result.get(0));
		assertEquals("789 0123", result.get(1));

		result = obj.wrapLabel("  12   3456  ", 5);
		assertEquals(2, result.size());
		assertEquals("12", result.get(0));
		assertEquals("3456", result.get(1));
	}

	public void testWrapLabelThreeLines(){
		GraphObject obj = new GraphObject();
		List<String> result = obj.wrapLabel("123 4567 890123", 5);
		assertEquals(3, result.size());
		assertEquals("123", result.get(0));
		assertEquals("4567", result.get(1));
		assertEquals("890123", result.get(2));

		result = obj.wrapLabel("123456 789 0123", 5);
		assertEquals(3, result.size());
		assertEquals("123456", result.get(0));
		assertEquals("789", result.get(1));
		assertEquals("0123", result.get(2));

	}
}
