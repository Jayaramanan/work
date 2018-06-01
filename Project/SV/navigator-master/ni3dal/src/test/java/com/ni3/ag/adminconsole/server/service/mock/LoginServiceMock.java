/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.remoting.UserSession;
import com.ni3.ag.adminconsole.shared.service.def.LoginService;
import com.ni3.ag.adminconsole.validation.ACException;

public class LoginServiceMock implements LoginService{

	@Override
	public String getSaltForUser(String login){
		return "";
	}

	public UserSession login(String arg0, String arg1){
		User u = new User();
		u.setId(1);
		u.setFirstName("USER");
		u.setLastName("user");
		u.setUserName("user");
		return new UserSession(u, "1");
	}

	@Override
	public void logout(User u) throws ACException{
		// TODO Auto-generated method stub

	}
}
