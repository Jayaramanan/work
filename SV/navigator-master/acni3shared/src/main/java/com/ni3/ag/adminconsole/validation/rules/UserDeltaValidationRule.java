/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserDeltaValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;
	private UserAdminService userAdminService;

	public UserAdminService getUserAdminService(){
		return userAdminService;
	}

	public void setUserAdminService(UserAdminService userAdminService){
		this.userAdminService = userAdminService;
	}

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		UserAdminModel model = (UserAdminModel) amodel;
		User u = model.getUserToDelete();
		if (u.getId() == null)
			return true;
		Integer deltaCount = userAdminService.getDeltasByUser(u);
		Integer deltaUserCount = userAdminService.getOutDeltasByUser(u);
		if (deltaCount > 0){
			errors.add(new ErrorEntry(TextID.MsgUserReferencedInDeltasTable));
			return false;
		}
		if (deltaUserCount > 0){
			errors.add(new ErrorEntry(TextID.MsgUserReferencedInDeltasOutTable));
			return false;
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
