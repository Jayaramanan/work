/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.db.mock;

import java.util.HashMap;

import com.ni3.ag.navigator.server.db.DatabaseAdapter;

public class PostgreSqlAdapterMock implements DatabaseAdapter{
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
	public String getProcedureSQL(final String procedure, final String arguments){
		return procedure;
	}

	@Override
	public String addLimit(String query, int maxResults){
		return query;
	}

	@Override
	public void getOperators(HashMap<String, String> SQLOperator){
		SQLOperator.put("~", "ILIKE");
		SQLOperator.put("LIKE", "ILIKE");
		SQLOperator.put("StringLIKE", "ILIKE");
		SQLOperator.put("String=", "ILIKE");
		SQLOperator.put("String<>", "NOT ILIKE");
	}

}
