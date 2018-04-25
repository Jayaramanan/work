package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.ObjectDefinition;

public interface ObjectDefinitionDAO{

	List<ObjectDefinition> getObjectDefinitions();

	List<Integer> getEntitiesWithValueListAttributes(int schemaId);

	ObjectDefinition get(Integer id);
}
