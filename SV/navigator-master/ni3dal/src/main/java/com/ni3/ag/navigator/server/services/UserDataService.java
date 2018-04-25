/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.navigator.server.dictionary.DBObject;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;

public interface UserDataService{

	DBObject loadObject(ObjectDefinition entity, int objectId, List<Integer> attributeIds);

	void relinkEdges(int oldNodeId, int newNodeId, Collection<Integer> edges);

	Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> getDataForIdList(ObjectDefinition entity, Collection<Integer> ids);

	Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> getContextDataForIdList(ObjectDefinition entity, int contextId, String key, Collection<Integer> ids);

	Map<Integer,Set<Integer>> getDataOfPredefinedForIdList(ObjectDefinition od, List<Integer> integers);
}
