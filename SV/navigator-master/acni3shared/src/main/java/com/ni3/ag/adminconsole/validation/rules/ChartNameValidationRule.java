/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartNameValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;
	private static Logger log = Logger.getLogger(ChartNameValidationRule.class);

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();
		ChartModel chartModel = (ChartModel) model;
		Object o = chartModel.getCurrentObject();
		Schema schema = null;

		if (o == null)
			return true;
		else if (o instanceof Schema)
			schema = (Schema) o;
		else if (o instanceof Chart)
			schema = ((Chart) o).getSchema();

		List<Chart> charts = schema.getCharts();
		String newChartName = chartModel.getNewChartName();
		for (Chart c : charts){
			log.debug("COMPARE: " + c.getName() + " == " + newChartName + " "
			        + c.getName().equalsIgnoreCase(newChartName));
			if (c.getName().equalsIgnoreCase(newChartName)){
				errors.add(new ErrorEntry(TextID.MsgDuplicateChartName));
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
