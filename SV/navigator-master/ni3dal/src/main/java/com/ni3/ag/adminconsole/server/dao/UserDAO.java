/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.User;

public interface UserDAO{
	public User getUser(final String userName, final String password);

	public User getById(int id);

	public User saveOrUpdate(final User user);

	public void saveOrUpdateAll(List<User> users);

	public void deleteAll(List<User> users);

	public List<User> getUnassignedUsers();

	public Integer addUser(User user);

	public User getUser(final String userName);

	public User getUserByEmail(final String eMail);

	public List<User> getUsers();

	public void merge(User u);

	public void redirectSequence(String seqName, int start);

	public List<User> getActiveAdministrators();

	public List<User> getUsersByIds(Integer[] ids);

}
