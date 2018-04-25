/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserAdminGroupNameValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	UserAdminGroupNameValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		if (model != null){
			Group current = ((UserAdminModel) model).getCurrentGroup();
			List<Group> groups = ((UserAdminModel) model).getGroups();
			for (Group g : groups){
				if (current.getName().equalsIgnoreCase(g.getName())){
					errors.add(new ErrorEntry(TextID.MsgDuplicateGroup, new String[] { current.getName() }));
					break;
				}
			}
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
