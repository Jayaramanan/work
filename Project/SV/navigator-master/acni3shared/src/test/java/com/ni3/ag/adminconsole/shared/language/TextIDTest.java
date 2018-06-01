/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.language;

import java.io.IOException;

import junit.framework.TestCase;

public class TextIDTest extends TestCase{
	public void testIds() throws IOException{
		TextID[] ids = TextID.values();
		// FileOutputStream fs = new FileOutputStream("user_lang.sql");
		for (int i = 0; i < ids.length; i++){
			String s = "insert into SYS_USER_LANGUAGE([LanguageID], [Prop], [Value]) " + " values (1, '" + ids[i].getKey()
			        + "', '" + ids[i].getKey() + "');\n";
			byte[] buf = s.getBytes();
			// fs.write(buf);
		}
		// fs.close();
	}
}
