package com.ni3.ag.navigator.server.dao;

import java.util.List;

import org.postgis.PGgeometry;

import com.ni3.ag.navigator.shared.domain.GISOverlay;

public interface GISOverlayDAO{

	List<GISOverlay> getOverlaysForSchema(int schemaId);

	List<Integer> getOverlayGeometryList(int overlayId);

	PGgeometry getOverlayGeometry(int overlayId, int geometryId);

}
