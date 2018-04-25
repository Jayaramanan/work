/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.controller.useradmin;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.User;

public class UpdateUserActionListenerTest extends ACTestCase{

	User user;
	UpdateUserActionListener ls;

	@Override
	protected void setUp() throws Exception{
		user = new User();
		ls = new UpdateUserActionListener(null);
	}

	public void testCheckSIDNull(){
		user.setUserName("name");
		user.setSID(null);
		ls.checkSID(user);
		assertEquals("name4Ni3", user.getSID());
	}

	public void testCheckSIDDifferent(){
		user.setUserName("name");
		user.setSID("sid");
		ls.checkSID(user);
		assertEquals("name4Ni3", user.getSID());
	}
}
