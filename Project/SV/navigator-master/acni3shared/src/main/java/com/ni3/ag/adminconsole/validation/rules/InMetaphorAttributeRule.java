/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class InMetaphorAttributeRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel aModel){
		errors = new ArrayList<ErrorEntry>();
		NodeMetaphorModel model = ((NodeMetaphorModel) aModel);
		ObjectDefinition object = model.getCurrentObjectDefinition();
		if (object != null){
			List<ObjectAttribute> attributes = object.getObjectAttributes();
			if (attributes == null || attributes.isEmpty()){
				errors.add(new ErrorEntry(TextID.MsgObjectShouldHaveInMetaphorAttributes));
			} else{
				boolean found = false;
				for (ObjectAttribute attr : attributes){
					if (attr.isInMetaphor()){
						found = true;
					}
				}
				if (!found){
					errors.add(new ErrorEntry(TextID.MsgObjectShouldHaveInMetaphorAttributes));
				}
			}
		}

		return errors.isEmpty();
	}
}
