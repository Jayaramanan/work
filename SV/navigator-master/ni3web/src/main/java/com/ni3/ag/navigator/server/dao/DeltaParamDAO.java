package com.ni3.ag.navigator.server.dao;

import java.util.Map;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;

public interface DeltaParamDAO{
	DeltaParam save(DeltaParam param, int parentId);

	Map<DeltaParamIdentifier, DeltaParam> save(Map<DeltaParamIdentifier, DeltaParam> params, int parentId);

	DeltaParam get(long id);

	void delete(Map<DeltaParamIdentifier, DeltaParam> deltaParameters);

	void delete(DeltaParam param);

	Map<DeltaParamIdentifier, DeltaParam> getByDeltaHeader(DeltaHeader deltaHeader);
}
