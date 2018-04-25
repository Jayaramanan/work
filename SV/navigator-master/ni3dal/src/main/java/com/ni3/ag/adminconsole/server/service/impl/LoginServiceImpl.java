/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.remoting.SessionIdHolder;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.remoting.TransferConstants;
import com.ni3.ag.adminconsole.remoting.UserSession;
import com.ni3.ag.adminconsole.remoting.UserSessionStore;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.service.def.LoginService;
import com.ni3.ag.adminconsole.validation.ACLoginException;

public class LoginServiceImpl implements LoginService{
	private static final Logger log = Logger.getLogger(LoginServiceImpl.class);
	private static final int BCRYPT_PASSWORD_LEN = 60;
	private static final int BCRYPT_SALT_LEN = 29;

	private UserDAO userDAO;

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public String getSaltForUser(String login){
		log.debug("Request salt for user: " + login);
		if(login == null || "".equals(login))
			return "";
		User user = userDAO.getUser(login);
		if(user == null)
			return "";
		String pass = user.getPassword();
		if(pass.length() != BCRYPT_PASSWORD_LEN){
			log.warn("Requesting salt for password len: " + pass.length() +
							 "  expected bcrypt pass len: " + BCRYPT_PASSWORD_LEN);
			return "";
		}
		return pass.substring(0, BCRYPT_SALT_LEN);
	}

	public UserSession login(String userName, String password) throws ACLoginException{

		// TODO: Remove this line. This is a reference implementation of ThreadLocalStorage.
		log.info("ThreadLocal " + TransferConstants.DB_INSTANCE_ID + " = "
		        + ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId());

		if (userName == null || userName.length() == 0){
			throw new ACLoginException("User name is empty");
		}

		User user = userDAO.getUser(userName, password);
		if (user == null || !user.getActive()){
			throw new ACLoginException("No user found for such login/password pair");
		}
		validateUserGroup(user);

		UserSessionStore store = UserSessionStore.getInstance();
		String sesId = SessionIdHolder.getInstance().getSessionId();
		String dbId = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();

		log.info("logon successful");
		log.info("   session id: " + sesId);
		log.info("   user id: " + user.getId());

		store.put(dbId, user.getId(), sesId);
		UserSession us = new UserSession(user, sesId);
		return us;
	}

	private void validateUserGroup(User user) throws ACLoginException{
		ACLoginException ex = new ACLoginException("User is not member of `administrators` group");
		if (user.getGroups() == null)
			throw ex;
		if (user.getGroups().isEmpty())
			throw ex;
		Group g = user.getGroups().get(0);
		if (!Group.ADMINISTRATORS_GROUP_NAME.equals(g.getName()))
			throw ex;
	}

	@Override
	public void logout(User user){
		UserSessionStore store = UserSessionStore.getInstance();
		String dbId = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		store.remove(user.getId(), dbId);
	}
}
