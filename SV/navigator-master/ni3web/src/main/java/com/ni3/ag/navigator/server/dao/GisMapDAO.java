package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.GisMap;

public interface GisMapDAO{
	GisMap get(int mapId);

	List<GisMap> getMaps();
}
