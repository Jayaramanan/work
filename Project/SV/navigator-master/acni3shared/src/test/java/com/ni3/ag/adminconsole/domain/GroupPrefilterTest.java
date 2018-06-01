/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class GroupPrefilterTest extends TestCase{
	public void testEquals(){
		GroupPrefilter gp1 = new GroupPrefilter();
		GroupPrefilter gp2 = new GroupPrefilter();
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
