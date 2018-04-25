/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class SrcIdColumnCheckTask extends HibernateDaoSupport implements DiagnosticTask{

	private static final String MY_DESCRIPTION = "Checking that every node and edge have a value in srcid column";
	private static final String ERROR_SRC_ID_COLUMN_EXISTANCE = "One or more user tables does not have `srcid` column";

	private SchemaDAO schemaDAO;

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(final Schema sch){
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				List<String> usrTables = new ArrayList<String>();
				if (!getUserTableList(sch, usrTables))
					return BigInteger.valueOf(-1);
				if (usrTables.isEmpty())
					return BigInteger.valueOf(0);
				String select = "select count(\"id\") from (";
				for (int i = 0; i < usrTables.size(); i++){
					String usrTableName = usrTables.get(i);
					if (i > 0)
						select += " union ";
					select += "(select t" + i + ".id from " + usrTableName + " t" + i + " where (t" + i
					        + ".srcid is null or t" + i + ".srcid like '')) ";
				}
				select += ") as \"id\"";
				try{
					SQLQuery query = (SQLQuery) session.createSQLQuery(select);
					return query.uniqueResult();
				} catch (Exception ex){
					return BigInteger.valueOf(-1);
				}
			}
		};
		BigInteger count = (BigInteger) getHibernateTemplate().execute(callback);
		String descr = (count.longValue() == -1) ? MY_DESCRIPTION + "\n" + ERROR_SRC_ID_COLUMN_EXISTANCE : MY_DESCRIPTION;
		String tooltip = (count.longValue() == -1) ? ERROR_SRC_ID_COLUMN_EXISTANCE : null;
		DiagnoseTaskStatus stat = (count.longValue() == -1) ? DiagnoseTaskStatus.Error : (count.intValue() == 0
		        ? DiagnoseTaskStatus.Ok : DiagnoseTaskStatus.Warning);
		boolean fixable = (count.longValue() != -1);
		return new DiagnoseTaskResult(getClass().getName(), descr, fixable, stat, tooltip, null);
	}

	private boolean getUserTableList(Schema sch, List<String> usrTables){
		Hibernate.initialize(sch.getObjectDefinitions());
		List<ObjectDefinition> objects = sch.getObjectDefinitions();
		for (ObjectDefinition od : objects){
			Hibernate.initialize(od.getObjectAttributes());
			ObjectAttribute srcIdAttr = getSrcIdAttribute(od);
			if (srcIdAttr == null)
				return false;
			if (srcIdAttr.getInTable().startsWith("USR_"))
				usrTables.add(srcIdAttr.getInTable());
			else
				return false;
		}
		return true;
	}

	private ObjectAttribute getSrcIdAttribute(ObjectDefinition od){
		for (ObjectAttribute oa : od.getObjectAttributes()){
			if (oa.getName().equals(ObjectAttribute.SRCID_ATTRIBUTE_NAME))
				return oa;
		}
		return null;
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		final Object[] params = taskResult.getFixParams();
		final Schema sch = schemaDAO.getSchema((Integer) params[DiagnoseTaskResult.SCHEMA_ID_FIX_PARAM]);

		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				List<String> usrTables = new ArrayList<String>();
				getUserTableList(sch, usrTables);
				for (int i = 0; i < usrTables.size(); i++){
					String usrTable = usrTables.get(i);
					String update = "update " + usrTable + " set srcid = id || '_" + System.currentTimeMillis()
					        + "' where (srcid is null or srcid like '')";
					SQLQuery query = (SQLQuery) session.createSQLQuery(update);
					query.executeUpdate();
				}
				return 1;
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
