/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import com.ni3.ag.adminconsole.validation.ACException;

public interface DatabaseVersionService{
	public void checkDatabaseVersion() throws ACException;

	public String getExpectedVersion();

	public String getActualVersion();
}
