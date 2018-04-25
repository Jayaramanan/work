/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public interface ExportService{

	List<ObjectDefinition> getObjectDefinitionsByCisObjects(String ids);

	boolean isAvailableObject(Integer odId, Integer groupId);

	List<ObjectAttribute> getAvailableAttributes(Integer odId, Integer groupId);

	Map<Integer, String> getSrcIdMap(List<ObjectDefinition> objectDefinitions);

	Integer getGroupId(Integer userId);

	List<Object[]> getUserData(ObjectDefinition od, List<ObjectAttribute> attributes, String objectIds);

}
