/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.remoting;

import org.springframework.context.annotation.Scope;

@Scope("session")
public class SessionIdHolder{

	private static SessionIdHolder instance;

	private String sessionId;

	private SessionIdHolder(){

	}

	public static synchronized SessionIdHolder getInstance(){
		if (instance == null)
			instance = new SessionIdHolder();
		return instance;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public String getSessionId(){
		return sessionId;
	}
}
