/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */ package com.ni3.ag.adminconsole.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;

public class PredefAttributeValidationRuleTest extends ACTestCase{
	PredefAttributeValidationRule rule;
	PredefinedAttribute pa1;
	PredefinedAttribute pa2;

	@Override
	protected void setUp() throws Exception{
		List<PredefinedAttribute> predefinedAttributes = new ArrayList<PredefinedAttribute>();
		pa1 = new PredefinedAttribute();
		pa1.setValue("value1");
		pa1.setLabel("label1");
		pa2 = new PredefinedAttribute();
		pa2.setValue("value2");
		pa2.setLabel("label2");

		predefinedAttributes.add(pa1);
		predefinedAttributes.add(pa2);

		rule = new PredefAttributeValidationRule(predefinedAttributes);
	}

	public void testPerformCheckSuccess(){
		assertNull(rule.performCheck());
	}

	public void testPerformCheckValueEmpty(){
		pa2.setValue(null);
		assertNotNull(rule.performCheck());

		pa2.setValue("");
		assertNotNull(rule.performCheck());
	}

	public void testPerformCheckLabelEmpty(){
		pa2.setLabel(null);
		assertNotNull(rule.performCheck());

		pa2.setLabel("");
		assertNotNull(rule.performCheck());
	}

}
