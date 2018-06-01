/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

/**
 * 
 * @author user
 */
public class ObjectDefinitionTest extends TestCase{

	ObjectDefinition od;

	@Override
	protected void setUp() throws Exception{
		od = new ObjectDefinition();
	}

	public void testCompareToWithNulls(){
		od.setName(null);
		assertEquals(0, od.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		od.setName("ObjectDefinition");
		assertTrue(od.compareTo(null) > 0);
	}

	public void testCompareToWithNullObjectDefinition(){
		od.setName(null);
		ObjectDefinition arg = new ObjectDefinition();
		arg.setName("ArgObjectDefinition");
		assertTrue(od.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgObjectDefinition(){
		od.setName("ObjectDefinition");
		ObjectDefinition arg = new ObjectDefinition();
		arg.setName(null);
		assertTrue(od.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		od.setName("ObjectDefinition");
		ObjectDefinition arg = new ObjectDefinition();
		arg.setName("ObjectDefinition");
		assertEquals(0, od.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		od.setName("AObjectDefinition");
		ObjectDefinition arg = new ObjectDefinition();
		arg.setName("BObjectDefinition");
		assertTrue(od.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		od.setName("BObjectDefinition");
		ObjectDefinition arg = new ObjectDefinition();
		arg.setName("AObjectDefinition");
		assertTrue(od.compareTo(arg) > 0);
	}

	public void testEqualsSameIds(){
		od.setId(1);
		ObjectDefinition arg = new ObjectDefinition();
		arg.setId(1);
		assertTrue(od.equals(arg));
	}

	public void testEqualsDiffernetIds(){
		od.setId(1);
		ObjectDefinition arg = new ObjectDefinition();
		arg.setId(2);
		assertFalse(od.equals(arg));
	}

	public void testEqualsSameObjects(){
		ObjectDefinition arg = od;
		assertTrue(od.equals(arg));
	}

	public void testEqualsNullArg(){
		od.setId(1);
		assertFalse(od.equals(null));
	}
}
