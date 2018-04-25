/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PredefAttributeValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	public PredefAttributeValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){

		errors = new ArrayList<ErrorEntry>();

		if (model != null){
			List<PredefinedAttribute> predefinedAttributes;
			PredefinedAttributeEditModel mdl = (PredefinedAttributeEditModel) model;
			if (mdl.getCurrentAttribute() == null){
				predefinedAttributes = new ArrayList<PredefinedAttribute>();
			} else{
				predefinedAttributes = mdl.getCurrentAttribute().getPredefinedAttributes();
			}
			if (containsEmptyValues(predefinedAttributes)){
				errors.add(new ErrorEntry(TextID.MsgPredefinedAttributeFieldsEmpty));
			}
			if (containsDuplicateValueOrLabel(predefinedAttributes)){
				errors.add(new ErrorEntry(TextID.MsgDuplicatePredefinedAttributeValueOrLabel));
			}
		}

		return errors.isEmpty();
	}

	private boolean containsEmptyValues(List<PredefinedAttribute> predefinedAttributes){
		for (PredefinedAttribute pa : predefinedAttributes){
			if (pa.getValue() == null || pa.getValue().length() == 0 || pa.getLabel() == null || pa.getLabel().length() == 0){
				return true;
			}
		}
		return false;
	}

	protected boolean containsDuplicateValueOrLabel(List<PredefinedAttribute> attributes){
		for (PredefinedAttribute pa : attributes){
			for (PredefinedAttribute pa2 : attributes){
				if (pa != pa2 && pa.getValue() != null && pa.getValue().equals(pa2.getValue()) && pa.getLabel() != null
						&& pa.getLabel().equals(pa2.getLabel())){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
