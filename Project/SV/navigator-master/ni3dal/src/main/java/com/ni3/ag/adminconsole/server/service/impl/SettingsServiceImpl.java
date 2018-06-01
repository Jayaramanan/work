/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ApplicationSettingsDAO;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.LanguageDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.dao.SettingDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;

public class SettingsServiceImpl implements SettingsService{

	private GroupDAO groupDAO;
	private UserDAO userDAO;
	private LanguageDAO languageDAO;
	private SchemaDAO schemaDAO;
	private ApplicationSettingsDAO applicationSettingsDAO;
	private SettingDAO settingDAO;

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public ApplicationSettingsDAO getApplicationSettingsDAO(){
		return applicationSettingsDAO;
	}

	public void setApplicationSettingsDAO(ApplicationSettingsDAO applicationSettingsDAO){
		this.applicationSettingsDAO = applicationSettingsDAO;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public List<ApplicationSetting> getApplicationSettings(){
		return applicationSettingsDAO.getSettings();
	}

	public LanguageDAO getLanguageDAO(){
		return languageDAO;
	}

	public void setLanguageDAO(LanguageDAO languageDAO){
		this.languageDAO = languageDAO;
	}

	public void setSettingDAO(SettingDAO settingDAO){
		this.settingDAO = settingDAO;
	}

	public List<Group> getGroups(){
		List<Group> groups = groupDAO.getGroups();
		for (Group group : groups){
			List<User> users = group.getUsers();
			Hibernate.initialize(users);
			for (User user : users){
				Hibernate.initialize(user.getSettings());
				Hibernate.initialize(user.getGroups());
			}
			Hibernate.initialize(group.getGroupSettings());
		}
		return groups;
	}

	public void updateApplicationSettings(List<ApplicationSetting> applicationSettings,
	        List<ApplicationSetting> deletableApplicationSettings){
		applicationSettingsDAO.deleteSettings(deletableApplicationSettings);
		applicationSettingsDAO.saveOrUpdate(applicationSettings);
	}

	public void updateGroupSettings(Group currentObject){
		groupDAO.saveOrUpdate(currentObject);
	}

	public void updateUserSettings(User currentObject){
		userDAO.saveOrUpdate(currentObject);
	}

	@Override
	public List<Language> getLanguages(){
		return languageDAO.getLanguages();
	}

	@Override
	public List<Schema> getSchemas(){
		return schemaDAO.getSchemas();
	}

	@Override
	public Group reloadGroup(Integer id){
		Group group = groupDAO.getGroup(id);
		Hibernate.initialize(group.getUsers());
		Hibernate.initialize(group.getGroupSettings());
		return group;
	}

	@Override
	public User reloadUser(Integer id){
		User user = userDAO.getById(id);
		Hibernate.initialize(user.getSettings());
		return user;
	}

	@Override
	public List<User> getAllUsers(){
		List<User> users = userDAO.getUsers();
		for (User u : users)
			Hibernate.initialize(u.getSettings());
		return users;
	}

	@Override
	public Setting getApplicationSetting(String section, String prop){
		return settingDAO.getApplicationSetting(section, prop);
	}

	@Override
	public void updateApplicationSetting(String section, String prop, String value){
		ApplicationSetting s = settingDAO.getApplicationSetting(section, prop);
		if (s == null){
			s = new ApplicationSetting(section, prop, value);
		}
		s.setValue(value);

		List<ApplicationSetting> settings = new ArrayList<ApplicationSetting>();
		settings.add(s);

		applicationSettingsDAO.saveOrUpdate(settings);
	}

    @Override
    public void updateUserSettings(List<User> currentUsers) {
        userDAO.saveOrUpdateAll(currentUsers);
    }
}
