/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service;

import java.util.List;

import com.ni3.ag.adminconsole.validation.ACException;

public interface CSVUserDataImporter{

	void importDataFromCSV(List<String> lines, Integer schemaId, Integer userId, String fileName, String columnSeparator,
			boolean recalculateFormulas) throws ACException;

}
