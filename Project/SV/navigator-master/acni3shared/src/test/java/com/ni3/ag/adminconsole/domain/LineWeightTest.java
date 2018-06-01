/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class LineWeightTest extends TestCase{
	LineWeight lineWeight;

	@Override
	protected void setUp() throws Exception{
		lineWeight = new LineWeight();
	}

	public void testEquals(){
		LineWeight ls1 = new LineWeight();
		LineWeight ls2 = new LineWeight();
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

	public void testCompareToWithNulls(){
		lineWeight.setLabel(null);
		assertEquals(0, lineWeight.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		lineWeight.setLabel("LineWeight");
		assertTrue(lineWeight.compareTo(null) > 0);
	}

	public void testCompareToWithNullLineWeight(){
		lineWeight.setLabel(null);
		LineWeight arg = new LineWeight();
		arg.setLabel("ArgLineWeight");
		assertTrue(lineWeight.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgLineWeight(){
		lineWeight.setLabel("LineWeight");
		LineWeight arg = new LineWeight();
		arg.setLabel(null);
		assertTrue(lineWeight.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		lineWeight.setLabel("LineWeight");
		LineWeight arg = new LineWeight();
		arg.setLabel("LineWeight");
		assertEquals(0, lineWeight.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		lineWeight.setLabel("ALineWeight");
		LineWeight arg = new LineWeight();
		arg.setLabel("BLineWeight");
		assertTrue(lineWeight.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		lineWeight.setLabel("BLineWeight");
		LineWeight arg = new LineWeight();
		arg.setLabel("ALineWeight");
		assertTrue(lineWeight.compareTo(arg) > 0);
	}
}
