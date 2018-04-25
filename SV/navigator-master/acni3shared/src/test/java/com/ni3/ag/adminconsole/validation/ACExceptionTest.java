/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import com.ni3.ag.adminconsole.shared.language.TextID;

import junit.framework.TestCase;

public class ACExceptionTest extends TestCase{
	public void testAddError(){
		ACException ex = new ACException(TextID.About);
		assertEquals(1, ex.getErrors().size());
	}

}
