package com.ni3.ag.navigator.server.services;


import java.util.List;

import com.ni3.ag.navigator.server.domain.DeltaHeader;

public interface DeltaHeaderService{
	List<DeltaHeader> getUnprocessedForUser(Integer userId, int limit);

	void prepareDataForUser(Integer id);

	List<DeltaHeader> getUnprocessedForMaster(int deltaChunkCount);

	void prepareDataForMaster();
}
