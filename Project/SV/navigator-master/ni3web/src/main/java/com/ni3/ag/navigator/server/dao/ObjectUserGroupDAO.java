package com.ni3.ag.navigator.server.dao;

import java.util.List;
import java.util.Map;

public interface ObjectUserGroupDAO{

	/**
	 * 
	 * @return Map of GroupID, List of ObjectTypeID
	 */
	Map<Integer, List<Integer>> getReadACL();
}
