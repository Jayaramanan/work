package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgis.Geometry;
import org.postgis.LinearRing;
import org.postgis.MultiPolygon;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GisTerritoryDAO;
import com.ni3.ag.navigator.server.services.GISService;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.domain.GISOverlay;
import com.ni3.ag.navigator.shared.domain.GisMap;
import com.ni3.ag.navigator.shared.domain.GisTerritory;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.GIS;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class GISServlet extends Ni3Servlet{
	private static final long serialVersionUID = 2870983636645475105L;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		InputStream is = request.getInputStream();
		NRequest.GIS protoRequest = NRequest.GIS.parseFrom(is);
		NResponse.Envelope.Builder result = NResponse.Envelope.newBuilder();
		switch (protoRequest.getAction()){
			case GET_MAP:
				handleGetMap(protoRequest, result);
				break;
			case GET_MAPS:
				handleGetMaps(protoRequest, result);
				break;
			case GET_TERRITORIES:
				handleGetTerritories(result);
				break;
			case GET_OVERLAYS:
				handleGetOverlays(protoRequest, result);
				break;
			case GET_OVERLAY_DATA:
				handleGetOverlayData(protoRequest, result);
				break;
			case GET_OVERLAY_GEOMETRY:
				handleGetOverlayGeometry(protoRequest, result);
				break;
		}
		result.setStatus(NResponse.Envelope.Status.SUCCESS);
		result.build().writeTo(response.getOutputStream());
	}

	private void handleGetOverlayGeometry(GIS protoRequest, Builder result){
		final int overlayId = protoRequest.getOverlayId();
		final int geometryId = protoRequest.getOverlayGeometryId();

		GISService gisService = NSpringFactory.getInstance().getGISService();
		final PGgeometry geometry = gisService.getOverlayGeometry(overlayId, geometryId);
		final NResponse.GisGeometry.Builder protoGeometry = NResponse.GisGeometry.newBuilder();

		switch (geometry.getGeoType()){
			case Geometry.MULTIPOLYGON:
				final MultiPolygon multiPolygon = (MultiPolygon) geometry.getGeometry();

				for (final Polygon polygon : multiPolygon.getPolygons()){
					final NResponse.GisPolygon.Builder protoPolygon = NResponse.GisPolygon.newBuilder();
					protoPolygon.setGisId(geometryId);
					final NResponse.GisRing.Builder gisRingBuilder = NResponse.GisRing.newBuilder();
					if (polygon.numRings() > 0){
						final LinearRing ring = polygon.getRing(0);
						final NResponse.GisPoint.Builder pointBuilder = NResponse.GisPoint.newBuilder();
						for (final Point point : ring.getPoints()){
							pointBuilder.setX(point.getX());
							pointBuilder.setY(point.getY());
							gisRingBuilder.addPoints(pointBuilder);
						}
						protoPolygon.setPolygon(gisRingBuilder);
					}
					protoGeometry.addPolygons(protoPolygon);
				}
				break;
			default:
				break;
		}

		result.setPayload(protoGeometry.build().toByteString());
	}

	private void handleGetOverlayData(GIS protoRequest, Builder result){
		GISService gisService = NSpringFactory.getInstance().getGISService();
		final List<Integer> data = gisService.getOverlayGeometryList(protoRequest.getOverlayId());
		NResponse.GIS.Builder gisBuilder = NResponse.GIS.newBuilder();
		for (final Integer geometryId : data){
			gisBuilder.addOverlayGeometryIds(geometryId);
		}
		result.setPayload(gisBuilder.build().toByteString());
	}

	private void handleGetOverlays(NRequest.GIS protoRequest, NResponse.Envelope.Builder result){
		GISService gisService = NSpringFactory.getInstance().getGISService();
		List<GISOverlay> overlays = gisService.getOverlays(protoRequest.getSchemaId());
		NResponse.GIS.Builder gisBuilder = NResponse.GIS.newBuilder();
		for (GISOverlay overlay : overlays){
			final com.ni3.ag.navigator.shared.proto.NResponse.GisOverlay.Builder builder = NResponse.GisOverlay.newBuilder();
			builder.setId(overlay.getId());
			builder.setSchemaId(overlay.getSchemaId());
			builder.setName(overlay.getName());
			builder.setLineWidth(overlay.getLineWidth());
			builder.setFilled(overlay.isFilled());
			builder.setVersion(overlay.getVersion());
			if (overlay.getColor() != null){
				builder.setLineColor(Utility.colorToHexString(overlay.getColor()));
			}
			gisBuilder.addOverlays(builder);
		}
		result.setPayload(gisBuilder.build().toByteString());
	}

	private void handleGetMaps(NRequest.GIS protoRequest, NResponse.Envelope.Builder result){
		GISService gisService = NSpringFactory.getInstance().getGISService();
		List<GisMap> maps = gisService.getMaps();
		NResponse.GIS.Builder mapBuilder = NResponse.GIS.newBuilder();
		for (GisMap map : maps){
			mapBuilder.addMaps(NResponse.GisMap.newBuilder().setId(map.getId()).setName(map.getName()));
		}
		result.setPayload(mapBuilder.build().toByteString());
	}

	private void handleGetTerritories(NResponse.Envelope.Builder result){
		GisTerritoryDAO gisTerritoryDAO = NSpringFactory.getInstance().getGisTerritoryDAO();
		List<GisTerritory> territories = gisTerritoryDAO.getTerritories();
		NResponse.GIS.Builder builder = NResponse.GIS.newBuilder();
		for (GisTerritory gt : territories){
			final NResponse.GisTerritory.Builder tBuilder = NResponse.GisTerritory.newBuilder();
			tBuilder.setId(gt.getId());
			tBuilder.setLabel(gt.getLabel());
			tBuilder.setTerritory(gt.getTerritory());
			tBuilder.setVersion(gt.getVersion());
			if (gt.getTableName() != null){
				tBuilder.setTableName(gt.getTableName());
			}
			builder.addTerritories(tBuilder);
		}
		result.setPayload(builder.build().toByteString());
	}

	private void handleGetMap(NRequest.GIS protoRequest, NResponse.Envelope.Builder result){
		GISService gisService = NSpringFactory.getInstance().getGISService();
		GisMap map = gisService.getMap(protoRequest.getMapId());
		NResponse.GIS.Builder builder = NResponse.GIS.newBuilder();
		builder.setMap(NResponse.GisMap.newBuilder().setId(map.getId()).setName(map.getName()));
		result.setPayload(builder.build().toByteString());
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}
}
