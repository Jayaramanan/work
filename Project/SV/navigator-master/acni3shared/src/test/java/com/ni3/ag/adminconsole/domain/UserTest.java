/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.util.ArrayList;

import junit.framework.TestCase;

public class UserTest extends TestCase{
	User user;

	@Override
	protected void setUp() throws Exception{
		user = new User();
	}

	public void testUserSettings(){
		User u = new User();
		assertNull(u.getSettings());
		u.setSettings(new ArrayList<UserSetting>());
		assertNotNull(u.getSettings());
		u.getSettings().add(new UserSetting());
		assertEquals(u.getSettings().size(), 1);
	}

	public void testCompareToWithNulls(){
		user.setUserName(null);
		assertEquals(0, user.compareTo(null));
	}

	public void testCompareToWithNullArg(){
		user.setUserName("User");
		assertTrue(user.compareTo(null) > 0);
	}

	public void testCompareToWithNullUser(){
		user.setUserName(null);
		User arg = new User();
		arg.setUserName("ArgUser");
		assertTrue(user.compareTo(arg) < 0);
	}

	public void testCompareToWithNullArgUser(){
		user.setUserName("User");
		User arg = new User();
		arg.setUserName(null);
		assertTrue(user.compareTo(arg) > 0);
	}

	public void testCompareToEquals(){
		user.setUserName("User");
		User arg = new User();
		arg.setUserName("User");
		assertEquals(0, user.compareTo(arg));
	}

	public void testCompareToArgIsLess(){
		user.setUserName("AUser");
		User arg = new User();
		arg.setUserName("BUser");
		assertTrue(user.compareTo(arg) < 0);
	}

	public void testCompareToArgIsGreater(){
		user.setUserName("BUser");
		User arg = new User();
		arg.setUserName("AUser");
		assertTrue(user.compareTo(arg) > 0);
	}

	public void testEquals(){
		User ls1 = new User();
		User ls2 = new User();
		assertTrue(ls1.equals(ls1));
		assertTrue(ls2.equals(ls2));
		assertFalse(ls1.equals(ls2));
		assertFalse(ls2.equals(ls1));
		ls1.setId(1);
		assertFalse(ls1.equals(ls2));
		assertFalse(ls2.equals(ls1));
		ls2.setId(2);
		assertFalse(ls1.equals(ls2));
		assertFalse(ls2.equals(ls1));
		ls2.setId(1);
		assertTrue(ls1.equals(ls2));
		assertTrue(ls2.equals(ls1));
	}
}
