package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Point2D;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.gateway.GeoAnalyticsGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.*;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.GisPoint;

public class HttpGeoAnalyticsGatewayImpl extends AbstractGatewayImpl implements GeoAnalyticsGateway{

	@Override
	public boolean deleteThematicMap(final int thematicMapId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.DELETE_THEMATIC_MAP);
		request.setThematicMapId(thematicMapId);
		try{
			sendRequest(ServletName.GeoAnalyticsServlet, request.build());
		} catch (IOException e){
			showErrorAndThrow("Error delete thematic map", e);
			return false;
		}
		return true;
	}

	@Override
	public List<GeoTerritory> getAllGeoTerritories(final int gisTerritoryId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_GEO_TERRITORIES_BY_LAYER);
		request.setGisTerritoryId(gisTerritoryId);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoAnalytics protoResponse = NResponse.GeoAnalytics.parseFrom(payload);

			final List<NResponse.GeoTerritory> protoTerritories = protoResponse.getTerritoriesList();
			final List<GeoTerritory> territories = new ArrayList<GeoTerritory>();
			for (final NResponse.GeoTerritory protoTerritory : protoTerritories){
				final GeoTerritory territory = new GeoTerritory(protoTerritory.getId(), protoTerritory.getSum(),
				        protoTerritory.getNodeCount());
				territory.setName(protoTerritory.getName());
				territories.add(territory);
			}
			return territories;
		} catch (final IOException ex){
			showErrorAndThrow("Error get geo territories", ex);
			return null;
		}
	}

	@Override
	public int getDefaultFolderId(final int schemaId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_DEFAULT_FOLDER_ID);
		request.setSchemaId(schemaId);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoThematicFolder protoResponse = NResponse.GeoThematicFolder.parseFrom(payload);
			return protoResponse.getId();
		} catch (final IOException ex){
			showErrorAndThrow("Error get default folder", ex);
			return 0;
		}
	}

	@Override
	public List<GeoTerritory> getGeoTerritories(final Attribute attribute, final GeoObjectSource source,
	        final List<Integer> nodeIds, final List<Integer> geoTerritoryIds, final int territoryId, int schemaId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_GEO_TERRITORIES);
		request.setAttributeId(attribute.ID);
		request.setGisTerritoryId(territoryId);
		request.setSchemaId(schemaId);
		switch (source){
			case DATABASE:
				request.setSource(NRequest.GeoAnalytics.Source.DATABASE);
				break;
			case GRAPH:
			case MATRIX:
				request.setSource(NRequest.GeoAnalytics.Source.NODE_SET);
				request.addAllNodeIds(nodeIds);
				break;
		}

		if (geoTerritoryIds != null && !geoTerritoryIds.isEmpty()){
			request.addAllGisIds(geoTerritoryIds);
		}

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoAnalytics protoResponse = NResponse.GeoAnalytics.parseFrom(payload);
			final List<NResponse.GeoTerritory> protoTerritories = protoResponse.getTerritoriesList();
			final List<GeoTerritory> aggregations = new ArrayList<GeoTerritory>();
			for (final NResponse.GeoTerritory protoTerritory : protoTerritories){
				final GeoTerritory aggregation = new GeoTerritory(protoTerritory.getId(), protoTerritory.getSum(),
				        protoTerritory.getNodeCount());
				aggregations.add(aggregation);
			}
			return aggregations;
		} catch (final IOException ex){
			showErrorAndThrow("Error get geo territories for current user", ex);
			return null;
		}
	}

	@Override
	public List<GeoTerritory> getGeoTerritoriesForDynamicAttribute(final Attribute attribute, final GeoObjectSource source,
	        final List<Integer> nodeIds, final List<Double> dynamicValues, final List<Integer> geoTerritoryIds,
	        final int gisTerritoryId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_GEO_TERRITORIES_FOR_DYNAMIC_ATTRIBUTE);
		request.setEntityId(attribute.ent.ID);
		request.setGisTerritoryId(gisTerritoryId);
		request.setSource(NRequest.GeoAnalytics.Source.NODE_SET);
		request.addAllNodeIds(nodeIds);
		request.addAllValues(dynamicValues);

		if (geoTerritoryIds != null && !geoTerritoryIds.isEmpty()){
			request.addAllGisIds(geoTerritoryIds);
		}

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoAnalytics protoResponse = NResponse.GeoAnalytics.parseFrom(payload);

			final List<NResponse.GeoTerritory> protoTerritories = protoResponse.getTerritoriesList();
			final List<GeoTerritory> aggregations = new ArrayList<GeoTerritory>();
			for (final NResponse.GeoTerritory protoTerritory : protoTerritories){
				final GeoTerritory aggregation = new GeoTerritory(protoTerritory.getId(), protoTerritory.getSum(),
				        protoTerritory.getNodeCount());
				aggregations.add(aggregation);
			}
			return aggregations;
		} catch (final IOException ex){
			showErrorAndThrow("Error get geo territories for current user", ex);
			return null;
		}
	}

	private List<Point2D.Double> getPointsFromProtoList(final NResponse.GisRing ring){
		final List<Point2D.Double> points = new ArrayList<Point2D.Double>();
		for (final GisPoint p : ring.getPointsList()){
			points.add(new Point2D.Double(p.getX(), p.getY()));
		}
		return points;
	}

	@Override
	public List<Integer> getGeometryIdsByThematicMap(final int thematicMapId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_GEOMETRY_IDS_BY_THEMATIC_MAP);
		request.setThematicMapId(thematicMapId);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoAnalytics protoResponse = NResponse.GeoAnalytics.parseFrom(payload);

			final List<Integer> result = protoResponse.getGeometryIdsList();
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error get thematic data", ex);
			return null;
		}
	}

	@Override
	public List<GISPolygon> getThematicData(final List<Integer> gisIds, final int territoryId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_THEMATIC_DATA_BY_GIS_IDS);
		request.setGisTerritoryId(territoryId);
		request.addAllGisIds(gisIds);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.ThematicData protoResponse = NResponse.ThematicData.parseFrom(payload);

			final List<NResponse.GisPolygon> polygonsList = protoResponse.getPolygonsList();
			final List<GISPolygon> polygons = new ArrayList<GISPolygon>();
			for (final NResponse.GisPolygon protoPolygon : polygonsList){
				final GISPolygon gisPolygon = new GISPolygon(protoPolygon.getGisId());
				gisPolygon.setPoints(getPointsFromProtoList(protoPolygon.getPolygon()));
				if (protoPolygon.getExclusionsCount() > 0){
					for (final NResponse.GisRing exclusion : protoPolygon.getExclusionsList()){
						gisPolygon.addExclusion(getPointsFromProtoList(exclusion));
					}
				}
				polygons.add(gisPolygon);
			}
			return polygons;
		} catch (final IOException ex){
			showErrorAndThrow("Error get thematic data", ex);
			return null;
		}
	}

	@Override
	public List<ThematicFolder> getThematicFoldersWithThematicMaps(final int schemaId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_FOLDERS_WITH_THEMATIC_MAPS);
		request.setSchemaId(schemaId);
		try{
			final List<ThematicFolder> result = new ArrayList<ThematicFolder>();
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoAnalytics protoResponse = NResponse.GeoAnalytics.parseFrom(payload);
			final List<NResponse.GeoThematicFolder> protoFolders = protoResponse.getFoldersList();
			for (final NResponse.GeoThematicFolder protoFolder : protoFolders){
				final ThematicFolder folder = new ThematicFolder(protoFolder.getId(), protoFolder.getName());
				folder.setThematicMaps(new ArrayList<ThematicMap>());
				result.add(folder);
				final List<NResponse.GeoThematicMap> protoMaps = protoFolder.getThematicMapsList();
				for (final NResponse.GeoThematicMap protoMap : protoMaps){
					final ThematicMap tm = new ThematicMap();
					tm.setId(protoMap.getId());
					tm.setName(protoMap.getName());
					tm.setLayerId(protoMap.getLayerId());
					tm.setAttribute(protoMap.getAttribute());
					folder.getThematicMaps().add(tm);
				}
			}
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error get thematic folders", ex);
			return null;
		}
	}

	@Override
	public ThematicMap getThematicMapByName(final String name, final int thematicFolderId, final int schemaId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_THEMATIC_MAP_BY_NAME);
		request.setSchemaId(schemaId);
		final NRequest.GeoThematicMap.Builder tmBuilder = NRequest.GeoThematicMap.newBuilder();
		tmBuilder.setName(name);
		tmBuilder.setFolderId(thematicFolderId);
		request.setThematicMap(tmBuilder);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoThematicMap protoResponse = NResponse.GeoThematicMap.parseFrom(payload);
			final ThematicMap result = new ThematicMap();
			result.setId(protoResponse.getId());
			result.setLayerId(protoResponse.getLayerId());
			result.setName(protoResponse.getName());
			result.setAttribute(protoResponse.getAttribute());
			result.setClusters(new ArrayList<ThematicCluster>());
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error get thematic map", ex);
			return null;
		}
	}

	@Override
	public ThematicMap getThematicMapWithClusters(final int thematicMapId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.GET_THEMATIC_MAP_WITH_CLUSTERS);
		request.setThematicMapId(thematicMapId);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoThematicMap protoResponse = NResponse.GeoThematicMap.parseFrom(payload);
			if (!protoResponse.hasId()){
				return null;
			}
			final ThematicMap result = new ThematicMap();
			result.setId(protoResponse.getId());
			result.setLayerId(protoResponse.getLayerId());
			result.setName(protoResponse.getName());
			result.setAttribute(protoResponse.getAttribute());
			result.setClusters(new ArrayList<ThematicCluster>());

			final List<NResponse.GeoThematicCluster> protoClusters = protoResponse.getClustersList();
			for (final NResponse.GeoThematicCluster protoCluster : protoClusters){
				final ThematicCluster cluster = new ThematicCluster();
				cluster.setId(protoCluster.getId());
				cluster.setFromValue(protoCluster.getFromValue());
				cluster.setToValue(protoCluster.getToValue());
				cluster.setThematicMapId(thematicMapId);
				cluster.setColor(protoCluster.getColor());
				cluster.setDescription(protoCluster.getDescription());
				cluster.setGisIds(protoCluster.getGisIds());
				result.getClusters().add(cluster);
			}
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error get thematic map", ex);
			return null;
		}
	}

	@Override
	public ThematicMap saveThematicMapWithClusters(final ThematicMap thematicMap, final int schemaId){
		final NRequest.GeoAnalytics.Builder request = NRequest.GeoAnalytics.newBuilder();
		request.setAction(NRequest.GeoAnalytics.Action.SAVE_THEMATIC_MAP_WITH_CLUSTERS);
		request.setSchemaId(schemaId);
		final NRequest.GeoThematicMap.Builder tmBuilder = NRequest.GeoThematicMap.newBuilder();
		tmBuilder.setId(thematicMap.getId());
		tmBuilder.setName(thematicMap.getName());
		tmBuilder.setFolderId(thematicMap.getFolderId());
		tmBuilder.setAttribute(thematicMap.getAttribute());
		tmBuilder.setLayerId(thematicMap.getLayerId());

		for (final ThematicCluster cluster : thematicMap.getClusters()){
			final NRequest.GeoThematicCluster.Builder clusterBuilder = NRequest.GeoThematicCluster.newBuilder();
			clusterBuilder.setFromValue(cluster.getFromValue());
			clusterBuilder.setToValue(cluster.getToValue());
			clusterBuilder.setColor(cluster.getColor());
			clusterBuilder.setGisIds(cluster.getGisIds());
			if (cluster.getDescription() != null){
				clusterBuilder.setDescription(cluster.getDescription());
			}
			tmBuilder.addClusters(clusterBuilder);
		}
		request.setThematicMap(tmBuilder);

		try{
			ByteString payload = sendRequest(ServletName.GeoAnalyticsServlet, request.build());
			final NResponse.GeoThematicMap protoResponse = NResponse.GeoThematicMap.parseFrom(payload);
			final ThematicMap result = new ThematicMap();
			result.setId(protoResponse.getId());
			result.setLayerId(protoResponse.getLayerId());
			result.setName(protoResponse.getName());
			result.setAttribute(protoResponse.getAttribute());
			result.setClusters(new ArrayList<ThematicCluster>());
			return result;
		} catch (final IOException ex){
			showErrorAndThrow("Error save thematic map", ex);
			return null;
		}
	}
}
