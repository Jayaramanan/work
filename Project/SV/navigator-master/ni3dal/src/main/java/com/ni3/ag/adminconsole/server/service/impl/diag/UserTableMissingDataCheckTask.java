/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

public class UserTableMissingDataCheckTask implements DiagnosticTask{
	private static final Logger log = Logger.getLogger(UserTableMissingDataCheckTask.class);
	private final static String DESCRIPTION = "Checking that every row in objects table has corresponding row in user table";
	private final static String TOOLTIP = "There are missing rows in user tables: `%`";
	private SchemaDAO schemaDAO;
	private SessionFactory factory;

	public void setFactory(SessionFactory factory){
		this.factory = factory;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		Schema schema = schemaDAO.getSchema(sch.getId());
		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		String tablesWithErrors = "";
		for (ObjectDefinition obj : objects){
			String tableName = obj.getTableName().toLowerCase();
			final String sql = "select count(ot.id) from cis_objects ot where ot.objecttype = ? and not exists (select id from "
					+ tableName + " where id = ot.id)";
			SQLQuery query = factory.getCurrentSession().createSQLQuery(sql);
			query.setInteger(0, obj.getId());
			BigInteger count = (BigInteger) query.uniqueResult();
			if (count.intValue() > 0){
				if (!tablesWithErrors.isEmpty())
					tablesWithErrors += ", ";
				tablesWithErrors += tableName;
			}
		}
		if (tablesWithErrors.isEmpty())
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
		else{
			String tooltip = TOOLTIP.replaceAll("%", tablesWithErrors);
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, DiagnoseTaskStatus.Error, tooltip, null);
		}
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		final Object[] params = taskResult.getFixParams();
		Integer schemaId = (Integer) params[1];
		Schema schema = schemaDAO.getSchema(schemaId);
		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		for (final ObjectDefinition obj : objects){
			processUserTable(obj);
		}
		taskResult.setStatus(DiagnoseTaskStatus.Ok);
		return taskResult;
	}

	private void processUserTable(ObjectDefinition obj) throws ACFixTaskException{
		String tableName = obj.getTableName().toLowerCase();
		String selectSql = "select ot.id from cis_objects ot where ot.objecttype = " + obj.getId() + " and not exists (select id from "
				+ tableName + " where id = ot.id)";
		List<String> fixSQLs = new ArrayList<String>();
		if (obj.isNode()){
			String selectEdgesToDelete = "select id from cis_edges where fromid in (" + selectSql + ") or toid in (" + selectSql + ")";
			for (ObjectDefinition od : obj.getSchema().getObjectDefinitions())
				if (od.isEdge())
					fixSQLs.add("delete from " + od.getTableName() + " where id in (" + selectEdgesToDelete + ")");
			fixSQLs.add("delete from cis_edges where fromid in (" + selectSql + ") or toid in (" + selectSql + ")");
			fixSQLs.add("delete from cis_nodes where id in (" + selectSql + ")");
		} else
			fixSQLs.add("delete from cis_edges where id in (" + selectSql + ")");
		fixSQLs.add("delete from cis_objects ot where ot.objecttype = " + obj.getId() + " and not exists (select id from "
				+ tableName + " where id = ot.id)");

		try{
			for (String sql : fixSQLs){
				SQLQuery query = factory.getCurrentSession().createSQLQuery(sql);
				log.debug(sql);
				query.executeUpdate();
			}
		} catch (Exception e){
			throw new ACFixTaskException(e.getClass().getName(), e.getMessage());
		}

	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

}
