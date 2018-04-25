/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartAttributeUniqueValidationRuleTest extends TestCase{
	public void testGetErrors(){
		ACValidationRule rule = new ChartAttributeUniqueValidationRule();
		ChartModel model = new ChartModel();
		ObjectChart oc = new ObjectChart();
		model.setCurrentObjectChart(oc);
		oc.setChartAttributes(new ArrayList<ChartAttribute>());
		ObjectAttribute attr = new ObjectAttribute();
		attr.setId(1);
		attr.setName("name1");
		ChartAttribute cca = new ChartAttribute();
		cca.setAttribute(attr);
		oc.getChartAttributes().add(cca);
		rule.performCheck(model);
		List<ErrorEntry> errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());

		ObjectAttribute attr2 = new ObjectAttribute();
		attr2.setId(2);
		attr2.setName("name2");
		cca = new ChartAttribute();
		cca.setAttribute(attr2);
		oc.getChartAttributes().add(cca);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());

		ObjectAttribute attr3 = new ObjectAttribute();
		attr3.setId(1);
		attr3.setName("name1");
		cca = new ChartAttribute();
		cca.setAttribute(attr3);
		oc.getChartAttributes().add(cca);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertFalse(errors.isEmpty());

		oc.getChartAttributes().remove(cca);
		rule.performCheck(model);
		errors = rule.getErrorEntries();
		assertNotNull(errors);
		assertTrue(errors.isEmpty());

	}

	public void testPerformCheck(){
	}
}
