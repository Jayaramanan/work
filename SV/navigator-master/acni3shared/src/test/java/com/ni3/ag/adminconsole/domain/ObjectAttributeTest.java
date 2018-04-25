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
public class ObjectAttributeTest extends TestCase{
	public void testObjectAttributeClone() throws CloneNotSupportedException{
		ObjectAttribute att = new ObjectAttribute(new ObjectDefinition());
		ObjectAttribute clonedAtt = att.clone();
		TestCase.assertNotSame(att, clonedAtt);
	}

	ObjectAttribute attr;

	@Override
	protected void setUp() throws Exception{
		attr = new ObjectAttribute(new ObjectDefinition());
	}

	public void testCompareToWithNulls(){
		attr.setLabel(null);
		assertEquals(0, attr.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		attr.setLabel("ObjectAttribute");
		assertTrue(attr.compareTo(null) > 0);
	}

	public void testCompareToWithNullObjectAttribute(){
		attr.setLabel(null);
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setLabel("ArgObjectAttribute");
		assertTrue(attr.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgObjectAttribute(){
		attr.setLabel("ObjectAttribute");
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setLabel(null);
		assertTrue(attr.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		attr.setLabel("ObjectAttribute");
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setLabel("ObjectAttribute");
		assertEquals(0, attr.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		attr.setLabel("AObjectAttribute");
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setLabel("BObjectAttribute");
		assertTrue(attr.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		attr.setLabel("BObjectAttribute");
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setLabel("AObjectAttribute");
		assertTrue(attr.compareTo(arg) > 0);
	}

	public void testEqualsSameIds(){
		attr.setId(1);
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setId(1);
		assertTrue(attr.equals(arg));
	}

	public void testEqualsDiffernetIds(){
		attr.setId(1);
		ObjectAttribute arg = new ObjectAttribute(new ObjectDefinition());
		arg.setId(2);
		assertFalse(attr.equals(arg));
	}

	public void testEqualsSameObjects(){
		ObjectAttribute arg = attr;
		assertTrue(attr.equals(arg));
	}

	public void testEqualsNullArg(){
		attr.setId(1);
		assertFalse(attr.equals(null));
	}

	public void testGetDatabaseDataTypeText(){
		assertEquals(DataType.TEXT, attr.getDatabaseDataType());

		attr.setDataType(DataType.TEXT);
		assertEquals(DataType.TEXT, attr.getDatabaseDataType());

		attr.setDataType(DataType.DATE);
		assertEquals(DataType.TEXT, attr.getDatabaseDataType());

		attr.setDataType(DataType.URL);
		assertEquals(DataType.TEXT, attr.getDatabaseDataType());

		attr.setDataType(DataType.INT);
		attr.setIsMultivalue(true);
		assertEquals(DataType.TEXT, attr.getDatabaseDataType());

		attr.setPredefined(true);
		assertEquals(DataType.TEXT, attr.getDatabaseDataType());
	}

	public void testGetDatabaseDataTypeInteger(){
		attr.setDataType(DataType.INT);
		assertEquals(DataType.INT, attr.getDatabaseDataType());

		attr.setDataType(DataType.BOOL);
		assertEquals(DataType.INT, attr.getDatabaseDataType());

		attr.setDataType(DataType.TEXT);
		attr.setPredefined(true);
		assertEquals(DataType.INT, attr.getDatabaseDataType());
	}

	public void testGetDatabaseDataTypeNumeric(){
		attr.setDataType(DataType.DECIMAL);
		assertEquals(DataType.DECIMAL, attr.getDatabaseDataType());
	}
}
