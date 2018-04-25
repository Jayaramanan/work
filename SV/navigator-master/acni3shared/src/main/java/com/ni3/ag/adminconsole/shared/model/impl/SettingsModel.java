/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class SettingsModel extends AbstractModel{

	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private Object current;
	private Map<DatabaseInstance, List<ApplicationSetting>> appSettingsMap = new HashMap<DatabaseInstance, List<ApplicationSetting>>();
	private Map<DatabaseInstance, List<Schema>> schemaMap = new HashMap<DatabaseInstance, List<Schema>>();
	private Map<DatabaseInstance, List<Language>> languageMap = new HashMap<DatabaseInstance, List<Language>>();
	private List<ApplicationSetting> appSettingsToDelete = new ArrayList<ApplicationSetting>();
	private List<Setting> settingsToDelete = new ArrayList<Setting>();
	private Map<DatabaseInstance, List<GroupSetting>> groupSettingsMap = new HashMap<DatabaseInstance, List<GroupSetting>>();
	private Setting settingToDelete;

	public List<GroupSetting> getGroupSettings(){
		return groupSettingsMap.get(currentDatabaseInstance);
	}

	public void setGroupSettings(List<GroupSetting> groupSettings){
		groupSettingsMap.put(currentDatabaseInstance, groupSettings);
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

	public void setCurrentObject(Object object){
		current = object;
	}

	public Object getCurrentObject(){
		return current;
	}

	public boolean isCurrentApplication(){
		return current != null && current instanceof DatabaseInstance;
	}

	public boolean isCurrentGroup(){
		return current != null && current instanceof Group;
	}

	public boolean isCurrentUser(){
		return current != null && current instanceof User;
	}

	public boolean isCurrent(){
		return current != null;
	}

	public void setApplicationSettings(List<ApplicationSetting> applicationSettings){
		appSettingsMap.put(currentDatabaseInstance, applicationSettings);
		appSettingsToDelete.clear();
	}

	public List<ApplicationSetting> getApplicationSettings(){
		return appSettingsMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<ApplicationSetting>> getAppSettingsMap(){
		return appSettingsMap;
	}

	public void addDeletableApplicationSetting(ApplicationSetting as){
		appSettingsToDelete.add(as);
	}

	public List<ApplicationSetting> getDeletableApplicationSettings(){
		return appSettingsToDelete;
	}

	public void addDeletableSetting(Setting as){
		settingsToDelete.add(as);
	}

	public void setDeletableSettings(List<Setting> settings){
		settingsToDelete = settings;
	}

	public List<Setting> getDeletableSettings(){
		return settingsToDelete;
	}

	public List<Language> getLanguages(){
		return languageMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Language>> getLanguageMap(){
		return languageMap;
	}

	public void setLanguages(List<Language> languages){
		languageMap.put(currentDatabaseInstance, languages);
	}

	public List<Schema> getSchemas(){
		return schemaMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Schema>> getSchemaMap(){
		return schemaMap;
	}

	public void setSchemas(List<Schema> schemaList){
		schemaMap.put(currentDatabaseInstance, schemaList);
	}

	public void clearDeletableApplicationSettings(){
		appSettingsToDelete.clear();
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return groupMap.containsKey(instance);
	}

	public Setting getSettingsToDelete(){
		return settingToDelete;
	}

	public void setSettingToDelete(Setting set){
		settingToDelete = set;
	}

	public List<?> getCurrentSettings(){
		if (isCurrentUser()){
			return ((User) current).getSettings();
		} else if (isCurrentGroup()){
			return ((Group) current).getGroupSettings();
		} else if (isCurrentApplication()){
			return getApplicationSettings();
		}
		return null;
	}
}
