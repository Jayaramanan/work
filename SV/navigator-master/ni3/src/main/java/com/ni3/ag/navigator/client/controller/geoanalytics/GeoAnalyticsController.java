/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.geoanalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.GeoAnalyticsCache;
import com.ni3.ag.navigator.client.gateway.GISGateway;
import com.ni3.ag.navigator.client.gateway.GeoAnalyticsGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGISGatewayImpl;
import com.ni3.ag.navigator.client.gateway.impl.HttpGeoAnalyticsGatewayImpl;
import com.ni3.ag.navigator.client.gui.datalist.DBObjectList;
import com.ni3.ag.navigator.client.gui.geoanalytics.*;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GeoAnalyticsModel;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.*;
import org.apache.log4j.Logger;

public class GeoAnalyticsController{
	private static final Logger log = Logger.getLogger(GeoAnalyticsController.class);
	private static final String DEFAULT_NAME_FOR_FAVORITE = "@@FAV";
	private Color initialStartColor;
	private Color initialEndColor;
	private GeoAnalyticsFrame dlg;
	private GeoAnalyticsFilterDialog filterDlg;
	private GeoAnalyticsModel model;
	private Ni3Document doc;
	private GeoAnalyticsGateway gaGateway;

	private static GeoAnalyticsController instance;

	private GeoAnalyticsController(Ni3Document doc){
		this.doc = doc;
		this.model = new GeoAnalyticsModel();
		String startColor = UserSettings.getStringAppletProperty("GEO_ANALYTICS_GRADIENT_START_COLOR", "132,168,2");
		initialStartColor = Utility.createColor(startColor);
		String endColor = UserSettings.getStringAppletProperty("GEO_ANALYTICS_GRADIENT_END_COLOR", "216,235,200");
		initialEndColor = Utility.createColor(endColor);
		model.setStartColor(initialStartColor);
		model.setEndColor(initialEndColor);

		gaGateway = new HttpGeoAnalyticsGatewayImpl();
	}

	public static GeoAnalyticsController getInstance(Ni3Document doc){
		if (instance == null){
			instance = new GeoAnalyticsController(doc);
		}
		return instance;
	}

	public void showDialog(){
		if (dlg == null){
			createDialog();
		}
		dlg.setVisible(true);
	}
	
	public void invalidateDialogData(){
		dlg = null;
	}

	public void createDialog(){
		dlg = new GeoAnalyticsFrame();
		initListeners();
		initEntities(doc.DB.schema.definitions);
		initLayers();
		dlg.setSaveButtonEnabled(false);
		dlg.setShowButtonEnabled(false);
		dlg.setLocation(SystemGlobals.MainFrame.getLocation());
	}

	private void initListeners(){
		dlg.addEntityComboListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				final Entity entity = dlg.getSelectedEntity();
				initAttributes(entity);
				clearGeoTerritories();
			}
		});

		dlg.addSourceComboListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				final Entity entity = dlg.getSelectedEntity();
				initAttributes(entity);
				clearGeoTerritories();
			}
		});

		dlg.addAttributeComboListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				clearGeoTerritories();
			}
		});

		dlg.addLayerComboListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				clearGeoTerritories();
			}
		});

		dlg.addGetTerritoriesButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				clearGeoTerritories();
				loadGeoTerritoryData();
				if (model.getTerritories() != null && model.getTerritories().size() == 1)
					JOptionPane.showMessageDialog(dlg, UserSettings.getWord("MsgResultContainsOnly1Cluster").replace("\\n",
							"\n"));
			}
		});

		dlg.addClusterCountComboListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				updateSliders();
				populateClusterTable(true);
			}
		});

		dlg.addAvgButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (((JRadioButton) e.getSource()).isSelected()){
					updateDiagram();
					populateClusterTable(true);
				}
			}
		});

		dlg.addSumButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (((JRadioButton) e.getSource()).isSelected()){
					updateDiagram();
					populateClusterTable(true);
				}
			}
		});
		dlg.addShowButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showGeoAnalytics();
			}
		});

		dlg.addSliderChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				//save old cluster colors
				List<Cluster> oldClusters = new ArrayList<Cluster>();
				oldClusters.addAll(model.getClusters());
				//do recalc
				populateClusterTable(true);
				//restore old colors
				for(int i = 0; i < Math.max(model.getClusters().size(), oldClusters.size()); i++){
					Color oldColor = Color.BLACK;
					if(i < oldClusters.size())
						oldColor = oldClusters.get(i).getColor();
					if(i < model.getClusters().size())
						model.getClusters().get(i).setColor(oldColor);
				}
			}
		});

		dlg.addFilterButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showFilter();
			}
		});

		dlg.addSaveButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				saveThematicMap();
			}
		});

		dlg.addDeleteButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				deleteThematicMaps();
			}
		});

		dlg.addTableMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				if (e.getButton() == MouseEvent.BUTTON3){
					shopTablePopup(e.getPoint());
				}
			}
		});
	}

	protected void clearGeoTerritories(){
		if (model.getTerritories() != null && !model.getTerritories().isEmpty()){
			model.setTerritories(null);
			populateClusterTable(false);
			updateDiagram();
		}
	}

	private void initLayers(){
		GISGateway gisGateway = new HttpGISGatewayImpl();
		List<GisTerritory> territories = gisGateway.getTerritories();
		dlg.fillLayerCombo(territories);

		for (GisTerritory gt : territories){
			if (gt.getTableName() != null && !gt.getTableName().isEmpty()){
				final List<GeoTerritory> geoTerritories = gaGateway.getAllGeoTerritories(gt.getId());
				model.getAllTerritoryMap().put(gt.getId(), geoTerritories);
			}
		}
	}

	private void initEntities(List<Entity> allEntities){
		List<Entity> entities = new ArrayList<Entity>();
		for (Entity entity : allEntities){
			if (entity.CanRead && entity.isNode()){
				entities.add(entity);
			}
		}
		dlg.fillEntitiesCombo(entities);
	}

	private void initAttributes(Entity entity){
		GeoObjectSource source = dlg.getSelectedSource();
		List<Attribute> attributes = new ArrayList<Attribute>();
		if (entity != null){
			for (final Attribute a : entity.getReadableAttributes()){
				if (a.isNumericAttribute() && !a.isSystemAttribute() && !a.predefined && !a.inContext
						&& (source == GeoObjectSource.GRAPH || !a.isDynamic())){
					attributes.add(a);
				}
			}
		}
		dlg.fillAttributesCombo(attributes);
	}

	private void showGeoAnalytics(){
		dlg.stopTableEditing();
		boolean sumMode = dlg.isSumMode();
		if (model.getTerritories() != null && !model.getTerritories().isEmpty()){
			if (model.getClusters() == null){
				model.setClusters(getClusters(sumMode));
			}

			final GisTerritory selectedLayer = dlg.getSelectedLayer();
			doc.setGeoLegendData(model.getClusters(), dlg.getSelectedAttribute(), selectedLayer);

			if (selectedLayer != null){
				loadThematicDataAsync();
			}
		}
	}

	private void loadThematicDataAsync(){
		doc.clearThematicData();
		doc.setTerritoryTotalCount(model.getTerritories().size());

		new Thread(){
			@Override
			public void run(){
				final long id = System.currentTimeMillis();
				doc.setGeoAnalyticsLoadThreadId(id);
				boolean loaded = true;
				final GisTerritory layer = model.getLayer();
				final List<GeoTerritory> territories = model.getTerritories();
				final List<Cluster> clusters = model.getClusters();
				for (int i = 0; i < territories.size(); i++){
					final GeoTerritory territory = territories.get(i);
					final int geometryId = territory.getId();
					log.debug("Loading polygons for territory " + geometryId);
					final GeoAnalyticsCache cache = GeoAnalyticsCache.getInstance();
					List<GISPolygon> thematicData = cache.getPolygons(layer.getId(), geometryId, layer.getVersion());
					if (thematicData == null){
						log.debug("Polygons for territory " + geometryId + " not found in cache, downloading");
						final GeoAnalyticsGateway gateway = new HttpGeoAnalyticsGatewayImpl();
						final List<Integer> gisIds = new ArrayList<Integer>();
						gisIds.add(geometryId);
						thematicData = gateway.getThematicData(gisIds, layer.getId());
						log.debug("Loaded polygon count: " + (thematicData != null ? thematicData.size() : 0));
						if (thematicData != null && !thematicData.isEmpty()){
							cache.savePolygons(layer.getId(), geometryId, layer.getVersion(), thematicData);
						}
					}

					if (thematicData != null && !thematicData.isEmpty()){
						GisThematicGeometry geometry = new GisThematicGeometry(geometryId, thematicData);
						geometry.setColor(getColor(geometryId, clusters));
						if (doc.getGeoAnalyticsLoadThreadId() != id){
							log.debug("Loading stopped for geo analytics");
							loaded = false;
							break; // stop the loading
						}
						doc.addThematicData(geometry);
					}
				}
				if (loaded){
					doc.finishTerritoryLoad();
					dlg.setSaveButtonEnabled(true);
				}
			}
		}.start();
	}

	private void loadGeoTerritoryData(){
		final Attribute attribute = dlg.getSelectedAttribute();
		final GeoObjectSource source = dlg.getSelectedSource();
		final GisTerritory layer = dlg.getSelectedLayer();
		if (attribute == null || source == null || layer == null){
			return; // not all parameters are selected
		}

		List<Integer> nodeIds = null;
		List<Double> dynamicValues = null;
		switch (source){
			case GRAPH:
				final List<Node> nodes = getNodesFromGraph(attribute);
				nodeIds = getNodeIds(nodes);
				if (attribute.isDynamic()){
					dynamicValues = fillValuesForDynamicAttribute(attribute, nodes);
				}
				filterNullValues(nodeIds, dynamicValues);
				break;
			case MATRIX:
				nodeIds = getNodeIdsFromMatrix();
				break;
		}
		if ((source == GeoObjectSource.GRAPH || source == GeoObjectSource.MATRIX) && (nodeIds == null || nodeIds.isEmpty())){
			log.debug("No nodes found for geo-analytics");
			return; // no nodes are on graph or in matrix
		}

		List<Integer> geoTerritoryIds = null;
		if (model.getFilteredOutTerritories() != null && !model.getFilteredOutTerritories().isEmpty()){
			geoTerritoryIds = new ArrayList<Integer>();
			for (GeoTerritory territory : model.getAllGeoTerritories(layer.getId())){
				if (!model.getFilteredOutTerritories().contains(territory)){
					geoTerritoryIds.add(territory.getId());
				}
			}
			if (geoTerritoryIds.isEmpty()){
				log.debug("No territories are selected in filter");
				return;
			}
		}

		final GeoAnalyticsGateway gateway = new HttpGeoAnalyticsGatewayImpl();

		List<GeoTerritory> aggregations;
		if (attribute.isDynamic() && dynamicValues != null){
			aggregations = gateway.getGeoTerritoriesForDynamicAttribute(attribute, source, nodeIds, dynamicValues,
					geoTerritoryIds, layer.getId());
		} else{
			aggregations = gateway.getGeoTerritories(attribute, source, nodeIds, geoTerritoryIds, layer.getId(), doc.getSchemaId());
		}

		model.setTerritories(aggregations);
		model.setClusters(null);
		model.setLayer(layer);
		model.setEntity(dlg.getSelectedEntity());
		model.setAttribute(attribute);
		model.setSource(source);
		log.debug("Loaded geo territories count: " + (model.getTerritories() != null ? model.getTerritories().size() : 0));

		updateSliders();
		updateDiagram();
		populateClusterTable(true);
	}

	private void filterNullValues(List<Integer> nodeIds, List<Double> dynamicValues){
		if (nodeIds == null || dynamicValues == null)
			return;
		List<Integer> ids = new ArrayList<Integer>(nodeIds.size());
		List<Double> values = new ArrayList<Double>(dynamicValues.size());
		for (int i = 0; i < dynamicValues.size(); i++){
			Double d = dynamicValues.get(i);
			int id = nodeIds.get(i);
			if (d == null)
				continue;
			ids.add(id);
			values.add(d);
		}
		nodeIds.clear();
		nodeIds.addAll(ids);
		dynamicValues.clear();
		dynamicValues.addAll(values);
	}

	private List<Double> fillValuesForDynamicAttribute(Attribute attribute, List<Node> nodes){
		List<Double> values = new ArrayList<Double>();
		for (Node node : nodes){
			final Object value = node.Obj.getValue(attribute.ID);
			values.add((Double) value);
		}
		return values;
	}

	private List<Integer> getNodeIdsFromMatrix(){
		List<Integer> nodeIds = null;
		final Entity entity = dlg.getSelectedEntity();
		// TODO nodes for matrix should be stored in Ni3Document, not in ItemsPanel itself
		final List<DBObjectList> matrix = SystemGlobals.MainFrame.itemsPanel.getMatrixLists();
		for (final DBObjectList obj : matrix){
			if (obj.listDescription.getEntity().ID == entity.ID){
				nodeIds = obj.listDescription.getNodeIds();
				break;
			}
		}
		return nodeIds;
	}

	private List<Node> getNodesFromGraph(Attribute attribute){
		final List<Node> nodes = doc.Subgraph.getDisplayedNodes();
		final List<Node> result = new ArrayList<Node>();
		for (Node node : nodes){
			if (attribute.ent.ID == node.Obj.getEntity().ID){
				result.add(node);
			}
		}
		return result;
	}

	private List<Integer> getNodeIds(List<Node> nodes){
		List<Integer> nodeIds = new ArrayList<Integer>();
		for (Node node : nodes){
			nodeIds.add(node.ID);
		}
		return nodeIds;
	}

	private void updateSliders(){
		Integer clusterCount = dlg.getSelectedClusterCount();
		final List<GeoTerritory> territories = model.getTerritories();
		if (clusterCount != null && territories != null && !territories.isEmpty()){
			if (territories.size() <= clusterCount){
				clusterCount = territories.size();
				dlg.setSelectedClusterCount(clusterCount);
				dlg.setSliderVisible(false);
			} else{
				dlg.setSliderVisible(true);
				final int thumbCount = clusterCount - 1;
				List<Integer> positions = calculatePositions(thumbCount, territories.size());
				dlg.setSliderValues(territories.size(), positions);
			}
		}
	}

	private void updateDiagram(){
		boolean sumMode = dlg.isSumMode();
		List<Double> data = new ArrayList<Double>();
		final List<GeoTerritory> territories = model.getTerritories();
		if (territories != null && territories.size() > 0){
			Collections.sort(territories, new GisAggregationComparator(sumMode));
			for (GeoTerritory ga : territories){
				data.add(ga.getValue(sumMode));
			}
		}

		dlg.setDiagramData(data);
	}

	private List<Integer> calculatePositions(int thumbCount, int aggregationCount){
		double coeff = (double) aggregationCount / (thumbCount + 1);
		List<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < thumbCount; i++){
			positions.add((int) ((i + 1) * coeff));
		}
		return positions;
	}

	private List<Cluster> getClusters(boolean sumMode){
		final List<Cluster> clusters = new ArrayList<Cluster>();
		final int clusterCount = dlg.getSelectedClusterCount();
		final List<GeoTerritory> territories = model.getTerritories();
		if (territories.size() > 1){
			if (territories.size() > clusterCount){
				final List<Integer> values = dlg.getSliderValues();
				Collections.sort(values);
				int fromIndex = 0, toIndex = 0;
				for (int i = 0; i < clusterCount - 1 && i < values.size(); i++){
					if (i > 0){
						fromIndex = toIndex;
					}
					toIndex = values.get(i);
					final Cluster cluster = createCluster(sumMode, fromIndex, toIndex, i == 0);
					clusters.add(cluster);
				}

				if (toIndex < territories.size() - 1){
					final Cluster cluster = createCluster(sumMode, toIndex, territories.size() - 1, false);
					clusters.add(cluster);
				}
			} else{
				for (int i = 0; i < territories.size(); i++){
					GeoTerritory territory = territories.get(i);
					double value = territory.getValue(sumMode);
					Cluster cluster = new Cluster(value, value);
					cluster.addTerritory(territory);
					clusters.add(cluster);
				}
			}

			updateClusterColors(clusters);
		}

		log.debug("Calculated cluster count: " + clusters.size());
		return clusters;
	}

	private Cluster createCluster(boolean sumMode, int fromIndex, int toIndex, boolean first){
		final List<GeoTerritory> territories = model.getTerritories();
		final double from = territories.get(fromIndex).getValue(sumMode);
		final double to = territories.get(toIndex).getValue(sumMode);
		final Cluster cluster = new Cluster(from, to);
		for (int k = first ? fromIndex : fromIndex + 1; k <= toIndex && k < territories.size(); k++){
			cluster.addTerritory(territories.get(k));
		}
		return cluster;
	}

	private Color getColor(Integer geometryId, List<Cluster> clusters){
		Color color = null;
		for (Cluster cluster : clusters){
			if (containsPolygon(cluster, geometryId)){
				color = cluster.getColor();
				break;
			}
		}
		if (color == null){
			log.warn("Gis polygon with gisId = " + geometryId + " is not in any cluster");
			color = Color.BLACK;
		}
		return color;
	}

	private boolean containsPolygon(Cluster cluster, int gisId){
		boolean result = false;
		for (GeoTerritory territory : cluster.getTerritories()){
			if (territory.getId() == gisId){
				result = true;
				break;
			}
		}
		return result;
	}

	private void populateClusterTable(boolean reset){
		boolean sumMode = dlg.isSumMode();
		final List<GeoTerritory> territories = model.getTerritories();
		if (territories != null && !territories.isEmpty()){
			if (reset || model.getClusters() == null){
				model.setClusters(getClusters(sumMode));
			} else{
				updateClusterRanges(model.getClusters(), sumMode);
			}
		} else{
			model.setClusters(null);
		}
		dlg.setTableData(model.getClusters());
		dlg.setShowButtonEnabled(model.getClusters() != null && !model.getClusters().isEmpty());
		dlg.setSaveButtonEnabled(false);
	}

	private void updateClusterRanges(List<Cluster> clusters, boolean sumMode){
		for (Cluster cluster : clusters){
			final List<GeoTerritory> territories = cluster.getTerritories();
			cluster.setFrom(territories.get(0).getValue(sumMode));
			cluster.setTo(territories.get(territories.size() - 1).getValue(sumMode));
		}
	}

	protected void showFilter(){
		if (filterDlg == null){
			filterDlg = new GeoAnalyticsFilterDialog(dlg);
			filterDlg.addApplyButtonListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					setFilter(filterDlg.getFilteredOutTerritories());
					clearGeoTerritories();
					filterDlg.setVisible(false);
				}
			});
			filterDlg.addCancelButtonListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					filterDlg.setVisible(false);
				}
			});
		}

		final GisTerritory selectedLayer = dlg.getSelectedLayer();
		if (selectedLayer == null)
			return;
		final List<GeoTerritory> allGeoTerritories = model.getAllGeoTerritories(selectedLayer.getId());
		filterDlg.setTableData(allGeoTerritories, model.getFilteredOutTerritories());
		filterDlg.setLocationRelativeTo(dlg);
		filterDlg.setVisible(true);
	}

	private void setFilter(Set<GeoTerritory> filteredOutTerritories){
		if (filteredOutTerritories != null){
			model.setFilteredOutTerritories(filteredOutTerritories);
		} else{
			model.getFilteredOutTerritories().clear();
		}
		dlg.setFilterSelected(!model.getFilteredOutTerritories().isEmpty());
	}

	private void saveThematicMap(){
		dlg.stopTableEditing();
		final List<Cluster> clusters = model.getClusters();
		if (clusters != null && !clusters.isEmpty()){
			final GisTerritory layer = model.getLayer();
			final Attribute attribute = model.getAttribute();
			final String name = JOptionPane.showInputDialog(dlg, UserSettings.getWord("EnterThematicMapName"), UserSettings
					.getWord("SaveThematicMap"), JOptionPane.QUESTION_MESSAGE);
			if (name != null){
				final int schemaId = doc.DB.schema.ID;
				int thematicFolderId = gaGateway.getDefaultFolderId(schemaId); // TODO choose folder
				ThematicMap tm = gaGateway.getThematicMapByName(name, thematicFolderId, schemaId);
				if (tm != null && tm.getId() > 0){
					final int res = JOptionPane.showConfirmDialog(dlg, UserSettings.getWord("OverwriteThematicMap?"),
							UserSettings.getWord("Save"), JOptionPane.YES_NO_OPTION);
					if (res != 0){
						return;
					}
				} else{
					tm = new ThematicMap();
				}
				tm.setName(name);
				tm.setFolderId(thematicFolderId);
				tm.setLayerId(layer.getId());
				tm.setAttribute(attribute.label);
				final List<ThematicCluster> tClusters = getThematicClusters(clusters);
				tm.setClusters(tClusters);

				ThematicMap result = gaGateway.saveThematicMapWithClusters(tm, schemaId);
				if (result != null && result.getId() > 0){
					doc.setThematicMapID(result.getId());
					doc.updateThematicMaps();
				}
			}
		}
	}

	public int saveThematicMapForFavorite(GisTerritory layer, Attribute attribute, List<Cluster> clusters, int schemaId){
		ThematicMap tm = new ThematicMap();
		tm.setName(DEFAULT_NAME_FOR_FAVORITE);
		tm.setLayerId(layer.getId());
		tm.setAttribute(attribute.label);
		final List<ThematicCluster> tClusters = getThematicClusters(clusters);
		tm.setClusters(tClusters);

		ThematicMap result = gaGateway.saveThematicMapWithClusters(tm, schemaId);
		return result != null ? result.getId() : -1;
	}

	private List<ThematicCluster> getThematicClusters(final List<Cluster> clusters){
		final List<ThematicCluster> tClusters = new ArrayList<ThematicCluster>();
		for (Cluster cluster : clusters){
			final ThematicCluster tCluster = new ThematicCluster();
			tCluster.setFromValue(cluster.getFrom());
			tCluster.setToValue(cluster.getTo());
			tCluster.setColor(Utility.encodeColor(cluster.getColor()));
			tCluster.setGisIds(getGisIdsString(cluster.getTerritories()));
			tCluster.setDescription(cluster.getDescription());
			tClusters.add(tCluster);
		}
		return tClusters;
	}

	private String getGisIdsString(List<GeoTerritory> territories){
		StringBuilder gisIds = new StringBuilder();
		for (int i = 0; i < territories.size(); i++){
			GeoTerritory gt = territories.get(i);
			if (i > 0){
				gisIds.append(",");
			}
			gisIds.append(gt.getId());
		}
		return gisIds.toString();
	}

	private void deleteThematicMaps(){
		final DeleteThematicMapDialog deleteDlg = new DeleteThematicMapDialog(dlg);

		deleteDlg.addOkButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				deleteThematicMaps(deleteDlg.getSelectedThematicMaps());
				deleteDlg.setVisible(false);
			}
		});

		deleteDlg.addCancelButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				deleteDlg.setVisible(false);
			}
		});

		List<ThematicMap> thematicMaps = new ArrayList<ThematicMap>();
		for (ThematicFolder folder : doc.getThematicFolders()){
			thematicMaps.addAll(folder.getThematicMaps());
		}
		deleteDlg.setTableModel(new ThematicMapTableModel(thematicMaps));

		deleteDlg.setLocationRelativeTo(dlg);
		deleteDlg.setVisible(true);
	}

	private void deleteThematicMaps(Set<ThematicMap> thematicMaps){
		boolean deleted = false;
		if (thematicMaps != null && !thematicMaps.isEmpty()){
			for (ThematicMap tm : thematicMaps){
				if (gaGateway.deleteThematicMap(tm.getId())){
					log.debug("Deleted thematic map: " + tm.getName());
					deleted = true;
				}
				if (doc.getThematicMapID() == tm.getId()){
					doc.clearThematicData();
					doc.setGeoLegendData(null, null, null);
				}
			}
		}
		if (deleted){
			doc.updateThematicMaps();
		}
	}

	private void changeColorGradient(){
		final GradientDialog gradientDlg = new GradientDialog(dlg, model.getStartColor(), model.getEndColor());
		gradientDlg.addOkButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				model.setStartColor(gradientDlg.getStartColor());
				model.setEndColor(gradientDlg.getEndColor());
				updateClusterColors(model.getClusters());
				dlg.refreshTable();
				gradientDlg.setVisible(false);
			}
		});

		gradientDlg.addCancelButtonListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				gradientDlg.setVisible(false);
			}
		});
		gradientDlg.setLocationRelativeTo(dlg.getTable());
		gradientDlg.setVisible(true);
	}

	private List<Color> calculateColors(Color from, Color to, int count){
		List<Color> colors = new ArrayList<Color>();
		if (count > 1){
			int rStep = (to.getRed() - from.getRed()) / (count - 1);
			int gStep = (to.getGreen() - from.getGreen()) / (count - 1);
			int bStep = (to.getBlue() - from.getBlue()) / (count - 1);

			for (int i = 0; i < count - 1; i++){
				int red = Math.min(255, Math.max(0, from.getRed() + rStep * i));
				int green = Math.min(255, Math.max(0, from.getGreen() + gStep * i));
				int blue = Math.min(255, Math.max(0, from.getBlue() + bStep * i));
				colors.add(new Color(red, green, blue));
			}
			colors.add(to);
		} else{
			colors.add(from);
		}
		return colors;
	}

	private void updateClusterColors(List<Cluster> clusters){
		if (clusters == null || clusters.isEmpty()){
			return;
		}
		final Color from = model.getStartColor();
		final Color to = model.getEndColor();
		List<Color> colors = calculateColors(from, to, clusters.size());
		for (int i = 0; i < clusters.size(); i++){
			clusters.get(i).setColor(colors.get(i));
		}
	}

	private void shopTablePopup(Point point){
		if (dlg.isColorColumnAtPoint(point)){
			JPopupMenu menu = new JPopupMenu();
			JMenuItem gradientItem = new JMenuItem(UserSettings.getWord("Gradient"));
			gradientItem.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					changeColorGradient();
				}
			});
			menu.add(gradientItem);
			menu.show(dlg.getTable(), point.x, point.y);
		}
	}
}
