/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.DuplicateObjectChartValidationRule;

import junit.framework.TestCase;

public class DuplicateObjectChartValidationRuleTest extends TestCase{
	public void testPerformCheck(){
		ChartModel model = new ChartModel();
		Chart ch = new Chart();
		model.setCurrentObject(ch);
		ch.setObjectCharts(new ArrayList<ObjectChart>());

		ObjectDefinition od1 = new ObjectDefinition();
		od1.setId(1);
		ObjectDefinition od2 = new ObjectDefinition();
		od2.setId(2);

		ObjectChart oc = new ObjectChart();
		oc.setObject(od1);
		ch.getObjectCharts().add(oc);
		oc = new ObjectChart();
		oc.setObject(od2);
		ch.getObjectCharts().add(oc);

		ACValidationRule rule = new DuplicateObjectChartValidationRule();
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());

		oc = new ObjectChart();
		oc.setObject(od1);
		ch.getObjectCharts().add(oc);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());

		ch.getObjectCharts().remove(2);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());
	}

}
