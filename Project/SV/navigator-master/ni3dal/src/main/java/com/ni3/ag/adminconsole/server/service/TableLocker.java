/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

public interface TableLocker{
	boolean lockTables(String[] names);
}
