/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import com.ni3.ag.adminconsole.validation.ACException;

public interface CalculateFormulaService{
	public void calculateFormulaValue(Integer attributeId) throws ACException;
}
