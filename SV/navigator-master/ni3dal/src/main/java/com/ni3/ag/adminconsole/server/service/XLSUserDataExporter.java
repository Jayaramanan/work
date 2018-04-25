/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import java.io.ByteArrayOutputStream;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface XLSUserDataExporter{

	ByteArrayOutputStream performAction(Schema sch, User u) throws ACException;

}
