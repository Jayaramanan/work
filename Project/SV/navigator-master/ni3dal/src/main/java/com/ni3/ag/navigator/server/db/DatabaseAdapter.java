package com.ni3.ag.navigator.server.db;

import java.util.HashMap;

public interface DatabaseAdapter{

	void getOperators(final HashMap<String, String> SQLOperator);

	String getProcedureSQL(String Procedure, String Arguments);

	String addLimit(String query, int maxResults, String orderBy);

	String addLimit(String query, int maxResults);

}
