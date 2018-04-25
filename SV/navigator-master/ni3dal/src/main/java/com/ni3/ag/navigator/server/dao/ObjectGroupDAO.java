/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.ObjectDefinitionGroup;

public interface ObjectGroupDAO{

	List<ObjectDefinitionGroup> getByObjectDefinitionId(int objectDefinitionId);

    List<ObjectDefinitionGroup> getByGroupId(int groupId, int schemaId);

}
