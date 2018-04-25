/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import static com.ni3.ag.adminconsole.shared.language.TextID.MsgDuplicatePropertyName;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgPropertyNameEmpty;
import static com.ni3.ag.adminconsole.shared.language.TextID.MsgSectionNameEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ApplicationSettingsValidationRule implements ACValidationRule{
	private List<String> exceptionValues;

	private List<ErrorEntry> errorContainer;

	@Override
	public boolean performCheck(AbstractModel model){
		exceptionValues = new ArrayList<String>();
		errorContainer = new ArrayList<ErrorEntry>();
		exceptionValues.add(ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY);

		Set<String> propNames = new HashSet<String>();
		boolean wasAboutSections = false;
		boolean wasAboutProp = false;
		SettingsModel sModel = (SettingsModel) model;
		List<ApplicationSetting> settings = sModel.getApplicationSettings();
		for (Setting as : settings){
			if (propNames.contains(as.getProp()))
				errorContainer.add(new ErrorEntry(MsgDuplicatePropertyName, new String[] { as.getProp() }));
			propNames.add(as.getProp());
			if (((as.getSection() == null) || (as.getSection() != null && "".equals(as.getSection().trim())))
			        && !wasAboutSections){
				errorContainer.add(new ErrorEntry(MsgSectionNameEmpty));
				wasAboutSections = true;
			}
			if (((as.getProp() == null) || (as.getProp() != null && "".equals(as.getProp().trim()))) && !wasAboutProp){
				errorContainer.add(new ErrorEntry(MsgPropertyNameEmpty));
				wasAboutProp = true;
			}
			if (exceptionValues.contains(as.getProp()))
				continue;
		}
		return errorContainer.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errorContainer;
	}

}
