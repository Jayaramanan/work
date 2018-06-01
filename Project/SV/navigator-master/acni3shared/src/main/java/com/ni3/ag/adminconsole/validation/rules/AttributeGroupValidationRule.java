/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeGroupModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeGroupValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();
		AttributeGroupModel attributeGroupModel = (AttributeGroupModel) model;

		AttributeGroup ag = attributeGroupModel.getAttributeGroup();

		if (ag.getObjectAttribute() == null)
			errors.add(new ErrorEntry(TextID.MsgInvalidObjectAttribute, new String[] { attributeGroupModel
			        .getAttributeName() }));
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
