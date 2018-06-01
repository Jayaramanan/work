/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.login;

import com.ni3.ag.navigator.shared.domain.User;

public class LoginResult{
	private LoginStatus loginStatus;
	private String sessionId;
	private User user;
	private int groupId;
	private String instance;

	public LoginResult(LoginStatus loginStatus, User user){
		super();
		this.loginStatus = loginStatus;
		this.user = user;
	}

	public LoginStatus getLoginStatus(){
		return loginStatus;
	}

	public void setLoginStatus(LoginStatus loginStatus){
		this.loginStatus = loginStatus;
	}

	public String getSessionId(){
		return sessionId;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user){
		this.user = user;
	}

	public int getGroupId(){
		return groupId;
	}

	public void setGroupId(int groupId){
		this.groupId = groupId;
	}

	public String getInstance(){
		return instance;
	}

	public void setInstance(String instance){
		this.instance = instance;
	}

	public boolean isOk(){
		return loginStatus == LoginStatus.Ok;
	}
}
