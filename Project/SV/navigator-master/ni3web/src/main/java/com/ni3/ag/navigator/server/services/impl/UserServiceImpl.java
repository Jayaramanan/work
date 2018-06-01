/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import com.ni3.ag.navigator.shared.domain.User;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.license.LicenseValidator;
import com.ni3.ag.navigator.server.services.UserService;

public class UserServiceImpl implements UserService{

	private UserDAO userDAO;

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public List<User> getOfflineUsers(){
		final List<User> offlineUsers = userDAO.getOfflineUsers();
		final List<User> result = new ArrayList<User>();

		final LicenseValidator validator = NSpringFactory.getInstance().getLicenseValidator();
		final List<LicenseData> licenseData = validator.getValidLicenses();
		for (User user : offlineUsers){
			if (validator.hasThickClientModule(user.getId(), licenseData)){
				result.add(user);
			}
		}
		return result;
	}

}
