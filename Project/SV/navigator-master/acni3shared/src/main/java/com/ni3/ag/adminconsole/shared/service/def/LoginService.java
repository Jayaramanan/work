/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.remoting.UserSession;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACLoginException;

public interface LoginService{
	String getSaltForUser(String login);

	UserSession login(String name, String password) throws ACLoginException;

	void logout(User u) throws ACException;
}