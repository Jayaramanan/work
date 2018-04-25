package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.DeltaHeader;

public interface DeltaHeaderDAO{

	List<DeltaHeader> getUnprocessedDeltas(int limit);

	void markProcessed(List<DeltaHeader> deltas);

	DeltaHeader save(DeltaHeader delta);

	DeltaHeader get(int id);

	int getUnprocessedCount();

	void delete(DeltaHeader delta);

	void markProcessed(DeltaHeader delta);

	List<DeltaHeader> getLastDeltas(int limit, int offset, long lastId);
}
