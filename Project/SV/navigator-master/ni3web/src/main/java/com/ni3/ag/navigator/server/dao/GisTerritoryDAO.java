package com.ni3.ag.navigator.server.dao;

import com.ni3.ag.navigator.shared.domain.GisTerritory;

import java.util.List;

public interface GisTerritoryDAO{
	List<GisTerritory> getTerritories();

	GisTerritory getTerritory(int id);
}
