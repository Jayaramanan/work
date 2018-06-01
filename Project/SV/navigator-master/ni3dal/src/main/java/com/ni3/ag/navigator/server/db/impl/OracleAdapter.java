/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.db.impl;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.db.AbstractAdapter;

public class OracleAdapter extends AbstractAdapter{
	private static final Logger log = Logger.getLogger(OracleAdapter.class);

	public static String CLOBing(final String toCLOB){
		int from = 0, lng;
		String CLOBed = "";
		lng = toCLOB.length();
		if (lng > 4000){
			for (int i = 0; from <= lng; i++){
				if (i == 0){
					CLOBed = "TO_CLOB('" + toCLOB.substring(from, from + 4000) + "')";
				} else{
					CLOBed = CLOBed + " || TO_CLOB('" + toCLOB.substring(from, Math.min(from + 4000, lng)) + "')";
				}
				from = from + 4000 + 1;
			}
		} else{
			CLOBed = "'" + toCLOB + "'";
		}

		return CLOBed;
	}

	@Override
	public String addLimit(final String query, final int maxResults, final String orderBy){
		final StringBuilder builder = new StringBuilder();
		builder.append(query);
		// TODO: check for "WHERE ...=... or this "and ..." will break
		builder.append(" and rownum <= ");
		builder.append(maxResults);
		builder.append(" ");
		builder.append(orderBy);

		return builder.toString();
	}

	@Override
	public String getProcedureSQL(final String Procedure, final String Arguments){
		final String query = "select Ni3." + Procedure + "(" + (Arguments != null ? Arguments : "") + ") from dual";

		if ("sp_getseq".equalsIgnoreCase(Procedure)){
			return query;
		}

		// procedures that are returning SQL for the RECORDSET
		String ret = (String) getJdbcTemplate().queryForObject(query, String.class);

		return ret;
	}
}
