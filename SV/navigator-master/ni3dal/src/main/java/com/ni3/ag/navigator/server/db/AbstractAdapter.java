/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.db;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

//TODO extract interface
public abstract class AbstractAdapter extends JdbcDaoSupport implements DatabaseAdapter{

	private static final Logger log = Logger.getLogger(AbstractAdapter.class);

	@Override
	public void getOperators(final HashMap<String, String> SQLOperator){
		SQLOperator.put("Multivalue=", "IN");
		SQLOperator.put("Multivalue<>", "NOT IN");

		SQLOperator.put("=", "=");
		SQLOperator.put("!=", "!=");
		SQLOperator.put(">", ">");
		SQLOperator.put("<", "<");
		SQLOperator.put("<=", "<=");
		SQLOperator.put(">=", ">=");
		SQLOperator.put("LIKE", "LIKE");
		SQLOperator.put("String=", "=");
	}

	@Override
	public String addLimit(String query, int maxResults){
		return addLimit(query, maxResults, "");
	}
}
