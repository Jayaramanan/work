/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

/**
 * Checks for loops of child-parent relations in predefined attributes, and that child-parent relations are not used for
 * one object attribute
 */
public class PredefinedAttributeLevelValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();
		PredefinedAttributeEditModel pModel = (PredefinedAttributeEditModel) model;
		ObjectAttribute attr = pModel.getCurrentAttribute();
		List<PredefinedAttribute> values = pModel.getCurrentPredefinedAttributes();
		List<PredefinedAttribute> oaValues = attr.getPredefinedAttributes();
		if (values != null)
			for (PredefinedAttribute pa : values){
				List<PredefinedAttribute> loop = checkForLoop(pa);
				if (loop == null)
					continue;
				String ids = "";
				for (PredefinedAttribute loopPa : loop){
					if (!ids.isEmpty())
						ids += "->";
					ids += loopPa.getId();
				}
				errors.add(new ErrorEntry(TextID.MsgLoopOfPredefinedAttributesDetected, new String[] { ids }));
				break;
			}
		if (oaValues != null)
			for (PredefinedAttribute pa : oaValues){
				if (!checkForSameLevelInheritance(pa))
					errors.add(new ErrorEntry(TextID.MsgAttributeValueHasParentOfSameAttribute,
					        new String[] { pa.getLabel() }));
			}

		return errors.isEmpty();
	}

	boolean checkForSameLevelInheritance(PredefinedAttribute pa){
		if (pa.getParent() != null){
			ObjectAttribute parentOa = pa.getParent().getObjectAttribute();
			if (pa.getObjectAttribute().equals(parentOa))
				return false;
		}
		return true;
	}

	/**
	 * Checks for a loop of child-parent relations, starting with the given predefined attribute
	 * 
	 * @param pa
	 * @return list of predefined attributes that form a loop or null if no loop found
	 */
	List<PredefinedAttribute> checkForLoop(PredefinedAttribute pa){
		List<PredefinedAttribute> checked = new ArrayList<PredefinedAttribute>();
		checked.add(pa);
		while (pa.getParent() != null){
			pa = pa.getParent();
			boolean loop = checked.contains(pa);
			checked.add(pa);
			if (loop)
				return checked;
		}
		return null;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
