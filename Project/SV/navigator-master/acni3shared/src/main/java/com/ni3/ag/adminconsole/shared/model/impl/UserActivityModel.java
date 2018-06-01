/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class UserActivityModel extends AbstractModel{

	private List<User> users;
	private List<UserActivityType> activityTypes;

	public List<User> getUsers(){
		return users;
	}

	public void setUsers(List<User> users){
		this.users = users;
	}

	public void setActivityTypes(List<UserActivityType> allActivityTypes){
		this.activityTypes = allActivityTypes;
	}

	public List<UserActivityType> getActivityTypes(){
		return activityTypes;
	}
}
