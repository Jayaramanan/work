/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Color;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class RgbColorConverterTest extends ACTestCase{
	public void testGetColor(){
		RgbColorConverter converter = new RgbColorConverter();
		assertNull(converter.getColor(null));
		assertNull(converter.getColor(""));
		assertNull(converter.getColor("123,123"));
		assertNull(converter.getColor("22,22,22,22"));
		assertNull(converter.getColor("#22222"));

		assertNotNull(converter.getColor("0,0,0"));
		assertNotNull(converter.getColor("22,22,22"));
		assertNotNull(converter.getColor(" 22, 2, 220 "));
		assertNotNull(converter.getColor("250,255,255"));
	}

	public void testGetColorString(){
		RgbColorConverter converter = new RgbColorConverter();
		assertEquals("0,0,0", converter.getColorString(new Color(0, 0, 0)));
		assertEquals("200,100,50", converter.getColorString(new Color(200, 100, 50)));
	}
}
