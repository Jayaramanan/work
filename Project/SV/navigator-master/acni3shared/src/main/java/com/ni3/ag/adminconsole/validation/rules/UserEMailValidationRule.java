/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserEMailValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		VersioningModel model = (VersioningModel) amodel;
		User u = model.getUserToSend();
		if (u.geteMail() == null){
			errors.add(new ErrorEntry(TextID.MsgEmptyUserEmail));
			return false;
		}
		if (u.geteMail().trim().isEmpty()){
			errors.add(new ErrorEntry(TextID.MsgEmptyUserEmail));
			return false;
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
