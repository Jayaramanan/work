/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeFormatRule implements ACValidationRule{

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return null;
	}

	@Override
	public boolean performCheck(AbstractModel sModel){
		SchemaAdminModel model = (SchemaAdminModel) sModel;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		if (od != null){
			for (ObjectAttribute oa : od.getObjectAttributes()){
				adjustFormats(oa);
			}
		}
		return true;
	}

	void adjustFormats(ObjectAttribute oa){
		if (oa.isPredefined() || oa.isFormulaAttribute() || oa.isURLDataType()){
			oa.clearFormatFields();
		} else if (oa.isDateDataType()){
			oa.setMaxValue(null);
			oa.setMinValue(null);
			oa.setFormatInvalidCharacters(null);
			oa.setFormatValidCharacters(null);
		} else if (oa.isTextDataType()){
			oa.setMaxValue(null);
			oa.setMinValue(null);
		} else if (oa.isIntDataType() || oa.isDecimalDataType()){
			oa.setFormatInvalidCharacters(null);
			oa.setFormatValidCharacters(null);
		}
	}
}