/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserTableDataCheckTask implements DiagnosticTask{

	private final static String DESCRIPTION = "Checking that every row in user tables has corresponding row in objects table";
	private final static String TOOLTIP = "Tables: `%` have rows that are not present in object tables";
	private final static String NODES_TABLE = "cis_nodes";
	private final static String EDGES_TABLE = "cis_edges";
	private SchemaDAO schemaDAO;
	private DiagnosticTask nodeTableCheckTask;
	private SessionFactory factory;

	public void setFactory(SessionFactory factory){
		this.factory = factory;
	}

	public void setNodeTableCheckTask(DiagnosticTask nodeTableCheckTask){
		this.nodeTableCheckTask = nodeTableCheckTask;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		Schema schema = schemaDAO.getSchema(sch.getId());
		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		String tablesWithErrors = "";
		for (int i = 0; i < objects.size(); i++){
			ObjectDefinition obj = objects.get(i);
			String tableName = obj.getTableName().toLowerCase();
			String objectTypeTable = obj.isNode() ? NODES_TABLE : EDGES_TABLE;
			final String sql = "select count(ut.id) from " + tableName
			        + " ut where not exists (select id from cis_objects where id = ut.id) or not exists (select id from "
			        + objectTypeTable + " where id = ut.id)";
			SQLQuery query = factory.getCurrentSession().createSQLQuery(sql);
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
		for (int i = 0; i < objects.size(); i++){
			final ObjectDefinition obj = objects.get(i);

			String[] fixSql = new String[2];
			String tableName = obj.getTableName().toLowerCase();
			if (obj.isNode()){
				fixSql[0] = "insert into cis_nodes (id, nodetype) select ut.id, " + obj.getId() + " from " + tableName
				        + " ut where not exists (select id from cis_nodes where id = ut.id)";
				fixSql[1] = "insert into cis_objects (id, objecttype, userid, creator, status) select id, nodetype, "
				        + params[0] + ", " + params[0] + ", 0 from cis_nodes where nodetype = " + obj.getId()
				        + " and not exists (select id from cis_objects t where t.id=cis_nodes.id)";
			} else{
				fixSql[0] = "delete from " + tableName + " ut where not exists (select id from cis_edges where id = ut.id)";
				fixSql[1] = "delete from cis_objects o where o.objecttype = " + obj.getId()
				        + " and not exists (select id from cis_edges where id = o.id)";
			}

			for (String sql : fixSql){
				SQLQuery query = factory.getCurrentSession().createSQLQuery(sql);
				try{
					query.executeUpdate();
				} catch (Exception e){
					throw new ACFixTaskException(e.getClass().getName(), e.getMessage());
				}
			}
		}
		nodeTableCheckTask.makeFix(taskResult);
		taskResult.setStatus(DiagnoseTaskStatus.Ok);
		return taskResult;
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

}
