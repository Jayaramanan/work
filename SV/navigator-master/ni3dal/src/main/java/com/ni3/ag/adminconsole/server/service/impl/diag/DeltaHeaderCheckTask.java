/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.dao.DeltaHeaderDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.DeltaThreshold;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DeltaHeaderCheckTask extends HibernateDaoSupport implements DiagnosticTask{

	private static final String MY_DESCRIPTION = "Checking sys_delta_header table";
	private static final String TOOLTIP = "Number of records unprocessed: ";
	private final static String ACTION_DESCRIPTION = "Contact system administrator";

	private DeltaHeaderDAO deltaHeaderDAO;
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
	}

	@Override
	public String getTaskDescription(){
		return MY_DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		Integer recordCount = deltaHeaderDAO.getUnprocessedCount();
		String dbid = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		ACRoutingDataSource acds = (ACRoutingDataSource) dataSource;
		Map<String, InstanceDescriptor> instanceMap = acds.getDatasourceDescriptors();
		InstanceDescriptor id = instanceMap.get(dbid);
		DeltaThreshold dt = id.getDeltaThreshold();
		if (dt != null){
			if (dt.getWarningMaxRecords() < recordCount)
				return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Error, TOOLTIP
				        + recordCount, ACTION_DESCRIPTION);
			if (dt.getOkMaxRecords() < recordCount)
				return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Warning,
				        TOOLTIP + recordCount, null);
		}
		return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

}
