/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.remoting;

import java.io.Serializable;

import com.ni3.ag.adminconsole.domain.User;

public class UserSession implements Serializable{

	private static final long serialVersionUID = -2616776956928930789L;

	private User user;
	private String sessionId;

	protected UserSession(Integer userId, String sessionId){
		User user = new User();
		user.setId(userId);
		this.user = user;
		this.sessionId = sessionId;
	}

	public UserSession(User user, String sessionId){
		this.user = user;
		this.sessionId = sessionId;
	}

	public User getUser(){
		return user;
	}

	public String getSessionId(){
		return sessionId;
	}

}
