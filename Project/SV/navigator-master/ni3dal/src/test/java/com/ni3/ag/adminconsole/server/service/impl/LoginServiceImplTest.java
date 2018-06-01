/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.validation.ACLoginException;

public class LoginServiceImplTest extends TestCase{
	LoginServiceImpl impl;

	@Override
	protected void setUp() throws Exception{
		impl = new LoginServiceImpl();
	}

	public void testLoginNullUserName(){
		try{
			impl.login(null, "");
			fail();
		} catch (ACLoginException e){
			// success
		}
	}

	public void testLoginEmptyUserName(){
		try{
			impl.login("", "");
			fail();
		} catch (ACLoginException e){
			// success
		}
	}

	public void testLoginReturnNullUser(){
		UserDAO dao = new UserDAO(){
			public User saveOrUpdate(User user){
				return null;
			}

			public User getUser(String userName, String password){
				return null;
			}

			public User getById(int id){
				return null;
			}

			public void saveOrUpdateAll(List<User> users){
			}

			public void deleteAll(List<User> users){
			}

			public List<User> getUnassignedUsers(){
				return null;
			}

			public Integer addUser(User user){
				return null;
			}

			public User getUser(String userName){
				return null;
			}

			@Override
			public List<User> getUsers(){
				return null;
			}

			@Override
			public void merge(User u){
			}

			@Override
			public User getUserByEmail(String eMail){
				return null;
			}

			@Override
			public void redirectSequence(String seqName, int start){
			}

			@Override
			public List<User> getActiveAdministrators(){
				return null;
			}

			@Override
			public List<User> getUsersByIds(Integer[] ids){
				return null;
			}
		};
		impl.setUserDAO(dao);
		try{
			impl.login("user", "pass");
			fail();
		} catch (ACLoginException e){
			// success
		}
	}

	public void testLoginSuccess(){
		UserDAO dao = new UserDAO(){
			public User saveOrUpdate(User user){
				return null;
			}

			public User getUser(String userName, String password){
				User u = new User();
				u.setUserName(userName);
				u.setPassword(password);
				u.setActive(true);
				Group g = new Group();
				g.setName(Group.ADMINISTRATORS_GROUP_NAME);
				u.setGroups(new ArrayList<Group>());
				u.getGroups().add(g);
				return u;
			}

			public User getById(int id){
				return null;
			}

			public void saveOrUpdateAll(List<User> users){
			}

			public void deleteAll(List<User> users){
			}

			public List<User> getUnassignedUsers(){
				return null;
			}

			public Integer addUser(User user){
				return null;
			}

			@Override
			public User getUser(String userName){
				return null;
			}

			@Override
			public List<User> getUsers(){
				return null;
			}

			@Override
			public void merge(User u){
			}

			@Override
			public User getUserByEmail(String eMail){
				return null;
			}

			@Override
			public void redirectSequence(String seqName, int start){
			}

			@Override
			public List<User> getActiveAdministrators(){
				return null;
			}

			@Override
			public List<User> getUsersByIds(Integer[] ids){
				return null;
			}
		};
		impl.setUserDAO(dao);
		try{
			impl.login("user", "pass");
		} catch (ACLoginException e){
			fail();
		}
	}
}
