package com.ni3.ag.navigator.server.dao;

public interface UserActivityDAO{

	void save(Integer userId, String activityType, String message, String remoteAddr, String httpHeader);

}
