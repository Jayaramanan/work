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
import com.ni3.ag.adminconsole.validation.rules.ObjectChartValidationRule;

import junit.framework.TestCase;

public class ObjectChartValidationRuleTest extends TestCase{

	public void testPerformCheck(){
		Chart ch = new Chart();
		ObjectChart oc = new ObjectChart();
		ch.setObjectCharts(new ArrayList<ObjectChart>());
		ch.getObjectCharts().add(oc);
		ACValidationRule rule = new ObjectChartValidationRule();
		ChartModel model = new ChartModel();
		model.setCurrentObject(ch);
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.size());

		oc.setObject(new ObjectDefinition());
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());

		oc.setObject(null);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());
		assertEquals(1, errors.size());
	}

	public void testGetErrors(){
		ACValidationRule rule = new ObjectChartValidationRule();
		ChartModel model = new ChartModel();
		Chart ch = new Chart();
		ObjectChart oc = new ObjectChart();
		List<ObjectChart> objectCharts = new ArrayList<ObjectChart>();
		objectCharts.add(oc);
		ch.setObjectCharts(objectCharts);
		model.setCurrentObject(ch);
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}

}
