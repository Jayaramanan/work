/**
\ * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server.service.util;

public class FileNameValidator{

	public static String getNameWOSpecialChars(String name){
		String regex = "[^a-zA-Z0-9_\\.-]";
		return name.replaceAll(regex, "");
	}

}
