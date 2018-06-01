/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NavigatorLicenseModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class NavigatorLicenseCountValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		NavigatorLicenseModel model = (NavigatorLicenseModel) amodel;

		List<LicenseData> lDataList = model.getLicenseData();
		for (NavigatorModule module : NavigatorModule.values()){
			String strModule = module.getValue().toString();
			int userCount = 0;
			for (LicenseData lData : lDataList)
				if (lData.isValid() && lData.containsKey(strModule)){
					Integer count = (Integer) lData.get(strModule);
					if (count != null)
						userCount += count.intValue();
				}
			int usedCount = getUsedLicenseCount(strModule, model);
			if (usedCount > userCount){
				errors.add(new ErrorEntry(TextID.ExceededUserLicenseCount, new String[] { strModule }));
				break;
			}
		}

		return errors.isEmpty();
	}

	private int getUsedLicenseCount(String strModule, NavigatorLicenseModel model){
		int usedCount = 0;
		List<Group> groups = model.getGroups();
		for (Group group : groups){
			for (User user : group.getUsers()){
				if (hasAccess(user, strModule)){
					usedCount++;
				}
			}
		}
		return usedCount;
	}

	private boolean hasAccess(User user, String strModule){
		for (UserEdition ue : user.getUserEditions()){
			if (ue.getEdition().equals(strModule)){
				return true;
			}
		}
		return false;
	}

}
