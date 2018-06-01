/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class ChartGroupTest extends TestCase{

	public void testEquals(){
		ChartGroup gp1 = new ChartGroup();
		ChartGroup gp2 = new ChartGroup();
		assertTrue(gp1.equals(gp1));
		assertTrue(gp2.equals(gp2));

		gp1.setId(1);
		gp2.setId(2);
		assertFalse(gp1.equals(gp2));
		assertFalse(gp2.equals(gp1));

		gp2.setId(1);
		assertTrue(gp1.equals(gp2));
		assertTrue(gp2.equals(gp1));
	}

}
