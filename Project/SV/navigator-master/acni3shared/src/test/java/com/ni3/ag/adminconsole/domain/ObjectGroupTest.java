/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class ObjectGroupTest extends TestCase{

	public void testEquals(){
		ObjectGroup oug1 = new ObjectGroup();
		ObjectGroup oug2 = new ObjectGroup();
		assertTrue(oug1.equals(oug1));
		assertTrue(oug2.equals(oug2));
		assertFalse(oug1.equals(oug2));
		assertFalse(oug2.equals(oug1));
		Group g = new Group();
		g.setId(1);
		ObjectDefinition od = new ObjectDefinition();
		od.setId(2);
		oug1.setObject(od);
		assertFalse(oug1.equals(oug2));
		assertFalse(oug2.equals(oug1));
		oug2.setObject(od);
		assertFalse(oug1.equals(oug2));
		assertFalse(oug2.equals(oug1));
		oug1.setGroup(g);
		assertFalse(oug1.equals(oug2));
		assertFalse(oug2.equals(oug1));
		oug2.setGroup(g);
		assertTrue(oug1.equals(oug2));
		assertTrue(oug2.equals(oug1));
	}
}
