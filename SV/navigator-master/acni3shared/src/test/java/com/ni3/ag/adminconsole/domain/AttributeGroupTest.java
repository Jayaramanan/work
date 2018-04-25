/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class AttributeGroupTest extends TestCase{
	public void testEquals(){
		AttributeGroup ag1 = new AttributeGroup();
		AttributeGroup ag2 = new AttributeGroup();
		assertTrue(ag1.equals(ag2));
		assertTrue(ag2.equals(ag1));
		assertTrue(ag1.equals(ag1));
		assertTrue(ag2.equals(ag2));
		Group g = new Group();
		g.setId(1);
		ag1.setGroup(g);
		assertFalse(ag1.equals(ag2));
		assertFalse(ag2.equals(ag1));
		assertTrue(ag1.equals(ag1));
		assertTrue(ag2.equals(ag2));
		ag2.setGroup(g);
		assertTrue(ag1.equals(ag2));
		assertTrue(ag2.equals(ag1));
		assertTrue(ag1.equals(ag1));
		assertTrue(ag2.equals(ag2));
		ObjectAttribute oa = new ObjectAttribute(new ObjectDefinition());
		ag1.setObjectAttribute(oa);
		assertFalse(ag1.equals(ag2));
		assertFalse(ag2.equals(ag1));
		assertTrue(ag1.equals(ag1));
		assertTrue(ag2.equals(ag2));
		ag2.setObjectAttribute(oa);
		assertTrue(ag1.equals(ag2));
		assertTrue(ag2.equals(ag1));
		assertTrue(ag1.equals(ag1));
		assertTrue(ag2.equals(ag2));
	}

}
