package com.ni3.ag.navigator.server.dao;

import java.util.Map;

public interface UserGroupDAO{

	void save(int id, int grId);

	Map<Integer, Integer> getUserGroups();

}
