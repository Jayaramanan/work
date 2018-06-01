/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

import junit.framework.TestCase;

public class LanguageNameValidationRuleTest extends TestCase{
	public void testNotNull(){
		ACValidationRule rule = new LanguageNameValidationRule();
		rule.performCheck(new LanguageModel());
		assertNotNull(rule.getErrorEntries());
	}

}
