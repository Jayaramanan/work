/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dbservice.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.validation.ACException;

public class UserTableStructureServiceMock implements UserTableStructureService{

	@Override
	public void dropUserTables(List<String> tableNames) throws ACException{
		// TODO Auto-generated method stub
	}

	@Override
	public void renameUserTable(String oldTableName, String newTableName, boolean isCtxt){
		// TODO Auto-generated method stub
	}

	@Override
	public ErrorContainer updateUserTable(ObjectDefinition object){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorContainer updateUserTables(Schema schema){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExistPKConstraint(String tableName){
		return false;
	}

}
