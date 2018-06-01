/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import junit.framework.TestCase;

public class UserSettingTest extends TestCase{

	public void testEqualsObject(){
		UserSetting us1 = new UserSetting();
		UserSetting us2 = new UserSetting();
		User u = new User();
		u.setId(1);
		us1.setUser(u);
		us2.setUser(u);
		assertTrue(us1.equals(us1));
		assertTrue(us2.equals(us2));
		assertFalse(us1.equals(us2));
		assertFalse(us2.equals(us1));
		us1.setSection("sec");
		assertFalse(us1.equals(us2));
		assertFalse(us2.equals(us1));
		us2.setSection("sec");
		assertFalse(us1.equals(us2));
		assertFalse(us2.equals(us1));
		us1.setProp("prop");
		assertFalse(us1.equals(us2));
		assertFalse(us2.equals(us1));
		us2.setProp("prop");
		assertTrue(us1.equals(us2));
		assertTrue(us2.equals(us1));
	}

}
