/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.Map;

import javax.sql.DataSource;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class NavigatorHostPropertyTask implements DiagnosticTask{
	private static final String DESCRIPTION = "Checking if 'com.ni3.ag.adminconsole.instance<n>.navigator.host' exists in database.properties file";
	private static final String ACTION_DESCRIPTION = "Fill 'com.ni3.ag.adminconsole.instance<n>.navigator.host' in database.properties file and redeploy application";

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	private DataSource dataSource;

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		String dbid = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		ACRoutingDataSource acds = (ACRoutingDataSource) dataSource;
		Map<String, InstanceDescriptor> instanceMap = acds.getDatasourceDescriptors();
		InstanceDescriptor id = instanceMap.get(dbid);
		boolean problem = false;
		if (id.getNavigatorHost() == null || id.getNavigatorHost().isEmpty())
			problem = true;
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, problem ? DiagnoseTaskStatus.Warning
		        : DiagnoseTaskStatus.Ok, null, problem ? ACTION_DESCRIPTION : null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		throw new ACFixTaskException("Non fixable", "Non fixable");
	}

}
