/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

public class IntParserTest extends TestCase{
	public void testGetInt(){
		assertNull(IntParser.getInt(null));
		assertEquals(Integer.MAX_VALUE, IntParser.getInt(null, Integer.MAX_VALUE).intValue());
		assertNull(IntParser.getInt("", null));
		assertEquals(new Integer(12), IntParser.getInt(new Integer(12)));
		assertEquals(new Integer(12), IntParser.getInt(new BigDecimal(12)));
		assertEquals(new Integer(12), IntParser.getInt(new BigInteger("12", 10)));
		assertEquals(new Integer(12), IntParser.getInt(new Long(12)));
		assertEquals(new Integer(12), IntParser.getInt(new Short("12")));
		assertEquals(new Integer(12), IntParser.getInt(new Double(12.0)));
		assertEquals(new Integer(12), IntParser.getInt(new Float(12.0)));
	}

}
