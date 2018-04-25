/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectDefinitionNameValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel model = (SchemaAdminModel) amodel;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		String newName = od.getName();
		String temp = newName.replaceAll("[^a-zA-Z0-9 -]", "");
		if (!temp.equals(newName)){
			errors.add(new ErrorEntry(TextID.MsgInvalidObjectName, new String[] { newName }));
			return false;
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
