package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.GISOverlay;
import com.ni3.ag.navigator.shared.domain.GisMap;
import com.ni3.ag.navigator.shared.domain.GisOverlayGeometry;
import com.ni3.ag.navigator.shared.domain.GisTerritory;

public interface GISGateway{

	GisMap getMap(final int id);

	List<GisTerritory> getTerritories();

	List<GisMap> getMaps();

	List<GISOverlay> getOverlaysForSchema(int schemaID);

	List<Integer> getOverlayGeometryList(Integer overlayId);

	GisOverlayGeometry getOverlayGeometry(Integer overlayId, Integer geometryId);

}