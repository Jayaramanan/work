/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.util.IntParser;
import com.ni3.ag.adminconsole.util.OfflineObjectId;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class SequenceValidityCheckTask extends HibernateDaoSupport implements DiagnosticTask{

	private static final String DESCRIPTION = "Checking validity of sequences";
	private static final String TOOLTIP = "Sequence does not exist: ";
	private static final String TOOLTIP_WRONG = "Sequence last value is less than maximum id in a table ";

	private static final Integer DIAGNOSE_RESULT_OK = new Integer(0);
	private static final Integer DIAGNOSE_RESULT_WRONG = new Integer(1);

	private static final Hashtable<String, String> TABLE_SEQUENCES = new Hashtable<String, String>();
	private static final Hashtable<String, String> OFFLINE_TABLE_SEQUENCES = new Hashtable<String, String>();

	static{
		OFFLINE_TABLE_SEQUENCES.put("cis_favorites", "seq_cis_favorites");
		OFFLINE_TABLE_SEQUENCES.put("cis_objects", "seq_objectcount");
		OFFLINE_TABLE_SEQUENCES.put("cis_favorites_folder", "seq_user_favorites_folder");
		OFFLINE_TABLE_SEQUENCES.put("geo_thematicmap", "geo_thematicmap_id_seq");
		OFFLINE_TABLE_SEQUENCES.put("geo_thematiccluster", "geo_thematiccluster_id_seq");
		OFFLINE_TABLE_SEQUENCES.put("geo_thematicfolder", "geo_thematicfolder_id_seq");
	}

	static{
		TABLE_SEQUENCES.put("cht_icons", "cht_icons_id_seq");
		TABLE_SEQUENCES.put("cht_predefinedattributes", "cht_predefinedattributes_id_seq");
		TABLE_SEQUENCES.put("sys_chart_attribute", "sys_chart_attribute_id_seq");
		TABLE_SEQUENCES.put("gis_map", "gis_map_id_seq");
		TABLE_SEQUENCES.put("sys_chart", "seq_cht_chart");
		TABLE_SEQUENCES.put("cht_language", "seq_cht_language");
		TABLE_SEQUENCES.put("sys_metaphor", "sys_metaphor_id_seq");
		TABLE_SEQUENCES.put("sys_metaphor_data", "sys_metaphor_data_keyid_seq");
		TABLE_SEQUENCES.put("sys_object_connection", "seq_sys_object_connection");
		TABLE_SEQUENCES.put("sys_user_activity", "seq_user_activity");
		TABLE_SEQUENCES.put("sys_chart_job", "sys_chart_job_id_seq");
		TABLE_SEQUENCES.put("sys_group", "sys_group_id_seq");
		TABLE_SEQUENCES.put("sys_object_attributes", "sys_object_attributes_id_seq");
		TABLE_SEQUENCES.put("sys_object_chart", "sys_object_chart_id_seq");
		TABLE_SEQUENCES.put("sys_offline_job", "sys_offline_job_id_seq");
		TABLE_SEQUENCES.put("sys_schema", "sys_schema_object_id_seq");
		TABLE_SEQUENCES.put("sys_object", "sys_schema_object_id_seq");
		TABLE_SEQUENCES.put("sys_user", "sys_user_id_seq");
		TABLE_SEQUENCES.put("sys_context_attributes", "sys_context_attributes_id_seq");
		TABLE_SEQUENCES.put("sys_context", "sys_context_id_seq");
		TABLE_SEQUENCES.put("sys_chart_group", "sys_chart_group_id_seq");
		TABLE_SEQUENCES.put("sys_delta_header", "sys_delta_header_id_seq");
		TABLE_SEQUENCES.put("sys_delta_user", "sys_delta_user_id_seq");
		TABLE_SEQUENCES.put("sys_delta_params", "sys_delta_params_id_seq");
		TABLE_SEQUENCES.put("sys_formula", "sys_formula_id_seq");
		TABLE_SEQUENCES.put("sys_group_prefilter", "sys_group_prefilter_id_seq");
		TABLE_SEQUENCES.put("sys_licenses", "sys_licenses_id_seq");
		TABLE_SEQUENCES.put("sys_map_job", "sys_map_job_id_seq");
		TABLE_SEQUENCES.put("sys_module_list", "sys_module_list_id_seq");
		TABLE_SEQUENCES.put("sys_module_user", "sys_module_user_id_seq");
		TABLE_SEQUENCES.put("sys_report_template", "sys_report_template_id_seq");
		TABLE_SEQUENCES.put("sys_user_data_error", "sys_user_data_error_id_seq");
		TABLE_SEQUENCES.put("sys_user_edition", "sys_user_edition_id_seq");
		TABLE_SEQUENCES.put("gis_territory", "gis_territory_id_seq");
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

	private Integer checkOfflineSequences(Session session) throws SQLGrammarException{
		Integer result = DIAGNOSE_RESULT_OK;
		for (String tableName : OFFLINE_TABLE_SEQUENCES.keySet()){
			String tableIdSql = "select coalesce(max(id),0) from " + tableName + " where id < "
			        + OfflineObjectId.OFFLINE_OBJECT_START_ID.getResult();
			String sequenceSql = "select last_value from " + OFFLINE_TABLE_SEQUENCES.get(tableName);

			Query tableIdQuery = session.createSQLQuery(tableIdSql);
			Query sequenceQuery = session.createSQLQuery(sequenceSql);
			Integer maxTableId = (Integer) tableIdQuery.uniqueResult();

			BigInteger sequenceValue = (BigInteger) sequenceQuery.uniqueResult();
			if (maxTableId > sequenceValue.intValue()){
				result = DIAGNOSE_RESULT_WRONG;
				break;
			}
		}
		return result;
	}

	private void fixOfflineSequences(Session session) throws SQLGrammarException{
		for (String tableName : OFFLINE_TABLE_SEQUENCES.keySet()){
			String tableIdSql = "select coalesce(max(id),0) from " + tableName + " where id < "
			        + OfflineObjectId.OFFLINE_OBJECT_START_ID.getResult();
			String sequenceSql = "select last_value from " + OFFLINE_TABLE_SEQUENCES.get(tableName);
			Query tableIdQuery = session.createSQLQuery(tableIdSql);
			Query sequenceQuery = session.createSQLQuery(sequenceSql);
			Integer maxTableId = (Integer) tableIdQuery.uniqueResult();

			BigInteger sequenceValue = (BigInteger) sequenceQuery.uniqueResult();
			if (maxTableId.intValue() > sequenceValue.intValue()){
				String fixSql = "select setval('" + OFFLINE_TABLE_SEQUENCES.get(tableName) + "', "
				        + (maxTableId.intValue() + 1) + ")";
				Query fixQuery = session.createSQLQuery(fixSql);
				fixQuery.uniqueResult();
			}
		}
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		final Enumeration<String> tables = TABLE_SEQUENCES.keys();

		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Object result = DIAGNOSE_RESULT_OK;
				while (tables.hasMoreElements()){
					String tableName = tables.nextElement();
					String sequenceName = TABLE_SEQUENCES.get(tableName);

					String tableIdSql = "select coalesce(max(id),0) from " + tableName;
					String sequenceSql = "select last_value from " + sequenceName;

					Query tableIdQuery = session.createSQLQuery(tableIdSql);
					Query sequenceQuery = session.createSQLQuery(sequenceSql);

					Integer maxTableId = IntParser.getInt(tableIdQuery.uniqueResult());

					try{
						BigInteger sequenceValue = (BigInteger) sequenceQuery.uniqueResult();
						if (maxTableId.intValue() > sequenceValue.intValue()){
							result = DIAGNOSE_RESULT_WRONG;
							break;
						}
					} catch (SQLGrammarException e){
						return sequenceName;
					}
				}

				if (DIAGNOSE_RESULT_OK.equals(result)){
					try{
						result = checkOfflineSequences(session);
					} catch (SQLGrammarException e){
						return OFFLINE_TABLE_SEQUENCES;
					}
				}

				return result;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		DiagnoseTaskStatus status;
		if (result.equals(DIAGNOSE_RESULT_OK)){
			status = DiagnoseTaskStatus.Ok;
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, status, null, null);
		} else if (result.equals(DIAGNOSE_RESULT_WRONG)){
			status = DiagnoseTaskStatus.Error;
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, status, TOOLTIP_WRONG, null);
		} else{ // no sequence
			status = DiagnoseTaskStatus.Warning;
			return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, true, status, TOOLTIP + String.valueOf(result),
			        null);
		}

	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		final Enumeration<String> tables = TABLE_SEQUENCES.keys();
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String result = null;
				while (tables.hasMoreElements()){
					String tableName = tables.nextElement();
					String sequenceName = TABLE_SEQUENCES.get(tableName);

					String tableIdSql = "select coalesce(max(id),0) from " + tableName;
					String sequenceSql = "select last_value from " + sequenceName;

					Query tableIdQuery = session.createSQLQuery(tableIdSql);
					Query sequenceQuery = session.createSQLQuery(sequenceSql);

					Integer maxTableId = IntParser.getInt(tableIdQuery.uniqueResult());
					try{
						BigInteger sequenceValue = (BigInteger) sequenceQuery.uniqueResult();

						if (maxTableId.intValue() > sequenceValue.intValue()){
							String fixSql = "select setval('" + sequenceName + "', " + (maxTableId.intValue() + 1) + ")";
							Query fixQuery = session.createSQLQuery(fixSql);
							fixQuery.uniqueResult();
						}
					} catch (SQLGrammarException e){
						return sequenceName;
					}
				}

				try{
					fixOfflineSequences(session);
				} catch (SQLGrammarException e){
					return OFFLINE_TABLE_SEQUENCES;
				}

				return result;
			}
		};
		try{
			Object result = getHibernateTemplate().execute(callback);
			if (result != null)
				throw new SQLException(TOOLTIP + result);
			taskResult.setStatus(DiagnoseTaskStatus.Ok);
			return taskResult;
		} catch (Exception e){
			throw new ACFixTaskException(e.getClass().getName(), e.getMessage());
		}
	}

}
