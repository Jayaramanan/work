/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DuplicateObjectChartValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel m){
		errors = new ArrayList<ErrorEntry>();
		ChartModel model = (ChartModel) m;
		Chart ch = (Chart) model.getCurrentObject();
		Set<Integer> usedObjects = new HashSet<Integer>();
		for (ObjectChart oc : ch.getObjectCharts()){
			if (usedObjects.contains(oc.getObject().getId())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateObjectChart, new String[] { oc.getObject().getName() }));
				break;
			}
			usedObjects.add(oc.getObject().getId());
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
