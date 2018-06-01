/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UniqModuleNameValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		Set<String[]> names = new HashSet<String[]>();
		VersioningModel model = (VersioningModel) amodel;
		List<Module> modules = model.getModules();
		for (Module m : modules){
			String[] nameVers = new String[] { m.getName() == null ? "" : m.getName(),
			        m.getVersion() == null ? "" : m.getVersion() };
			for (String[] setNameVers : names){
				if (setNameVers[0].equals(nameVers[0]) && setNameVers[1].equals(nameVers[1])){
					errors.add(new ErrorEntry(TextID.ModuleNameAlreadyExists, nameVers));
					return false;
				}
			}
			names.add(nameVers);
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
