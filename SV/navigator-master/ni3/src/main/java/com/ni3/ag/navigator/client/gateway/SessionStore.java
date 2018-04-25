package com.ni3.ag.navigator.client.gateway;

public class SessionStore{

	private static final long serialVersionUID = 4766155636889798405L;
	private static final String JSESSIONID_PREFIX = "JSESSIONID=";

	private static SessionStore instance;
	private String sessionId;

	private SessionStore(){
	}

	public synchronized static SessionStore getInstance(){
		if (instance == null)
			instance = new SessionStore();
		return instance;
	}

	public String getSessionId(){
		return sessionId;
	}

	public String getSessionString(){
		String result = null;
		if (sessionId != null){
			result = JSESSIONID_PREFIX + sessionId;
		}
		return result;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public void clearSession(){
		sessionId = null;
	}
}
