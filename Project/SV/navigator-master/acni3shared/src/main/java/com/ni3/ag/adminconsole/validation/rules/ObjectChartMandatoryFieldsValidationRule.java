/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectChartMandatoryFieldsValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		ChartModel model = (ChartModel) amodel;
		Chart ch = (Chart) model.getCurrentObject();
		for (ObjectChart oc : ch.getObjectCharts()){
			if (oc.getIsValueDisplayed() == null || oc.getLabelInUse() == null || oc.getChartType() == null
			        || oc.getDisplayOperation() == null || oc.getLabelFontSize() == null || oc.getMaxScale() == null
			        || oc.getMaxValue() == null || oc.getMinScale() == null || oc.getMinValue() == null
			        || oc.getNumberFormat() == null || "".equals(oc.getLabelFontSize()) || "".equals(oc.getNumberFormat())){
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
