/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class VersioningModel extends AbstractModel{
	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private Map<DatabaseInstance, List<Module>> moduleMap = new HashMap<DatabaseInstance, List<Module>>();
	private Map<DatabaseInstance, List<String>> pathMap = new HashMap<DatabaseInstance, List<String>>();
	private Group currenGroup;
	private List<Module> deletedModules = new ArrayList<Module>();
	private Module moduleToDelete;
	private User userToSend;

	public void setGroups(List<Group> groups){
		groupMap.put(currentDatabaseInstance, groups);
	}

	public Map<DatabaseInstance, List<Group>> getGroupMap(){
		return groupMap;
	}

	public void setModules(List<Module> modules){
		moduleMap.put(currentDatabaseInstance, modules);
	}

	public List<Module> getModules(){
		if (getCurrentDatabaseInstance() == null)
			return new ArrayList<Module>();
		else
			return moduleMap.get(getCurrentDatabaseInstance());
	}

	public Map<DatabaseInstance, List<Module>> getModuleMap(){
		return moduleMap;
	}

	public void setCurrentGroup(Group g){
		this.currenGroup = g;
	}

	public Group getCurrentGroup(){
		return currenGroup;
	}

	public List<Module> getDeletedModules(){
		return deletedModules;
	}

	public void clearDeleted(){
		deletedModules.clear();
	}

	public void setModudleToDelete(Module module){
		moduleToDelete = module;
	}

	public Module getModudleToDelete(){
		return moduleToDelete;
	}

	public List<Group> getCurrentGroups(){
		if (getCurrentDatabaseInstance() == null)
			return new ArrayList<Group>();
		else
			return groupMap.get(getCurrentDatabaseInstance());
	}

	public void setPaths(List<String> paths){
		pathMap.put(currentDatabaseInstance, paths);
	}

	public List<String> getPaths(){
		if (getCurrentDatabaseInstance() == null)
			return new ArrayList<String>();
		else
			return pathMap.get(getCurrentDatabaseInstance());
	}

	public Map<DatabaseInstance, List<String>> getPathMap(){
		return pathMap;
	}

	public boolean isInstanceLoaded(DatabaseInstance dbInstance){
		return groupMap.containsKey(dbInstance);
	}

	public void setUserToSend(User targetUser){
		userToSend = targetUser;
	}

	public User getUserToSend(){
		return userToSend;
	}

	public List<Group> getGroups(){
		if (getCurrentDatabaseInstance() == null)
			return new ArrayList<Group>();
		else
			return groupMap.get(getCurrentDatabaseInstance());
	}
}
