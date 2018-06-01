/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class PredefinedAttributeParentValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();
		PredefinedAttributeEditModel pModel = (PredefinedAttributeEditModel) model;
		List<Schema> schemas = pModel.getFullSchemas();
		ObjectDefinition od = pModel.getCurrentObject();
		int fullSchIndex = schemas.indexOf(od.getSchema());
		Schema fullSchema = schemas.get(fullSchIndex);
		int fullOdIndex = fullSchema.getObjectDefinitions().indexOf(od);
		ObjectDefinition fullOd = fullSchema.getObjectDefinitions().get(fullOdIndex);

		List<PredefinedAttribute> deletedPredefineds = pModel.getDeletedPredefinedAttributes();

		if (pModel.getCurrentAttribute() != null){
			List<PredefinedAttribute> onScreenPredefineds = pModel.getCurrentAttribute().getPredefinedAttributes();
			for (PredefinedAttribute screenPa : onScreenPredefineds){
				PredefinedAttribute parent = screenPa.getParent();
				boolean contain = false;
				for (ObjectAttribute oa : fullOd.getObjectAttributes()){
					if (parent != null && oa.getPredefinedAttributes().contains(parent)){
						contain = true;
						break;
					}
				}
				if (parent != null && !contain)
					errors.add(new ErrorEntry(TextID.MsgInvalidPredefinedAttributeId, new String[] { String.valueOf(parent
					        .getId()) }));
			}
		}

		for (PredefinedAttribute deletedPa : deletedPredefineds){
			String childPredefinedIds = "";
			for (ObjectAttribute oa : od.getObjectAttributes()){
				for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
					if (deletedPredefineds.contains(pa))
						continue;
					if (deletedPa.equals(pa.getParent())){
						if (!childPredefinedIds.isEmpty())
							childPredefinedIds += ", ";
						childPredefinedIds += pa.getId();
					}
				}
			}
			if (!childPredefinedIds.isEmpty())
				errors.add(new ErrorEntry(TextID.MsgCanDeletePredefinedIsParent, new String[] {
				        String.valueOf(deletedPa.getId()), childPredefinedIds }));
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
