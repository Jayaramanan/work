/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteSchemaValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	private SettingsService settingsService;

	public void setSettingsService(SettingsService settingsService){
		this.settingsService = settingsService;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		SchemaAdminModel schemaAdminModel = (SchemaAdminModel) model;
		Schema schema = schemaAdminModel.getCurrentSchema();

		checkUserSettings(schema);
		checkGroupSettings(schema);
		checkApplicationSettings(schema);

		return errors.isEmpty();
	}

	void checkUserSettings(Schema schema){
		List<User> users = settingsService.getAllUsers();
		List<User> usersThatContainSchema = new ArrayList<User>();
		for (User u : users){
			if (isContainSchema(u.getSettings(), schema))
				usersThatContainSchema.add(u);
		}
		if (!usersThatContainSchema.isEmpty()){
			String userNames = usersThatContainSchema.get(0).getUserName();
			for (int i = 1; i < usersThatContainSchema.size(); i++)
				userNames += ", " + usersThatContainSchema.get(i).getUserName();
			errors.add(new ErrorEntry(TextID.MsgSchemaIsDefaultForUsers, new String[] { userNames }));
		}
	}

	void checkGroupSettings(Schema schema){
		List<?> groups = settingsService.getGroups();
		List<Group> groupsThatContainSchema = new ArrayList<Group>();
		for (int i = 0; groups != null && i < groups.size(); i++){
			Group g = (Group) groups.get(i);
			if (isContainSchema(g.getGroupSettings(), schema))
				groupsThatContainSchema.add(g);
		}
		if (!groupsThatContainSchema.isEmpty()){
			String groupNames = groupsThatContainSchema.get(0).getName();
			for (int i = 1; i < groupsThatContainSchema.size(); i++)
				groupNames += ", " + groupsThatContainSchema.get(i).getName();
			errors.add(new ErrorEntry(TextID.MsgSchemaIsDefaultForGroups, new String[] { groupNames }));
		}
	}

	void checkApplicationSettings(Schema schema){
		List<?> settings = settingsService.getApplicationSettings();
		if (isContainSchema(settings, schema)){
			errors.add(new ErrorEntry(TextID.MsgSchemaIsDefaultForApplication));
		}
	}

	boolean isContainSchema(List<?> settings, Schema schema){
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting us = (Setting) settings.get(i);
			String idPattern = "(\\d+)";
			if (us.getProp().equals(Setting.SCHEME_PROPERTY) && us.getValue() != null && us.getValue().matches(idPattern)
			        && Integer.valueOf(us.getValue()).equals(schema.getId()))
				return true;
		}
		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
