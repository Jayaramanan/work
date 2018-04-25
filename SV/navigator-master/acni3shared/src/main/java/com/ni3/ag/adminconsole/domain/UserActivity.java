/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Date;

public class UserActivity implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final String SYNC_IDENTIFIER = "sync=1";
	public static final String USER_ID_MINUS_ONE = "userIdMinusOne";

	public static final String DATE_TIME_LESS_FILTER = "dateTimeLessFilter";
	public static final String DATE_TIME_GREATER_FILTER = "dateTimeGreaterFilter";
	public static final String REMOVE_NOT_LOG_ACTIVITIES_FILTER = "removeNotLogs";

	public static final String DATE_TIME_PROPERTY = "dateTime";
	public static final String USER_PROPERTY = "user";
	public static final String REQUEST = "request";
	public static final String DATE_TIME = "dateTime";
	public static final String ACTIVITY_TYPE_PROPERTY = "activityType";

	public static final String UNPROCESSED_ACTIVITY = "-";
	public static final String NOT_A_LOG_ACTIVITY = "N";

	private Integer id;
	private User user;

	private String httpHeader;
	private String request;
	private String ipAddress;
	private Date dateTime;
	private String activityType;
	private Long sessionDuration;
	private Boolean isSync;

	public UserActivity(){
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user){
		this.user = user;
	}

	public String getHttpHeader(){
		return httpHeader;
	}

	public void setHttpHeader(String httpHeader){
		this.httpHeader = httpHeader;
	}

	public String getRequest(){
		return request;
	}

	public void setRequest(String request){
		this.request = request;
	}

	public String getIpAddress(){
		return ipAddress;
	}

	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}

	public Date getDateTime(){
		return dateTime;
	}

	public void setDateTime(Date dateTime){
		this.dateTime = dateTime;
	}

	public Long getSessionDuration(){
		return sessionDuration;
	}

	public void setSessionDuration(Long sessionDuration){
		this.sessionDuration = sessionDuration;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof UserActivity))
			return false;

		UserActivity oc = (UserActivity) o;
		if (oc.getId() == null || getId() == null)
			return false;
		return getId().equals(oc.getId());
	}

	public void setActivityType(String activityType){
		this.activityType = activityType;
	}

	public String getActivityType(){
		return activityType;
	}

	public UserActivityType getUserActivityType(){
		UserActivityType result = UserActivityType.getActivityType(activityType);
		return result != null ? result : UserActivityType.NotALog;
	}

	public boolean isLoginActivity(){
		return isPasswordLoginActivity() || isSIDLoginActivity();
	}

	public boolean isSyncLoginActivity(){
		return getUserActivityType() == UserActivityType.Synchronization;
	}

	public boolean isPasswordLoginActivity(){
		return getUserActivityType() == UserActivityType.PasswordLogin;
	}

	public boolean isSIDLoginActivity(){
		return getUserActivityType() == UserActivityType.SIDLogin;
	}

	public boolean isLogoutActivity(){
		return getUserActivityType() == UserActivityType.Logout;
	}

	public boolean isGetModuleActivity(){
		return getUserActivityType() == UserActivityType.OfflineGetModule;
	}

	public void setIsSync(Boolean isSync){
		this.isSync = isSync;
	}

	public boolean isSync(){
		if (isSync == null)
			isSync = request.contains(SYNC_IDENTIFIER);
		return isSync;
	}
}
