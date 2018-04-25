package com.ni3.ag.navigator.server.dao.impl.mock;

import java.util.HashMap;
import java.util.Map;

import com.ni3.ag.navigator.server.dao.UserGroupDAO;

public class UserGroupDAOImpl implements UserGroupDAO{

	@Override
	public void save(int id, int grId){
	}

	@Override
	public Map<Integer, Integer> getUserGroups(){
		final Map<Integer, Integer> ret = new HashMap<Integer, Integer>(5);
		ret.put(1, 1);
		ret.put(2, 1);
		ret.put(3, 1);
		ret.put(4, 1);
		ret.put(5, 1);
		return ret;
	}

}
