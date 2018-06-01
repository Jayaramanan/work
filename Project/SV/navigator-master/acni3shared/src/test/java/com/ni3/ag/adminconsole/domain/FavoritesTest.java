/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class FavoritesTest extends TestCase{
	public void testEquals(){
		Favorites f1 = new Favorites();
		Favorites f2 = new Favorites();
		Favorites f3 = new Favorites();
		assertTrue(f1.equals(f1));
		assertTrue(f2.equals(f2));
		assertTrue(f3.equals(f3));

		assertFalse(f1.equals(f2));
		assertFalse(f2.equals(f1));
		assertFalse(f1.equals(f3));
		assertFalse(f3.equals(f1));

		f1.setId(1);
		f2.setId(1);
		f3.setId(2);
		assertTrue(f1.equals(f2));
		assertTrue(f2.equals(f1));
		assertFalse(f1.equals(f3));
		assertFalse(f3.equals(f1));
		assertFalse(f2.equals(f3));
		assertFalse(f3.equals(f2));
	}
}
