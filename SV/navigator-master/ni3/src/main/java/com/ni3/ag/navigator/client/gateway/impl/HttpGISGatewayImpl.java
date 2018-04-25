package com.ni3.ag.navigator.client.gateway.impl;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.UserSettings;
import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.GISGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.GISOverlay;
import com.ni3.ag.navigator.shared.domain.GisMap;
import com.ni3.ag.navigator.shared.domain.GisOverlayGeometry;
import com.ni3.ag.navigator.shared.domain.GisOverlayPolygon;
import com.ni3.ag.navigator.shared.domain.GisTerritory;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.GIS.Builder;
import com.ni3.ag.navigator.shared.proto.NResponse.GisPoint;

public class HttpGISGatewayImpl extends AbstractGatewayImpl implements GISGateway{
	private static final Logger log = Logger.getLogger(HttpGISGatewayImpl.class);
	private final static int MAX_RETRY_COUNT = 3;

	@Override
	public GisMap getMap(final int id){
		final NRequest.GIS request = NRequest.GIS.newBuilder().setAction(NRequest.GIS.Action.GET_MAP).setMapId(id).build();
		try{
			ByteString payload = sendRequest(ServletName.GISServlet, request);
			final NResponse.GIS response = NResponse.GIS.parseFrom(payload);
			final NResponse.GisMap protoMap = response.getMap();
			final GisMap result = new GisMap();
			result.setId(protoMap.getId());
			result.setName(protoMap.getName());
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error get map: id " + id, ex);
			return null;
		}
	}

	@Override
	public List<GisMap> getMaps(){
		final NRequest.GIS request = NRequest.GIS.newBuilder().setAction(NRequest.GIS.Action.GET_MAPS).build();
		try{
			ByteString payload = sendRequest(ServletName.GISServlet, request);
			final NResponse.GIS protoResponse = NResponse.GIS.parseFrom(payload);
			final List<GisMap> result = new ArrayList<GisMap>();
			final List<NResponse.GisMap> protoMaps = protoResponse.getMapsList();
			for (final NResponse.GisMap protoMap : protoMaps){
				final GisMap map = new GisMap();
				map.setId(protoMap.getId());
				map.setName(protoMap.getName());

				result.add(map);
			}
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error get maps", ex);
			return null;
		}
	}

	@Override
	public GisOverlayGeometry getOverlayGeometry(final Integer overlayId, final Integer geometryId){
		return getOverlayGeometry(overlayId, geometryId, 0);
	}

	private GisOverlayGeometry getOverlayGeometry(final Integer overlayId, final Integer geometryId, int retryNr){
		GisOverlayGeometry result = null;
		final List<GisOverlayPolygon> polygons = new ArrayList<GisOverlayPolygon>();
		final Builder builder = NRequest.GIS.newBuilder().setAction(NRequest.GIS.Action.GET_OVERLAY_GEOMETRY);
		builder.setOverlayId(overlayId);
		builder.setOverlayGeometryId(geometryId);
		final NRequest.GIS request = builder.build();
		try{
			ByteString payload = sendRequest(ServletName.GISServlet, request, false);
			final NResponse.GisGeometry protoGeometry = NResponse.GisGeometry.parseFrom(payload);
			final List<NResponse.GisPolygon> protoPolygons = protoGeometry.getPolygonsList();
			for (final NResponse.GisPolygon protoPolygon : protoPolygons){
				final List<GisPoint> pointsList = protoPolygon.getPolygon().getPointsList();
				final List<Point2D.Double> points = new ArrayList<Point2D.Double>();
				for (final GisPoint point : pointsList){
					points.add(new Point2D.Double(point.getX(), point.getY()));
				}
				final GisOverlayPolygon polygon = new GisOverlayPolygon(points);
				polygons.add(polygon);
			}
			if (!polygons.isEmpty()){
				result = new GisOverlayGeometry(geometryId, polygons);
			}
		} catch (final IOException ex){
			if (retryNr < MAX_RETRY_COUNT && needRetry(ex)){
				log.info("Got error 503 from server, retry nr. " + retryNr + 1);
				result = getOverlayGeometry(overlayId, geometryId, retryNr + 1);
			} else{
				showErrorAndThrow("Can't get overlay geometry", ex);
			}
		}

		return result;
	}

	@Override
	public List<Integer> getOverlayGeometryList(final Integer overlayId){
		final NRequest.GIS request = NRequest.GIS.newBuilder().setAction(NRequest.GIS.Action.GET_OVERLAY_DATA).setOverlayId(
		        overlayId).build();
		try{
			ByteString payload = sendRequest(ServletName.GISServlet, request);
			final NResponse.GIS protoResponse = NResponse.GIS.parseFrom(payload);
			return protoResponse.getOverlayGeometryIdsList();
		} catch (final IOException ex){
			showErrorAndThrow("Can't get overlay geometry list", ex);
			return null;
		}
	}

	@Override
	public List<GISOverlay> getOverlaysForSchema(final int schemaID){
		final NRequest.GIS request = NRequest.GIS.newBuilder().setAction(NRequest.GIS.Action.GET_OVERLAYS).setSchemaId(
		        schemaID).build();
		try{
			ByteString payload = sendRequest(ServletName.GISServlet, request);
			final NResponse.GIS protoResponse = NResponse.GIS.parseFrom(payload);
			final List<GISOverlay> result = new ArrayList<GISOverlay>();

			final List<NResponse.GisOverlay> protoOverlays = protoResponse.getOverlaysList();
			for (final NResponse.GisOverlay protoOverlay : protoOverlays){
				final GISOverlay overlay = new GISOverlay();
				overlay.setId(protoOverlay.getId());
				overlay.setSchemaId(protoOverlay.getSchemaId());
				overlay.setName(protoOverlay.getName());
				overlay.setLineWidth(protoOverlay.getLineWidth());
				if (protoOverlay.getLineColor() != null){
					overlay.setColor(Color.decode(protoOverlay.getLineColor()));
				}
				overlay.setFilled(protoOverlay.getFilled());
				overlay.setVersion(protoOverlay.getVersion());
				result.add(overlay);
			}
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Can't get overlays for schema", ex);
			return null;
		}
	}

	@Override
	public List<GisTerritory> getTerritories(){
		final NRequest.GIS request = NRequest.GIS.newBuilder().setAction(NRequest.GIS.Action.GET_TERRITORIES).build();
		try{
			ByteString payload = sendRequest(ServletName.GISServlet, request);
			final NResponse.GIS response = NResponse.GIS.parseFrom(payload);
			final List<NResponse.GisTerritory> protoTerritories = response.getTerritoriesList();
			final List<GisTerritory> territories = new ArrayList<GisTerritory>();
			for (final NResponse.GisTerritory protoGT : protoTerritories){
				final GisTerritory gisTerritory = new GisTerritory();
				gisTerritory.setId(protoGT.getId());
				gisTerritory.setLabel(UserSettings.getWord(protoGT.getLabel()));
				gisTerritory.setTerritory(UserSettings.getWord(protoGT.getTerritory()));
				gisTerritory.setTableName(protoGT.getTableName());
				gisTerritory.setVersion(protoGT.getVersion());
				territories.add(gisTerritory);
			}
			return territories;
		} catch (final IOException ex){
			showErrorAndThrow("Error get gis territories", ex);
			return null;
		}
	}

	private boolean needRetry(IOException ex){
		return ex.getMessage() != null && ex.getMessage().contains("503");
	}
}
