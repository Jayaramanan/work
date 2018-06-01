/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.MetaphorData;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PredefinedAttributeDeleteRule implements ACValidationRule{
	private List<ErrorEntry> errors;
	private ObjectDefinitionDAO objectDefinitionDAO;

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	@Override
	public boolean performCheck(AbstractModel m){
		errors = new ArrayList<ErrorEntry>();
		PredefinedAttributeEditModel model = (PredefinedAttributeEditModel) m;
		PredefinedAttribute pa = model.getCurrentPredefinedAttribute();
		if (pa == null){
			return true;
		}
		ObjectDefinition object = pa.getObjectAttribute().getObjectDefinition();
		ObjectDefinition od = objectDefinitionDAO.getObjectDefinitionWithInMetaphor(object.getId());
		for (Metaphor metaphor : od.getMetaphors()){
			for (MetaphorData mData : metaphor.getMetaphorData()){
				if (pa.equals(mData.getData())){
					errors.add(new ErrorEntry(TextID.MsgPredefinedAttributeUsedInMetaphors));
					break;
				}
			}
			if (!errors.isEmpty())
				break;
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
