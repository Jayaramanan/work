/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import static com.ni3.ag.adminconsole.shared.language.TextID.MsgDuplicatePropertyName;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgPropertyNameEmpty;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgSectionNameEmpty;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgValueEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserSettingsValidationRule implements ACValidationRule{
	private List<String> exceptionValues = new ArrayList<String>();

	private List<ErrorEntry> errors;

	UserSettingsValidationRule(){
		exceptionValues.add(ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY);
	}

	@Override
	public boolean performCheck(AbstractModel model){

		errors = new ArrayList<ErrorEntry>();

		SettingsModel settingsModel = (SettingsModel) model;
		User current = (User) settingsModel.getCurrentObject();
		List<UserSetting> settings = current.getSettings();
		Set<String> propNames = new HashSet<String>();
		boolean wasAboutSections = false;
		boolean wasAboutProp = false;
		for (UserSetting as : settings){
			if (propNames.contains(as.getProp()))
				errors.add(new ErrorEntry(MsgDuplicatePropertyName, new String[] { as.getProp() }));
			propNames.add(as.getProp());

			if (((as.getSection() == null) || (as.getSection() != null && "".equals(as.getSection().trim())))
			        && !wasAboutSections){
				errors.add(new ErrorEntry(MsgSectionNameEmpty));
				wasAboutSections = true;
			}
			if (((as.getProp() == null) || (as.getProp() != null && "".equals(as.getProp().trim()))) && !wasAboutProp){
				errors.add(new ErrorEntry(MsgPropertyNameEmpty));
				wasAboutProp = true;
			}
			if (exceptionValues.contains(as.getProp()))
				continue;
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
