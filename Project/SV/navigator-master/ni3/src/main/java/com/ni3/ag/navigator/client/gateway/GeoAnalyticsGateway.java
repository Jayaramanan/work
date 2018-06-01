package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.shared.domain.*;

public interface GeoAnalyticsGateway{

	List<GeoTerritory> getGeoTerritories(Attribute attribute, GeoObjectSource source, List<Integer> nodeIds,
	        List<Integer> geoTerritoryIds, int gisTerritoryId, int schemaId);

	List<GeoTerritory> getGeoTerritoriesForDynamicAttribute(Attribute attribute, GeoObjectSource source,
	        List<Integer> nodeIds, List<Double> dynamicValues, List<Integer> geoTerritoryIds, int gisTerritoryId);

	List<GISPolygon> getThematicData(List<Integer> gisIds, int territoryId);

	List<GeoTerritory> getAllGeoTerritories(int gisTerritoryId);

	List<Integer> getGeometryIdsByThematicMap(int thematicMapId);

	List<ThematicFolder> getThematicFoldersWithThematicMaps(int schemaId);

	ThematicMap getThematicMapWithClusters(int thematicMapId);

	ThematicMap getThematicMapByName(String name, int thematicFolderId, int schemaId);

	ThematicMap saveThematicMapWithClusters(ThematicMap thematicMap, int schemaId);

	int getDefaultFolderId(int schemaId);

	boolean deleteThematicMap(int thematicMapId);
}
