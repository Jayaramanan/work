/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserDataExistanceValidationRule implements ACValidationRule{
	private DataSource dataSource;
	private List<ErrorEntry> errors;

	private static final Logger log = Logger.getLogger(UserDataExistanceValidationRule.class);
	private static final String CIS_EDGES_TABLE_NAME = "CIS_EDGES";
	private static final String CIS_NODES_TABLE_NAME = "CIS_NODES";

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		SchemaAdminModel saModel = (SchemaAdminModel) model;
		ObjectDefinition objectToValidate = saModel.getCurrentObjectDefinition();
		if (objectToValidate == null)
			return true;

		errors = new ArrayList<ErrorEntry>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			conn = dataSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("select DISTINCT inTable from sys_object_attributes where objectdefinitionid="
			        + objectToValidate.getId());
			List<String> tables = new ArrayList<String>();
			while (rs.next()){
				tables.add(rs.getString(1));
			}
			rs.close();

			for (String s : tables){
				if (s.equalsIgnoreCase(CIS_EDGES_TABLE_NAME))
					continue;
				String sql = "select count(*) from " + s;
				if (s.equalsIgnoreCase(CIS_NODES_TABLE_NAME))
					sql += " where nodeType = " + objectToValidate.getId();
				rs = st.executeQuery(sql);
				rs.next();
				long count = rs.getLong(1);
				rs.close();
				if (count > 0){
					errors.add(new ErrorEntry(TextID.UsetTableContainsData, new String[] { s }));
				}
			}
		} catch (SQLException e){
			log.debug("ERROR validate user data", e);
		} finally{
			if (rs != null){
				try{
					rs.close();
				} catch (SQLException e){
					log.error(e.getMessage(), e);
				}
				rs = null;
			}
			if (conn != null){
				try{
					conn.close();
				} catch (SQLException e){
					log.error(e.getMessage(), e);
				}
				conn = null;
			}

		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
