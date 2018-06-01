/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class CascadeConstraintCheckTask implements DiagnosticTask{
	private final static String DESCRIPTION = "Checking constraints for cascade option";
	private final static String TOOLTIP = "No constraint or incorrect cascade option for tables: ";

	private List<CheckTable> toCheck;

	private SessionFactory factory;

	public void setFactory(SessionFactory factory){
		this.factory = factory;
	}

	CascadeConstraintCheckTask(){
		toCheck = new ArrayList<CheckTable>();
		toCheck.add(new CheckTable("geo_thematiccluster", "thematicmapid", "geo_thematicmap", "id",
				"fk_geo_thematiccluster_geo_thematicmap", "cascade"));
		toCheck.add(new CheckTable("geo_thematicfolder", "schemaid", "sys_schema", "id", "fk_geo_thematicfolder_sys_schema",
				"cascade"));
		toCheck.add(new CheckTable("geo_thematicmap", "layerid", "gis_territory", "id", "fk_geo_thematicmap_gis_territory",
				"cascade"));
		toCheck.add(new CheckTable("geo_thematicmap", "folderid", "geo_thematicfolder", "id",
				"fk_geo_thematicmap_geo_thematicfolder", "set null"));
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		final List<CheckTable> toFix = getTablesToFix();
		final DiagnoseTaskResult taskResult = getTaskResult(toFix);
		return taskResult;
	}

	private List<CheckTable> getTablesToFix(){
		List<CheckTable> toFix = new ArrayList<CheckTable>();
		final String sql = "SELECT constraint_name, delete_rule FROM information_schema.referential_constraints "
				+ "WHERE constraint_schema = current_schema() AND lower(constraint_name) = ?";
		SQLQuery query = factory.getCurrentSession().createSQLQuery(sql);
		for (CheckTable table : toCheck){
			query.setString(0, table.constraintName);
			Object[] result = (Object[]) query.uniqueResult();
			if (result != null && result.length == 2){
				if (result[1] == null || !((String) result[1]).equalsIgnoreCase(table.option)){
					toFix.add(table);
					table.exists = true;
				}
			} else{
				toFix.add(table);
				table.exists = false;
			}
		}
		return toFix;
	}

	private DiagnoseTaskResult getTaskResult(List<CheckTable> toFix){
		DiagnoseTaskResult result = null;
		if (!toFix.isEmpty()){
			String tooltip = TOOLTIP;
			for (int i = 0; i < toFix.size(); i++){
				CheckTable table = toFix.get(i);
				if (i > 0){
					tooltip += ", ";
				}
				tooltip += table.tableName + "(" + table.columnName + ")";
			}
			result = new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, DiagnoseTaskStatus.Error, tooltip,
					null);
		} else{
			result = new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
		}
		return result;
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		final List<CheckTable> toFix = getTablesToFix();
		try{
			if (!toFix.isEmpty()){
				for (CheckTable table : toFix){
					if (table.exists){
						final String dropSql = "ALTER TABLE " + table.tableName + " DROP CONSTRAINT " + table.constraintName;
						final SQLQuery dropQuery = factory.getCurrentSession().createSQLQuery(dropSql);
						dropQuery.executeUpdate();
					}
					final String addSql = "ALTER TABLE " + table.tableName + " ADD CONSTRAINT " + table.constraintName
							+ " FOREIGN KEY (" + table.columnName + ") REFERENCES " + table.refTableName + " ("
							+ table.refColumnName + ") MATCH SIMPLE ON UPDATE NO ACTION ON DELETE " + table.option;
					final SQLQuery addQuery = factory.getCurrentSession().createSQLQuery(addSql);
					addQuery.executeUpdate();
				}
			}

			taskResult.setStatus(DiagnoseTaskStatus.Ok);
			return taskResult;
		} catch (Exception e){
			throw new ACFixTaskException(e.getClass().getName(), e.getMessage());
		}
	}

	private class CheckTable{
		String tableName;
		String columnName;
		String refTableName;
		String refColumnName;
		String constraintName;
		String option;
		boolean exists;

		public CheckTable(String tableName, String columnName, String refTableName, String refColumnName,
				String constraintName, String option){
			super();
			this.tableName = tableName;
			this.columnName = columnName;
			this.refTableName = refTableName;
			this.refColumnName = refColumnName;
			this.constraintName = constraintName;
			this.option = option;
		}

	}
}