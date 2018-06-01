/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class BooleanAttributeValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	BooleanAttributeValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){

		errors = new ArrayList<ErrorEntry>();

		if (model != null){
			PredefinedAttributeEditModel mdl = (PredefinedAttributeEditModel) model;
			ObjectAttribute oa = mdl.getCurrentAttribute();
			if (!oa.isBoolDataType()){
				return true;
			}
			if (oa.getPredefinedAttributes() != null && oa.getPredefinedAttributes().size() > 2){
				errors.add(new ErrorEntry(TextID.MsgBooleanAttributeMoreThanTwoPredefineds));
			}
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
