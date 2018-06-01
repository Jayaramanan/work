/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class ObjectConnectionTest extends TestCase{
	ObjectConnection conn;

	@Override
	protected void setUp() throws Exception{
		conn = new ObjectConnection();
	}

	public void testEqualsSameIds(){
		conn.setId(1);
		ObjectConnection arg = new ObjectConnection();
		arg.setId(1);
		assertTrue(conn.equals(arg));
	}

	public void testEqualsDiffernetIds(){
		conn.setId(1);
		ObjectConnection arg = new ObjectConnection();
		arg.setId(2);
		assertFalse(conn.equals(arg));
	}

	public void testEqualsSameObjects(){
		ObjectConnection arg = conn;
		assertTrue(conn.equals(arg));
	}

	public void testEqualsNullArg(){
		conn.setId(1);
		assertFalse(conn.equals(null));
	}

	public void testEquals(){

	}
}
