/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.UserDAO;

public class UserDAOMock implements UserDAO{

	private List<User> userList = new ArrayList<User>();

	public User getById(int id){
		for (User usr : userList){
			if (usr.getId().equals(new Integer(id)))
				return usr;
		}
		return null;
	}

	public User getUser(String userName, String password){
		for (User usr : userList){
			if (usr.getUserName().equals(userName) && usr.getPassword().equals(password))
				return usr;
		}
		return null;
	}

	private Integer getMaxId(){
		int max = -1;
		for (User user : userList)
			if (user.getId().intValue() > max)
				max = user.getId().intValue();
		return max + 1;
	}

	public User saveOrUpdate(User user){
		if (user.getId() == null){
			user.setId(getMaxId());
		}
		for (User usr : userList){
			if (usr.getId().equals(user.getId())){
				userList.remove(usr);
				userList.add(user);
				return user;
			}
		}
		userList.add(user);
		return user;
	}

	public void saveOrUpdateAll(List<User> users){
		for (User usr : users)
			saveOrUpdate(usr);
	}

	public void deleteAll(List<User> users){
		throw new UnsupportedOperationException();
	}

	public List<User> getUnassignedUsers(){
		return null;
	}

	public Integer addUser(User user){
		return null;
	}

	@Override
	public User getUser(String userName){
		for (User usr : userList){
			if (usr.getUserName().equals(userName))
				return usr;
		}
		return null;
	}

	@Override
	public List<User> getUsers(){
		return userList;
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
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> getActiveAdministrators(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getUsersByIds(Integer[] ids){
		return null;
	}
}
