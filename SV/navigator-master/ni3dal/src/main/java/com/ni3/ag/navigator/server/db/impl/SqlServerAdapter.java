/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.db.impl;

import com.ni3.ag.navigator.server.db.AbstractAdapter;

public class SqlServerAdapter extends AbstractAdapter{

	@Override
	public String addLimit(final String query, final int maxResults, final String orderBy){
		final StringBuilder builder = new StringBuilder();
		builder.append(query.replace("SELECT", "SELECT TOP " + maxResults));
		builder.append(" ");
		builder.append(orderBy);

		return builder.toString();
	}

	@Override
	public String getProcedureSQL(final String Procedure, final String Arguments){
		return "exec " + Procedure + " " + (Arguments != null ? Arguments : "");
	}
}
