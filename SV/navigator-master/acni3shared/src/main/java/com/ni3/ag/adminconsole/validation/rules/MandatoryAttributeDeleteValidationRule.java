/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MandatoryAttributeDeleteValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	MandatoryAttributeDeleteValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		SchemaAdminModel sModel = (SchemaAdminModel) model;
		List<ObjectAttribute> attrs = sModel.getAttributesToDelete();

		for (ObjectAttribute oa : attrs){
			if (oa == null || oa.getName() == null)
				continue;

			ObjectDefinition od = oa.getObjectDefinition();

			if (od.isEdge() && ObjectAttribute.isFixedEdgeAttribute(oa, false) || od.isNode()
			        && ObjectAttribute.isFixedNodeAttribute(oa, false)){
				errors.add(new ErrorEntry(TextID.MsgCannotDeleteSystemAttribute));
				break;
			}
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
