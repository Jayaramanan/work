package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import java.util.List;

public interface DeltaHeaderUserDAO{

	Long getUnprocessedCountForUser(int userId);

	List<DeltaHeader> getUnprocessedForUser(int i, int i1);

	void markUserDeltasAsProcessed(List<DeltaHeader> deltas);

	void create(DeltaHeader delta, Integer userId);

}
