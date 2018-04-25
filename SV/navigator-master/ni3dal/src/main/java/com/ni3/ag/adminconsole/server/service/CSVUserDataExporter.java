/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface CSVUserDataExporter{

	byte[] performAction(ObjectDefinition od, User u, String columnSeparator, String lineSeparator) throws ACException;

}
