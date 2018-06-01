/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class ObjectsTableCheckTask extends HibernateDaoSupport implements DiagnosticTask{
	private static final String MY_DESCRIPTION = "Checking objects table";
	private static final String TOOLTIP = "Objects without records in nodes or edges tables: ";
	private static final String ACTION_SQL = "select count(*) " + "from cis_objects "
	        + "where id not in(select id from cis_nodes t " + "where t.id=cis_objects.id "
	        + "union select id from cis_edges t " + "where t.id=cis_objects.id)";
	private static final String FIX_SQL = "delete from cis_objects " + "where id not in(select id " + "from cis_nodes t "
	        + "where t.id=cis_objects.id " + "union " + "select id from cis_edges t " + "where t.id=cis_objects.id)";

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		HibernateCallback callback = new HibernateCallback(){

			@Override
			public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(ACTION_SQL);
				return query.uniqueResult();
			}
		};
		BigInteger count = (BigInteger) getHibernateTemplate().execute(callback);
		int cnt = count.intValue();

		String errMsg = cnt == 0 ? null : TOOLTIP + cnt;
		return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, true, cnt == 0 ? DiagnoseTaskStatus.Ok
		        : DiagnoseTaskStatus.Error, errMsg, null);
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException{
		HibernateCallback callback = new HibernateCallback(){

			@Override
			public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(FIX_SQL);
				return query.executeUpdate();
			}
		};
		try{
			getHibernateTemplate().execute(callback);
			taskResult.setStatus(DiagnoseTaskStatus.Ok);
			return taskResult;
		} catch (Exception e){
			throw new ACFixTaskException(e.getClass().getName(), e.getMessage());
		}
	}

	@Override
	public String getTaskDescription(){
		return MY_DESCRIPTION;
	}
}
