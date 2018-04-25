package com.ni3.ag.navigator.server.services.impl;

import java.util.List;

import org.postgis.PGgeometry;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GISOverlayDAO;
import com.ni3.ag.navigator.server.dao.GisMapDAO;
import com.ni3.ag.navigator.server.services.GISService;
import com.ni3.ag.navigator.shared.domain.GISOverlay;
import com.ni3.ag.navigator.shared.domain.GisMap;

public class GISServiceImpl implements GISService{
	@Override
	public GisMap getMap(int mapId){
		GisMapDAO gisMapDAO = NSpringFactory.getInstance().getGisMapDAO();
		return gisMapDAO.get(mapId);
	}

	@Override
	public List<GisMap> getMaps(){
		GisMapDAO mapDAO = NSpringFactory.getInstance().getGisMapDAO();

		return mapDAO.getMaps();
	}

	@Override
	public List<GISOverlay> getOverlays(int schemaId){
		GISOverlayDAO overlayDAO = NSpringFactory.getInstance().getGisOverlayDAO();
		return overlayDAO.getOverlaysForSchema(schemaId);
	}

	@Override
	public List<Integer> getOverlayGeometryList(int overlayId){
		GISOverlayDAO overlayDAO = NSpringFactory.getInstance().getGisOverlayDAO();
		return overlayDAO.getOverlayGeometryList(overlayId);
	}

	@Override
	public PGgeometry getOverlayGeometry(int overlayId, int geometryId){
		GISOverlayDAO overlayDAO = NSpringFactory.getInstance().getGisOverlayDAO();
		return overlayDAO.getOverlayGeometry(overlayId, geometryId);
	}

}
