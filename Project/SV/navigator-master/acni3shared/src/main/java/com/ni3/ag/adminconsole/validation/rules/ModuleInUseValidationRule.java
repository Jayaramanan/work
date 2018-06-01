/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ModuleInUseValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		VersioningModel model = (VersioningModel) amodel;
		Module m = model.getModudleToDelete();
		List<Group> groups = model.getCurrentGroups();
		for (Group g : groups){// in all groups
			for (User u : g.getUsers()){ // in all users
				for (ModuleUser mu : u.getUserModules()){
					// search any occurrence of this module
					if (m.equals(mu.getCurrent()) || m.equals(mu.getTarget())){
						errors.add(new ErrorEntry(TextID.MsgModuleAlreadyInUse, new String[] { m.getName() }));
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
