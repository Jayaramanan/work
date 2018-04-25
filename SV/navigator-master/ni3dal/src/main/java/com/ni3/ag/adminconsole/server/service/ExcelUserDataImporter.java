/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import com.ni3.ag.adminconsole.validation.ACException;

public interface ExcelUserDataImporter{

	void importDataFromExcel(byte[] data, Integer schemaId, Integer userId, boolean recalculateFormulas) throws ACException;

}
