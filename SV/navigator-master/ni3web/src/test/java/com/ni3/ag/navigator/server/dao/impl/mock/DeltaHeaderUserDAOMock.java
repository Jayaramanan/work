package com.ni3.ag.navigator.server.dao.impl.mock;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import java.util.List;

import com.ni3.ag.navigator.server.dao.DeltaHeaderUserDAO;

public class DeltaHeaderUserDAOMock implements DeltaHeaderUserDAO{

	private static int created = 0;

	/**
	 * Hack, this methid returns number of records "created" by create()
	 */
	@Override
	public Long getUnprocessedCountForUser(int userId){
		return (long) created;
	}

	@Override
	public List<DeltaHeader> getUnprocessedForUser(int i, int i1){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markUserDeltasAsProcessed(List<DeltaHeader> deltas){
		// TODO Auto-generated method stub

	}

	@Override
	public void create(DeltaHeader delta, Integer userId){
		created++;
	}

}
