/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DuplicateAttributeSortValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		AttributeEditModel model = (AttributeEditModel) amodel;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		if (od == null)
			return false;
		Set<Integer> sorts = new HashSet<Integer>();
		for (ObjectAttribute oa : od.getObjectAttributes()){
			if (sorts.contains(oa.getSort())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateSortValue, new String[] { oa.getSort().toString() }));
				return true;
			}
			sorts.add(oa.getSort());
		}
		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
