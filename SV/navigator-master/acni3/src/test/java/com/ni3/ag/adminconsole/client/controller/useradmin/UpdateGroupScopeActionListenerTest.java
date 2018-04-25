/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class UpdateGroupScopeActionListenerTest extends ACTestCase{

	public void testCorrectStringValue(){
		UpdateGroupScopeActionListener ls = new UpdateGroupScopeActionListener(null);
		assertNull(ls.correctStringValue(null));
		assertNull(ls.correctStringValue(""));
		assertNull(ls.correctStringValue("   "));

		assertEquals("test", ls.correctStringValue("test"));
		assertEquals("test", ls.correctStringValue(" test  "));
	}
}
