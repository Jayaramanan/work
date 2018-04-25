/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;

public class DBIDTagGenerator{

	public static String generate(){
		return "<dbid>" + ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId() + "</dbid>";
	}

}
