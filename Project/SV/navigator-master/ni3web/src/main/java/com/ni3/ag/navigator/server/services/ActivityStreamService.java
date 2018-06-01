/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;

public interface ActivityStreamService{

	Integer getObjectId(DeltaHeader delta, Map<DeltaParamIdentifier, DeltaParam> params);

	String getObjectName(DeltaHeader delta, int objectId);

	List<DeltaHeader> getLastDeltas(int count, long lastId, int schemaId, int groupId);

}
