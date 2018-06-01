/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class NavigatorLicenseModel extends AbstractModel{

	private Map<DatabaseInstance, List<LicenseData>> licenseMap = new HashMap<DatabaseInstance, List<LicenseData>>();
	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private List<User> currentTableData;
	private Object currentObject;

	public void setLicenseData(List<LicenseData> licenseData){
		licenseMap.put(currentDatabaseInstance, licenseData);
	}

	public Map<DatabaseInstance, List<LicenseData>> getLicenseMap(){
		return licenseMap;
	}

	public List<LicenseData> getLicenseData(){
		return licenseMap.get(currentDatabaseInstance);
	}

	public void setGroups(List<Group> groups){
		groupMap.put(currentDatabaseInstance, groups);
	}

	public Map<DatabaseInstance, List<Group>> getGroupMap(){
		return groupMap;
	}

	public List<Group> getGroups(){
		return groupMap.get(currentDatabaseInstance);
	}

	public void setCurrentObject(Object o){
		this.currentObject = o;
	}

	public Object getCurrentObject(){
		return currentObject;
	}

	public boolean isGroupSelected(){
		return currentObject != null && currentObject instanceof Group;
	}

	public boolean isInstanceSelected(){
		return currentObject != null && currentObject instanceof DatabaseInstance;
	}

	public boolean isInstanceLoaded(){
		return licenseMap.containsKey(currentDatabaseInstance);
	}

	public boolean isInstanceLoaded(DatabaseInstance dbInstance){
		return licenseMap.containsKey(dbInstance);
	}

	public List<User> getCurrentTableData(){
		return currentTableData;
	}

	public void setCurrentTableData(List<User> currentTableData){
		this.currentTableData = currentTableData;
	}

}