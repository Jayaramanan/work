/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Color;
import java.awt.Font;

import com.ni3.ag.adminconsole.client.test.ACTestCase;

public class UtilityTest extends ACTestCase{

	public void testCreateColor(){
		Color c = Utility.createColor("#ff0000");
		assertEquals(Color.red, c);

		c = Utility.createColor("null");
		assertNull(c);

		c = Utility.createColor("R");
		assertTrue(c instanceof Color);
	}

	public void testCreateFont(){
		Font f = Utility.createFont("Dialog,0,10");
		// testing of font family is disabled because of different fonts available on windows/linux machinesF
		// assertEquals("Dialog", f.getFamily());
		assertEquals(10, f.getSize());
		assertEquals(Font.PLAIN, f.getStyle());

		f = Utility.createFont("Dialog,3,11");
		// assertEquals("Dialog", f.getFamily());
		assertEquals(11, f.getSize());
		assertEquals(Font.BOLD | Font.ITALIC, f.getStyle());
	}

	public void testProcessBooleanString(){
		assertTrue(Utility.processBooleanString("1"));
		assertFalse(Utility.processBooleanString("0"));
		assertFalse(Utility.processBooleanString("kfsdjgh"));
		assertTrue(Utility.processBooleanString("Yes"));
		assertTrue(Utility.processBooleanString("y"));
		assertTrue(Utility.processBooleanString("true"));
		assertTrue(Utility.processBooleanString("TRUE"));
		assertTrue(Utility.processBooleanString("t"));
	}

	public void testEncodeColor(){
		String c = Utility.encodeColor(null);
		assertEquals("null", c);
		c = Utility.encodeColor(Color.green);
		assertEquals("#00ff00", c);
	}
}
