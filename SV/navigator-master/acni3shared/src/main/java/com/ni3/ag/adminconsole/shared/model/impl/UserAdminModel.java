/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSequenceState;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class UserAdminModel extends AbstractModel{
	public static final int SCHEMAS = 6;
	public static final int USER_RANGES = 10;

	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private Map<DatabaseInstance, Map<Integer, Boolean>> loadedDataMap = new HashMap<DatabaseInstance, Map<Integer, Boolean>>();
	private Map<DatabaseInstance, String> passwordFormatMap = new HashMap<DatabaseInstance, String>();
	private Group currentGroup;
	private List<User> deletedUsers;
	private String currentPanel;
	private Group unassignedGroup;
	private boolean allUserModel;
	private User userToDelete;
	private User userToReset;
	private User userToChangePassword;
	private Map<Integer, List<UserSequenceState>> userRanges;
	private boolean currentShowLockedColumns;

	private UserAdminModelDataRequestListener listener;

	public List<Group> getGroups(){
		return groupMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Group>> getGroupMap(){
		return groupMap;
	}

	public void setGroups(List<Group> groups){
		groupMap.put(currentDatabaseInstance, groups);
	}

	public Group getCurrentGroup(){
		return currentGroup;
	}

	public void setCurrentGroup(Group currentGroup){
		this.currentGroup = currentGroup;
	}

	public List<User> getDeletedUsers(){
		return deletedUsers;
	}

	public void setDeletedUsers(List<User> deletedUsers){
		this.deletedUsers = deletedUsers;
	}

	public String getCurrentPanel(){
		return currentPanel;
	}

	public void setCurrentPanel(String currentPanel){
		this.currentPanel = currentPanel;
	}

	public Group getUnassignedGroup(){
		return unassignedGroup;
	}

	public void setUnassignedGroup(Group unassignedGroup){
		this.unassignedGroup = unassignedGroup;
	}

	public void setAllUserMode(boolean allUserMode){
		this.allUserModel = allUserMode;
	}

	public boolean isAllUserModel(){
		return allUserModel;
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return groupMap.containsKey(instance);
	}

	public void setUserToDelete(User userToDelete){
		this.userToDelete = userToDelete;
	}

	public User getUserToDelete(){
		return this.userToDelete;
	}

	public void setUserToReset(User userToReset){
		this.userToReset = userToReset;
	}

	public User getUserToReset(){
		return userToReset;
	}

	public void setUserRanges(Map<Integer, List<UserSequenceState>> map){
		this.userRanges = map;
		setKey(USER_RANGES, true);
	}

	public Map<Integer, List<UserSequenceState>> getUserRanges(){
		if (!isLoaded(USER_RANGES))
			listener.dataRequested(USER_RANGES);
		return userRanges;
	}

	public List<Schema> getSchemas(){
		if (!isLoaded(SCHEMAS))
			listener.dataRequested(SCHEMAS);
		return schemaMap.get(currentDatabaseInstance);
	}

	public void setSchemas(List<Schema> schemas){
		setKey(SCHEMAS, true);
		schemaMap.put(currentDatabaseInstance, schemas);
	}

	public void setListener(UserAdminModelDataRequestListener listener){
		this.listener = listener;
	}

	private void setKey(int key, boolean b){
		containsCurrentInstance();
		Map<Integer, Boolean> dataMap = loadedDataMap.get(currentDatabaseInstance);
		dataMap.put(key, b);
	}

	private boolean isLoaded(int key){
		if (!containsCurrentInstance())
			return false;
		return getKey(key);
	}

	private boolean getKey(int key){
		Map<Integer, Boolean> dataMap = loadedDataMap.get(currentDatabaseInstance);
		boolean b = dataMap.containsKey(key);
		if (!b)
			dataMap.put(key, false);
		b = dataMap.get(key);
		return b;
	}

	private boolean containsCurrentInstance(){
		boolean b = loadedDataMap.containsKey(currentDatabaseInstance);
		if (!b)
			loadedDataMap.put(currentDatabaseInstance, new HashMap<Integer, Boolean>());
		return b;
	}

	public void resetLoadedMap(){
		loadedDataMap = new HashMap<DatabaseInstance, Map<Integer, Boolean>>();
	}

	public void resetLoaded(int key){
		setKey(key, false);
	}

	public void clearGroups(){
		groupMap.clear();
	}

	public void clearSchemas(){
		schemaMap.clear();
	}

	public String getPasswordFormat(){
		return passwordFormatMap.get(currentDatabaseInstance);
	}

	public void setPasswordFormat(String passwordFormat){
		passwordFormatMap.put(currentDatabaseInstance, passwordFormat);
	}

	public User getUserToChangePassword(){
		return userToChangePassword;
	}

	public void setUserToChangePassword(User userToChangePassword){
		this.userToChangePassword = userToChangePassword;
	}

	public boolean isCurrentShowLockedColumns(){
		return currentShowLockedColumns;
	}

	public void setCurrentShowLockedColumns(boolean currentShowLockedColumns){
		this.currentShowLockedColumns = currentShowLockedColumns;
	}

}
