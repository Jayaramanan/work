package com.ni3.ag.navigator.server.services;

import java.util.List;

import org.postgis.PGgeometry;

import com.ni3.ag.navigator.shared.domain.GISOverlay;
import com.ni3.ag.navigator.shared.domain.GisMap;

public interface GISService{

	GisMap getMap(int mapId);

	List<GisMap> getMaps();

	List<GISOverlay> getOverlays(int schemaId);

	List<Integer> getOverlayGeometryList(int overlayId);

	PGgeometry getOverlayGeometry(int overlayId, int geometryId);
}
