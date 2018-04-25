/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class ApplicationSettingTest extends TestCase{
	public void testApplicationSetting(){
		ApplicationSetting as = new ApplicationSetting();
		assertNull(as.getProp());
		assertNull(as.getSection());
		assertNull(as.getValue());

		as.setProp("prop");
		as.setSection("sect");
		as.setValue("val");

		assertEquals(as.getProp(), "prop");
		assertEquals(as.getValue(), "val");
		assertEquals(as.getSection(), "sect");
	}

	public void testEquals(){
		ApplicationSetting as1 = new ApplicationSetting();
		ApplicationSetting as2 = new ApplicationSetting();
		assertTrue(as1.equals(as2));
		assertTrue(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as1.setSection("sec");
		assertFalse(as1.equals(as2));
		assertFalse(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as2.setSection("sec");
		assertTrue(as1.equals(as2));
		assertTrue(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as1.setProp("prop");
		assertFalse(as1.equals(as2));
		assertFalse(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as2.setProp("prop");
		assertTrue(as1.equals(as2));
		assertTrue(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as1.setValue("val1");
		assertFalse(as1.equals(as2));
		assertFalse(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as2.setValue("val2");
		assertFalse(as1.equals(as2));
		assertFalse(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
		as2.setValue("val1");
		assertTrue(as1.equals(as2));
		assertTrue(as2.equals(as1));
		assertTrue(as1.equals(as1));
		assertTrue(as2.equals(as2));
	}
}
