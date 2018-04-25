/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.geom.Point2D;

import com.ni3.ag.navigator.server.dao.*;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.GISGeometry;
import com.ni3.ag.navigator.server.services.GeoAnalyticsService;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.domain.*;
import org.postgis.*;

public class GeoAnalyticsServiceImpl implements GeoAnalyticsService{

	private GeoAnalyticsDAO geoAnalyticsDAO;
	private GisTerritoryDAO gisTerritoryDAO;
	private ThematicClusterDAO thematicClusterDAO;
	private ThematicMapDAO thematicMapDAO;
	private ThematicFolderDAO thematicFolderDAO;
	private AttributeDAO attributeDAO;

	@Override
	public void deleteThematicMap(final int thematicMapId){
		thematicMapDAO.deleteThematicMap(thematicMapId);
	}

	@Override
	public int createThematicMapWithClusters(ThematicMap tm){
		int id = thematicMapDAO.createThematicMapWithId(tm);
		if (id != tm.getId())
			return id;
		thematicClusterDAO.insertThematicClustersWithIds(tm.getClusters());
		return id;
	}

	@Override
	public int saveThematicMapWithClustersWithIds(ThematicMap thematicMap){
		int thematicMapId = thematicMap.getId();
		thematicMapDAO.updateThematicMap(thematicMap);
		thematicClusterDAO.deleteClustersByThematicMapId(thematicMapId);
		thematicClusterDAO.insertThematicClustersWithIds(thematicMap.getClusters());
		return thematicMapId;
	}

	private void fillPolygon(final Polygon polygon, final GISPolygon gp){
		for (int i = 0; i < polygon.numRings(); i++){
			final LinearRing ring = polygon.getRing(i);

			final List<Point2D.Double> gisPoints = new ArrayList<Point2D.Double>();
			for (final Point point : ring.getPoints()){
				gisPoints.add(new Point2D.Double(point.x, point.y));
			}

			if (i == 0){
				gp.setPoints(gisPoints); // first one is the polygon
			} else{
				gp.addExclusion(gisPoints); // others are exclusion polygons of the first one
			}
		}
	}

	// TODO use folderid from client
	@Override
	public int getDefaultFolderId(final int schemaId){
		ThematicFolder folder = thematicFolderDAO.getThematicFolder(ThematicFolder.DEFAULT_GEO_ANALYTICS_FOLDER_NAME,
				schemaId);
		final int folderId;
		if (folder == null){
			folder = new ThematicFolder(0, ThematicFolder.DEFAULT_GEO_ANALYTICS_FOLDER_NAME);
			folder.setSchemaId(schemaId);
			folderId = thematicFolderDAO.createThematicFolder(folder);
		} else{
			folderId = folder.getId();
		}
		return folderId;
	}

	@Override
	public List<ThematicFolder> getFoldersWithThematicMaps(final int schemaId, final int groupId){
		final List<ThematicFolder> folders = thematicFolderDAO.getThematicFolders(schemaId);
		for (final ThematicFolder folder : folders){
			folder.setThematicMaps(thematicMapDAO.getThematicMapsByFolderId(folder.getId(), groupId));
		}
		return folders;
	}

	@Override
	public List<GeoTerritory> getGeoTerritories(final int gisTerritoryId){
		final GisTerritory territory = gisTerritoryDAO.getTerritory(gisTerritoryId);
		List<GeoTerritory> territories = null;
		if (territory != null){
			territories = geoAnalyticsDAO.getAllGeoTerritories(territory);
		}
		return territories;

	}

	@Override
	public List<GeoTerritory> getGeoTerritories(final int attributeId, final List<Integer> nodeIds,
												final List<Integer> gIds, final int territoryId, int schemaId){
		final Attribute attribute = attributeDAO.getAttribute(attributeId);
		final GisTerritory territory = gisTerritoryDAO.getTerritory(territoryId);
		List<GeoTerritory> territories = null;
		if (attribute != null && territory != null){
			territories = geoAnalyticsDAO.getAggregationsPerTerritory(attribute, nodeIds, gIds, territory.getTableName(), schemaId);
		}
		return territories;
	}

	@Override
	public List<GeoTerritory> getGeoTerritoriesForDynamicAttribute(final int entityId, final List<Integer> nodeIds,
																   final List<Double> values, final List<Integer> gIds, final int gisTerritoryId){
		final GisTerritory territory = gisTerritoryDAO.getTerritory(gisTerritoryId);
		final List<GeoTerritory> geoTerritories = new ArrayList<GeoTerritory>();
		if (territory != null){
			final Map<Integer, Integer> map = geoAnalyticsDAO.getNodeToTerritoryMapping(entityId, nodeIds, gIds, territory
					.getTableName());
			for (final Integer nodeId : map.keySet()){
				final Integer gId = map.get(nodeId);
				final int index = nodeIds.indexOf(nodeId);
				if (index >= 0){
					final Double value = values.get(index);
					GeoTerritory gt = getTerritory(geoTerritories, gId);
					if (gt == null){
						gt = new GeoTerritory(gId, value, 1);
						geoTerritories.add(gt);
					} else{
						gt.setSum(gt.getSum() + value);
						gt.setNodeCount(gt.getNodeCount() + 1);
					}
				}
			}
		}
		return geoTerritories;
	}

	private GeoTerritory getTerritory(final List<GeoTerritory> territories, final Integer gId){
		GeoTerritory result = null;
		for (final GeoTerritory gt : territories){
			if (gt.getId() == gId){
				result = gt;
				break;
			}
		}
		return result;
	}

	public ThematicFolderDAO getThematicFolderDAO(){
		return thematicFolderDAO;
	}

	@Override
	public ThematicMap getThematicMap(final int thematicMapId){
		return thematicMapDAO.getThematicMap(thematicMapId);
	}

	@Override
	public ThematicMap getThematicMapByName(final String name, final int folderId, final int groupId){
		final ThematicMap thematicMap = thematicMapDAO.getThematicMapByName(name, folderId, groupId);
		return thematicMap;
	}

	@Override
	public ThematicMap getThematicMapWithClusters(final int thematicMapId){
		final ThematicMap thematicMap = thematicMapDAO.getThematicMap(thematicMapId);
		if (thematicMap != null){
			thematicMap.setClusters(new ArrayList<ThematicCluster>());
			final List<ThematicCluster> clusters = thematicClusterDAO.getClustersByThematicMapId(thematicMapId);
			thematicMap.setClusters(clusters);
		}
		return thematicMap;
	}

	@Override
	public List<Integer> getGeometryIdsByThematicMap(final int thematicMapId){
		final List<Integer> result = new ArrayList<Integer>();
		final List<ThematicCluster> clusters = thematicClusterDAO.getClustersByThematicMapId(thematicMapId);
		for (final ThematicCluster cluster : clusters){
			final List<Integer> gisIds = Utility.stringToIntegerList(cluster.getGisIds());
			if (gisIds == null || gisIds.isEmpty()){
				continue;
			}
			result.addAll(gisIds);
		}
		return result;
	}

	@Override
	public List<GISPolygon> getThematicPolygons(final List<Integer> gisIds, final int gisTerritoryId){
		final GisTerritory gisTerritory = gisTerritoryDAO.getTerritory(gisTerritoryId);
		final String geoTableName = gisTerritory.getTableName();
		final List<GISPolygon> gisPolygons = loadThematicData(geoTableName, gisIds);
		return gisPolygons;
	}

	private List<GISPolygon> loadThematicData(final String geoTableName, final List<Integer> gisIds){
		final List<GISPolygon> gisPolygons = new ArrayList<GISPolygon>();
		final List<GISGeometry> data = geoAnalyticsDAO.getThematicData(gisIds, geoTableName);

		for (final GISGeometry geom : data){
			final PGgeometry geometry = geom.getGeometry();
			if (geometry.getGeoType() != Geometry.MULTIPOLYGON){
				continue;
			}
			final MultiPolygon multiPolygon = (MultiPolygon) geometry.getGeometry();
			for (final Polygon dbPolygon : multiPolygon.getPolygons()){
				final GISPolygon gisPolygon = new GISPolygon(geom.getId());
				fillPolygon(dbPolygon, gisPolygon);
				gisPolygons.add(gisPolygon);
			}
		}
		return gisPolygons;
	}

	@Override
	public int saveThematicMapWithClusters(final ThematicMap thematicMap){
		int thematicMapId = thematicMap.getId();
		if (thematicMapId > 0){
			thematicMapDAO.updateThematicMap(thematicMap);
			thematicClusterDAO.deleteClustersByThematicMapId(thematicMapId);
		} else{
			thematicMapId = thematicMapDAO.createThematicMap(thematicMap);
		}

		if (thematicMapId > 0){
			for (final ThematicCluster cluster : thematicMap.getClusters()){
				cluster.setThematicMapId(thematicMapId);
				//TODO rewrite to batch insert
				thematicClusterDAO.insertThematicCluster(cluster);
			}
		}
		return thematicMapId;
	}

	public void setAttributeDAO(final AttributeDAO attributeDAO){
		this.attributeDAO = attributeDAO;
	}

	public void setGeoAnalyticsDAO(final GeoAnalyticsDAO geoAnalyticsDAO){
		this.geoAnalyticsDAO = geoAnalyticsDAO;
	}

	public void setGisTerritoryDAO(final GisTerritoryDAO gisTerritoryDAO){
		this.gisTerritoryDAO = gisTerritoryDAO;
	}

	public void setThematicClusterDAO(final ThematicClusterDAO thematicClusterDAO){
		this.thematicClusterDAO = thematicClusterDAO;
	}

	public void setThematicFolderDAO(final ThematicFolderDAO thematicFolderDAO){
		this.thematicFolderDAO = thematicFolderDAO;
	}

	public void setThematicMapDAO(final ThematicMapDAO thematicMapDAO){
		this.thematicMapDAO = thematicMapDAO;
	}
}
