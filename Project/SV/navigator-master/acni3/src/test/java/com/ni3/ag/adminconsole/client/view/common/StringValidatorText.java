/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import junit.framework.TestCase;

public class StringValidatorText extends TestCase{

	public void testValidate(){
		assertNull(StringValidator.validate(null));
		assertNull(StringValidator.validate(""));
		assertNull(StringValidator.validate("   "));

		assertEquals("test", StringValidator.validate("test"));
		assertEquals("test", StringValidator.validate(" test  "));
	}
}
