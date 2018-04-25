/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DefaultMetaphorSetCheckTask extends HibernateDaoSupport implements DiagnosticTask{

	private static final String MY_DESCRIPTION = "Checking schema for default metaphor set";
	private final static String TOOLTIP = "Default metaphor set is not defined for object(s): ";
	private static final String ACTION_DESCRIPTION = "Go to Metaphors tab and add record(s) for 'Default' metaphor set for object(s): ";

	private static final String METAPHORSET_SQL = "select name from sys_object where objecttypeid = "
	        + ObjectType.NODE.toInt() + " and schemaid = ?"
	        + " and id not in (select objectdefinitionid from sys_metaphor where schemaid = ? and metaphorset = 'Default')";

	@Override
	@SuppressWarnings("unchecked")
	public DiagnoseTaskResult makeDiagnose(final Schema sch){
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(METAPHORSET_SQL);
				query.setInteger(0, sch.getId());
				query.setInteger(1, sch.getId());
				List<String> objectNames = (List<String>) query.list();
				return objectNames;
			}

		};
		List<String> result = (List<String>) getHibernateTemplate().execute(callback);
		DiagnoseTaskResult res = null;
		if (result != null && !result.isEmpty()){
			String objects = "";
			for (int i = 0; i < result.size(); i++){
				if (i > 0)
					objects += ", ";
				objects += result.get(i);
			}
			res = new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Error, TOOLTIP
			        + objects, ACTION_DESCRIPTION + objects);
		} else{
			res = new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
		}
		return res;
	}

	@Override
	public DiagnoseTaskResult makeFix(final DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

	@Override
	public String getTaskDescription(){
		return MY_DESCRIPTION;
	}
}
