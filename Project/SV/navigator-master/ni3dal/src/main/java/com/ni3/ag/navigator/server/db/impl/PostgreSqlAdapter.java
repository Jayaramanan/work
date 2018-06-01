/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.db.impl;

import java.util.HashMap;

import com.ni3.ag.navigator.server.db.AbstractAdapter;

public class PostgreSqlAdapter extends AbstractAdapter{

	@Override
	public String addLimit(final String query, final int maxResults, final String orderBy){
		StringBuilder builder = new StringBuilder();
		builder.append(query);
		builder.append(" ");
		builder.append(orderBy);
		builder.append(" LIMIT ").append(maxResults);

		return builder.toString();
	}

	@Override
	public void getOperators(final HashMap<String, String> SQLOperator){
		super.getOperators(SQLOperator);

		SQLOperator.put("~", "ILIKE");
		SQLOperator.put("LIKE", "ILIKE");
		SQLOperator.put("StringLIKE", "ILIKE");
		SQLOperator.put("String=", "ILIKE");
		SQLOperator.put("String<>", "NOT ILIKE");
	}

	@Override
	public String getProcedureSQL(final String Procedure, final String Arguments){
		final String query = "Select " + Procedure + "(" + (Arguments != null ? Arguments : "") + ");";

		if (Procedure.startsWith("sp_")){
			return query;
		}

		String ret = (String) getJdbcTemplate().queryForObject(query, String.class);

		return ret;
	}
}
