/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Context;

public interface ContextDAO{

	void deleteAll(List<Context> contextsToDelete);

}
