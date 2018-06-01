package com.ni3.ag.navigator.server.cache;

import com.ni3.ag.navigator.server.domain.ObjectDefinition;

public interface SrcIdToIdCache{
	int getId(String srcId, ObjectDefinition entity);

	String getSrcId(Integer id);

	void add(ObjectDefinition entity, int id, String newSrcId);
}
