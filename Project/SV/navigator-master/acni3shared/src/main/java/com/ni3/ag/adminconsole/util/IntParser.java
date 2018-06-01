/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;


public class IntParser{

	public static Integer getInt(Object number){
		return getInt(number, null);
	}

	public static Integer getInt(Object number, Integer nullValue){
		if (number == null){
			return nullValue;
		}
		Integer result = nullValue;
		if (number instanceof Integer){
			result = (Integer) number;
		} else if (number instanceof Number){
			result = ((Number) number).intValue();
		}

		return result;
	}
}
