/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.util;

import java.util.List;

public interface ModuleLister{
	List<String> list();

	boolean testPath();
}
