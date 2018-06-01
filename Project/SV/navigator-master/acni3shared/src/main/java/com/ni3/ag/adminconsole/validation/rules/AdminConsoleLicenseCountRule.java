/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.AdminConsoleLicenseModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AdminConsoleLicenseCountRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		AdminConsoleLicenseModel model = (AdminConsoleLicenseModel) amodel;
		List<ACModuleDescription> moduleDescriptions = model.getModuleDescriptions();

		for (ACModuleDescription md : moduleDescriptions){
			if (md.getUsedUserCount() > md.getUserCount()){
				String strModule = md.getModule().getValue();
				errors.add(new ErrorEntry(TextID.ExceededUserLicenseCount, new String[] { strModule }));
				break;
			}
		}

		return errors.isEmpty();
	}

}
