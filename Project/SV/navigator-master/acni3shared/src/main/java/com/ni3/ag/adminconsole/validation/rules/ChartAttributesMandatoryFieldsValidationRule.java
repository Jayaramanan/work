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

public class ChartAttributesMandatoryFieldsValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		ChartModel model = (ChartModel) amodel;
		ObjectChart ch = (ObjectChart) model.getCurrentObjectChart();
		if (ch == null || ch.getChartAttributes() == null){
			return true;
		}
		for (ChartAttribute cca : ch.getChartAttributes()){
			if (cca.getAttribute() == null || cca.getRgb() == null || cca.getRgb().isEmpty()){
				errors.add(new ErrorEntry(TextID.MsgNotAllRequeredFieldsHasValues));
				break;
			}
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
