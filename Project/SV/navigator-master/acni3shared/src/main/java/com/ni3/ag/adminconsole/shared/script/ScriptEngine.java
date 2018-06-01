/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.script;

import com.ni3.ag.adminconsole.validation.ACException;

public interface ScriptEngine{
	public Object calculateSliceValueForNode(ScriptDataAdapter sda) throws ACException;

	public void dispose();

	public Object calculateValue(ScriptDataAdapter sda) throws ACException;
}
