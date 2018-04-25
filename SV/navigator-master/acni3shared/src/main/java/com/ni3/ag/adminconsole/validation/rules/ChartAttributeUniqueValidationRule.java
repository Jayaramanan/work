/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartAttributeUniqueValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		ChartModel model = (ChartModel) amodel;
		ObjectChart oc = (ObjectChart) model.getCurrentObjectChart();
		if (oc == null || oc.getChartAttributes() == null || oc.getChartAttributes().isEmpty())
			return true;
		for (ChartAttribute cca : oc.getChartAttributes()){
			for (ChartAttribute cca1 : oc.getChartAttributes()){
				if (cca != cca1 && cca.getAttribute().equals(cca1.getAttribute())){
					errors.add(new ErrorEntry(TextID.MsgDuplicateChartAttributeName, new String[] { cca.getAttribute()
					        .getName() }));
					return false;
				}
			}
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
