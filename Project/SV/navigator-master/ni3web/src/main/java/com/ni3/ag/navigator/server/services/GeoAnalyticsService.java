/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.GISPolygon;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;
import com.ni3.ag.navigator.shared.domain.ThematicFolder;
import com.ni3.ag.navigator.shared.domain.ThematicMap;

public interface GeoAnalyticsService{

	List<GeoTerritory> getGeoTerritories(int attributeId, List<Integer> nodeIds, List<Integer> gIds, int territoryId, int schemaId);

	List<GeoTerritory> getGeoTerritories(int gisTerritoryId);

	List<GeoTerritory> getGeoTerritoriesForDynamicAttribute(int entityId, List<Integer> nodeIds, List<Double> values,
	        List<Integer> gIds, int gisTerritoryId);

	List<ThematicFolder> getFoldersWithThematicMaps(int schemaId, int groupId);

	List<Integer> getGeometryIdsByThematicMap(int thematicMapId);

	List<GISPolygon> getThematicPolygons(List<Integer> gisIds, int gisTerritoryId);

	ThematicMap getThematicMapWithClusters(int thematicMapId);

	ThematicMap getThematicMapByName(String name, int folderId, int groupId);

	ThematicMap getThematicMap(int thematicMapId);

    int saveThematicMapWithClusters(ThematicMap thematicMap);

    int getDefaultFolderId(int schemaId);

    void deleteThematicMap(int thematicMapId);

	int createThematicMapWithClusters(ThematicMap tm);

	int saveThematicMapWithClustersWithIds(ThematicMap tm);
}
