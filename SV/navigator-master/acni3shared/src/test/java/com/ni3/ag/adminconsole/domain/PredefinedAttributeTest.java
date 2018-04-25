/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class PredefinedAttributeTest extends TestCase{
	PredefinedAttribute pattr;

	@Override
	protected void setUp() throws Exception{
		pattr = new PredefinedAttribute();
	}

	public void testClone() throws CloneNotSupportedException{
		PredefinedAttribute attr = new PredefinedAttribute();
		attr.setId(10);
		PredefinedAttribute pClonedAttr = (PredefinedAttribute) attr.clone();
		assertTrue(attr.equals(pClonedAttr));
		assertTrue(pClonedAttr.equals(attr));
	}

	public void testCompareToWithNulls(){
		pattr.setLabel(null);
		assertEquals(0, pattr.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		pattr.setLabel("PredefinedAttribute");
		assertTrue(pattr.compareTo(null) > 0);
	}

	public void testCompareToWithNullPredefinedAttribute(){
		pattr.setLabel(null);
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setLabel("ArgPredefinedAttribute");
		assertTrue(pattr.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgPredefinedAttribute(){
		pattr.setLabel("PredefinedAttribute");
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setLabel(null);
		assertTrue(pattr.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		pattr.setLabel("PredefinedAttribute");
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setLabel("PredefinedAttribute");
		assertEquals(0, pattr.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		pattr.setLabel("APredefinedAttribute");
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setLabel("BPredefinedAttribute");
		assertTrue(pattr.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		pattr.setLabel("BPredefinedAttribute");
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setLabel("APredefinedAttribute");
		assertTrue(pattr.compareTo(arg) > 0);
	}

	public void testEqualsSameIds(){
		pattr.setId(1);
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setId(1);
		assertTrue(pattr.equals(arg));
	}

	public void testEqualsDiffernetIds(){
		pattr.setId(1);
		PredefinedAttribute arg = new PredefinedAttribute();
		arg.setId(2);
		assertFalse(pattr.equals(arg));
	}

	public void testEqualsSameObjects(){
		PredefinedAttribute arg = pattr;
		assertTrue(pattr.equals(arg));
	}

	public void testEqualsNullArg(){
		pattr.setId(1);
		assertFalse(pattr.equals(null));
	}

	public void testEquals(){
		PredefinedAttribute ls1 = new PredefinedAttribute();
		PredefinedAttribute ls2 = new PredefinedAttribute();
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
