/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.DeltaHeader;
import com.ni3.ag.adminconsole.domain.User;

public interface DeltaHeaderDAO{

	Integer getCountByUser(User u);

	Integer getUnprocessedCount();

	DeltaHeader load(Long id);

	void saveOrUpdate(DeltaHeader header);

    void saveOrUpdateAll(List<DeltaHeader> deltaHeaders);
}
