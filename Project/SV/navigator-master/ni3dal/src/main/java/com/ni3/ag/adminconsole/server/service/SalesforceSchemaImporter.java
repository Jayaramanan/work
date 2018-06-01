/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service;

import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.validation.ACException;

public interface SalesforceSchemaImporter{

	Map<String, List<String>> getAvailableSalesforceTabs(String url, String username, String password) throws ACException;

	Schema importSchema(String schemaName, List<String> objectNames, int userId, String sfUrl, String sfUsername,
			String sfPassword) throws ACException;

}
