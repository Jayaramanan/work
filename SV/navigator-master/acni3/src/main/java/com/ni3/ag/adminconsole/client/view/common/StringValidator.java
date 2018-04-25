/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

public class StringValidator{
	public static String validate(String text){
		if (text == null || text.isEmpty())
			return null;

		String s = (String) text;
		s = s.trim();
		if (s.isEmpty())
			return null;
		return s;
	}
}
