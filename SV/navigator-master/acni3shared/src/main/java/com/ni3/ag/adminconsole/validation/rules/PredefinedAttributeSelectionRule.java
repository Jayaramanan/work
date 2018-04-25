/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PredefinedAttributeSelectionRule implements ACValidationRule{
	private static final Logger log = Logger.getLogger(PredefinedAttributeSelectionRule.class);
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors.clear();
		PredefinedAttributeEditModel model = (PredefinedAttributeEditModel) amodel;
		PredefinedAttribute pa = model.getFirstSelectedPredefined();
		if (!validateColor(pa.getHaloColor())){
			errors.add(new ErrorEntry(TextID.MsgSelectHaloColorForFirstPredefinedInTable));
			return false;
		}
		pa = model.getLastSelectedPredefined();
		if (!validateColor(pa.getHaloColor())){
			errors.add(new ErrorEntry(TextID.MsgSelectHaloColorForLastPredefinedInTable));
			return false;
		}
		return true;
	}

	private boolean validateColor(String haloColor){
		try{
			java.awt.Color.decode(haloColor);
			return true;
		} catch (NumberFormatException ex){
			log.warn("Not valid hex color: " + haloColor);
		}
		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
