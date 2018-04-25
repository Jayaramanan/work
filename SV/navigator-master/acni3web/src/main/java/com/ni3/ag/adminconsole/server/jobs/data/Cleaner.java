/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jobs.data;

import com.ni3.ag.adminconsole.validation.ACException;

public interface Cleaner{
	public void cleanData() throws ACException;
}
