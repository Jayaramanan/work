/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class DefaultSchemaCheckTask extends HibernateDaoSupport implements DiagnosticTask{

	private static final String MY_DESCRIPTION = "Checking user settings for default schema property";
	private static final String ACTION_DESCRIPTION = "Go to Users tab and for mandatory field `Schema` select corresponding schema from dropdown list";

	private static final String TOOLTIP = "Users with no default schema set: ";

	private static final String ACTION_SQL = "select count(u.id) from "
	        + " sys_user u left join sys_user_settings su on su.section ilike '" + UserSetting.APPLET_SECTION
	        + "' and su.prop ilike '" + UserSetting.SCHEME_PROPERTY
	        + "' and su.userid = u.id where u.id not in (select userid from sys_user_settings where prop ilike '"
	        + UserSetting.SCHEME_PROPERTY + "' and section ilike '" + UserSetting.APPLET_SECTION
	        + "' and cast (value as integer) in (select id from sys_schema))";

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

		if (count.intValue() > 0){
			return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Error, TOOLTIP
			        + count.intValue(), ACTION_DESCRIPTION);
		} else{
			return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
		}
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		return null;
	}

	@Override
	public String getTaskDescription(){
		return MY_DESCRIPTION;
	}

}
