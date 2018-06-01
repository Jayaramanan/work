/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class ChartTest extends TestCase{
	public void testEquals(){
		Chart c1 = new Chart();
		Chart c2 = new Chart();
		assertFalse(c1.equals(c2));
		assertFalse(c2.equals(c1));
		c1.setId(1);
		assertFalse(c1.equals(c2));
		assertFalse(c2.equals(c1));
		c2.setId(2);
		assertFalse(c1.equals(c2));
		assertFalse(c2.equals(c1));
		c2.setId(1);
		assertTrue(c1.equals(c2));
		assertTrue(c2.equals(c1));
	}

}
