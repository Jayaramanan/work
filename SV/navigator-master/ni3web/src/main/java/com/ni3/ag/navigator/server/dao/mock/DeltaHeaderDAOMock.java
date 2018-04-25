package com.ni3.ag.navigator.server.dao.mock;

import java.util.*;

import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.SyncStatus;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;

public class DeltaHeaderDAOMock implements DeltaHeaderDAO{

	private void fillList(List<DeltaHeader> deltas, int count)
	{
		for (int i = 0; i < count; i++)
		{
			User usr = new User();
			usr.setId(1);
			DeltaHeader dh = new DeltaHeader(DeltaType.SETTING_UPDATE, usr, createParamsForDelta());
			dh.setId(1);
			dh.setTimestamp(new Date());
			dh.setSyncStatus(SyncStatus.New);
			dh.setSync(true);
			deltas.add(dh);
		}
	}

	private Map<DeltaParamIdentifier, DeltaParam> createParamsForDelta()
	{
		Map<DeltaParamIdentifier, DeltaParam> result = new HashMap<DeltaParamIdentifier, DeltaParam>();
		result.put(DeltaParamIdentifier.UpdateSettingsPropertyName, new DeltaParam(DeltaParamIdentifier.UpdateSettingsPropertyName, "prop"));
		result.put(DeltaParamIdentifier.UpdateSettingsPropertyValue, new DeltaParam(DeltaParamIdentifier.UpdateSettingsPropertyValue, "val"));
		return result;
	}

	@Override
	public DeltaHeader save(DeltaHeader delta)
	{
		return null;
	}

	@Override
	public DeltaHeader get(int id)
	{
		return null;
	}

	@Override
	public int getUnprocessedCount()
	{
		return 100;
	}

	@Override
	public void markProcessed(List<DeltaHeader> deltas)
	{
	}

	@Override
	public List<DeltaHeader> getUnprocessedDeltas(int limit)
	{
		List<DeltaHeader> result = new ArrayList<DeltaHeader>();
		fillList(result, 100);
		return result;
	}

	@Override
	public void delete(DeltaHeader delta)
	{
	}

	@Override
	public void markProcessed(DeltaHeader delta)
	{
	}

	@Override
	public List<DeltaHeader> getLastDeltas(int limit, int offset, long last)
	{
		List<DeltaHeader> deltas = new ArrayList<DeltaHeader>();
		fillList(deltas, limit);
		return deltas;
	}
}
