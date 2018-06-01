/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import junit.framework.TestCase;

public class MD5PasswordEncoderTest extends TestCase{
	public void testEncode(){
		MD5PasswordEncoder encoder = new MD5PasswordEncoder();
		String s = encoder.generate("hello");
		assertEquals("5d41402abc4b2a76b9719d911017c592", s);

		assertEquals("5d41402abc4b2a76b9719d911017c592", encoder.encode("", "hello"));
	}
}
