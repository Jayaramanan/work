/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface ExcelSchemaImporter{
	void importExcelSchema(byte[] data, String schemaName, User user) throws ACException;
}
