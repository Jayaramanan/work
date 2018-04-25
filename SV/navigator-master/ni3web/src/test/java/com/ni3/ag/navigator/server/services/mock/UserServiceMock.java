/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.mock;

import com.ni3.ag.navigator.shared.domain.User;
import java.util.List;

import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.services.UserService;

public class UserServiceMock implements UserService{

	private UserDAO userDAO;

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public List<User> getOfflineUsers(){
		return userDAO.getOfflineUsers();
	}

}
