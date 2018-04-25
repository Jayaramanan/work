package com.ni3.ag.navigator.client.gui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.ToolTipManager;

import com.ni3.ag.navigator.client.gui.graph.CommandPanelSettings;
import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.controller.HtmlDataFormatter;
import com.ni3.ag.navigator.client.controller.LicenseValidator;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.ObjectPopupListener;
import com.ni3.ag.navigator.client.controller.search.SearchController;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.ObjectPopupMenu;
import com.ni3.ag.navigator.client.gui.geoanalytics.GeoLegendFrame;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.painter.MapEdgePainter;
import com.ni3.ag.navigator.client.gui.graph.painter.MapNodePainter;
import com.ni3.ag.navigator.client.gui.graph.painter.impl.MapEdgePainterImpl;
import com.ni3.ag.navigator.client.gui.graph.painter.impl.MapNodePainterImpl;
import com.ni3.ag.navigator.client.gui.search.DlgCombineSearch;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.shared.domain.ChartType;
import com.ni3.ag.navigator.shared.domain.GISOverlay;

public class MapView extends MapPanel implements Ni3ItemListener{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MapView.class);
	private static final int INITIAL_ZOOM = 6;
	private static final Point INITIAL_POSITION = new Point(8282, 5179);
	private static final int MAX_LON = 180;
	private static final int MAX_LAT = 90;
	private static final double MIN_SCALE_FACTOR = 0.2;
	private static final double DEFAULT_SCALE_FACTOR = 1;
	private static final double MAX_SCALE_FACTOR = 1.5;
	private static final double SCALE_CHANGE_COEFF = 0.8;
	private final LicenseValidator validator = LicenseValidator.getInstance();
	private Ni3Document doc;
	private MapNodePainter mapNodePainter;
	private MapEdgePainter mapEdgePainter;
	private GisDataPainter gisDataPainter;
	private ObjectPopupMenu objectPopupMenu;
	private HtmlDataFormatter htmlFormatter;

	private int oldNodeCount = 0;
	private boolean showSearchRectangle;
	private boolean showZoomRectangle;
	private double nodeScaleFactor;
	private Timer timer;
	private boolean paintInProgress;
	private Node createNodeFrom;
	private Node createNodeTo;
	private BufferedImage offscreenImage;
	private Graphics2D offscreenGraphics;
	private int blinkCount;
	private GeoLegendFrame legendFrame;

	public MapView(){
		super(true);
	}

	// TODO Dirty hack, remove parameter from constructor
	public MapView(Ni3Document doc){

		this.doc = doc;
		mapNodePainter = new MapNodePainterImpl(this);
		mapEdgePainter = new MapEdgePainterImpl(this);
		gisDataPainter = new GisDataPainter(this);
		nodeScaleFactor = 1;
		initTimer();
		paintInProgress = false;

		htmlFormatter = new HtmlDataFormatter();

		final String serverUrl = UserSettings.getStringAppletProperty("Tile_Server_Url", "http://eu1.ni3.net/tiles/");
		final int maxZoom = UserSettings.getIntAppletProperty("Tile_Server_Max_Zoom", 15);
		setTileServer(serverUrl, maxZoom);

		checkTileServers();

		setUseAnimations(false);
		getOverlayPanel().setVisible(false);
		getControlPanel().setVisible(false);
		getProgressPanel().setVisible(true);
		getProgressPanel().setLabel(UserSettings.getWord("Loading..."));

		doc.registerListener(this);

		setZoom(INITIAL_ZOOM);
		setMapPosition(INITIAL_POSITION);

		DragListener listener = new MouseDragListener();
		setDragListener(listener);

		// enable tooltips
		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(99999);

		addMouseListener(new MouseClickListener());
		addMouseMotionListener(new MouseMoveListener());
		addComponentListener(new MapViewComponentListener());

		legendFrame = new GeoLegendFrame();
	}

	private void recreateOffscreenImage(){
		if (offscreenGraphics != null)
			offscreenGraphics.dispose();
		if (getGraphics() == null)
			return;
		if (getHeight() <= 0 || getWidth() <= 0)
			return;
		offscreenImage = ((Graphics2D) getGraphics()).getDeviceConfiguration().createCompatibleImage(getWidth(),
				getHeight(), Transparency.TRANSLUCENT);
		offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
	}

	private void initTimer(){
		timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				if (!paintInProgress && isBlinking()){
					nextBlink();
					repaint();
				}
			}
		}, 0, 200);
	}

	public void initPopupMenu(MainPanel parentMP){
		ObjectPopupListener listener = new ObjectPopupListener(parentMP);
		objectPopupMenu = new ObjectPopupMenu(listener, doc);
	}

	public int getMaxZoomLevel(){
		final TileServer server = getTileServer();
		return server.getMaxZoom();
	}

	protected void paintComponent(Graphics gOrig){
		if (offscreenGraphics == null)
			return;
		Graphics2D g = (Graphics2D) gOrig.create();
		offscreenGraphics.clearRect(0, 0, getWidth(), getHeight());
		super.paintComponent(offscreenGraphics);
		try{
			paintInProgress = true;
			performCustomPainting(offscreenGraphics);
			g.drawImage(offscreenImage, null, 0, 0);
		} finally{
			paintInProgress = false;
			g.dispose();
		}
	}

	private void performCustomPainting(Graphics2D g){
		if (doc.hasThematicData()){
			gisDataPainter.paintThematicData(g, doc.getThematicData());
		}
		if (doc.hasSelectedOverlays()){
			gisDataPainter.paintOverlayData(g, doc.getSelectedOverlays());
		}

		if (doc.Subgraph != null){
			GraphCollection subgraph = doc.Subgraph;
			final List<Edge> edges = subgraph.getDisplayedEdges();
			if (edges != null && !edges.isEmpty()){
				paintEdges(g, edges);
			}

			List<Node> nodes;
			synchronized (subgraph.getNodes()){
				nodes = subgraph.getDisplayedNodes();
			}
			Collections.sort(nodes, new Comparator<Node>(){
				@Override
				public int compare(Node o1, Node o2){
					if (o1.Obj == null || o2.Obj == null || o1.Obj.getMetaphor() == null || o2.Obj.getMetaphor() == null){
						log.warn("invalid data");
						log.warn(o1.Obj);
						log.warn(o2.Obj);
						log.warn(o1.Obj != null ? o1.Obj.getMetaphor() : null);
						log.warn(o2.Obj != null ? o2.Obj.getMetaphor() : null);
						return 0;
					}
					return o1.Obj.getMetaphor().getPriority() - o2.Obj.getMetaphor().getPriority();
				}
			});
			if (nodes != null && !nodes.isEmpty()){
				paintNodes(g, nodes);
			}
		}

		if (showSearchRectangle){
			paintRectangle(g, Color.GRAY);
		} else if (showZoomRectangle){
			paintRectangle(g, Color.RED.darker());
		} else if (createNodeFrom != null){
			paintConnectionOnCreate(g);
		}

		if (isBlinking()){
			nextBlink();
		}
	}

	private void paintConnectionOnCreate(Graphics2D g){
		final BasicStroke pen = new BasicStroke(2);
		g.setStroke(pen);
		g.setColor(Color.RED);

		final DragListener lsn = getDragListener();
		final Point fromPoint = lsn.getDownCoords();
		final Point toPoint = lsn.getMouseCoords();
		g.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
	}

	private void paintNodes(Graphics2D g, List<Node> nodes){
		Node highlighted = doc.getMatrixPointedNode();
		for (Node n : nodes){
			MapSettings settings = doc.getMapSettings();
			double scale = settings.getNodeScale();

			final CommandPanelSettings cpSettings = doc.getCommandPanelSettings();
			double blinkScaleFactor = n == highlighted ? nodeScaleFactor : 1;
			mapNodePainter.paint(n, g, doc, scale, blinkScaleFactor, cpSettings.isShowNodeLabels());
		}
	}

	private void paintEdges(Graphics2D g, List<Edge> edges){
		for (Edge e : edges){
			mapEdgePainter.paint(e, g, doc.getMapSettings(), doc.getInFocusEdges(), doc.getInPathEdges(), doc
					.getCommandPanelSettings().isShowEdgeLabels(), doc.getCommandPanelSettings().isShowDirectedEdges());
		}
	}

	private void zoomToGraphExtents(List<Node> nodes){
		if (nodes != null && !nodes.isEmpty() && oldNodeCount < nodes.size()){
			Rectangle2D.Double rect = getBoundingRectangle(nodes);
			zoomToRectangle(rect);
		}
		oldNodeCount = nodes != null ? nodes.size() : 0;
	}

	void zoomToRectangle(Rectangle2D.Double rect){
		if (rect != null){
			final int zoom = calculateZoomLevel(rect);
			setZoom(zoom);
			Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
			final Point position = computePosition(center);
			setCenterPosition(position);
			repaint();
		}
	}

	void zoomToRectangle(Point2D.Double fromLonLat, Point2D.Double toLonLat){
		Rectangle2D.Double rect = new Rectangle2D.Double(fromLonLat.x, fromLonLat.y, toLonLat.x - fromLonLat.x, toLonLat.y
				- fromLonLat.y);
		zoomToRectangle(rect);
	}

	Rectangle2D.Double getBoundingRectangle(List<Node> nodes){
		Rectangle2D.Double result = null;
		double minLon = MAX_LON;
		double maxLon = -MAX_LON;
		double minLat = MAX_LAT;
		double maxLat = -MAX_LAT;
		for (Node n : nodes){
			if (n.getLat() == 0){
				continue;
			}
			minLon = Math.min(minLon, Math.max(n.getLon(), -MAX_LON));
			maxLon = Math.max(maxLon, Math.min(n.getLon(), MAX_LON));
			minLat = Math.min(minLat, Math.max(n.getLat(), -MAX_LAT));
			maxLat = Math.max(maxLat, Math.min(n.getLat(), MAX_LAT));
		}
		if (minLat < MAX_LAT){
			result = new Rectangle2D.Double(minLon, minLat, maxLon - minLon, maxLat - minLat);
		}
		return result;
	}

	int calculateZoomLevel(Rectangle2D.Double rect){
		final int width = getWidth();
		final int height = getHeight();
		final int maxZoom = getTileServer().getMaxZoom();

		int zoom;
		if (rect.getWidth() == 0 || rect.getHeight() == 0){
			zoom = maxZoom;
		} else{
			for (zoom = 1; zoom <= maxZoom; zoom++){
				final double xFrom = lon2position(rect.getX(), zoom);
				final double xTo = lon2position(rect.getX() + rect.getWidth(), zoom);
				final double yFrom = lat2position(rect.getY(), zoom);
				final double yTo = lat2position(rect.getY() + rect.getHeight(), zoom);
				if (width <= Math.abs(xTo - xFrom) || height <= Math.abs(yTo - yFrom)){
					zoom--;
					break;
				}
			}
		}
		zoom = Math.max(1, Math.min(zoom, maxZoom));
		log.debug("Calculated zoom level = " + zoom);

		return zoom;
	}

	Point getNewCenterPosition(int newZoom){
		final Point centerPosition = getCenterPosition();
		final double lon = position2lon(centerPosition.x, getZoom());
		final double lat = position2lat(centerPosition.y, getZoom());

		final int newX = lon2position(lon, newZoom);
		final int newY = lat2position(lat, newZoom);

		return new Point(newX, newY);
	}

	private void geoSearch(Point.Double fromLonLat, Point.Double toLonLat){

		final DlgCombineSearch sSearch = new DlgCombineSearch(doc, true);
		sSearch.setLocation(getWidth() / 2 - sSearch.getHeight() / 2, getHeight() / 2 - sSearch.getHeight() / 2);
		sSearch.setVisible(true);

		if (sSearch.getReturnStatus() == DlgCombineSearch.RET_OK){
			final StringBuilder geo = new StringBuilder();
			geo.append(Math.min(fromLonLat.x, toLonLat.x)).append(",");
			geo.append(Math.max(fromLonLat.x, toLonLat.x)).append(",");
			geo.append(Math.min(fromLonLat.y, toLonLat.y)).append(",");
			geo.append(Math.max(fromLonLat.y, toLonLat.y));
			if (log.isDebugEnabled()){
				log.debug("Search bounds = " + geo);
			}
			final Query query = sSearch.getQuery();

			if (query != null){
				query.setGeoSearchCondition(geo.toString());

				SearchController searchController = new SearchController(doc);
				searchController.combineSearch(query, doc.isSearchNew(), true);
			}
		}
	}

	private void paintRectangle(Graphics2D g, Color color){
		final Rectangle rect = getSearchRectangle();
		if (rect != null){
			final BasicStroke pen = new BasicStroke(2);
			g.setStroke(pen);
			g.setColor(color);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

	Rectangle getSearchRectangle(){
		final DragListener lsn = getDragListener();
		final Point fromPoint = lsn.getDownCoords();
		final Point toPoint = lsn.getMouseCoords();
		Rectangle searchRectangle = null;
		if (fromPoint != null && toPoint != null){
			final int x = Math.min(fromPoint.x, toPoint.x);
			final int y = Math.min(fromPoint.y, toPoint.y);
			final int width = Math.abs(fromPoint.x - toPoint.x);
			final int height = Math.abs(fromPoint.y - toPoint.y);

			searchRectangle = new Rectangle(x, y, width, height);
		}
		return searchRectangle;
	}

	private void updateMapView(MapSettings settings){
		Point newPosition = getNewCenterPosition(settings.getZoom());
		setZoom(settings.getZoom());
		setCenterPosition(newPosition);
		repaint();
	}

	private void nextBlink(){
		if (doc.getMatrixPointedNode() != null){
			nodeScaleFactor *= SCALE_CHANGE_COEFF;
			if (nodeScaleFactor <= MIN_SCALE_FACTOR){
				nodeScaleFactor = MAX_SCALE_FACTOR;
			}
		} else{
			nodeScaleFactor = DEFAULT_SCALE_FACTOR;
		}
		if (!doc.getInPathEdges().isEmpty()){
			MapSettings settings = doc.getMapSettings();
			double scale = settings.getBlinkingEdgeScale();
			scale *= SCALE_CHANGE_COEFF;
			if (scale <= MIN_SCALE_FACTOR)
				scale = MAX_SCALE_FACTOR;
			settings.setBlinkingEdgeScale(scale);
		}
		if (doc.hasPolylineNodes() && blinkCount >= 7){
			blinkCount = 0;
			doc.setBlinkingPolylineVisible(!doc.isBlinkingPolylineVisible());
		} else
			blinkCount++;
	}

	private boolean isBlinking(){
		return doc.getMatrixPointedNode() != null || !doc.getInPathEdges().isEmpty() || doc.hasPolylineNodes();
	}

	@Override
	public String getToolTipText(MouseEvent event){
		final Point point = event.getPoint();
		final GraphObject object = getObjectInPoint(point);
		String tooltip = null;
		if (object != null && object.Obj != null){
			tooltip = htmlFormatter.getObjectTooltip(object.Obj);
		}
		return tooltip;
	}

	private GraphObject getObjectInPoint(Point point){
		GraphObject object = getNodeInPoint(point, doc.Subgraph.getDisplayedNodes());
		if (object == null){
			object = getEdgeInPoint(point, doc.Subgraph.getDisplayedEdges());
		}
		return object;
	}

	private List<GraphObject> getAllObjectsInPoint(Point point){
		List <GraphObject> res = new ArrayList<GraphObject>();
		res.addAll(getNodesInPoint(point, doc.Subgraph.getDisplayedNodes()));
		return res;
	}

	private Node getNodeInPoint(Point point, List<Node> nodes){
		Node result = null;
		for (Node node : nodes){
			ChartType chartType = node.hasChart() ? doc.getChartParams(node).getChartType() : null;
			if (mapNodePainter.isPointOnNode(node, point, doc.isShowNumericMetaphors(), chartType, doc.getMapSettings()
					.getNodeScale())){
				result = node;
			}
		}
		return result;
	}

	private List<Node> getNodesInPoint(Point point, List<Node> nodes){
		List<Node> result = new ArrayList<Node>();
		for (Node node : nodes){
			ChartType chartType = node.hasChart() ? doc.getChartParams(node).getChartType() : null;
			if (mapNodePainter.isPointOnNode(node, point, doc.isShowNumericMetaphors(), chartType, doc.getMapSettings()
					.getNodeScale())){
				result.add(node);
			}
		}
		return result;
	}

	private Edge getEdgeInPoint(Point point, List<Edge> edges){
		Edge result = null;
		for (Edge edge : edges){
			if (mapEdgePainter.isPointOnEdge(edge, point, 3)){
				result = edge;
			}
		}
		return result;
	}

	private void setSelectedToNode(Node node){
		if (node != null){
			node.selectedTo = true;
		}
		if (createNodeTo != null && node != createNodeTo){
			createNodeTo.selectedTo = false;
		}
		createNodeTo = node;
	}

	private void createEdge(Point point){
		Node node = getNodeInPoint(point, doc.Subgraph.getDisplayedNodes());
		setSelectedToNode(node);
		if (node != null){
			doc.dispatchEvent(Ni3ItemListener.MSG_CreateEdge, Ni3ItemListener.SRC_GIS, MapView.this, null);
			node.selectedTo = false;
		}
		createNodeFrom.selectedFrom = false;
		createNodeFrom = null;
		createNodeTo = null;
	}

	@Override
	public void setZoom(int zoom){
		if (zoom != getZoom() && gisDataPainter != null){
			gisDataPainter.clearOverlayCache();
			gisDataPainter.clearThematicDataCache();
		}
		super.setZoom(zoom);
		if (doc != null){
			final MapSettings settings = doc.getMapSettings();
			if (settings.getZoom() != getZoom()){
				settings.setZoom(zoom);
				doc.setMapSettings(settings);
			}
		}
	}

	@Override
	public void setMapPosition(int x, int y){
		super.setMapPosition(x, y);
		if (doc != null){
			final MapSettings settings = doc.getMapSettings();
			settings.setMapPosition(getMapPosition());
		}
	}

	private void setMapPosition(MapSettings settings){
		final Point position = settings.getMapPosition();
		if (position != null){
			super.setMapPosition(position.x, position.y);
			repaint();
		}
	}

	private void updateLoadingStatus(){
		int totalCount = doc.getTerritoryTotalCount();
		int currentCount = doc.getTerritoryCurrentCount();
		if (!doc.getThematicData().isEmpty() && totalCount > currentCount){
			getProgressPanel().setProgressLine(-1, currentCount + " / " + totalCount);
		} else{
			getProgressPanel().removeProgressLine(-1);
		}
	}

	private void updateOverlayLoadingStatus(){
		for (GISOverlay overlay : doc.getAllOverlays()){
			Integer overlayId = overlay.getId();
			if (doc.isSelectedOverlay(overlay)){
				Integer totalCount = doc.getOverlayTerritoryTotalCount(overlayId);
				Integer currentCount = doc.getOverlayTerritoryCurrentCount(overlayId);
				if (totalCount != null && currentCount != null && totalCount > currentCount){
					getProgressPanel().setProgressLine(overlayId, currentCount + " / " + totalCount);
				} else{
					getProgressPanel().removeProgressLine(overlayId);
				}
			} else{
				getProgressPanel().removeProgressLine(overlayId);
			}
		}
	}

	public Image getMapImage(){
		Image mapImage = null;
		if (getWidth() > 0 && getHeight() > 0){
			mapImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			final Graphics mapGraphics = mapImage.getGraphics();
			print(mapGraphics);
		}
		return mapImage;
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		if (doc.isFavoriteLoadMode()){
			return;
		}
		switch (eventCode){
			case MSG_SubgraphChanged:
				if (param instanceof GraphCollection){
					final GraphCollection subgraph = (GraphCollection) param;
					zoomToGraphExtents(subgraph.getDisplayedNodes());
				}
				break;
			case MSG_FilterChanged:
				zoomToGraphExtents(doc.Subgraph.getDisplayedNodes());
				repaint();
				break;
			case MSG_ChartFilterChanged:
				repaint();
				break;
			case MSG_ClearSubgraph:
				oldNodeCount = 0;
				break;
			case MSG_MapSettingsChanged:
				if (param instanceof MapSettings)
					updateMapView((MapSettings) param);
				break;
			case MSG_ShowNumericMetaphorsChanged:
			case MSG_CommandPanelSettingsChanged:
				repaint();
				break;
			case MSG_MatrixPointedNodeChanged:
			case MSG_InFocusEdgesChanged:
			case MSG_NodeSelectionChanged:
				repaint();
				break;
			case MSG_MapPositionChanged:
				setMapPosition((MapSettings) param);
				updateOldNodeCount();
				break;
			case MSG_OverlayDataChanged:
				updateOverlayLoadingStatus();
				repaint();
				break;
			case MSG_OverlaysChanged:
				gisDataPainter.clearOverlayCache();
				repaint();
				break;
			case MSG_ThematicDataChanged:
				if (getSize().width > 0 && getSize().height > 0){
					updateLoadingStatus();
					repaint();
				}
				break;
			case MSG_ThematicMapChanged:
				gisDataPainter.clearThematicDataCache();
				break;
			case MSG_GeoLegendDataChanged:
				showLegendFrame((Boolean) param);
				break;
			case MSG_ChartChanged:
			case MSG_ChartTypeChanged:
				repaint();
				break;
			case MSG_NodeIconChanged:
			case MSG_MetaphorSetChanged:
				repaint();
				break;
			case MSG_PolygonColorChanged:
				repaint();
				break;
		}
	}

	private void updateOldNodeCount(){
		final List<Node> nodes = doc.Subgraph.getDisplayedNodes();
		oldNodeCount = nodes.size();
	}

	private void showLegendFrame(Boolean flag){
		if (flag)
			legendFrame.setLegendData(doc.getGeoLegendData(), doc.getGeoLegendAttribute());
		legendFrame.setVisible(flag);
	}

	public void toggleLegend(){
		if (legendFrame.isVisible())
			legendFrame.setVisible(false);
		else if (doc.getGeoLegendData() != null){
			showLegendFrame(true);
		}
	}

	@Override
	public int getListenerType(){
		return SRC_GIS;
	}

	class MouseDragListener extends DragListener{
		@Override
		public void mousePressed(MouseEvent e){
			super.mousePressed(e);
			final int mdf = e.getModifiersEx();
			if (mdf == (InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
				showSearchRectangle = true;
			} else if (mdf == (InputEvent.ALT_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK)){
				showZoomRectangle = true;
			} else if (doc.isExpressEditMode() && isEdgeDataChangeEnabled() && e.getButton() == MouseEvent.BUTTON1){
				Node node = getNodeInPoint(e.getPoint(), doc.Subgraph.getDisplayedNodes());
				if (node != null){
					createNodeFrom = node;
					node.selectedFrom = true;
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e){
			if (showSearchRectangle || showZoomRectangle){
				super.handlePosition(e);
				repaint();
			} else if (createNodeFrom != null){
				super.handlePosition(e);
				Node node = getNodeInPoint(e.getPoint(), doc.Subgraph.getDisplayedNodes());
				setSelectedToNode(node);
				repaint();
			} else{
				super.mouseDragged(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e){
			if (showSearchRectangle || showZoomRectangle){
				if (getDownCoords() != null && getMouseCoords() != null){
					final Point fromPosition = new Point(getDownPosition().x + getDownCoords().x, getDownPosition().y
							+ getDownCoords().y);
					final Point toPosition = getCursorPosition();
					if (Math.abs(toPosition.x - fromPosition.x) > 0 && Math.abs(toPosition.x - fromPosition.x) > 0){
						final Point.Double fromLonLat = getLongitudeLatitude(fromPosition);
						final Point.Double toLonLat = getLongitudeLatitude(toPosition);
						if (showSearchRectangle){
							showSearchRectangle = false;
							geoSearch(fromLonLat, toLonLat);
						} else{
							showZoomRectangle = false;
							zoomToRectangle(fromLonLat, toLonLat);
						}
					}
				}
				cleanupCoordinates();
				repaint();
			} else if (createNodeFrom != null){
				createEdge(e.getPoint());
				repaint();
			} else{
				super.mouseReleased(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e){
			if (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown()){
				super.mouseClicked(e);
			}
		}
	}

	private class MouseClickListener extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent mouseEvent){
			if (mouseEvent.isAltDown() || mouseEvent.isShiftDown()){
				final Point point = mouseEvent.getPoint();
				final Node node = getNodeInPoint(point, doc.Subgraph.getDisplayedNodes());
				if (node != null){
					if (mouseEvent.isAltDown()){
						if (mouseEvent.getButton() == MouseEvent.BUTTON1){
							node.selectedFrom = !node.selectedFrom;
							node.selectedTo = false;
						} else if (mouseEvent.getButton() == MouseEvent.BUTTON3){
							node.selectedTo = !node.selectedTo;
							node.selectedFrom = false;
						}
					} else if (mouseEvent.isShiftDown() && mouseEvent.getButton() == MouseEvent.BUTTON1){
						node.selected = !node.selected;
					}

					repaint();
				}
			} else if (mouseEvent.getButton() == MouseEvent.BUTTON1){
				if (!(doc.isExpressEditMode() && isNodeDataChangeEnabled()))
					return;

				Point.Double geoPoint = getCursorGeoPosition();
				doc.dispatchEvent(Ni3ItemListener.MSG_CreateNode, Ni3ItemListener.SRC_GIS, MapView.this, new Double[] {
						geoPoint.x, geoPoint.y });
			} else if (mouseEvent.getButton() == MouseEvent.BUTTON3){
				if (objectPopupMenu != null){
					Point point = mouseEvent.getPoint();
					GraphObject object = getObjectInPoint(point);
					List<GraphObject> allObjects = getAllObjectsInPoint(point);
					if (object != null){
						objectPopupMenu.createPopupMenuItems(object, allObjects);
						objectPopupMenu.show(MapView.this, point.x, point.y);
					}
				}
			}
		}
	}

	private class MouseMoveListener implements MouseMotionListener{
		@Override
		public void mouseDragged(MouseEvent mouseEvent){

		}

		@Override
		public void mouseMoved(MouseEvent mouseEvent){
			final Point point = mouseEvent.getPoint();
			final Node node = getNodeInPoint(point, doc.Subgraph.getDisplayedNodes());
			final Edge edge = getEdgeInPoint(point, doc.Subgraph.getDisplayedEdges());
			Set<Edge> edgesToHighlight = new HashSet<Edge>();
			doc.setMapPointedNode(node);
			if (node != null){
				// if node under cursor - highlight all it's edges
				edgesToHighlight.addAll(node.inEdges);
				edgesToHighlight.addAll(node.outEdges);
			} else if (edge != null){
				// if edge under cursor - highlight edge
				edgesToHighlight.add(edge);
			}
			Set<Edge> highlightedEdges = doc.getInFocusEdges();
			if (!edgesToHighlight.equals(highlightedEdges)){
				// only redraw map if highlight list changed
				doc.setInFocusEdges(edgesToHighlight);
			}
		}
	}

	private class MapViewComponentListener implements ComponentListener{
		@Override
		public void componentResized(ComponentEvent componentEvent){
			recreateOffscreenImage();
			repaint();
		}

		@Override
		public void componentMoved(ComponentEvent componentEvent){
			repaint();
		}

		@Override
		public void componentShown(ComponentEvent componentEvent){
			recreateOffscreenImage();
			repaint();
		}

		@Override
		public void componentHidden(ComponentEvent componentEvent){
		}
	}

	private boolean isNodeDataChangeEnabled(){
		return validator.isNodeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("Toolbar_CreateNode_InUse", true);
	}

	private boolean isEdgeDataChangeEnabled(){
		return validator.isEdgeDataChangeEnabled()
				&& UserSettings.getBooleanAppletProperty("Toolbar_CreateEdge_InUse", true);
	}
}
