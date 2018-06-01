/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class AdminConsoleLicenseModel extends AbstractModel{

	private Map<DatabaseInstance, List<User>> userMap = new HashMap<DatabaseInstance, List<User>>();
	private Map<DatabaseInstance, List<ACModuleDescription>> mDescrMap = new HashMap<DatabaseInstance, List<ACModuleDescription>>();

	public void setUsers(List<User> users){
		userMap.put(currentDatabaseInstance, users);
	}

	public Map<DatabaseInstance, List<User>> getUserMap(){
		return userMap;
	}

	public List<User> getUsers(){
		return userMap.get(currentDatabaseInstance);
	}

	public boolean isInstanceLoaded(){
		return userMap.containsKey(currentDatabaseInstance);
	}

	public boolean isInstanceLoaded(DatabaseInstance dbInstance){
		return userMap.containsKey(dbInstance);
	}

	public void setModuleDescriptions(List<ACModuleDescription> moduleDescriptions){
		mDescrMap.put(currentDatabaseInstance, moduleDescriptions);
	}

	public Map<DatabaseInstance, List<ACModuleDescription>> getModuleDescriptionMap(){
		return mDescrMap;
	}

	public List<ACModuleDescription> getModuleDescriptions(){
		return mDescrMap.get(currentDatabaseInstance);
	}

}