package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeInMetaphorUserRule implements ACValidationRule{
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	@Override
	public boolean performCheck(AbstractModel model){
		errors.clear();
		ObjectAttribute oa = ((SchemaAdminModel) model).getAttributeToValidate();
		if(oa.getPredefinedAttributes() == null || oa.getPredefinedAttributes().isEmpty())
			return errors.isEmpty();
		for (PredefinedAttribute pa : oa.getPredefinedAttributes())
			checkUsedInMetaphors(pa, oa.getObjectDefinition());
		return errors.isEmpty();
	}

	private void checkUsedInMetaphors(PredefinedAttribute pa, ObjectDefinition objectDefinition){
		if (objectDefinition.getMetaphors() == null)
			return;
		for (Metaphor m : objectDefinition.getMetaphors()){
			if (m.getMetaphorData() == null)
				continue;
			for (MetaphorData md : m.getMetaphorData()){
				if (md.getData().equals(pa)){
					errors.add(new ErrorEntry(TextID.MsgPredefinedValueUsedInMetaphor,
							new String[]{pa.getObjectAttribute().getLabel(), pa.getLabel(), m.getMetaphorSet()}));
					break;
				}
			}
		}
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
