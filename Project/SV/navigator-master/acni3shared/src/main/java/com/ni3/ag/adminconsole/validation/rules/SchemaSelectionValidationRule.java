/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class SchemaSelectionValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel aModel){
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel model = ((SchemaAdminModel) aModel);
		Schema schema = model.getCurrentSchema();
		if (schema == null){
			ObjectDefinition currentObject = model.getCurrentObjectDefinition();
			schema = currentObject != null ? currentObject.getSchema() : null;
		}
		if (schema == null){
			errors.add(new ErrorEntry(TextID.MsgSelectSchema));
		}
		return errors.isEmpty();
	}

}
