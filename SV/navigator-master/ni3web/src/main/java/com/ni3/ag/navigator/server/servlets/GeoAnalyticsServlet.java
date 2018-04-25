/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.servlets;

import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.services.GeoAnalyticsService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.GISPolygon;
import com.ni3.ag.navigator.shared.domain.GeoTerritory;
import com.ni3.ag.navigator.shared.domain.ThematicCluster;
import com.ni3.ag.navigator.shared.domain.ThematicFolder;
import com.ni3.ag.navigator.shared.domain.ThematicMap;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.GeoAnalytics;
import com.ni3.ag.navigator.shared.proto.NRequest.GeoThematicMap;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class GeoAnalyticsServlet extends Ni3Servlet{
	private static final long serialVersionUID = -3177389445051539482L;
	private GeoAnalytics gaProtoRequest;
	private int newThematicMap;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		final InputStream is = request.getInputStream();
		NRequest.GeoAnalytics protoRequest = NRequest.GeoAnalytics.parseFrom(is);
		gaProtoRequest = protoRequest;
		NResponse.Envelope.Builder resultBuilder = NResponse.Envelope.newBuilder();

		switch (protoRequest.getAction()){
			case GET_GEO_TERRITORIES:
				handleGetGeoTerritories(protoRequest, resultBuilder);
				break;
			case GET_GEOMETRY_IDS_BY_THEMATIC_MAP:
				handleGetGeometryIdsByThematicMap(protoRequest, resultBuilder);
				break;
			case GET_THEMATIC_DATA_BY_GIS_IDS:
				handleGetThematicDataByGisIds(protoRequest, resultBuilder);
				break;
			case GET_GEO_TERRITORIES_BY_LAYER:
				handleGetGeoTerritoriesByLayer(protoRequest, resultBuilder);
				break;
			case GET_GEO_TERRITORIES_FOR_DYNAMIC_ATTRIBUTE:
				handleGetGeoTerritoriesForDynamicAttribute(protoRequest, resultBuilder);
				break;
			case GET_FOLDERS_WITH_THEMATIC_MAPS:
				handleGetFoldersWithThematicMaps(protoRequest, resultBuilder);
				break;
			case GET_THEMATIC_MAP_WITH_CLUSTERS:
				handleGetThematicMapWithClusters(protoRequest, resultBuilder);
				break;
			case GET_THEMATIC_MAP_BY_NAME:
				handleGetThematicMapByName(protoRequest, resultBuilder);
				break;
			case SAVE_THEMATIC_MAP_WITH_CLUSTERS:
				handleSaveThematicMapWithClusters(protoRequest, resultBuilder);
				break;
			case GET_DEFAULT_FOLDER_ID:
				handleGetDefaultFolderId(protoRequest, resultBuilder);
				break;
			case DELETE_THEMATIC_MAP:
				handleDeleteThematicMap(protoRequest);
				break;
		}

		resultBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		resultBuilder.build().writeTo(response.getOutputStream());
	}

	private void handleDeleteThematicMap(GeoAnalytics protoRequest){
		final int thematicMapId = protoRequest.getThematicMapId();

		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		geoAnalyticsService.deleteThematicMap(thematicMapId);
	}

	private void handleGetDefaultFolderId(GeoAnalytics protoRequest, Builder resultBuilder){
		final int schemaId = protoRequest.getSchemaId();

		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final int thematicFolderId = geoAnalyticsService.getDefaultFolderId(schemaId);
		NResponse.GeoThematicFolder.Builder tfProto = NResponse.GeoThematicFolder.newBuilder();
		tfProto.setId(thematicFolderId);

		resultBuilder.setPayload(tfProto.build().toByteString());
	}

	private void handleGetThematicMapByName(GeoAnalytics protoRequest, Builder resultBuilder){
		final GeoThematicMap reqTm = protoRequest.getThematicMap();
		final String name = reqTm.getName();
		final int folderId = reqTm.getFolderId();
		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		final Group group = groupDAO.getByUser(storage.getCurrentUser().getId());

		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final ThematicMap thematicMap = geoAnalyticsService.getThematicMapByName(name, folderId, group.getId());
		NResponse.GeoThematicMap.Builder tmProto = NResponse.GeoThematicMap.newBuilder();
		if (thematicMap != null){
			tmProto.setId(thematicMap.getId());
			tmProto.setName(thematicMap.getName());
			tmProto.setLayerId(thematicMap.getLayerId());
			tmProto.setAttribute(thematicMap.getAttribute());
		}

		resultBuilder.setPayload(tmProto.build().toByteString());
	}

	private void handleSaveThematicMapWithClusters(GeoAnalytics protoRequest, Builder resultBuilder){
		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		final Group group = groupDAO.getByUser(storage.getCurrentUser().getId());

		final NRequest.GeoThematicMap protoTM = protoRequest.getThematicMap();
		final ThematicMap thematicMap = new ThematicMap();
		thematicMap.setId(protoTM.getId());
		thematicMap.setName(protoTM.getName());
		thematicMap.setFolderId(protoTM.getFolderId());
		thematicMap.setLayerId(protoTM.getLayerId());
		thematicMap.setAttribute(protoTM.getAttribute());
		thematicMap.setGroupId(group.getId());
		thematicMap.setClusters(new ArrayList<ThematicCluster>());

		final List<NRequest.GeoThematicCluster> protoClusters = protoTM.getClustersList();

		for (NRequest.GeoThematicCluster protoCluster : protoClusters){
			final ThematicCluster cluster = new ThematicCluster();
			cluster.setFromValue(protoCluster.getFromValue());
			cluster.setToValue(protoCluster.getToValue());
			cluster.setColor(protoCluster.getColor());
			cluster.setGisIds(protoCluster.getGisIds());
			if (protoCluster.hasDescription())
				cluster.setDescription(protoCluster.getDescription());
			thematicMap.getClusters().add(cluster);
		}

		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		int id = geoAnalyticsService.saveThematicMapWithClusters(thematicMap);
		if (id > 0){
			ThematicMap tm = geoAnalyticsService.getThematicMap(id);
			NResponse.GeoThematicMap.Builder tmProto = NResponse.GeoThematicMap.newBuilder();
			tmProto.setId(tm.getId());
			tmProto.setName(tm.getName());
			tmProto.setLayerId(tm.getLayerId());
			tmProto.setAttribute(tm.getAttribute());

			resultBuilder.setPayload(tmProto.build().toByteString());
		}
		newThematicMap = id;
	}

	private void handleGetThematicMapWithClusters(GeoAnalytics protoRequest, Builder resultBuilder){
		final int thematicMapId = protoRequest.getThematicMapId();
		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final ThematicMap thematicMap = geoAnalyticsService.getThematicMapWithClusters(thematicMapId);
		NResponse.GeoThematicMap.Builder tmProto = NResponse.GeoThematicMap.newBuilder();
		if (thematicMap != null){
			tmProto.setId(thematicMap.getId());
			tmProto.setName(thematicMap.getName());
			tmProto.setLayerId(thematicMap.getLayerId());
			tmProto.setAttribute(thematicMap.getAttribute());
			for (ThematicCluster cluster : thematicMap.getClusters()){
				NResponse.GeoThematicCluster.Builder clusterProto = NResponse.GeoThematicCluster.newBuilder();
				clusterProto.setId(cluster.getId());
				clusterProto.setFromValue(cluster.getFromValue());
				clusterProto.setToValue(cluster.getToValue());
				clusterProto.setColor(cluster.getColor());
				if (cluster.getDescription() != null)
					clusterProto.setDescription(cluster.getDescription());
				clusterProto.setGisIds(cluster.getGisIds());
				tmProto.addClusters(clusterProto);
			}
		}
		resultBuilder.setPayload(tmProto.build().toByteString());
	}

	private void handleGetFoldersWithThematicMaps(GeoAnalytics protoRequest, Builder resultBuilder){
		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final int schemaId = protoRequest.getSchemaId();
		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		final Group group = groupDAO.getByUser(storage.getCurrentUser().getId());

		final List<ThematicFolder> folders = geoAnalyticsService.getFoldersWithThematicMaps(schemaId, group.getId());
		NResponse.GeoAnalytics.Builder gaProto = NResponse.GeoAnalytics.newBuilder();
		for (ThematicFolder folder : folders){
			NResponse.GeoThematicFolder.Builder folderProto = NResponse.GeoThematicFolder.newBuilder();
			folderProto.setId(folder.getId());
			folderProto.setName(folder.getName());
			folderProto.setSchemaId(folder.getSchemaId());
			for (ThematicMap thematicMap : folder.getThematicMaps()){
				NResponse.GeoThematicMap.Builder tmProto = NResponse.GeoThematicMap.newBuilder();
				tmProto.setId(thematicMap.getId());
				tmProto.setName(thematicMap.getName());
				tmProto.setLayerId(thematicMap.getLayerId());
				tmProto.setAttribute(thematicMap.getAttribute());
				folderProto.addThematicMaps(tmProto);
			}
			gaProto.addFolders(folderProto);
		}
		resultBuilder.setPayload(gaProto.build().toByteString());
	}

	private void handleGetGeometryIdsByThematicMap(GeoAnalytics protoRequest, Builder resultBuilder){
		GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final int thematicMapId = protoRequest.getThematicMapId();

		List<Integer> gIds = geoAnalyticsService.getGeometryIdsByThematicMap(thematicMapId);
		NResponse.GeoAnalytics.Builder dataBuilder = NResponse.GeoAnalytics.newBuilder();
		dataBuilder.addAllGeometryIds(gIds);
		resultBuilder.setPayload(dataBuilder.build().toByteString());
	}

	private void handleGetThematicDataByGisIds(GeoAnalytics protoRequest, Builder resultBuilder){
		final List<Integer> gisIds = protoRequest.getGisIdsList();
		final int gisTerritoryId = protoRequest.getGisTerritoryId();

		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final List<GISPolygon> polygons = geoAnalyticsService.getThematicPolygons(gisIds, gisTerritoryId);
		final NResponse.ThematicData.Builder dataBuilder = NResponse.ThematicData.newBuilder();
		for (GISPolygon gisPolygon : polygons){
			final NResponse.GisPolygon.Builder polyBuilder = NResponse.GisPolygon.newBuilder();
			polyBuilder.setGisId(gisPolygon.getGisId());
			polyBuilder.setPolygon(createRing(gisPolygon.getPoints()));

			final List<List<Double>> exclusions = gisPolygon.getExclusions();
			if (exclusions != null && !exclusions.isEmpty()){
				for (List<Double> exclusion : exclusions){
					polyBuilder.addExclusions(createRing(exclusion));
				}
			}

			dataBuilder.addPolygons(polyBuilder);
		}
		resultBuilder.setPayload(dataBuilder.build().toByteString());
	}

	private NResponse.GisRing.Builder createRing(List<Double> points){
		NResponse.GisRing.Builder builder = NResponse.GisRing.newBuilder();
		if (points != null){
			for (Double point : points){
				NResponse.GisPoint.Builder gp = NResponse.GisPoint.newBuilder();
				gp.setX(point.x);
				gp.setY(point.y);
				builder.addPoints(gp);
			}
		}
		return builder;
	}

	private void handleGetGeoTerritories(GeoAnalytics protoRequest, Builder resultBuilder){
		final int attributeId = protoRequest.getAttributeId();
		final int gisTerritoryId = protoRequest.getGisTerritoryId();
		final int schemaId = protoRequest.getSchemaId();
		final NRequest.GeoAnalytics.Source source = protoRequest.getSource();
		List<Integer> nodeIds = null;
		switch (source){
			case DATABASE:
				break;
			case NODE_SET:
				nodeIds = protoRequest.getNodeIdsList();
				break;
		}

		final List<Integer> gIds = protoRequest.getGisIdsList();
		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final List<GeoTerritory> territories = geoAnalyticsService.getGeoTerritories(attributeId, nodeIds, gIds,
		        gisTerritoryId, schemaId);

		writeTerritoriesToOutput(resultBuilder, territories);
	}

	private void handleGetGeoTerritoriesForDynamicAttribute(GeoAnalytics protoRequest, Builder resultBuilder){
		final int entityId = protoRequest.getEntityId();
		final int gisTerritoryId = protoRequest.getGisTerritoryId();
		final List<Integer> nodeIds = protoRequest.getNodeIdsList();
		final List<java.lang.Double> values = protoRequest.getValuesList();
		final List<Integer> gIds = protoRequest.getGisIdsList();
		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final List<GeoTerritory> territories = geoAnalyticsService.getGeoTerritoriesForDynamicAttribute(entityId, nodeIds,
		        values, gIds, gisTerritoryId);

		writeTerritoriesToOutput(resultBuilder, territories);
	}

	private void handleGetGeoTerritoriesByLayer(GeoAnalytics protoRequest, Builder resultBuilder){
		final int gisTerritoryId = protoRequest.getGisTerritoryId();

		final GeoAnalyticsService geoAnalyticsService = NSpringFactory.getInstance().getGeoAnalyticsService();
		final List<GeoTerritory> territories = geoAnalyticsService.getGeoTerritories(gisTerritoryId);

		writeTerritoriesToOutput(resultBuilder, territories);
	}

	private void writeTerritoriesToOutput(Builder resultBuilder, List<GeoTerritory> territories){
		NResponse.GeoAnalytics.Builder builder = NResponse.GeoAnalytics.newBuilder();
		if (territories != null){
			for (GeoTerritory territory : territories){
				NResponse.GeoTerritory.Builder protoTerritory = NResponse.GeoTerritory.newBuilder();
				protoTerritory.setId(territory.getId());
				protoTerritory.setSum(territory.getSum());
				protoTerritory.setNodeCount(territory.getNodeCount());
				if (territory.getName() != null && !territory.getName().isEmpty()){
					protoTerritory.setName(territory.getName());
				}
				builder.addTerritories(protoTerritory);
			}
		}
		resultBuilder.setPayload(builder.build().toByteString());
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}

	protected DeltaHeader getTransactionDeltaForRequest(){
		User user = NSpringFactory.getInstance().getThreadLocalStorage().getCurrentUser();
		switch (gaProtoRequest.getAction()){
			case SAVE_THEMATIC_MAP_WITH_CLUSTERS:{
				Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
				params.put(DeltaParamIdentifier.SaveGeoAnalyticsId,
						new DeltaParam(DeltaParamIdentifier.SaveGeoAnalyticsId, "" + newThematicMap));
				return new DeltaHeader(DeltaType.GEO_ANALYTICS_SAVE, user, params);
			}
			case DELETE_THEMATIC_MAP:{
				Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
				params.put(DeltaParamIdentifier.DeleteGeoAnalyticsId,
						new DeltaParam(DeltaParamIdentifier.DeleteGeoAnalyticsId, "" + gaProtoRequest.getThematicMapId()));
				return new DeltaHeader(DeltaType.GEO_ANALYTICS_DELETE, user, params);
			}
			default:
				return DeltaHeader.DO_NOTHING;
		}
	}
}
