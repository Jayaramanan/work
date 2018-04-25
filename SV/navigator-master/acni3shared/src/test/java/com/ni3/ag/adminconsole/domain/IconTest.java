/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class IconTest extends TestCase{
	Icon icon;

	@Override
	protected void setUp() throws Exception{
		icon = new Icon();
	}

	public void testEquals(){
		Icon g1 = new Icon();
		Icon g2 = new Icon();
		assertTrue(g1.equals(g1));
		assertTrue(g2.equals(g2));
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
		g1.setId(1);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
		g2.setId(2);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
		g2.setId(1);
		assertTrue(g1.equals(g2));
		assertTrue(g2.equals(g1));
	}

	public void testCompareToWithNulls(){
		icon.setIconName(null);
		assertEquals(0, icon.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		icon.setIconName("Icon");
		assertTrue(icon.compareTo(null) > 0);
	}

	public void testCompareToWithNullIcon(){
		icon.setIconName(null);
		Icon arg = new Icon();
		arg.setIconName("ArgIcon");
		assertTrue(icon.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgIcon(){
		icon.setIconName("Icon");
		Icon arg = new Icon();
		arg.setIconName(null);
		assertTrue(icon.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		icon.setIconName("Icon");
		Icon arg = new Icon();
		arg.setIconName("Icon");
		assertEquals(0, icon.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		icon.setIconName("AIcon");
		Icon arg = new Icon();
		arg.setIconName("BIcon");
		assertTrue(icon.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		icon.setIconName("BIcon");
		Icon arg = new Icon();
		arg.setIconName("AIcon");
		assertTrue(icon.compareTo(arg) > 0);
	}
}
