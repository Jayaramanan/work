/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ChartNameValidationRule;

public class ChartNameValidationRuleTest extends TestCase{

	public void testPerformCheck(){
		ACValidationRule rule = new ChartNameValidationRule();
		Schema schema = new Schema();
		schema.setCharts(new ArrayList<Chart>());

		Chart ch1 = new Chart();
		ch1.setName("ch1");
		ch1.setSchema(schema);
		schema.getCharts().add(ch1);

		Chart ch2 = new Chart();
		ch2.setName("ch2");
		ch2.setSchema(schema);
		schema.getCharts().add(ch2);

		ChartModel model = new ChartModel();
		model.setNewChartName("ch1");
		model.setCurrentObject(schema);
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());

		model.setCurrentObject(ch1);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());

		model.setNewChartName("ch3");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());

		model.setNewChartName("ch2");
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());
	}

	public void testGetErrors(){
		ACValidationRule rule = new ChartNameValidationRule();
		ChartModel model = new ChartModel();
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}
}
