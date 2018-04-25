/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dbservice.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ErrorContainerImpl;

public class PGUserTableStructureServiceImpl extends HibernateDaoSupport implements UserTableStructureService{
	private static final Logger log = Logger.getLogger(PGUserTableStructureServiceImpl.class);
	private static final String INTEGER_DATA_TYPE = "integer";
	private static final String INT4_DB_TYPE = "int4";
	private static final String NUMERIC_DATA_TYPE = "numeric";
	private static final String TEXT_DATA_TYPE = "text";

	private static final String ID_COLUMN = "id";
	private static final String EDGES_TABLE = "cis_edges";
	private static final String CONSTRAINT_SQL = "SELECT constraint_name FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE WHERE column_name = 'id' AND constraint_schema = current_schema() AND lower(table_name) = lower(?)";

	private ACRoutingDataSource dataSource;

	public void setDataSource(ACRoutingDataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public ErrorContainer updateUserTables(Schema schema){
		ErrorContainerImpl result = new ErrorContainerImpl();
		for (ObjectDefinition object : schema.getObjectDefinitions()){
			ErrorContainer ec = updateUserTable(object);
			result.addAllErrors(ec.getErrors());
		}
		return result;
	}

	@Override
	public ErrorContainer updateUserTable(ObjectDefinition object){
		ErrorContainerImpl result = new ErrorContainerImpl();
		final String tableName = object.getTableName();
		List<ErrorEntry> errors = checkUserTable(tableName, object, false);
		if (errors != null && !errors.isEmpty())
			result.addAllErrors(errors);

		String ctxTableName = tableName + ObjectAttribute.CONTEXT_TABLE_SUFFIX;
		if (object.hasContextAttributes()){
			errors = checkUserTable(ctxTableName, object, true);
			if (errors != null && !errors.isEmpty())
				result.addAllErrors(errors);
		} else{
			// Executing drop table if exists for ctx table when there are no context attributes.
			// The code does not check if the table actually exists in database because the statement has small
			// overhead.
			executeUpdate(getDropTableSql(ctxTableName));
		}
		return result;
	}

	protected List<ErrorEntry> checkUserTable(String tableName, ObjectDefinition object, boolean isCtxt){
		List<DBColumn> dbColumns = getDatabaseColumns(tableName);
		if (dbColumns.isEmpty()){
			// create new usr_ table
			executeUpdate(getCreateUserTableSql(tableName, isCtxt));
			if (isCtxt){
				dbColumns = getDatabaseColumns(tableName);
			}
		} else{
			// remove excessive columns from usr_ table
			checkExcessiveColumns(tableName, object, dbColumns);
		}

		// add missing columns to usr_ table
		checkMissingColumns(tableName, object, dbColumns);

		// change column types if changed
		if (dbColumns != null)
			return checkDataTypes(tableName, object, dbColumns);
		return null;
	}

	private List<ErrorEntry> checkDataTypes(String tableName, ObjectDefinition object, List<DBColumn> dbColumns){
		List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
		for (ObjectAttribute oa : object.getObjectAttributes()){
			if (!oa.getInTable().equalsIgnoreCase(tableName) || oa.getName().equalsIgnoreCase(ID_COLUMN))
				continue;
			for (DBColumn dbCol : dbColumns){
				if (!oa.getName().equalsIgnoreCase(dbCol.getName()))
					continue;

				if (!isEqualDBTypes(oa, dbCol)){
					String physDType = getDatabaseDataType(oa);
					if (log.isDebugEnabled()){
						log.debug("Changing datatype of the column '" + oa.getName() + "' in the table '" + tableName + "'");
						log.debug("Old datatype: " + dbCol.getDataType() + ", new:  " + physDType);
					}

					String result = executeUpdate(getAlterColumnTypeSql(tableName, oa, dbCol));
					if (result != null){
						log.error("Error changing datatype of the column '" + oa.getName() + "' in the table '" + tableName
						        + "'");
						log.error("Old datatype: " + dbCol.getDataType() + ", new: " + physDType + ", error = " + result);
						errors.add(new ErrorEntry(TextID.MsgErrorChangingColumnType,
						        new String[] { oa.getName(), tableName }));
						executeUpdate(getFillErrorTableSql(tableName, oa, dbCol, result));
						executeUpdate(getDropColumnSql(tableName, dbCol));
						executeUpdate(getCreateColumnSql(tableName, oa));
						log.error("Column was overwriten, old data is available in 'sys_user_data_error' table");
					}
				}
				break;
			}
		}
		return errors;
	}

	boolean isEqualDBTypes(ObjectAttribute oa, DBColumn dbCol){
		String expectedType = getDatabaseDataType(oa);
		String currType = dbCol.getDataType();
		return expectedType.equals(currType) || (INTEGER_DATA_TYPE.equals(expectedType) && INT4_DB_TYPE.equals(currType));
	}

	void checkMissingColumns(String tableName, ObjectDefinition object, List<DBColumn> dbColumns){
		for (ObjectAttribute oa : object.getObjectAttributes()){
			if (!oa.getInTable().equalsIgnoreCase(tableName) || oa.getName().equalsIgnoreCase(ID_COLUMN))
				continue;
			boolean found = false;

			for (DBColumn dbCol : dbColumns){
				if (oa.getName().equalsIgnoreCase(dbCol.getName())){
					found = true;
					break;
				}
			}
			if (!found){
				if (log.isDebugEnabled())
					log.debug("Adding new column '" + oa.getName() + "' to the table '" + tableName + "'");
				executeUpdate(getCreateColumnSql(tableName, oa));
			}
		}
	}

	private void checkExcessiveColumns(String tableName, ObjectDefinition object, List<DBColumn> dbColumns){
		for (DBColumn dbCol : dbColumns){
			if (dbCol.getName().equalsIgnoreCase(ID_COLUMN))
				continue;
			boolean found = false;
			for (ObjectAttribute oa : object.getObjectAttributes()){
				if (oa.getInTable().equalsIgnoreCase(tableName) && oa.getName().equalsIgnoreCase(dbCol.getName())){
					found = true;
					break;
				}
			}
			if (!found){
				if (log.isDebugEnabled())
					log.debug("Removing column '" + dbCol.getName() + "' from the table '" + tableName + "'");
				executeUpdate(getDropColumnSql(tableName, dbCol));
			}
		}
	}

	private List<DBColumn> getDatabaseColumns(String tableName){
		List<DBColumn> columns = new ArrayList<DBColumn>();
		Connection c = null;
		ResultSet rs = null;
		try{
			c = dataSource.getConnection();
			c.setAutoCommit(false);

			Statement st = c.createStatement();
			rs = st.executeQuery("select * from " + tableName + " where id = -1");
			ResultSetMetaData metaData = rs.getMetaData();
			for (int col = 1; col <= metaData.getColumnCount(); col++){
				columns.add(new DBColumn(metaData.getColumnName(col), metaData.getColumnTypeName(col), metaData
				        .getPrecision(col)));
			}
			if (log.isDebugEnabled())
				log.debug("Found " + columns.size() + " columns in the table '" + tableName + "'");
		} catch (SQLException e){
			log.debug("Table " + tableName + " doesn't exist, it should to be created");
			return columns;
		} finally{
			try{
				if (rs != null)
					rs.close();
			} catch (SQLException e){
				log.error(e);
			}
			try{
				c.close();
			} catch (SQLException e){
				log.error(e);
			}
			c = null;
		}
		return columns;
	}

	String getDatabaseDataType(ObjectAttribute oa){
		String dbDataType = TEXT_DATA_TYPE;
		if (oa.getIsMultivalue()){
			dbDataType = TEXT_DATA_TYPE;
		} else if (oa.isPredefined()){
			dbDataType = INTEGER_DATA_TYPE;
		} else if (oa.isTextDataType() || oa.isDateDataType() || oa.isURLDataType()){
			dbDataType = TEXT_DATA_TYPE;
		} else if (oa.isIntDataType() || oa.isBoolDataType()){
			dbDataType = INTEGER_DATA_TYPE;
		} else if (oa.isDecimalDataType()){
			dbDataType = NUMERIC_DATA_TYPE;
		}
		return dbDataType;
	}

	private String getCreateColumnSql(String tableName, ObjectAttribute oa){
		String sql = "alter table " + tableName + " add " + oa.getName() + " ";
		sql += getDatabaseDataType(oa);
		sql += " null";
		return sql;
	}

	private String getCreatePKConstraintSql(String tableName, boolean isCtxt){
		String sql = "ALTER TABLE " + tableName + " ADD CONSTRAINT pk_" + tableName.toLowerCase() + " PRIMARY KEY(id";
		if (isCtxt){
			sql += ",favoritesId";
		}
		sql += ")";
		return sql;
	}

	private String getDropColumnSql(String tableName, DBColumn dbCol){
		String sql = "alter table " + tableName + " drop column " + dbCol.getName();
		return sql;
	}

	private String getDropTableSql(String tableName){
		return "drop table if exists " + tableName;
	}

	private String getCreateUserTableSql(String tableName, boolean isCtxt){
		String sql = null;
		if (isCtxt){
			sql = "create table " + tableName + " (ID bigint NOT NULL, favoritesid int NOT NULL, CONSTRAINT PK_" + tableName
			        + " PRIMARY KEY(ID, favoritesId))";
		} else{
			sql = "create table " + tableName + " (ID bigint NOT NULL, CONSTRAINT PK_" + tableName + " PRIMARY KEY(ID))";
		}
		return sql;
	}

	private String getAlterColumnTypeSql(String tableName, ObjectAttribute oa, DBColumn dbCol){
		String dt = getDatabaseDataType(oa);
		String columnName = oa.getName();
		String sql = "alter table " + tableName + " alter column " + columnName + " TYPE " + dt + " USING cast("
		        + columnName + " as " + dt + ")";
		return sql;
	}

	private String getRenameTableSql(String oldTableName, String newTableName){
		String str = "alter table " + oldTableName + " rename to " + newTableName;
		return str;
	}

	private String getFillErrorTableSql(String tableName, ObjectAttribute oa, DBColumn dbCol, String errorMsg){
		String dt = getDatabaseDataType(oa);
		String columnName = oa.getName();
		StringBuffer sql = new StringBuffer();
		sql.append("insert into sys_user_data_error (errorid, tablename, columnname,").append(
		        "olddatatype, newdatatype, objectid, value, error, errtime)");
		sql.append("SELECT (select coalesce(max(errorid),0)+1 from sys_user_data_error), '");
		sql.append(tableName).append("', '");
		sql.append(columnName).append("', '");
		sql.append(dbCol.getDataType()).append("', '");
		sql.append(dt).append("', id,");
		sql.append(columnName).append(", '");
		sql.append(errorMsg).append("', now()");
		sql.append(" FROM ").append(tableName);
		return sql.toString();
	}

	protected String executeUpdate(String sql){
		Connection c = null;
		try{
			c = dataSource.getConnection();
			c.createStatement().executeUpdate(sql);
			return null;
		} catch (SQLException ex){
			log.error("Error executing query: " + sql);
			return ex.getMessage();
		} finally{
			try{
				c.close();
			} catch (SQLException e){
				log.error(e);
			}
			c = null;
		}
	}

	@Override
	public void dropUserTables(List<String> tableNames) throws ACException{
		for (String name : tableNames){
			if (name == null)
				continue;
			name = name.trim();
			if (name.length() == 0)
				continue;
			if (name.toUpperCase().equals(EDGES_TABLE.toUpperCase()))
				continue;

			log.info("Drop user table: " + name);
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query query = session.createSQLQuery(getDropTableSql(name));
			query.executeUpdate();
		}
	}

	@Override
	public void renameUserTable(final String oldTableName, final String newTableName, boolean isCtxt){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				SQLQuery query = session.createSQLQuery(getRenameTableSql(oldTableName, newTableName));
				int count = query.executeUpdate();
				return count;
			}
		};
		getHibernateTemplate().execute(callback);
		renamePrimaryKey(newTableName, isCtxt);
	}

	@Override
	public boolean isExistPKConstraint(String tableName){
		String cName = getPrimaryKeyName(tableName);
		return cName != null;
	}

	private void renamePrimaryKey(final String tableName, final boolean isCtxt){
		final String cName = getPrimaryKeyName(tableName);
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				if (cName != null){
					if (log.isDebugEnabled()){
						log.debug("Dropping old PK constraint " + cName);
					}
					final String strDel = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + cName;
					SQLQuery queryDel = session.createSQLQuery(strDel);
					queryDel.executeUpdate();
				}

				if (log.isDebugEnabled()){
					log.debug("Creating new PK constraint pk_" + tableName.toLowerCase());
				}
				SQLQuery queryCreate = session.createSQLQuery(getCreatePKConstraintSql(tableName, isCtxt));
				return queryCreate.executeUpdate();

			}
		};

		try{
			getHibernateTemplate().execute(callback);
		} catch (Exception ex){
			log.error("Cannot recreate constraint for user table " + tableName, ex);
		}
	}

	private String getPrimaryKeyName(final String tableName){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery queryConstr = session.createSQLQuery(CONSTRAINT_SQL);
				queryConstr.setString(0, tableName);
				String cName = (String) queryConstr.uniqueResult();
				return cName;
			}
		};

		return (String) getHibernateTemplate().execute(callback);
	}

	class DBColumn{
		private String name;
		private String dataType;
		private int length;

		public DBColumn(String name, String dataType, int length){
			this.name = name;
			this.dataType = dataType;
			this.length = length;
		}

		public String getName(){
			return name;
		}

		public String getDataType(){
			return dataType;
		}

		public int getLength(){
			return length;
		}

		public void setDataType(String dataType){
			this.dataType = dataType;
		}
	}
}
