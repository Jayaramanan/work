/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class GroupSettingTest extends TestCase{
	public void testGroupSetting(){
		GroupSetting gs = new GroupSetting();
		assertNull(gs.getGroup());
		assertNull(gs.getProp());
		assertNull(gs.getSection());
		assertNull(gs.getValue());

		Group g = new Group();
		g.setId(1);
		gs.setGroup(g);
		gs.setProp("prop");
		gs.setSection("sect");
		gs.setValue("val");

		assertEquals(gs.getGroup(), g);
		assertEquals(gs.getSection(), "sect");
		assertEquals(gs.getProp(), "prop");
		assertEquals(gs.getValue(), "val");
	}

	public void testEquals(){
		GroupSetting gs1 = new GroupSetting();
		GroupSetting gs2 = new GroupSetting();
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		Group g = new Group();
		g.setId(1);
		gs1.setGroup(g);
		gs2.setGroup(g);
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs1.setSection("sec");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs2.setSection("sec");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs1.setProp("prop");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs2.setProp("prop");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs1.setValue("val1");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs2.setValue("val2");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertFalse(gs1.equals(gs2));
		assertFalse(gs2.equals(gs1));
		gs2.setValue("val1");
		assertTrue(gs1.equals(gs1));
		assertTrue(gs2.equals(gs2));
		assertTrue(gs1.equals(gs2));
		assertTrue(gs2.equals(gs1));
	}
}
