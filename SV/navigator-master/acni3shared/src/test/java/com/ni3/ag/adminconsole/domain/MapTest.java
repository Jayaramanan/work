/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class MapTest extends TestCase{

	public void testEquals(){
		Map ls1 = new Map();
		Map ls2 = new Map();
		assertTrue(ls1.equals(ls1));
		assertTrue(ls2.equals(ls2));
		assertFalse(ls1.equals(ls2));
		assertFalse(ls2.equals(ls1));
		ls1.setId(1);
		assertFalse(ls1.equals(ls2));
		assertFalse(ls2.equals(ls1));
		ls2.setId(2);
		assertFalse(ls1.equals(ls2));
		assertFalse(ls2.equals(ls1));
		ls2.setId(1);
		assertTrue(ls1.equals(ls2));
		assertTrue(ls2.equals(ls1));
	}
}
