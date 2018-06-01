package com.ni3.ag.navigator.server.cache.impl;

import java.util.Map;

import com.ni3.ag.navigator.server.cache.UserGroupCache;
import com.ni3.ag.navigator.server.dao.UserGroupDAO;

public class UserGroupCacheImpl implements UserGroupCache{

	private UserGroupDAO userGroupDAO;

	private Map<Integer, Integer> userGroups;

	public void setUserGroupDAO(UserGroupDAO userGroupDAO){
		this.userGroupDAO = userGroupDAO;
	}

	private UserGroupCacheImpl(){
	}

	@Override
	public Integer getGroup(Integer userId){
		if (userGroups == null){
			initUserGroups();
		}
		return userGroups.get(userId);
	}

	@Override
	public void reload(){
		initUserGroups();
	}

	private void initUserGroups(){
		userGroups = userGroupDAO.getUserGroups();
	}
}