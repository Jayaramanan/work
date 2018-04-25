/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.script;

import java.util.Map;

import com.ni3.ag.adminconsole.domain.FormulaHolder;

public interface ScriptDataAdapter{
	public FormulaHolder getFormula();

	public Map<?, ?> getData();
}
