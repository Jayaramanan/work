/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl;

import junit.framework.TestCase;

public class ObjectsConnectionsServiceImplTest extends TestCase{
	public void testContainsId(){
		ObjectsConnectionsServiceImpl service = new ObjectsConnectionsServiceImpl();
		assertFalse(service.containsId(12, null));
		assertFalse(service.containsId(12, ""));
		assertFalse(service.containsId(12, "123"));
		assertFalse(service.containsId(12, "123;1;2"));
		assertTrue(service.containsId(12, "12"));
		assertTrue(service.containsId(12, "1;12;3"));
		assertTrue(service.containsId(12, "1;2;12"));
	}
}
