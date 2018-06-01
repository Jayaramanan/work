/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;

public class ChartSelectionValidationRuleTest extends TestCase{
	private ChartSelectionValidationRule rule;
	private ChartModel model;

	@Override
	protected void setUp() throws Exception{
		rule = new ChartSelectionValidationRule();
		model = new ChartModel();
	}

	public void testPerformCheckSuccess(){
		model.setCurrentObject(new Chart());
		assertTrue(rule.performCheck(model));
		assertEquals(0, rule.getErrorEntries().size());
	}

	public void testPerformCheckFail(){
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());

		model.setCurrentObject(new Schema());
		assertFalse(rule.performCheck(model));
		assertEquals(1, rule.getErrorEntries().size());
	}
}
