/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartAttributesMandatoryFieldsValidationRuleTest extends TestCase{
	private ACValidationRule rule;

	@Override
	protected void setUp() throws Exception{
		rule = new ChartAttributesMandatoryFieldsValidationRule();
	}

	public void testPerformCheck(){
		ChartModel model = new ChartModel();
		ObjectChart ch = new ObjectChart();
		model.setCurrentObjectChart(ch);
		ch.setChartAttributes(new ArrayList<ChartAttribute>());

		ChartAttribute cca = new ChartAttribute();
		ch.getChartAttributes().add(cca);

		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		cca.setAttribute(new ObjectAttribute());
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		cca.setRgb("p");
		rule.performCheck(model);
		assertTrue(rule.getErrorEntries().isEmpty());

		cca.setAttribute(null);
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		cca.setAttribute(new ObjectAttribute());
		rule.performCheck(model);
		assertTrue(rule.getErrorEntries().isEmpty());

		cca.setRgb("");
		rule.performCheck(model);
		assertFalse(rule.getErrorEntries().isEmpty());

		cca.setRgb("a");
		rule.performCheck(model);
		assertTrue(rule.getErrorEntries().isEmpty());
	}

	public void testGetErrors(){
		ChartModel model = new ChartModel();
		ObjectChart ch = new ObjectChart();
		model.setCurrentObjectChart(ch);
		ch.setChartAttributes(new ArrayList<ChartAttribute>());
		ChartAttribute cca = new ChartAttribute();
		ch.getChartAttributes().add(cca);
		rule.performCheck(model);
		assertNotNull(rule.getErrorEntries());
	}

}
