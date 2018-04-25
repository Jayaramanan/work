/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.graphXXL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MetaphorCondition{
	String iconname;
	int priority;
	int val[];
	int weight;

	public MetaphorCondition(String iconname, int priority){
		val = null;
		this.iconname = iconname;
		priority = this.priority;
		weight = 1;
	}

	public MetaphorCondition(int attrCount, ResultSet rs) throws SQLException{
		val = new int[attrCount];

		weight = 0;
		priority = rs.getInt(1);
		iconname = rs.getString(2);
		for (int i = 0; i < attrCount; i++){
			val[i] = rs.getInt(i + 3);
			if (val[i] != 0)
				weight++;
		}
	}
};
