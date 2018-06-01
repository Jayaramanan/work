/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeEditMandatoryFieldValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		AttributeEditModel aeModel = (AttributeEditModel) model;
		ObjectDefinition od = aeModel.getCurrentObjectDefinition();
		List<ObjectAttribute> attrList = od.getObjectAttributes();
		if (attrList == null || attrList.isEmpty())
			return true;

		for (ObjectAttribute oa : attrList){
			if (oa.getSort() == null || oa.getMatrixSort() == null || oa.getLabelSort() == null
			        || oa.getFilterSort() == null || oa.getSearchSort() == null){
				errors.add(new ErrorEntry(TextID.MsgNotAllRequeredFieldsHasValues));
				return false;
			}
		}
		return true;
	}
}
