/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.Collection;
import java.util.List;

import com.ni3.ag.navigator.shared.domain.DBObject;

public interface MetaphorService{

	List<String> getMetaphorSets(int schemaId);

	void fillMetaphors(Collection<DBObject> objects, int schemaId);
}
