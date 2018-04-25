/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.remoting;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Mapping between DBIDs and User sessions
 * 
 */
public class UserSessionStore implements Serializable{

	private static final long serialVersionUID = 4766155636889798405L;

	private static UserSessionStore instance;

	private HashMap<String, HashMap<Integer, String>> userSessionMap;

	private UserSessionStore(){
		userSessionMap = new HashMap<String, HashMap<Integer, String>>();
	}

	public synchronized static UserSessionStore getInstance(){
		if (instance == null)
			instance = new UserSessionStore();
		return instance;
	}

	private HashMap<Integer, String> getUserMap(String dbid){
		HashMap<Integer, String> userMap = userSessionMap.get(dbid);
		if (userMap == null)
			userMap = new HashMap<Integer, String>();
		return userMap;
	}

	public void put(String dbid, Integer userId, String sessionId){
		HashMap<Integer, String> userMap = getUserMap(dbid);
		userMap.put(userId, sessionId);
		userSessionMap.put(dbid, userMap);
	}

	public String getSessionId(Integer userId, String dbid){
		HashMap<Integer, String> userMap = getUserMap(dbid);
		return userMap.get(userId);
	}

	public void remove(Integer userId, String dbid){
		HashMap<Integer, String> userMap = getUserMap(dbid);
		userMap.remove(userId);
		userSessionMap.put(dbid, userMap);
	}
}