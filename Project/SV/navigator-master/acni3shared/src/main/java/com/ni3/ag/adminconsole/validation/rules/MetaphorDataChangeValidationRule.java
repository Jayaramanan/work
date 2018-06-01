/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MetaphorDataChangeValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		NodeMetaphorModel model = (NodeMetaphorModel) amodel;
		List<Metaphor> metaphors = model.getCurrentMetaphors();
		if (metaphors == null)
			return false;
		for (Metaphor nm : metaphors){
			if (nm.getId() == null){
				errors.add(new ErrorEntry(TextID.MsgDataChangedUpdateOrRefresh));
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
