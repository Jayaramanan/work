/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseSettingsService;

public class DatabaseSettingsServiceMock implements DatabaseSettingsService{

	public String getDatabaseName(){
		return "NI3";
	}

	@Override
	public List<DatabaseInstance> getDatabaseInstanceNames(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getCommonProperties(){
		// TODO Auto-generated method stub
		return null;
	}

}
