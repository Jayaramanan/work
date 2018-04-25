/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ObjectChartMandatoryFieldsValidationRule;

public class ObjectChartMandatoryFieldsValidationRuleTest extends TestCase{
	public void testPerformCheck(){
		ACValidationRule rule = new ObjectChartMandatoryFieldsValidationRule();
		ChartModel model = new ChartModel();
		Chart ch = new Chart();
		model.setCurrentObject(ch);
		ch.setObjectCharts(new ArrayList<ObjectChart>());
		ObjectChart oc = new ObjectChart();
		ch.getObjectCharts().add(oc);

		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setChartType(ChartType.PIE);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setDisplayOperation(ChartDisplayOperation.SUM);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setIsValueDisplayed(false);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setLabelFontSize("a");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setLabelInUse(false);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setMaxScale(new BigDecimal(0));
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setMaxValue(0);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setMinScale(new BigDecimal(0));
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setMinValue(1);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setNumberFormat("b");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertTrue(errors.isEmpty());

		oc.setLabelFontSize("");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());

		oc.setLabelFontSize("a");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertTrue(errors.isEmpty());

		oc.setNumberFormat("");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertFalse(errors.isEmpty());
	}

}
