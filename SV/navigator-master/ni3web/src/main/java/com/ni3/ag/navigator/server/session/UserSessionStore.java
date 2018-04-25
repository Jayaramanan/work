/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UserSessionStore implements Serializable{

	private static final long serialVersionUID = 4766155636889798405L;

	private static UserSessionStore instance;

	private HashMap<Integer, String> userSessionMap;
	private Set<Integer> invalidSchemaUsers;

	private UserSessionStore(){
		userSessionMap = new HashMap<Integer, String>();
		invalidSchemaUsers = new HashSet<Integer>();
	}

	public synchronized static UserSessionStore getInstance(){
		if (instance == null)
			instance = new UserSessionStore();
		return instance;
	}

	public void put(Integer userId, String sessionId){
		userSessionMap.put(userId, sessionId);
		resetInvalidationNeeded(userId);
	}

	public String getSessionId(Integer userId){
		return userSessionMap.get(userId);
	}

	public void remove(Integer userId){
		userSessionMap.remove(userId);
		resetInvalidationNeeded(userId);
	}

	public void invalidateAllUsers(){
		invalidSchemaUsers.clear();
		invalidSchemaUsers.addAll(userSessionMap.keySet());
	}

	public Integer getUserId(String sessionId){
		Integer result = -1;
		for (Integer userId : userSessionMap.keySet()){
			String sId = userSessionMap.get(userId);
			if (sessionId != null && sessionId.equals(sId)){
				result = userId;
				break;
			}
		}
		return result;
	}

	public boolean needsInvalidation(Integer userId){
		return invalidSchemaUsers.contains(userId);
	}

	public void resetInvalidationNeeded(Integer userId){
		invalidSchemaUsers.remove(userId);
	}
}