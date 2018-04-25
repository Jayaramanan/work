/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class GroupScopeTest extends TestCase{

	public void testEquals(){
		GroupScope scope1 = new GroupScope();
		GroupScope scope2 = new GroupScope();

		assertTrue(scope1.equals(scope1));
		assertTrue(scope2.equals(scope2));

		assertFalse(scope1.equals(scope2));
		assertFalse(scope2.equals(scope1));

		Group gr = new Group();
		gr.setId(1);
		scope1.setGroup(gr.getId());
		assertFalse(scope1.equals(scope2));
		assertFalse(scope2.equals(scope1));

		scope2.setGroup(gr.getId());
		assertTrue(scope1.equals(scope1));
		assertTrue(scope2.equals(scope2));
	}
}
