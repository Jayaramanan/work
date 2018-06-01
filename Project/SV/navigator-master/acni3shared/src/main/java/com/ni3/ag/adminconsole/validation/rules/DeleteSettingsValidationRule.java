/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteSettingsValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		List<Setting> settings = new ArrayList<Setting>();

		boolean isApplicationLevel = false;
		if (model instanceof SettingsModel){
			SettingsModel settingsModel = (SettingsModel) model;
			Object obj = settingsModel.getCurrentObject();
			if (obj != null && obj instanceof DatabaseInstance){
				isApplicationLevel = true;
			}
			settings.addAll(settingsModel.getDeletableSettings());
		}

		for (Setting as : settings){
			if (ApplicationSetting.isMandatory(as)){
				errors.add(new ErrorEntry(TextID.MsgSettingIsMandatory, new String[] { as.getSection(), as.getProp() }));
			} else if (as.getProp() != null && as.getProp().equals(UserSetting.INHERITS_GROUP_SETTINGS_PROPERTY)){
				errors.add(new ErrorEntry(TextID.MsgCantDeleteSystemProperty));
				break;
			} else if (isApplicationLevel && isSystemProperty(as.getProp())){
				errors.add(new ErrorEntry(TextID.MsgCantDeleteSystemProperty));
				break;
			}
		}

		return errors.isEmpty();
	}

	private boolean isSystemProperty(String key){
		if (key == null || key.isEmpty())
			return false;
		for (String s : Setting.SETTINGS_MENU_TREE_NODES)
			if (key.startsWith(s))
				return true;
		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
