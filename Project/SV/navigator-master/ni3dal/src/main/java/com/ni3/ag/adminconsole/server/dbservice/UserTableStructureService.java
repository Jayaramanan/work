/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dbservice;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.validation.ACException;

public interface UserTableStructureService{

	ErrorContainer updateUserTables(Schema schema);

	ErrorContainer updateUserTable(ObjectDefinition object);

	void dropUserTables(List<String> tableNames) throws ACException;

	void renameUserTable(String oldTableName, String newTableName, boolean isCtxt);

	boolean isExistPKConstraint(String tableName);

}
