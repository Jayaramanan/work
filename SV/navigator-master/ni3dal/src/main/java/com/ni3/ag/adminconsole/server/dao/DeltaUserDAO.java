/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import com.ni3.ag.adminconsole.domain.User;

public interface DeltaUserDAO{
	public Integer getCountByUser(User u);

	public Integer getUnprocessedRecordCount();
}
