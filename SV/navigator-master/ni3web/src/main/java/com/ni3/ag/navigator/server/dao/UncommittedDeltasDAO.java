package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.server.domain.DeltaHeader;

import java.util.List;

public interface UncommittedDeltasDAO{
	List<DeltaHeader> getUncommittedDeltas();

	void clearUncommitted();

	void save(DeltaHeader deltaHeader);
}
