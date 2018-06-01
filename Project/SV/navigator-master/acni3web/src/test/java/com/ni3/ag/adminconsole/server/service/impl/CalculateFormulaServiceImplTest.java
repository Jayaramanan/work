/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl;

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;

public class CalculateFormulaServiceImplTest extends TestCase{
	private CalculateFormulaServiceImpl service;
	private ObjectAttribute attribute;

	@Override
	protected void setUp() throws Exception{
		service = new CalculateFormulaServiceImpl();
		attribute = new ObjectAttribute();
		attribute.setDataType(DataType.TEXT);
	}

	public void testFormatValueText(){
		assertNull(service.formatValue(null, attribute));
		assertEquals("", service.formatValue("", attribute));
		assertEquals("abc", service.formatValue("abc", attribute));
	}

	public void testFormatValueInt(){
		attribute.setDataType(DataType.INT);
		assertNull(service.formatValue(null, attribute));
		assertNull(service.formatValue("", attribute));
		assertNull(service.formatValue("abc", attribute));
		assertEquals(new Integer(12), service.formatValue(new Integer(12), attribute));
		assertEquals(new Integer(12), service.formatValue(new Short("12"), attribute));
		assertEquals(new Integer(12), service.formatValue(new BigDecimal(12.0), attribute));
		assertEquals(new Integer(12), service.formatValue(new Integer(12), attribute));
	}

	public void testFormatValueDecimal(){
		attribute.setDataType(DataType.DECIMAL);
		assertNull(service.formatValue(null, attribute));
		assertNull(service.formatValue("", attribute));
		assertNull(service.formatValue("abc", attribute));
		assertEquals(new Double(12.0), service.formatValue(new Integer(12), attribute));
		assertEquals(new Double(12.0), service.formatValue(new Short("12"), attribute));
		assertEquals(new Double(12.0), service.formatValue(new BigDecimal(12.0), attribute));
		assertEquals(new Double(12.0), service.formatValue(new Integer(12), attribute));
	}
}
