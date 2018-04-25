/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class GroupPrivilegesScopeCheckTask implements DiagnosticTask{
	private final static Logger log = Logger.getLogger(GroupPrivilegesScopeCheckTask.class);
	private final static String DESCRIPTION = "Checking group privileges (scope)";
	private final static String TOOLTIP_NO_GROUP_SCOPES = "Group does not have group scopes: ";
	private final static String TOOLTIP_NO_SQL = "SQL string is not specified for group: ";
	private final static String TOOLTIP_WRONG_SQL = "SQL string can not be executed for group: ";
	private final static String ACTION_FIX_NODE_SCOPE = "Go to Users tab and set a valid sql query or untick 'Use scope for nodes' for the group '%' ";
	private final static String ACTION_FIX_EDGE_SCOPE = "Go to Users tab and set a valid sql query or untick 'Use scope for edges' for the group '%' ";

	private GroupDAO groupDAO;
	private DataSource dataSource;

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<Group> groups = groupDAO.getGroups();
		for (Group g : groups){
			if (g.getNodeScope() == 'S'){
				if (g.getGroupScope() == null)
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_NO_GROUP_SCOPES + g.getName(), ACTION_FIX_NODE_SCOPE.replace("%", g.getName()));
				GroupScope gs = g.getGroupScope();
				String sql = gs.getNodeScope();
				if (sql == null)
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_NO_SQL + g.getName(), ACTION_FIX_NODE_SCOPE.replace("%", g.getName()));
				sql = sql.trim();
				if (!trySQL(sql))
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_WRONG_SQL + g.getName(), ACTION_FIX_NODE_SCOPE.replace("%", g.getName()));

			}

			if (g.getEdgeScope() == 'S'){
				if (g.getGroupScope() == null)
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_NO_GROUP_SCOPES + g.getName(), ACTION_FIX_EDGE_SCOPE.replace("%", g.getName()));
				GroupScope gs = g.getGroupScope();
				String sql = gs.getEdgeScope();
				if (sql == null)
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_NO_SQL + g.getName(), ACTION_FIX_EDGE_SCOPE.replace("%", g.getName()));
				sql = sql.trim();
				if (!trySQL(sql))
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_WRONG_SQL + g.getName(), ACTION_FIX_EDGE_SCOPE.replace("%", g.getName()));

			}
		}
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	private boolean trySQL(String sql){
		Connection c = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			c = dataSource.getConnection();
			st = c.createStatement();
			rs = st.executeQuery(sql);
			if (!rs.next())
				return false;
			Object o = rs.getObject(1);
			st.close();
			if (o instanceof Integer || o instanceof BigInteger || o instanceof Long)
				return true;
			return false;
		} catch (Exception ex){
			log.error(ex);
			return false;
		} finally{
			if (rs != null){
				try{
					rs.close();
				} catch (SQLException e){
					log.error(e.getMessage(), e);
				}
				rs = null;
			}
			if (c != null){
				try{
					c.close();
				} catch (SQLException e){
					log.error(e.getMessage(), e);
				}
				c = null;
			}

		}
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		throw new ACFixTaskException("ACFixTaskException", "Non fixable");
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

}
