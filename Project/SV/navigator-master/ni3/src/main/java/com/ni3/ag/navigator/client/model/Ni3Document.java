/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.model;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.HistoryManager;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.NodeMerger;
import com.ni3.ag.navigator.client.controller.charts.ChartController;
import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.controller.favorites.FavoritesController;
import com.ni3.ag.navigator.client.controller.geoanalytics.GeoAnalyticsController;
import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.domain.cache.MetaphorCache;
import com.ni3.ag.navigator.client.domain.cache.OverlayCache;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gateway.*;
import com.ni3.ag.navigator.client.gateway.impl.*;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.datalist.DataSetTable;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder.SortColumn;
import com.ni3.ag.navigator.client.gui.graph.*;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.map.MapSettings;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.*;
import org.apache.log4j.Logger;

public class Ni3Document{
	private static final Logger log = Logger.getLogger(Ni3Document.class);

	private int edgeCount;
	private int edgeTotalCount;
	private int nodeCount;
	private int nodeTotalCount;

	private List<GisThematicGeometry> thematicData;
	private int territoryTotalCount;
	private int thematicMapID;
	private int mapID;
	private boolean showNumericMetaphors;
	private List<GISOverlay> allOverlays;
	private Set<GISOverlay> selectedOverlays;
	private Map<Integer, Integer> overlayTerritoryTotalCounts;
	private long geoAnalyticsLoadThreadId;
	private List<ThematicFolder> thematicFolders;

	private MapSettings mapSettings;
	private boolean showOnlyDisplayedNodesInMatrix;
	private CommandPanelSettings commandPanelSettings;

	private boolean searchNew;

	private boolean expressEditMode;

	public DataProvider DB;
	public int SchemaID;

	public HistoryManager undoredoManager;
	public List<DBObject> DS; // Objects currently displayed
	public GraphCollection Subgraph;
	public DataFilter filter;
	public DataFilter SYSGroupPrefilter;

	private int showChartID;
	private Map<Integer, ChartParams> chartParams;

	private GraphPanelSettings graphVisualSettings;

	private FavoritesModel favoritesModel;
	private Favorite currentFavorite;
	private boolean topicMode;

	private List<Ni3ItemListener> listeners;

	private List<Query> queries;

	private Set<Edge> inPathEdges;
	private Set<Edge> inFocusEdges;
	private Node matrixPointedNode;

	private boolean favoriteLoadMode;
	private boolean blinkingPolylineVisible;
	private List<Cluster> geoLegendData;
	private Attribute geoLegendAttribute;
	private GisTerritory geoAnalyticsLayer;

	private Map<Integer, Integer> overlayVersion;

	private PolygonModel polygonModel;

	private MatrixSortOrder matrixSort;
	private final ValueUsageStatistics statistics;

	private String metaphorSet;

	public Ni3Document(){
		statistics = new ValueUsageStatistics();
		overlayVersion = new HashMap<Integer, Integer>();
		blinkingPolylineVisible = true;
		inFocusEdges = new HashSet<Edge>();
		listeners = new ArrayList<Ni3ItemListener>();

		queries = new ArrayList<Query>();

		thematicData = new ArrayList<GisThematicGeometry>();
		mapSettings = new MapSettings();
		selectedOverlays = new HashSet<GISOverlay>();
		searchNew = true;
		expressEditMode = false;
		inPathEdges = new HashSet<Edge>();
		polygonModel = new PolygonModel();

		allOverlays = new ArrayList<GISOverlay>();
		selectedOverlays = new HashSet<GISOverlay>();

		overlayTerritoryTotalCounts = new HashMap<Integer, Integer>();
		chartParams = new HashMap<Integer, ChartParams>();
		// TODO document should not initialize anything - controller should do it
		ChartController.initialize(this);

		graphVisualSettings = new GraphPanelSettings();
		matrixSort = new MatrixSortOrder();
		commandPanelSettings = new CommandPanelSettings();
	}

	public int getEdgeCount(){
		return edgeCount;
	}

	public int getEdgeTotalCount(){
		return edgeTotalCount;
	}

	public int getNodeCount(){
		return nodeCount;
	}

	public int getNodeTotalCount(){
		return nodeTotalCount;
	}

	public List<GisThematicGeometry> getThematicData(){
		return thematicData;
	}

	public void clearThematicData(){
		setThematicMapID(0);
		setGeoAnalyticsLoadThreadId(0);
		setTerritoryTotalCount(0);
		this.thematicData = Collections.synchronizedList(new ArrayList<GisThematicGeometry>());
		dispatchEvent(Ni3ItemListener.MSG_ThematicDataChanged, Ni3ItemListener.SRC_Doc, null, thematicData);
	}

	public int getThematicMapID(){
		return thematicMapID;
	}

	public void setThematicMapID(int thematicMapID){
		this.thematicMapID = thematicMapID;
		dispatchEvent(Ni3ItemListener.MSG_ThematicMapChanged, Ni3ItemListener.SRC_Doc, null, thematicMapID);
	}

	public boolean hasThematicData(){
		return !thematicData.isEmpty();
	}

	public int getMapID(){
		return mapID;
	}

	public void setMapID(int mapID){
		this.mapID = mapID;
	}

	public void setMap(int mapID){
		setMapID(mapID);
		clearThematicData();
		dispatchEvent(Ni3ItemListener.MSG_MapChanged, Ni3ItemListener.SRC_Doc, null, mapID);
	}

	public void addToQueryStack(final Query query){
		queries.add(query);
		dispatchEvent(Ni3ItemListener.MSG_QueryNumberChanged, Ni3ItemListener.SRC_Doc, null, queries.size());
	}

	public void setQueryStack(final List<Query> queries){
		this.queries = queries;
		dispatchEvent(Ni3ItemListener.MSG_QueryNumberChanged, Ni3ItemListener.SRC_Doc, null, queries.size());
	}

	public void setGraphPanelSettings(GraphPanelSettings settings){
		graphVisualSettings = settings;
		dispatchEvent(Ni3ItemListener.MSG_GraphPanelSettingsChanged, Ni3ItemListener.SRC_Doc, null, graphVisualSettings);
	}

	public void setStatus(String status){
		dispatchEvent(Ni3ItemListener.MSG_StatusChanged, Ni3ItemListener.SRC_Doc, null, status);
	}

	public void setFilter(DataFilter filter){
		this.filter = filter;
		dispatchEvent(Ni3ItemListener.MSG_FilterNew, Ni3ItemListener.SRC_Doc, null, SchemaID);
	}

	public void setPrefilter(DataFilter prefilter){
		DB.setDataFilter(prefilter);
		dispatchEvent(Ni3ItemListener.MSG_PreFilterChanged, Ni3ItemListener.SRC_Doc, null, prefilter);
	}

	public void setEdgeCounts(final int edgeCount, final int edgeTotalCount){
		this.edgeCount = edgeCount;
		this.edgeTotalCount = edgeTotalCount;
		dispatchEvent(Ni3ItemListener.MSG_EdgeCountChanged, Ni3ItemListener.SRC_Doc, null, null);
	}

	public void setNodeCounts(final int nodeCount, final int nodeTotalCount){
		this.nodeCount = nodeCount;
		this.nodeTotalCount = nodeTotalCount;
		dispatchEvent(Ni3ItemListener.MSG_NodeCountChanged, Ni3ItemListener.SRC_Doc, null, null);
	}

	public void setDataSet(List<DBObject> ds){
		this.DS = ds;
		if (showChartID != 0 && showChartID != SNA.SNA_CHART_ID){
			Subgraph.recalculateChartData(chartParams);
		}
		dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_Doc, null, Subgraph);
	}

	public MapSettings getMapSettings(){
		return mapSettings;
	}

	public void setMapSettings(MapSettings mapSettings){
		this.mapSettings = mapSettings;
		dispatchEvent(Ni3ItemListener.MSG_MapSettingsChanged, Ni3ItemListener.SRC_Doc, null, mapSettings);
	}

	public boolean isShowNumericMetaphors(){
		return showNumericMetaphors;
	}

	public void setShowNumericMetaphors(boolean showNumericMetaphors){
		this.showNumericMetaphors = showNumericMetaphors;
		dispatchEvent(Ni3ItemListener.MSG_ShowNumericMetaphorsChanged, Ni3ItemListener.SRC_Doc, null, showNumericMetaphors);
	}

	public void setOverlays(List<GISOverlay> overlays){
		this.allOverlays = overlays;
	}

	public List<GISOverlay> getAllOverlays(){
		return allOverlays;
	}

	public void addOverlay(final int overlayId){
		for (final GISOverlay overlay : allOverlays){
			if (overlay.getId() == overlayId){
				selectedOverlays.add(overlay);

				if (!overlay.isLoaded()){
					final Integer version = overlayVersion.get(overlayId);

					overlay.setGeometries(Collections.synchronizedList(new ArrayList<GisOverlayGeometry>(0)));

					final GISGateway gateway = new HttpGISGatewayImpl();
					final List<Integer> geometryList = gateway.getOverlayGeometryList(overlayId);
					setOverlayTerritoryTotalCount(overlayId, geometryList.size());

					log.debug("Got overlay geometry list with " + geometryList.size() + " records");

					new Thread(){
						@Override
						public void run(){
							boolean loaded = true;
							for (final Integer geometryId : geometryList){
								if (!isSelectedOverlay(overlay)){
									log.debug("Loading stopped for overlay " + overlay.getName());
									loaded = false;
									break; // stop the loading
								}
								log.debug("Loading cached overlay geometry " + geometryId);
								final OverlayCache cache = OverlayCache.getInstance();
								GisOverlayGeometry geometry = cache.getGeometry(overlayId, geometryId, version);
								if (geometry == null){
									log.debug("Requesting overlay geometry " + geometryId);
									geometry = gateway.getOverlayGeometry(overlayId, geometryId);
									cache.saveGeometry(overlayId, geometryId, version, geometry);
								}
								geometry.setGeometryId(geometryId);
								overlay.addGeometry(geometry);
								dispatchEvent(Ni3ItemListener.MSG_OverlayDataChanged, Ni3ItemListener.SRC_Doc, null,
										selectedOverlays);
							}
							finishOverlayTerritoryLoad(overlayId);
							if (loaded){
								overlay.setLoaded(true);
							}
						}

					}.start();
				}
				break;
			}
		}
		dispatchEvent(Ni3ItemListener.MSG_OverlaysChanged, Ni3ItemListener.SRC_Doc, null, selectedOverlays);
	}

	public boolean hasSelectedOverlays(){
		return !selectedOverlays.isEmpty();
	}

	public boolean isSelectedOverlay(GISOverlay overlay){
		return selectedOverlays.contains(overlay);
	}

	public void removeOverlay(Integer overlayId){
		for (GISOverlay ov : allOverlays){
			if (ov.getId() == overlayId){
				selectedOverlays.remove(ov);
				break;
			}
		}
		dispatchEvent(Ni3ItemListener.MSG_OverlaysChanged, Ni3ItemListener.SRC_Doc, null, selectedOverlays);
	}

	public void removeOverlays(){
		selectedOverlays.clear();
		dispatchEvent(Ni3ItemListener.MSG_OverlaysChanged, Ni3ItemListener.SRC_Doc, null, selectedOverlays);
	}

	public Set<GISOverlay> getSelectedOverlays(){
		return selectedOverlays;
	}

	public List<Integer> getSelectedOverlayIds(){
		List<Integer> ids = new ArrayList<Integer>();
		if (selectedOverlays != null){
			for (GISOverlay overlay : selectedOverlays){
				ids.add(overlay.getId());
			}
		}
		return ids;
	}

	public void setMetaphorSet(final String metaphorSet){
		if (metaphorSet != null && !"null".equals(metaphorSet) && !metaphorSet.equals(this.metaphorSet)){
			this.metaphorSet = metaphorSet;
			dispatchEvent(Ni3ItemListener.MSG_MetaphorSetChanged, Ni3ItemListener.SRC_Doc, null, metaphorSet);
		}
	}

	public String getMetaphorSet(){
		return metaphorSet;
	}

	public boolean isShowOnlyDisplayedNodesInMatrix(){
		return showOnlyDisplayedNodesInMatrix;
	}

	public void setShowOnlyDisplayedNodesInMatrix(boolean showOnlyDisplayedNodesInMatrix){
		this.showOnlyDisplayedNodesInMatrix = showOnlyDisplayedNodesInMatrix;
		dispatchEvent(Ni3ItemListener.MSG_ShowOnlyDisplayedNodesInMatrixChanged, Ni3ItemListener.SRC_Doc, null,
				showOnlyDisplayedNodesInMatrix);
	}

	public boolean isSearchNew(){
		return searchNew;
	}

	public void setSearchNew(boolean searchNew){
		this.searchNew = searchNew;
		dispatchEvent(Ni3ItemListener.MSG_SearchNewChanged, Ni3ItemListener.SRC_Doc, null, searchNew);
	}

	public void setMatrixPointedNode(Node n){
		if (matrixPointedNode != null)
			matrixPointedNode.scaleFactor = 0.66;
		matrixPointedNode = n;
		dispatchEvent(Ni3ItemListener.MSG_MatrixPointedNodeChanged, Ni3ItemListener.SRC_Doc, null, n);
	}

	public Node getMatrixPointedNode(){
		return matrixPointedNode;
	}

	public boolean isExpressEditMode(){
		return expressEditMode;
	}

	public void setExpressEditMode(boolean mode){
		this.expressEditMode = mode;
		dispatchEvent(Ni3ItemListener.MSG_NodeExpressCreateChanged, Ni3ItemListener.SRC_Doc, null, mode);
	}

	public CommandPanelSettings getCommandPanelSettings(){
		return commandPanelSettings;
	}

	public void setCommandPanelSettings(CommandPanelSettings commandPanelSettings){
		this.commandPanelSettings = commandPanelSettings;
		dispatchEvent(Ni3ItemListener.MSG_CommandPanelSettingsChanged, Ni3ItemListener.SRC_Doc, null, commandPanelSettings);
	}

	public ChartFilter getChartFilter(final Node n){
		final int entityId = showChartID == SNA.SNA_CHART_ID ? Entity.COMMON_ENTITY_ID : n.Obj.getEntity().ID;
		return filter.getChartFilter(entityId);
	}

	public ChartParams getChartParams(final Node n){
		final Integer entityId = showChartID == SNA.SNA_CHART_ID ? Entity.COMMON_ENTITY_ID : n.Obj.getEntity().ID;
		return getChartParams(entityId);
	}

	public boolean isFavoriteLoadMode(){
		return favoriteLoadMode;
	}

	public void startFavoriteLoad(){
		favoriteLoadMode = true;
	}

	public void finishFavoriteLoad(){
		favoriteLoadMode = false;
		dispatchEvent(Ni3ItemListener.MSG_FavoriteLoaded, Ni3ItemListener.SRC_Doc, null, currentFavorite);
		if (mapSettings.getMapPosition() != null){
			updateMapPosition(mapSettings);
		} else{
			dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_Doc, null, Subgraph);
		}
		setGeoLegendData(geoLegendData, geoLegendAttribute, geoAnalyticsLayer);
	}

	public void updateMapPosition(MapSettings mapSettings){
		this.mapSettings = mapSettings;
		Point position = mapSettings.getMapPosition();
		dispatchEvent(Ni3ItemListener.MSG_MapSettingsChanged, Ni3ItemListener.SRC_Doc, null, mapSettings);
		mapSettings.setMapPosition(position);
		dispatchEvent(Ni3ItemListener.MSG_MapPositionChanged, Ni3ItemListener.SRC_Doc, null, mapSettings);
	}

	public Favorite getCurrentFavorite(){
		return currentFavorite;
	}

	public FavoritesModel getFavoritesModel(){
		return favoritesModel;
	}

	public void setFavoritesModel(FavoritesModel favoritesModel){
		this.favoritesModel = favoritesModel;
	}

	public void setCurrentFavorite(Favorite favorite){
		boolean isTopic = (favorite != null && favorite.getMode() == FavoriteMode.TOPIC);
		boolean topicModeChanged = isCurrentTopic() != isTopic;
		this.currentFavorite = favorite;
		if (topicModeChanged){
			topicMode = isTopic;
			dispatchEvent(Ni3ItemListener.MSG_TopicModeChanged, Ni3ItemListener.SRC_Doc, null, Subgraph);
		}
	}

	public void updateFavorites(){
		dispatchEvent(Ni3ItemListener.MSG_FavoritesUpdated, Ni3ItemListener.SRC_Doc, null, favoritesModel);
	}

	public List<ThematicFolder> getThematicFolders(){
		return thematicFolders;
	}

	public void setThematicFolders(List<ThematicFolder> thematicFolders){
		this.thematicFolders = thematicFolders;
	}

	public MatrixSortOrder getMatrixSort(){
		return matrixSort;
	}

	public void setMatrixSort(MatrixSortOrder matrixSort, boolean fire){
		this.matrixSort = matrixSort;
		if (fire){
			dispatchEvent(Ni3ItemListener.MSG_MatrixSortChanged, Ni3ItemListener.SRC_Doc, null, matrixSort);
		}
	}

	public void updateMatrixSort(MatrixSortOrder order, int entityId){
		MatrixSortOrder sort = new MatrixSortOrder();
		if (matrixSort == null){
			matrixSort = new MatrixSortOrder();
		}
		for (SortColumn sc : matrixSort.getSorts()){
			if (sc.getEntityId() != entityId){
				sort.addSort(sc.getColumn(), sc.getAttr(), sc.getEntityId(), sc.isAsc());
			}
		}
		sort.addSorts(order.getSorts());
		this.matrixSort = sort;
	}

	public void changeSchema(final int schemaID, boolean initial){
		currentFavorite = null;
		clearQueryStack();

		this.SchemaID = schemaID;
		this.metaphorSet = "Default";

		filter = null;
		Subgraph = new GraphCollection(true);

		DB = new DataProvider(schemaID, UserSettings.getIntAppletProperty("Language", 0));
		DS = null;

		// TODO model shouldn't call controllers methods
		FavoritesController controller = new FavoritesController(this);
		controller.initFavoritesWithFolders(schemaID);

		undoredoManager.clear();

		// remove listeners of type DataSetTable when change schema
		// new listeners for new objects will be added in dispatchEvent
		clearEventsByClassType(DataSetTable.class);

		SYSGroupPrefilter = new DataFilter();
		SYSGroupPrefilter.initFilterProperties();

		SchemaGateway gateway = new HttpSchemaGatewayImpl();
		List<Prefilter> loadedPrefilters = gateway.getPrefilter(schemaID);
		for (Prefilter pf : loadedPrefilters){
			final Entity entity = DB.schema.getEntity(pf.getObjectDefinitionId());
			if (entity == null){
				log.warn("Cannot find entity for id " + pf.getObjectDefinitionId());
				continue;
			}
			final Attribute attribute = entity.getAttribute(pf.getAttributeId());
			if (attribute == null){
				log
						.warn("Cannot find attribute for entity " + pf.getObjectDefinitionId() + " and id "
								+ pf.getAttributeId());
				continue;
			}
			final Value v = attribute.getValue(pf.getPredefinedId());
			if (v == null){
				log.warn("Cannot find entity " + pf.getObjectDefinitionId() + " attribute " + pf.getAttributeId() + " "
						+ "predefined " + pf.getPredefinedId());
				continue;
			}
			SYSGroupPrefilter.addExclusion(v);
		}

		GeoAnalyticsController.getInstance(this).invalidateDialogData();

		dispatchEvent(Ni3ItemListener.MSG_SchemaChanged, Ni3ItemListener.SRC_Doc, null, schemaID);

		filter = new DataFilter(SYSGroupPrefilter);
		DB.setDataFilter(new DataFilter(SYSGroupPrefilter));
		dispatchEvent(Ni3ItemListener.MSG_FilterNew, Ni3ItemListener.SRC_Doc, null, filter);
		dispatchEvent(Ni3ItemListener.MSG_PrefilterNew, Ni3ItemListener.SRC_Doc, null, DB.getDataFilter());

		String favoriteForSchema = UserSettings.getStringAppletProperty("DefaultFavorite_" + schemaID, null);
		if (favoriteForSchema != null && !initial) {
			FavoritesController favoritesController = new FavoritesController(this);
			favoritesController.loadDocument(Integer.valueOf(favoriteForSchema), schemaID);
		}
	}

	public boolean checkUserRights(GraphObject graphObject, final String Operation){
		switch (UserSettings.getIntAppletProperty(Operation, 3)){
			case 0: // No rights
				return false;

			case 1: // If object is created by this user
				return isCurrentUserObject(graphObject);

			case 2: // If object is created by this user group
				return isCurrentGroupObject(graphObject);

			case 3: // Unlimited
				return true;

			default:
				return false;
		}
	}

	public boolean isCurrentGroupObject(final GraphObject graphObject){
		return graphObject.groupID == SystemGlobals.GroupID;
	}

	public boolean isCurrentUserObject(final GraphObject graphObject){
		return graphObject.userID == SystemGlobals.getUserId();
	}

	public void clearCurrentFavorite(){
		setCurrentFavorite(null);
		clearTopicContext();

		if (!Ni3.AppletMode && Ni3.mainF != null){
			((JFrame) (Ni3.mainF)).setTitle("Ni3 Navigator");
		}
		dispatchEvent(Ni3ItemListener.MSG_ClearFavorite, Ni3ItemListener.SRC_Doc, null, SchemaID);
	}

	private void clearEventsByClassType(final Class<?> clazz){
		int it = 0;
		while (it < listeners.size()){
			final Ni3ItemListener listener = listeners.get(it);
			if (listener.getClass().equals(clazz)){
				listeners.remove(listener);
				continue;
			}
			it++;
		}

	}

	public void clearGraph(final boolean SetUndo, final boolean resetChart){
		if (SetUndo){
			setUndoRedoPoint(true);
		}

		if (DS != null){
			DS.clear();
		}

		Subgraph.clear();

		SystemGlobals.MainFrame.Doc.dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc, null,
				this);

		if (resetChart){
			resetChart();
		}

		clearCurrentFavorite();

		dispatchEvent(Ni3ItemListener.MSG_ClearSubgraph, Ni3ItemListener.SRC_Doc, null, null);
	}

	public void clearQueryStack(){
		queries.clear();
		dispatchEvent(Ni3ItemListener.MSG_QueryNumberChanged, Ni3ItemListener.SRC_Doc, null, queries.size());
	}

	public List<Query> getQueries(){
		return queries;
	}

	public void clearSearchResult(final boolean SetUndo){
		if (SetUndo){
			setUndoRedoPoint(true);
		}

		if (DS != null){
			DS.clear();
		}

		Subgraph.clear();

		dispatchEvent(Ni3ItemListener.MSG_ClearSearchResult, Ni3ItemListener.SRC_Doc, null, null);
	}

	public void clearTopicContext(){
		DB.clearContext();

		clearTopicEdges();
	}

	public void clearTopicEdges(){
		final ArrayList<Edge> toDelete = new ArrayList<Edge>();
		Collection<Edge> edges = Subgraph.getEdges();
		for (final GraphObject o : edges){
			if (o.Obj.getEntity().isContextEdge()){
				toDelete.add((Edge) o);
			}
		}

		for (final Edge e : toDelete){
			Subgraph.removeEdge(e);
		}
	}

	public void deleteEdge(final DBObject obj, final int fromID, final int toID, Node from, Node to){
		if (obj != null){
			ObjectManagementGateway gateway = new HttpObjectManagementGatewayImpl();
			gateway.delete(obj);
			DB.cache.remove(obj.getId());
		}

		if (from == null){
			from = Subgraph.findNode(fromID);
		}

		if (from != null){
			reloadNode(from);
			from.refreshLabel();
		}

		if (to == null){
			to = Subgraph.findNode(toID);
		}

		if (to != null){
			reloadNode(to);
			to.refreshLabel();
		}
	}

	public void deleteEdge(final Edge e){
		deleteEdge(e.Obj, e.from.ID, e.to.ID, e.from, e.to);
		undoredoManager.deleteEdgeThroughHistory(e);
		Subgraph.removeEdge(e);
	}

	public boolean deleteNode(final Node node){
		resetInPathEdges();
		ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
		final boolean deleteAllowed = objGateway.checkUserObjectPermissions(node.ID, SchemaID);
		if (!deleteAllowed){
			return false;
		}

		List<Node> relatedNodes = getRelatedNodes(node);

		objGateway.delete(node.Obj);

		for (Node n : relatedNodes){
			reloadNode(n);
		}

		Subgraph.simpleRemoveNode(node);

		undoredoManager.DeleteNodeThroughHistory(node);

		dispatchEvent(Ni3ItemListener.MSG_NodeRemoved, Ni3ItemListener.SRC_Doc, null, node.Obj);

		DB.cache.remove(node.Obj.getId());

		setFilter(filter, false);

		dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, null, Subgraph);

		dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_Graph, null, null);

		return true;
	}

	private void reloadNode(Node n){
		DB.reloadNode(n, Subgraph);
		DB.reloadObject(n.Obj);
		DB.getFavoritesContextData(getTopicID(), n);
		n.refreshLabel();
	}

	private List<Node> getRelatedNodes(Node node){
		GraphCollection collection = new GraphCollection(false);
		List<Integer> roots = new ArrayList<Integer>();
		roots.add(node.ID);

		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		// TODO remove logic out of document (model)
		List<GraphObject> graphObjects = graphGateway.getNodesAndEdges(roots, SchemaID, DB.getDataFilter(), 0);
		collection.addResultToGraph(graphObjects);
		List<Node> relatedNodes = new ArrayList<Node>();
		Collection<Node> nodes = Subgraph.getNodes();
		Collection<Node> relatedGraphNodes = collection.getNodes();
		for (Node n : nodes){
			for (Node rn : relatedGraphNodes){
				if (n.ID == rn.ID && n.ID != node.ID){
					relatedNodes.add(n);
				}
			}
		}
		return relatedNodes;
	}

	public void dispatchEvent(final int EventCode, final int SourceID, final Ni3ItemListener source, final Object Param){
		// iterator should not be used because ConcurrentModificationException
		// is thrown when changing schema
		final int lCount = listeners.size();
		for (int i = 0; i < lCount; i++){
			if (listeners.size() > i){
				final Ni3ItemListener l = listeners.get(i);
				if (l.getListenerType() != SourceID && l != source){
					l.event(EventCode, SourceID, source, Param);
				}
			}
		}
	}

	// return value
	// 0 - Path founded
	// 1 - Path not found
	// 2 - Path filtered out
	public int findPath(final List<DBObject> ds, final int maxPathLength, final int pathLengthOverrun){
		DB.setMaxPathLength(maxPathLength);
		DB.setPathLengthOverrun(pathLengthOverrun);

		boolean PathFounded = false;
		resetInPathEdges();

		for (int i = 0; i < ds.size(); i++){
			for (int j = i + 1; j < ds.size(); j++){
				DBObject n1, n2;
				n1 = ds.get(i);
				n2 = ds.get(j);

				final List<GraphObject> pathObjects = DB.findPath(n1.getId(), n2.getId(), Subgraph);
				for (GraphObject pObj : pathObjects){
					if (pObj instanceof Edge){
						getInPathEdges().add((Edge) pObj);
					}
				}
				DS = Subgraph.getDataSet();

				Subgraph.filter(filter, getFavoritesID());
				Subgraph.recalculateChartData(chartParams);
				dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_Doc, null, Subgraph);
			}
		}

		while (Subgraph.trimLooseEndsInPath(ds) != 0){
			;
		}

		boolean pathFilteredOut = false;

		for (final Node n : Subgraph.getNodes()){
			if (ds.contains(n.Obj)){
				Subgraph.setRootNode(n);
			}
		}

		for (final Edge e : Subgraph.getEdges()){
			if (inPathEdges.contains(e)){
				PathFounded = true;
				if (e.isFilteredOut()){
					pathFilteredOut = true;
				}
			}
		}

		if (pathFilteredOut){
			return 2;
		}

		return PathFounded ? 0 : 1;
	}

	public int getFavoritesID(){
		if (currentFavorite == null){
			return 0;
		}

		return currentFavorite.getId();
	}

	public int getTopicID(){
		if (currentFavorite == null || currentFavorite.getMode() != FavoriteMode.TOPIC){
			return 0;
		}

		return currentFavorite.getId();
	}

	public boolean isCurrentTopic(){
		return topicMode;
	}

	public void initialize(){
		LoadSettings();

		SystemGlobals.DateFormat = UserSettings.getProperty("Applet", "DateFormat", "dd/MM/yyyy");

		changeSchema(UserSettings.getIntAppletProperty("Scheme", -1), true);
	}

	public void isolateSelected(){
		dispatchEvent(Ni3ItemListener.MSG_SubgraphBeforeChange, Ni3ItemListener.SRC_MainPanel, null, Subgraph);

		synchronized (Subgraph){
			if (DS != null){
				DS.clear();
			}
			DS = Subgraph.removeBySelection(false);

			Subgraph.setAllNodesAsRoots();
			for (final Node n : Subgraph.getNodes()){
				n.setExpandedManualy(false);
			}
		}

		Subgraph.clearSelection();

		dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc, null, this);

		dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, null, Subgraph);
	}

	void LoadSettings(){
		UserSettings.LoadSettings();
		if (UserSettings.getBooleanAppletProperty("ImageCacheRefresh", false)){
			MetaphorCache.getInstance().cleanup();
//			IconCache.getInstance().cleanup();
			UserSettings.resetImageRefreshSetting();
		}

	}

	public void refreshObjects(){
		for (final GraphObject o : Subgraph.getObjects()){
			DB.cache.remove(o.ID);
			o.Obj = null;
		}
		DS = DB.getSubgraphData(Subgraph);

		if (getTopicID() > 0){
			undoredoManager.loadContextData(getTopicID(), Subgraph);
		}

		for (final GraphObject o : Subgraph.getObjects()){
			o.refreshLabel();
		}
	}

	public void registerListener(final Ni3ItemListener obj){
		listeners.add(obj);
	}

	// TODO: check if unregister even works
	public void unregisterListener(final Ni3ItemListener obj){
		listeners.remove(obj);
	}

	public void setChart(final int chartID){
		setChart(chartID, true, true);
	}

	public void setChart(final int chartID, final boolean recalculate, final boolean updateChartFilter){
		boolean snaChanged = showChartID != chartID && (showChartID == SNA.SNA_CHART_ID || chartID == SNA.SNA_CHART_ID);
		showChartID = chartID;

		if (recalculate){
			if (chartID != SNA.SNA_CHART_ID){
				Subgraph.recalculateChartData(chartParams);
			}

			Subgraph.recalculateGraphValues(chartParams, filter, updateChartFilter);
			dispatchEvent(Ni3ItemListener.MSG_ChartChanged, Ni3ItemListener.SRC_Doc, null, showChartID);
		}

		if (snaChanged){
			if (chartID != SNA.SNA_CHART_ID){
				removeSnaAttributes();
			}
			dispatchEvent(Ni3ItemListener.MSG_SnaChartChanged, Ni3ItemListener.SRC_Doc, null, showChartID);
		}
	}

	public int getCurrentChartId(){
		return showChartID;
	}

	public void resetChart(){
		Subgraph.resetChart();
		boolean wasSna = showChartID == SNA.SNA_CHART_ID;
		showChartID = 0;
		clearChartParams();
		filter.removeChartFilters();
		Subgraph.filter(filter, getFavoritesID());
		dispatchEvent(Ni3ItemListener.MSG_ChartChanged, Ni3ItemListener.SRC_Doc, null, showChartID);
		if (wasSna){
			removeSnaAttributes();
			dispatchEvent(Ni3ItemListener.MSG_SnaChartChanged, Ni3ItemListener.SRC_Doc, null, showChartID);
		}
	}

	private void removeSnaAttributes(){
		final Schema schema = DB.schema;
		for (Entity entity : schema.definitions){
			if (entity.isNode() && entity.hasSnaAttribute()){
				entity.removeSnaAttributes();
			}
		}
	}

	public ChartParams getChartParams(int entityId){
		ChartParams params = null;
		if (chartParams != null){
			params = chartParams.get(entityId);
		}
		return params;
	}

	public Map<Integer, ChartParams> getChartParams(){
		return chartParams;
	}

	public void setChartParams(final ChartParams params, final int entityId){
		chartParams.put(entityId, params);
		fireRedrawGraphs();
	}

	public void setChartParams(final Map<Integer, ChartParams> params){
		chartParams = params;
		fireRedrawGraphs();
	}

	public void clearChartParams(){
		chartParams = new HashMap<Integer, ChartParams>();
	}

	public void setChartType(int entityId, final ChartType chartType){
		ChartParams params = chartParams.get(entityId);
		if (params != null){
			params.setChartType(chartType);
		}
		dispatchEvent(Ni3ItemListener.MSG_ChartTypeChanged, Ni3ItemListener.SRC_Doc, null, chartType);
	}

	public void setChartLegendVisible(int entityId, boolean visible){
		final ChartParams cp = chartParams.get(entityId);
		if (cp != null){
			cp.setLegendVisible(visible);
			dispatchEvent(Ni3ItemListener.MSG_ChartLegendVisibilityChanged, Ni3ItemListener.SRC_Doc, null, visible);
		}
	}

	public void setCurrentFavorite(){
		if (currentFavorite != null){
			if (!Ni3.AppletMode && Ni3.mainF != null){
				String title = "Ni3 Navigator";
				final String caption = currentFavorite.getCaption();
				if (caption != null && !caption.isEmpty()){
					title += " - " + caption;
				}
				((JFrame) (Ni3.mainF)).setTitle(title);
			}

			if (currentFavorite.getMode() != FavoriteMode.TOPIC){
				DB.clearContext();
				final ArrayList<Edge> toDelete = new ArrayList<Edge>();
				for (final GraphObject o : Subgraph.getObjects()){
					if (o.Obj != null && o.Obj.getEntity().isContextEdge()){
						toDelete.add((Edge) o);
					}
				}

				for (final Edge e : toDelete){
					Subgraph.removeEdge(e);
				}
			} else{
				for (final Entity e : DB.schema.definitions){
					final Context c = e.getContext("Favorites");
					if (c != null){
						for (final Node n : Subgraph.getNodes()){
							if (n.Obj.getEntity().ID == e.ID && n.Obj.hasContextValues(c)){
								final Object value = c.pk.getDataType().getValue(Integer.toString(currentFavorite.getId()));
								n.Obj.setValue(c.pk.ID, value);
							}
						}
					}
				}
			}

			dispatchEvent(Ni3ItemListener.MSG_LoadFavorite, Ni3ItemListener.SRC_Doc, null, currentFavorite);
		}
	}

	public void setFilter(final DataFilter filter, final boolean fireEvent){
		setFilter(filter, fireEvent, true);
	}

	public void setFilter(final DataFilter filter, final boolean fireEvent, final boolean checkChartFilter){
		if (this.filter != null && filter != this.filter){
			filter.copyChartFilters(this.filter);
		}

		this.filter = filter;

		DS = Subgraph.getDataSet();
		Subgraph.filter(this.filter, getFavoritesID(), checkChartFilter);

		if (fireEvent){
			dispatchEvent(Ni3ItemListener.MSG_FilterChanged, Ni3ItemListener.SRC_MainPanel, null, filter);
		}
	}

	public void setUndoRedoPoint(boolean incrementPosition){
		Utility.debugToConsole("setUndoRedoPoint ", 3);
		setStatus("");

		List<DBObject> ds = new ArrayList<DBObject>();
		if (DS != null){
			ds = new ArrayList<DBObject>(DS);
		}

		if (incrementPosition)
			undoredoManager.addHistoryData(SchemaID, showChartID, chartParams, mapID, thematicMapID, new GraphCollection(
					Subgraph), ds, graphVisualSettings, mapSettings.copy(), new DataFilter(filter), new DataFilter(DB
					.getDataFilter()), new ArrayList<Query>(queries), FavoriteMode.UNKNOWN, getMetaphorSet(),
					showNumericMetaphors, getSelectedOverlayIds(), getMatrixSort(), polygonModel.copy(),
					commandPanelSettings.copy(), inPathEdges);
		else{
			undoredoManager.saveCurrentState(SchemaID, showChartID, chartParams, mapID, thematicMapID, new GraphCollection(
					Subgraph), ds, graphVisualSettings, mapSettings.copy(), new DataFilter(filter), new DataFilter(DB
					.getDataFilter()), new ArrayList<Query>(queries), FavoriteMode.UNKNOWN, getMetaphorSet(),
					showNumericMetaphors, getSelectedOverlayIds(), getMatrixSort(), polygonModel.copy(),
					commandPanelSettings.copy(), inPathEdges);
		}

		copyGraphSettings();
	}

	private void copyGraphSettings(){
		if (graphVisualSettings != null){
			graphVisualSettings = graphVisualSettings.clone();
		} else{
			graphVisualSettings = new GraphPanelSettings();
		}
	}

	public void showSubgraph(List<?> graphObjects, boolean setRoots){
		showSubgraph(graphObjects, setRoots, 0);
	}

	public void showSubgraph(List<?> graphObjects, boolean setRoots, int newLevel){
		final List<GraphObject> gObjects = Subgraph.addResultToGraph(graphObjects, newLevel);
		DB.prepareSubgraph(Subgraph, false);
		if (setRoots){
			Subgraph.setRootNodes(gObjects);
		}
		updateSubgraph(true);
	}

	public void updateSubgraph(boolean fireEvents){
		for (final Node n : Subgraph.getNodes()){
			if (n.isLeading()){
				n.setLevel(0);
			}
		}

		while (Subgraph.setExpandLevels() > 0){
			;
		}

		DS = Subgraph.getDataSet();

		if (fireEvents){
			Subgraph.filter(filter, getFavoritesID());
			Subgraph.recalculateChartData(chartParams);
			dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_Doc, null, Subgraph);
		}
	}

	public void merge(DBObject fromObj, DBObject toObj){
		final Node fromNode = getNodeWithEdges(fromObj.getId());
		final Node toNode = getNodeWithEdges(toObj.getId());

		final NodeMerger nm = new NodeMerger(fromNode, toNode);
		nm.showDialog();
		if (nm.isOkPressed()){
			final List<Integer> attributes = nm.getAttributesToMerge();
			final List<Integer> connections = nm.getConnectionsToMerge();

			ObjectManagementGateway gateway = new HttpObjectManagementGatewayImpl();
			gateway.merge(toObj, fromObj, attributes, connections);

			if (fromNode != null){
				Subgraph.simpleRemoveNode(fromNode);
				undoredoManager.DeleteNodeThroughHistory(fromNode);
			}

			dispatchEvent(Ni3ItemListener.MSG_NodeRemoved, Ni3ItemListener.SRC_Doc, null, fromObj);

			DB.reloadObject(toObj);
			dispatchEvent(Ni3ItemListener.MSG_Reload, Ni3ItemListener.SRC_Doc, null, null);
		}
	}

	private Node getNodeWithEdges(int nodeId){
		final GraphCollection bunch = new GraphCollection(false);

		final ArrayList<Integer> roots = new ArrayList<Integer>();
		roots.add(nodeId);

		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		// TODO document should not contain any data processing operations - move to any controller(it is model)
		List<GraphObject> graphObjects = graphGateway.getNodesAndEdges(roots, SchemaID, DB.getDataFilter(), DB
				.getMaximumNodeCount());

		Node result = null;
		if (graphObjects != null){
			bunch.addResultToGraph(graphObjects);
			DB.prepareSubgraph(bunch, false);
			result = bunch.findNode(nodeId);
		}

		return result;
	}

	public Set<Edge> getInPathEdges(){
		return inPathEdges;
	}

	public void resetInPathEdges(){
		inPathEdges.clear();
	}

	public Set<Edge> getInFocusEdges(){
		return inFocusEdges;
	}

	public void clearInFocusEdges(){
		inFocusEdges.clear();
		setInFocusEdges(inFocusEdges);
	}

	public void setInFocusEdges(Set<Edge> inFocusEdges){
		this.inFocusEdges = inFocusEdges;
		dispatchEvent(Ni3ItemListener.MSG_InFocusEdgesChanged, Ni3ItemListener.SRC_Doc, null, inFocusEdges);
	}

	public void fireRedrawGraphs(){
		dispatchEvent(Ni3ItemListener.MSG_MapSettingsChanged, Ni3ItemListener.SRC_Doc, null, getMapSettings());
		dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_GIS, null, null);
	}

	public void setGraphPointedNode(Node graphPointedNode){
		dispatchEvent(Ni3ItemListener.MSG_GraphPointedNodeChanged, Ni3ItemListener.SRC_Doc, null, graphPointedNode);
	}

	public void setMapPointedNode(Node node){
		dispatchEvent(Ni3ItemListener.MSG_MapPointedNodeChanged, Ni3ItemListener.SRC_Doc, null, node);
	}

	public void updateNodeSelection(){
		dispatchEvent(Ni3ItemListener.MSG_NodeSelectionChanged, Ni3ItemListener.SRC_Doc, null, inFocusEdges);
	}

	public PolygonModel getPolygonModel(){
		return polygonModel;
	}

	public void setPolygonModel(PolygonModel polygonModel){
		this.polygonModel = polygonModel;
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public void addPolygonNode(Integer id){
		polygonModel.removePolylineNode(id);
		polygonModel.addPolygonNode(id);
		polygonModel.addPolyColor(id, DB.paletteInUse.nextColor());
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public void addPolylineNode(Integer id){
		polygonModel.removePolygonNode(id);
		polygonModel.addPolylineNode(id);
		polygonModel.addPolyColor(id, DB.paletteInUse.nextColor());
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public void removePolygonNode(Integer id){
		polygonModel.removePolygonNode(id);
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public void removePolylineNode(Integer id){
		polygonModel.removePolylineNode(id);
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public boolean isPolygonNode(int id){
		return polygonModel.getPolygonNodes().contains(id);
	}

	public boolean isPolylineNode(int id){
		return polygonModel.getPolylineNodes().contains(id);
	}

	public void setPolygonAlpha(float polygonAlpha){
		polygonModel.setPolygonAlpha(polygonAlpha);
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public float getPolygonAlpha(){
		return polygonModel.getPolygonAlpha();
	}

	public void setBlinkingPolylineVisible(boolean blinkingPolylineVisible){
		this.blinkingPolylineVisible = blinkingPolylineVisible;
	}

	public boolean isBlinkingPolylineVisible(){
		return blinkingPolylineVisible;
	}

	public Color getPolyColor(int id){
		return polygonModel.getPolyColors().get(id);
	}

	public boolean hasPolylineNodes(){
		return !polygonModel.getPolylineNodes().isEmpty();
	}

	public boolean hasPolygonNodes(){
		return !polygonModel.getPolygonNodes().isEmpty();
	}

	public void clearPolyshapes(){
		polygonModel.clear();
		dispatchEvent(Ni3ItemListener.MSG_PolygonModelChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public void firePolygonColorChanged(){
		dispatchEvent(Ni3ItemListener.MSG_PolygonColorChanged, Ni3ItemListener.SRC_Doc, null, polygonModel);
	}

	public void setGeoLegendData(List<ThematicCluster> geoClusters, String attr){
		final List<Cluster> clusters = new ArrayList<Cluster>();
		for (ThematicCluster geoCluster : geoClusters){
			final Cluster c = new Cluster(geoCluster.getFromValue(), geoCluster.getToValue());
			c.setColor(Utility.createColor(geoCluster.getColor()));
			c.setDescription(geoCluster.getDescription());
			clusters.add(c);
		}
		final Attribute attribute = new Attribute();
		attribute.label = attr;
		setGeoLegendData(clusters, attribute, null);
	}

	public void setGeoLegendData(List<Cluster> clusters, Attribute selectedAttribute, GisTerritory layer){
		this.geoLegendData = clusters;
		this.geoLegendAttribute = selectedAttribute;
		this.geoAnalyticsLayer = layer;
		dispatchEvent(Ni3ItemListener.MSG_GeoLegendDataChanged, Ni3ItemListener.SRC_Doc, null, clusters != null);
	}

	public void setOverlayVersion(int id, int version){
		overlayVersion.put(id, version);
	}

	public List<Cluster> getGeoLegendData(){
		return geoLegendData;
	}

	public Attribute getGeoLegendAttribute(){
		return geoLegendAttribute;
	}

	public GisTerritory getGeoAnalyticsLayer(){
		return geoAnalyticsLayer;
	}

	public void updateThematicMaps(){
		dispatchEvent(Ni3ItemListener.MSG_ThematicMapsChanged, Ni3ItemListener.SRC_Doc, null, null);
	}

	public void addThematicData(final GisThematicGeometry geometry){
		synchronized (thematicData){
			thematicData.add(geometry);
		}
		dispatchEvent(Ni3ItemListener.MSG_ThematicDataChanged, Ni3ItemListener.SRC_Doc, null, thematicData);
	}

	public void setTerritoryTotalCount(int total){
		territoryTotalCount = total;
	}

	public void finishTerritoryLoad(){
		territoryTotalCount = getTerritoryCurrentCount();
	}

	public int getTerritoryTotalCount(){
		return territoryTotalCount;
	}

	public int getTerritoryCurrentCount(){
		return thematicData != null ? thematicData.size() : 0;
	}

	public void setOverlayTerritoryTotalCount(int overlayId, int totalCount){
		this.overlayTerritoryTotalCounts.put(overlayId, totalCount);
	}

	public void finishOverlayTerritoryLoad(int overlayId){
		Integer count = overlayTerritoryTotalCounts.get(overlayId);
		if (count != null){
			overlayTerritoryTotalCounts.put(overlayId, getOverlayTerritoryCurrentCount(overlayId));
		}
	}

	public int getOverlayTerritoryCurrentCount(int overlayId){
		int count = 0;
		for (GISOverlay overlay : getSelectedOverlays()){
			if (overlay.getId() == overlayId){
				if (overlay.getGeometries() != null){
					count = overlay.getGeometries().size();
				}
				break;
			}
		}
		return count;
	}

	public Integer getOverlayTerritoryTotalCount(int overlayId){
		return overlayTerritoryTotalCounts.get(overlayId);
	}

	public long getGeoAnalyticsLoadThreadId(){
		return geoAnalyticsLoadThreadId;
	}

	public void setGeoAnalyticsLoadThreadId(long id){
		geoAnalyticsLoadThreadId = id;
	}

	public GraphPanelSettings getGraphVisualSettings(){
		return graphVisualSettings;
	}

	public void updateNodeIcon(Node node){
		dispatchEvent(Ni3ItemListener.MSG_NodeIconChanged, Ni3ItemListener.SRC_Doc, null, node);
	}

	public ValueUsageStatistics getStatistics(){
		return statistics;
	}

	public int getSchemaId(){
		return SchemaID;
	}
}
