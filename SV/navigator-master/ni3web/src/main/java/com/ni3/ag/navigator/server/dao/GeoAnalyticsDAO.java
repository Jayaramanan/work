/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao;

import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.GISGeometry;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;
import com.ni3.ag.navigator.shared.domain.GisTerritory;

public interface GeoAnalyticsDAO{

	List<GeoTerritory> getAggregationsPerTerritory(Attribute attribute, List<Integer> nodeIds, List<Integer> gIds,
	        String geoTableName, int schemaId);

	List<GeoTerritory> getAllGeoTerritories(GisTerritory territory);

	Map<Integer, Integer> getNodeToTerritoryMapping(int entityId, List<Integer> nodeIds, List<Integer> gIds,
	        String geoTableName);

	List<GISGeometry> getThematicData(List<Integer> gisIds, String geoTableName);

}
