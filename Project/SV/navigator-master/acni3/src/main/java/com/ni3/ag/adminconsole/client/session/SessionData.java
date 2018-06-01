/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.session;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.SavePromptDialog;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;

public class SessionData{
	private SessionData(){
	}

	private static SessionData instance;
	static{
		instance = new SessionData();
	}

	private String dbName;
	private DatabaseInstance currentDatabaseInstance;
	private Language currentUserLanguage;
	private List<DatabaseInstance> databaseInstances;
	private List<DatabaseInstance> connectedDatabaseInstances;
	private String sessionId;

	public Integer getUserId(){
		if (currentDatabaseInstance != null && getUser() != null)
			return getUser().getId();
		return null;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public String getSessionId(){
		return sessionId;
	}

	public String getCurrentDatabaseInstanceId(){
		return currentDatabaseInstance != null ? currentDatabaseInstance.getDatabaseInstanceId() : null;
	}

	public DatabaseInstance getCurrentDatabaseInstance(){
		return currentDatabaseInstance;
	}

	public void setCurrentDatabaseInstance(DatabaseInstance instance){
		this.currentDatabaseInstance = instance;
	}

	public static synchronized SessionData getInstance(){
		return instance;
	}

	public void setUser(User user){
		currentDatabaseInstance.setUser(user);
	}

	public User getUser(){
		return currentDatabaseInstance.getUser();
	}

	public String getDbName(){
		return dbName;
	}

	public void setDbName(String dbName){
		this.dbName = dbName;
	}

	public void setUserLanguage(Language userLanguage){
		currentUserLanguage = userLanguage;
	}

	public Language getUserLanguage(){
		return currentUserLanguage;
	}

	public List<DatabaseInstance> getDatabaseInstances(){
		return databaseInstances;
	}

	@SuppressWarnings("deprecation")
	public void setDatabaseInstanceConnected(DatabaseInstance di, boolean connected){
		di.setConnected(connected);
		if (connected && !connectedDatabaseInstances.contains(di)){
			int index = databaseInstances.indexOf(di);
			if (index == -1)
				return;
			int size = connectedDatabaseInstances.size();
			connectedDatabaseInstances.add(Math.min(size, index), di);
		} else if (!connected && connectedDatabaseInstances.contains(di))
			connectedDatabaseInstances.remove(di);
	}

	public List<DatabaseInstance> getConnectedDatabaseInstances(){
		return connectedDatabaseInstances;
	}

	public void setDatabaseInstances(List<DatabaseInstance> databaseInstances){
		this.databaseInstances = databaseInstances;
		this.connectedDatabaseInstances = new ArrayList<DatabaseInstance>();
	}

	public void setTabSwitchAction(String value){
		currentDatabaseInstance.setTabSwitchDefaultAction(value);
	}

	public String getTabSwitchAction(){
		return currentDatabaseInstance != null ? currentDatabaseInstance.getTabSwitchDefaultAction() : null;
	}

	public void resolveUserTabSwitchAction(User user){
		UserAdminService uss = ACSpringFactory.getInstance().getUserAdminService();
		User localUser = uss.getUser(user.getId());

		UserSetting us = null;
		// try to find tab switch property in user settings
		if (localUser.getSettings() != null){
			for (UserSetting cur : localUser.getSettings()){
				if (cur.getProp().equalsIgnoreCase(ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY)){
					us = cur;
					break;
				}
			}
		}

		if (us != null && us.getValue() != null){
			String action = us.getValue();
			if (action.trim().length() == 0)
				action = null;
			if (action != null){
				SessionData.getInstance().setTabSwitchAction(action);
				return;
			}
		}

		// try to find tab switch property in user's group
		GroupSetting gs = null;
		if (localUser.getGroups() != null && localUser.getGroups().size() > 0){
			Group g = uss.getGroup(localUser.getGroups().get(0).getId());
			for (GroupSetting cur : g.getGroupSettings()){
				if (cur.getProp().equals(ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY)){
					gs = cur;
					break;
				}
			}
		}

		if (gs != null){
			String action = gs.getValue();
			if (action != null && action.trim().length() == 0)
				action = null;
			if (action != null){
				SessionData.getInstance().setTabSwitchAction(action);
				return;
			}
		}

		// property not found neither for user nor for group
		// try to search for property in app settings
		ApplicationSetting as = null;
		SettingsService ss = ACSpringFactory.getInstance().getSettingsService();
		List<ApplicationSetting> appSettings = ss.getApplicationSettings();
		for (ApplicationSetting cur : appSettings){
			if (cur.getProp().equals(ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY)){
				as = cur;
				break;
			}
		}
		String action = SavePromptDialog.ALWAYS_ASK;
		if (as != null)
			action = as.getValue();
		if (action != null && action.trim().length() == 0)
			action = null;
		SessionData.getInstance().setTabSwitchAction(action);
	}

}
