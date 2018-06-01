/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.ni3.ag.navigator.client.controller.*;
import com.ni3.ag.navigator.client.controller.charts.ChartController;
import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.controller.favorites.FavoritesController;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.controller.login.LoginController;
import com.ni3.ag.navigator.client.controller.search.SearchController;
import com.ni3.ag.navigator.client.controller.toolbar.ToolBarController;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.cache.GeoAnalyticsCache;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.domain.cache.MetaphorCache;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gateway.*;
import com.ni3.ag.navigator.client.gateway.impl.*;
import com.ni3.ag.navigator.client.geocoding.Geocoder;
import com.ni3.ag.navigator.client.geocoding.GeocodingMock;
import com.ni3.ag.navigator.client.geocoding.GeocodingResult;
import com.ni3.ag.navigator.client.geocoding.GoogleClientSideGeocoder;
import com.ni3.ag.navigator.client.gui.common.Ni3FileChooser;
import com.ni3.ag.navigator.client.gui.common.Ni3OptionPane;
import com.ni3.ag.navigator.client.gui.datalist.DBObjectList;
import com.ni3.ag.navigator.client.gui.datalist.ItemsPanel;
import com.ni3.ag.navigator.client.gui.favorites.FavoritesMenu;
import com.ni3.ag.navigator.client.gui.filter.JPrefilterTree;
import com.ni3.ag.navigator.client.gui.graph.BasicGraphPanel;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphPanel;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.layoutManager.LineGraphLayoutManager;
import com.ni3.ag.navigator.client.gui.map.MapToolbar;
import com.ni3.ag.navigator.client.gui.map.MapView;
import com.ni3.ag.navigator.client.gui.polygon.PolygonLegendView;
import com.ni3.ag.navigator.client.gui.search.DlgCombineSearch;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.constants.QueryType;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.*;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import org.apache.log4j.Logger;

@SuppressWarnings( { "deprecation", "serial" })
public class MainPanel extends Ni3Panel implements ActionListener, WindowListener, Runnable, ClipboardOwner{

	private static final Logger log = Logger.getLogger(MainPanel.class);

	private JMenuBar menuBar;
	private List<JMenuItem> menuItems;
	// Matrix
	public ItemsPanel itemsPanel;
	public GraphPanel graphPanel;
	public CommandPanel commandPanel;
	public FavoritesMenu favoritesMenu;

	private MapView mapView;
	public JSplitPane leftSplit, mainSplit;

	public volatile boolean dontRelax;
	public volatile boolean suspended;
	public volatile boolean inProcessing;
	private volatile boolean running, staniBre;
	private Thread relaxer;

	private ButtonGroup chartGroup;
	private ButtonGroup mapGroup;

	private DlgCombineSearch combineSearch;
	private MetaphorLegendFrame metaphorLegend;
	private ReportManager reportManager;
	private ActivityStreamManager activityStreamManager;

	private JSplitPane GGSplit;
	private JSplitPane gisSplit;
	private ButtonGroup buttonSchemaGroup;
	private boolean searchInProgress;

	private JMenu menuMaps;
	private JMenu menuOverlayMaps;
	private JMenu menuCharts;
	private JMenu menuLegend;
	private JMenu layoutMenu;
	private JMenuItem menuItemLegend;
	private JMenuItem menuItemThematicLegend;
	private JMenuItem defaultMapMenuItem;
	private JMenu menuMetaphors;
	private List<JMenuItem> thematicMenuItems;
	private JRadioButtonMenuItem showChartOffItem;

	private String focusNodeID;

	private GraphController graphController;
	private ToolBarController toolBarController;

	public List<DBObject> searchResult;
	private ChartLegendView chartView;

	private boolean mapsEnabled;

	private Geocoder geocoder;

	public MainPanel(){
		super();
		setName("MainPanel");
	}

	@Override
	public int getListenerType(){
		return Ni3ItemListener.SRC_MainPanel;
	}

	public void init(final Ni3 theApp, final String username, final String password, final String SID, final String SSO){
		SystemGlobals.MainFrame = this;

		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		// Create a trust manager that does not validate certificate chains
		Security.setProperty("ssl.SocketFactory.provider", "com.ni3.ag.navigator.client.util.DummySSLSocketFactory");

		SystemGlobals.theApp = theApp;

		Doc = new Ni3Document();
		graphController = new GraphController(this);

		LoginController loginController = new LoginController();
		final boolean loginOk = loginController.login(username, password, SID, SSO);

		if (!loginOk){
			return;
		}

		Doc.setMatrixPointedNode(null);
		searchInProgress = false;

		running = false;
		dontRelax = false;
		suspended = false;
		inProcessing = false;

		Doc.undoredoManager = new HistoryManager(this);
		Doc.initialize();

		Doc.registerListener(this);

		menuCharts = null;
		menuMetaphors = null;
		thematicMenuItems = new ArrayList<JMenuItem>();

		Node.nodeScaleGraph = Double.valueOf(UserSettings.getProperty("Applet", "GraphNodeScale", null));

		SNA.MaxNodes = Integer.valueOf(UserSettings.getProperty("Applet", "SNAWarningMaxNodes", "500"));
		SNA.MaxEdges = Integer.valueOf(UserSettings.getProperty("Applet", "SNAWarningMaxEdges", "1000"));

		Node.MaxMetaphorZoom = Double.valueOf(UserSettings.getProperty("Applet", "MaxMetaphorZoom", null));

		String baseUrl = UserSettings.getStringAppletProperty("Geocoding_BaseUrl", "https://maps.googleapis.com/maps/api/geocode/json?address=");
		String accuracy = UserSettings.getStringAppletProperty("Geocoding_Accuracy", "APPROXIMATE");
		geocoder = new GoogleClientSideGeocoder(baseUrl, accuracy);

		final String iconname = UserSettings.getProperty("Applet", "MarkFocusIcon", "bullseye24_p_r4.png");
		// TODO:ALK: check
		IconCache images = new IconCache();
		Node.anchor = images.getImage(IconCache.GRAPH_ANCHOR_MARK);
		Node.markFocusNodes = Boolean.valueOf(UserSettings.getProperty("Applet", "MarkFocusNodes", "True"));
		if (Node.anchor == null){
			Node.markFocusNodes = false;
		} else{
			Node.anchorW = Node.anchor.getWidth(null);
			Node.anchorH = Node.anchor.getHeight(null);
		}

		BasicGraphPanel.ShowRootNodeMenu = Boolean.valueOf(UserSettings.getProperty("Applet", "ShowRootNodeMenu", null));

		Doc.setMapID(Integer.valueOf(UserSettings.getProperty("GIS", "DefaultMapID", "2")));

		GraphPanel.graphNodeLabelFont = UserSettings.getProperty("Applet", "GraphNodeLabelFont", null);
		GraphPanel.graphNodeLabelHeight = Integer.valueOf(UserSettings.getProperty("Applet", "GraphNodeLabelHeight", null));

		GraphPanel.graphEdgeLabelFont = UserSettings.getProperty("Applet", "GraphEdgeLabelFont", null);
		GraphPanel.graphEdgeLabelHeight = Integer.valueOf(UserSettings.getProperty("Applet", "GraphEdgeLabelHeight", "30"));

		JPrefilterTree.BackgroundColor = UserSettings.getColor("PREFILTER_BACKGROUND", Color.white);

		mapsEnabled = LicenseValidator.getInstance().isMapsEnabled()
				&& !UserSettings.getBooleanAppletProperty("HideGisPanel", false);
		Doc.getCommandPanelSettings().setShowNodeLabels(UserSettings.getBooleanGraphProperty("show_labels"));
		Doc.getCommandPanelSettings().setShowDirectedEdges(UserSettings.getBooleanGraphProperty("directed_edge"));

		toolBarController = new ToolBarController(this, Doc);
		toolBarController.init(UserSettings.getBooleanAppletProperty("ShowToolbarPanel", true));

		createComponents();
		createMenu();
		layoutComponents();

		if (Ni3.mainF != null && Ni3.mainF instanceof JFrame){
			((JFrame) (Ni3.mainF)).addWindowListener(this);
		}

		showMaps(mapsEnabled);
		chartView = new ChartLegendView(Doc);
		PolygonLegendView polygonView = new PolygonLegendView(Doc);

		Doc.registerListener(chartView);
		Doc.registerListener(polygonView);
	}

	private int getIdBySrcId(final String sourceId){
		int res = -1;
		final String str = SystemGlobals.ServerURL + ServletName.SrcIdToIdConvertionServlet.getUrl();
		final URLEx url = new URLEx(str);
		url.addParam(RequestParam.SRCID, sourceId);
		url.closeOutput(null);
		String line;
		if ((line = url.readLine()) != null){
			res = Integer.valueOf(line);
		}
		url.close();
		return res;
	}

	public String getMainPanelLayout(){
		StringBuilder ret;

		ret = new StringBuilder();

		ret.append(leftSplit.getDividerLocation()).append("#");
		ret.append(mainSplit.getDividerLocation()).append("#");
		int location = GGSplit.getDividerLocation();
		if (location > GGSplit.getMaximumDividerLocation()){
			ret.append("1.0");
		} else if (location <= 1){
			ret.append("0.0");
		} else{
			ret.append(location);
		}
		ret.append("#");

		return ret.toString();
	}

	public void setMainPanelLayout(final String layout){
		if (layout.length() > 0){
			paintImmediately(0, 0, 5000, 5000);
			StringTokenizerEx tok;

			tok = new StringTokenizerEx(layout, "#", false);
			final int splt1 = Integer.valueOf(tok.nextToken());
			final int splt2 = Integer.valueOf(tok.nextToken());
			final String splt3 = tok.nextToken();

			if (GGSplit != null){
				if (splt3.contains(".")){
					final double value = Double.valueOf(splt3);
					GGSplit.setDividerLocation(value);
					GGSplit.validate();
					setDividerExpandedState(GGSplit, value == 1.0);
				} else{
					GGSplit.setDividerLocation(Integer.valueOf(splt3));
				}
				GGSplit.doLayout();
				GGSplit.paintImmediately(0, 0, 5000, 5000);
			}

			leftSplit.setDividerLocation(splt1);
			leftSplit.doLayout();
			leftSplit.paintImmediately(0, 0, 5000, 5000);

			mainSplit.setDividerLocation(splt2);
			mainSplit.doLayout();
			mainSplit.paintImmediately(0, 0, 5000, 5000);

			if (Ni3.mainF != null){
				Ni3.mainF.setVisible(true); // forces component resize
			}
			revalidate();
			repaint();
		}
	}

	private void setDividerExpandedState(JSplitPane split, boolean expand){
		final SplitPaneUI ui = split.getUI();
		if (ui instanceof BasicSplitPaneUI){
			final BasicSplitPaneDivider divider = ((BasicSplitPaneUI) ui).getDivider();
			final JButton btn = (JButton) divider.getComponent(expand ? 1 : 0);
			btn.doClick(); // hack, otherwise expanded/collapse state will not be kept while resize
		}
	}

	public void showErrorMessage(final String message){
		Ni3OptionPane.showMessageDialog(Ni3.mainF, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void showDocumentation(){
		final String helpDocumentUrl = UserSettings.getProperty("Applet", "HelpDocumentUrl",
				"http://www.ni3.net/resources/documentation/");
		showInBrowser(helpDocumentUrl);
	}

	private void About(){
		final AboutBox about = new AboutBox();
		about.setVisible(true);
	}

	private void createComponents(){
		favoritesMenu = new FavoritesMenu(Doc);
		itemsPanel = new ItemsPanel(this);
		graphPanel = new GraphPanel(this);
		commandPanel = new CommandPanel(this);
		animationPanel = new AnimationPanel();

		mapView = new MapView(Doc);
	}

	private void createMenu(){
		menuBar = new JMenuBar();
		createMenuItems();
		for (final JMenuItem i : menuItems){
			menuBar.add(i);
		}
	}

	public List<JMenuItem> getMenuItems(){
		if (menuItems == null){
			createMenuItems();
		}
		return menuItems;
	}

	public List<JMenuItem> createMenuItems(){
		final LicenseValidator validator = LicenseValidator.getInstance();
		final boolean chartsEnabled = validator.isChartsEnabled()
				&& UserSettings.getBooleanAppletProperty("Charts_InUse", true);
		menuItems = new ArrayList<JMenuItem>();
		JMenu menu;
		JMenuItem item;
		final String UnusedMenuItems = UserSettings.getStringAppletProperty("UnusedMenuItems", "0");

		// UnusedMenuItems values: 1. Hide 2. Disable

		// Create a menu
		menu = new JMenu(UserSettings.getWord("File"));
		final boolean menuDisabled = "Disable".equals(UnusedMenuItems);
		if (UserSettings.getBooleanAppletProperty("File_InUse", true)){
			menuItems.add(menu);
			// Create a menu item

			if (UserSettings.getBooleanAppletProperty("File_ChangePassword_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("Change Password"));
				item.setActionCommand("Change Password");
				item.addActionListener(this);
				menu.add(item);
			}

			//f (validator.isReportsEnabled()){
				final ReportGateway reportGateway = new ReportGatewayImpl();
				final List<NResponse.Report> reportItems = reportGateway.getReportTemplates();
				if (!reportItems.isEmpty()){
					reportManager = new ReportManager(Doc, reportItems);
					final JMenuItem reportsMenu = new JMenuItem(UserSettings.getWord("Reports"));
					reportsMenu.setActionCommand("Reports");
					reportsMenu.addActionListener(this);
					menu.add(reportsMenu);
				}
			//}

			if (UserSettings.getBooleanAppletProperty("File_CopyGraph_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("Copy graph"));
				item.setActionCommand("Copy graph");
				item.addActionListener(this);
				menu.add(item);
			}

			if (mapsEnabled && UserSettings.getBooleanAppletProperty("File_CopyMap_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("Copy map"));
				item.setActionCommand("Copy map");
				item.addActionListener(this);
				menu.add(item);
			}

			if (UserSettings.getBooleanAppletProperty("File_ExportData_InUse", true)){
				final JMenu export = new JMenu(UserSettings.getWord("Export data"));
				menu.add(export);

				if (UserSettings.getBooleanAppletProperty("File_ExportAsCSV_InUse", false)){
					item = new JMenuItem(UserSettings.getWord("ExportAsCSV"));
					item.setActionCommand("ExportAsCSV");
					item.addActionListener(this);
					export.add(item);
				}

				item = new JMenuItem(UserSettings.getWord("ExportAsXLS"));
				item.setActionCommand("ExportAsXLS");
				item.addActionListener(this);
				export.add(item);
			}

			if (UserSettings.getBooleanAppletProperty("File_CopyData_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("Copy data"));
				item.setActionCommand("Copy data");
				item.addActionListener(this);
				menu.add(item);
			}

			menu.addSeparator();

			if (!Ni3.AppletMode && UserSettings.getBooleanAppletProperty("File_Exit_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("Exit"));
				item.setActionCommand("Exit");
				item.addActionListener(this);
				menu.add(item);
			}
		} else if (menuDisabled){
			menu.setEnabled(false);
			menuItems.add(menu);
		}

		menu = new JMenu(UserSettings.getWord("Node"));
		if (UserSettings.getBooleanAppletProperty("Node_InUse", true)){
			menuItems.add(menu);
			// Create a menu item

			if (validator.isNodeDataChangeEnabled() && UserSettings.getBooleanAppletProperty("Node_NodeCreate_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("NodeCreate"), KeyEvent.VK_C);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
				item.setActionCommand("NodeCreate");
				item.addActionListener(this);
				menu.add(item);
			}

			if (validator.isNodeDataChangeEnabled() && UserSettings.getBooleanAppletProperty("Node_NodeEdit_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("NodeEdit"), KeyEvent.VK_E);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
				item.setActionCommand("NodeEdit");
				item.addActionListener(this);
				menu.add(item);
			}

			if (validator.isNodeDataChangeEnabled() && UserSettings.getBooleanAppletProperty("Node_NodeDelete_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("NodeDelete"), KeyEvent.VK_D);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
				item.setActionCommand("NodeDelete");
				item.addActionListener(this);
				menu.add(item);
			}

		} else if (menuDisabled){
			menu.setEnabled(false);
			menuItems.add(menu);
		}

		menu = new JMenu(UserSettings.getWord("Connection"));
		if (UserSettings.getBooleanAppletProperty("Connection_InUse", true)){
			menuItems.add(menu);
			// Create a menu item
			if (validator.isEdgeDataChangeEnabled()
					&& UserSettings.getBooleanAppletProperty("Connection_ConnectionCreate_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("ConnectionCreate"), KeyEvent.VK_C);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
				item.setActionCommand("ConnectionCreate");
				item.addActionListener(this);
				menu.add(item);
			}

			if (validator.isEdgeDataChangeEnabled()
					&& UserSettings.getBooleanAppletProperty("Connection_ConnectionEdit_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("ConnectionEdit"), KeyEvent.VK_E);
				item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
				item.setActionCommand("ConnectionEdit");
				item.addActionListener(this);
				menu.add(item);
			}
		} else if (menuDisabled){
			menu.setEnabled(false);
			menuItems.add(menu);
		}

		menu = new JMenu(UserSettings.getWord("Schema"));
		JRadioButtonMenuItem bitem;
		buttonSchemaGroup = new ButtonGroup();

		if (UserSettings.getBooleanAppletProperty("Schema_InUse", true)){
			menuItems.add(menu);

			SchemaGateway schemaGateway = new HttpSchemaGatewayImpl();
			List<Schema_> schemas = schemaGateway.getSchemas();

			for (Schema_ sch : schemas){
				bitem = new JRadioButtonMenuItem(sch.getName(), MetaphorCache.getInstance().getImageIcon(
						"" + sch.getId() + ".png"), sch.getId() == Doc.SchemaID);

				bitem.addActionListener(this);
				bitem.setName("" + sch.getId());
				bitem.setActionCommand("#" + sch.getId());
				buttonSchemaGroup.add(bitem);
				menu.add(bitem);
			}
		} else if (menuDisabled){
			menu.setEnabled(false);
			menuItems.add(menu);
		}

		menuMetaphors = new JMenu(UserSettings.getWord("Metaphors"));
		if (UserSettings.getBooleanAppletProperty("Metaphors_InUse", true)){
			menuItems.add(menuMetaphors);
		} else if (menuDisabled)

		{
			menuMetaphors.setEnabled(false);
			menuItems.add(menuMetaphors);
		}

		menuMaps = new JMenu(UserSettings.getWord("Maps"));
		if (UserSettings.getBooleanAppletProperty("Maps_InUse", true)){
			menuItems.add(menuMaps);
		} else if (menuDisabled){
			menuMaps.setEnabled(false);
			menuItems.add(menuMaps);
		}

		menuCharts = new JMenu(UserSettings.getWord("Charts"));
		if (chartsEnabled){
			menuItems.add(menuCharts);
		} else if (menuDisabled){
			menuCharts.setEnabled(false);
			menuItems.add(menuCharts);
		}

		if (mapsEnabled || chartsEnabled){
			menuSchema(Doc.SchemaID);
		}

		if (UserSettings.getBooleanAppletProperty("Favorites_InUse", true)){
			menuItems.add(favoritesMenu);
		}

		if (SystemGlobals.isThickClient){
			menu = new JMenu(UserSettings.getWord("Synchronization"));
			menuItems.add(menu);
			item = new JMenuItem(UserSettings.getWord("SendRecvAll"));
			item.setActionCommand(OfflineSynchronizer.SYNCHRONIZE_BOTH);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(UserSettings.getWord("SendAll"));
			item.setActionCommand(OfflineSynchronizer.SYNCHRONIZE_OUT);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(UserSettings.getWord("RecvAll"));
			item.setActionCommand(OfflineSynchronizer.SYNCHRONIZE_IN);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(UserSettings.getWord("SyncImages"));
			item.setActionCommand(OfflineSynchronizer.SYNCHRONIZE_IMAGES);
			item.addActionListener(this);
			menu.add(item);
		}

		if (UserSettings.getBooleanAppletProperty("GraphLayout_InUse", true)){
			ButtonGroup group = new ButtonGroup();

			layoutMenu = new JMenu(UserSettings.getWord("Layout"));
			menuItems.add(layoutMenu);

			if (UserSettings.getBooleanAppletProperty("SpringLayout_InUse", true)){
				bitem = new JRadioButtonMenuItem(UserSettings.getWord("Spring"), true);
				bitem.setActionCommand("Spring");
				bitem.addActionListener(this);
				layoutMenu.add(bitem);
				group.add(bitem);
			}

			if (UserSettings.getBooleanAppletProperty("LineLayout_InUse", true)){
				bitem = new JRadioButtonMenuItem(UserSettings.getWord("Line"), false);
				bitem.setActionCommand(LineGraphLayoutManager.NAME);
				bitem.addActionListener(this);
				layoutMenu.add(bitem);
				group.add(bitem);
			}

			if (UserSettings.getBooleanAppletProperty("HierarchyLayout_InUse", true)){
				bitem = new JRadioButtonMenuItem(UserSettings.getWord("Hierarchy"));
				bitem.setActionCommand("Hierarchy");
				bitem.addActionListener(this);
				layoutMenu.add(bitem);
				group.add(bitem);
			}

			if (UserSettings.getBooleanAppletProperty("RadialLayout_InUse", true)){
				bitem = new JRadioButtonMenuItem(UserSettings.getWord("Radial"));
				bitem.setActionCommand("Radial");
				bitem.addActionListener(this);
				layoutMenu.add(bitem);
				group.add(bitem);
			}

			if (UserSettings.getBooleanAppletProperty("GridLayout_InUse", true)){
				bitem = new JRadioButtonMenuItem(UserSettings.getWord("Grid"));
				bitem.setActionCommand("Grid");
				bitem.addActionListener(this);
				layoutMenu.add(bitem);
				group.add(bitem);
			}

			if (UserSettings.getBooleanAppletProperty("LayoutSettings_InUse", true)){
				menu.addSeparator();

				item = new JMenuItem(UserSettings.getWord("Settings"));
				item.setActionCommand("LayoutSettings");
				item.addActionListener(this);
				layoutMenu.add(item);
			}
		}

		if (UserSettings.getBooleanAppletProperty("Help_InUse", true)){
			menu = new JMenu(UserSettings.getWord("Help"));
			menuItems.add(menu);

			if (UserSettings.getBooleanAppletProperty("Help_Documentation_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("Documentation"));
				item.setActionCommand("Documentation");
				item.addActionListener(this);
				menu.add(item);
			}

			if (UserSettings.getBooleanAppletProperty("Help_About_InUse", true)){
				item = new JMenuItem(UserSettings.getWord("About"));
				item.setActionCommand("About");
				item.addActionListener(this);
				menu.add(item);
			}
		}
		return menuItems;
	}

	private void menuMaps(){
		menuMaps.removeAll();
		mapGroup = new ButtonGroup();

		Doc.setMapID(Integer.valueOf(UserSettings.getProperty("GIS", "DefaultMapID", "2")));

		GISGateway gisGateway = new HttpGISGatewayImpl();
		List<GisMap> maps = gisGateway.getMaps();

		for (GisMap gisMap : maps){
			final JRadioButtonMenuItem bitem = new JRadioButtonMenuItem(gisMap.getName(), gisMap.getId() == Doc.getMapID());
			bitem.addActionListener(this);
			bitem.setActionCommand("Maps");
			bitem.setName("" + gisMap.getId());
			mapGroup.add(bitem);

			menuMaps.add(bitem);
			defaultMapMenuItem = bitem;
		}

		menuMaps.addSeparator();

		initThematicMapsMenu();

		if (UserSettings.getBooleanAppletProperty("ThematicLegend_InUse", true)){
			menuMaps.addSeparator();

			menuItemThematicLegend = new JMenuItem(UserSettings.getWord("ThematicLegend"));
			menuItemThematicLegend.addActionListener(this);
			menuItemThematicLegend.setActionCommand("ThematicLegend");
			menuMaps.add(menuItemThematicLegend);
		}

		menuOverlayMaps = new JMenu(UserSettings.getWord("Overlay"));
		menuMaps.add(menuOverlayMaps);
	}

	public void initThematicMapsMenu(){
		GeoAnalyticsGateway geoAnalyticsGateway = new HttpGeoAnalyticsGatewayImpl();
		List<ThematicFolder> folders = geoAnalyticsGateway.getThematicFoldersWithThematicMaps(Doc.DB.schema.ID);
		Doc.setThematicFolders(folders);
		thematicMenuItems.clear();

		for (ThematicFolder folder : folders){
			JMenu menu = new JMenu(folder.getName());
			menuMaps.add(menu);
			for (ThematicMap thematicMap : folder.getThematicMaps()){
				JRadioButtonMenuItem bitem = new JRadioButtonMenuItem(thematicMap.getName());

				bitem.addActionListener(this);
				bitem.setActionCommand("ThematicMap");
				bitem.setName("" + thematicMap.getId());

				if (mapGroup != null){
					mapGroup.add(bitem);
				}

				bitem.setSelected(Doc.getThematicMapID() == thematicMap.getId());

				menu.add(bitem);
				thematicMenuItems.add(bitem);
			}
		}
	}

	private void menuOverlay(){
		if (menuOverlayMaps != null){
			menuOverlayMaps.removeAll();
			List<GISOverlay> overlays = Doc.getAllOverlays();
			for (GISOverlay overlay : overlays){
				Doc.setOverlayVersion(overlay.getId(), overlay.getVersion());
				JCheckBoxMenuItem titem = new JCheckBoxMenuItem(overlay.getName(), false);

				titem.setActionCommand("MapOverlay");
				titem.addActionListener(this);
				titem.setName("" + overlay.getId());
				menuOverlayMaps.add(titem);
			}
		}
	}

	private List<GISOverlay> loadOverlays(){
		GISGateway gisGateway = new HttpGISGatewayImpl();
		List<GISOverlay> loadedOverlays = gisGateway.getOverlaysForSchema(Doc.SchemaID);
		Doc.setOverlays(loadedOverlays);
		return loadedOverlays;
	}

	void menuSchema(final int schemaID){
		ButtonGroup mgroup;
		JRadioButtonMenuItem bitem;

		if (mapsEnabled){
			menuMaps();
			loadOverlays();
			menuOverlay();
		}

		menuMetaphors.removeAll();
		mgroup = new ButtonGroup();

		MetaphorGateway metaphorGateway = new HttpMetaphorGatewayImpl();
		List<String> loadedMetaphorSets = metaphorGateway.getMetaphorSets(schemaID);
		for (String set : loadedMetaphorSets){
			bitem = new JRadioButtonMenuItem(set, "default".equalsIgnoreCase(set));
			bitem.setName(set);
			bitem.addActionListener(this);
			bitem.setActionCommand("Metaphor");
			mgroup.add(bitem);
			menuMetaphors.add(bitem);
		}

		menuMetaphors.addSeparator();

		menuCharts.removeAll();
		chartGroup = new ButtonGroup();

		final int ShowChartID = UserSettings.getIntAppletProperty("Default chart", -1);

		showChartOffItem = new JRadioButtonMenuItem(UserSettings.getWord("Off"), (ShowChartID == 0));
		showChartOffItem.addActionListener(this);
		showChartOffItem.setActionCommand("Chart");
		showChartOffItem.setName("0");
		chartGroup.add(showChartOffItem);
		menuCharts.add(showChartOffItem);

		if (UserSettings.getBooleanAppletProperty("DynamicCharts_InUse", true)){
			menuCharts.addSeparator();
			final JMenuItem item = new JRadioButtonMenuItem(UserSettings.getWord("DynamicChart"));
			item.addActionListener(this);
			item.setActionCommand("DynamicChart");
			item.setName(DynamicChart.DYNAMIC_CHART_ID + "");
			menuCharts.add(item);
			chartGroup.add(item);
		}
		final JMenu menu = new JMenu(UserSettings.getWord("SNA"));
		menu.setName("-1");
		final String UnusedMenuItems = UserSettings.getStringAppletProperty("UnusedMenuItems", "0");

		if (UserSettings.getBooleanAppletProperty("SNABasic_InUse", true)){
			menuCharts.addSeparator();
			bitem = new JRadioButtonMenuItem(UserSettings.getWord("SNABasic"), false);
			bitem.setActionCommand("SNABasic");
			bitem.addActionListener(this);
			bitem.setName(SNA.SNA_CHART_ID + "");
			chartGroup.add(bitem);

			menuCharts.add(bitem);
			menuCharts.addSeparator();
		} else if ("Disable".equals(UnusedMenuItems)){
			menuCharts.addSeparator();
			menu.setEnabled(false);
			menuBar.add(menu);
			menuCharts.addSeparator();
		}

		ChartsGateway chartsGateway = new HttpChartsGatewayImpl();
		List<Chart> charts = chartsGateway.getChartsForUser(schemaID);
		for (Chart chart : charts){
			bitem = new JRadioButtonMenuItem(chart.getName(), ShowChartID == chart.getId());

			if (ShowChartID == chart.getId()){
				ChartController.getInstance().setChart(ShowChartID);
			}
			bitem.addActionListener(this);
			bitem.setActionCommand("Chart");
			bitem.setName("" + chart.getId());
			chartGroup.add(bitem);

			menuCharts.add(bitem);
		}

		menuCharts.addSeparator();

		menuItemLegend = new LegendMenuItem(null, UserSettings.getWord("Legend"));
		menuItemLegend.addActionListener(this);
		menuItemLegend.setActionCommand("Legend");
		menuItemLegend.setSelected(false);
		menuCharts.add(menuItemLegend);

		menuLegend = new JMenu(UserSettings.getWord("Legend"));
		menuCharts.add(menuLegend);

		setLegendMenuVisible(false);
	}

	private void setLegendMenuVisible(boolean visible){
		menuLegend.setVisible(visible);
		menuItemLegend.setVisible(!visible);
	}

	private void layoutComponents(){
		setLayout(new BorderLayout());

		JSplitPane graphSplit;

		graphSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, graphPanel.toolbar, graphPanel);
		graphSplit.setResizeWeight(0);

		if (mapView != null){
			final MapToolbar mapToolbar = new MapToolbar(Doc, mapView.getMaxZoomLevel());
			mapView.initPopupMenu(this);
			mapView.setSize(500, 500);
			gisSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, mapToolbar, mapView);
			gisSplit.setResizeWeight(0);
		} else{
			gisSplit = null;
		}

		if (gisSplit != null){
			GGSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, graphSplit, gisSplit);
			GGSplit.setOneTouchExpandable(true);
			GGSplit.setDividerLocation(UserSettings.getIntAppletProperty("graph_panel_split_location", -1));
			GGSplit.setDividerSize(10);
		} else{
			GGSplit = null;
		}

		// Left split
		if (GGSplit != null){
			leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, commandPanel, GGSplit);
		} else{
			leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, commandPanel, graphPanel);
		}

		leftSplit.setOneTouchExpandable(true);
		leftSplit.setDividerSize(8);
		leftSplit.setResizeWeight(0.0);

		leftSplit.setDividerLocation(UserSettings.getIntAppletProperty("command_panel_split_location", -1));

		// Main split
		mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, leftSplit, itemsPanel);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setDividerLocation(UserSettings.getIntAppletProperty("list_panel_split_location", -1));
		mainSplit.setDividerSize(8);
		mainSplit.setResizeWeight(1.0);

		add(mainSplit, "Center");

		if (!Ni3.AppletMode){
			// Install the menu bar in the frame
			if (Ni3.mainF != null){
				((JFrame) Ni3.mainF).setJMenuBar(menuBar);
			} else{
				SystemGlobals.theApp.setJMenuBar(menuBar);
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e){
		final JMenuItem item = (JMenuItem) e.getSource();

		Doc.setStatus("");

		try{
			final String command = e.getActionCommand();
			if ("Spring".equals(command)){
				Doc.Subgraph.setGraphLayoutManager("Spring");
				Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
				setGraphDontRelax(false);
			} else if ("Hierarchy".equals(command)){
				Doc.Subgraph.setGraphLayoutManager("Hierarchy");
				Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
				setGraphDontRelax(false);
			} else if ("Radial".equals(command)){
				Doc.Subgraph.setGraphLayoutManager("Radial");
				Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
				setGraphDontRelax(false);
			} else if ("Grid".equals(command)){
				Doc.Subgraph.setGraphLayoutManager("Grid");
				Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
				setGraphDontRelax(false);
			} else if (LineGraphLayoutManager.NAME.equals(command)){
				Doc.Subgraph.setGraphLayoutManager(LineGraphLayoutManager.NAME);
				Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			} else if ("LayoutSettings".equals(command)){
				Doc.Subgraph.graphLayoutSettings();
				Doc.Subgraph.getGraphLayoutManager().doLayout(Doc.Subgraph);
				setGraphDontRelax(false);
				Doc.dispatchEvent(Ni3ItemListener.MSG_GraphDirty, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			} else if ("ExportAsCSV".equals(command)){
				graphController.exportDataAsCSV();
			} else if ("ExportAsXLS".equals(command)){
				exportDataAsXLS();
			} else if ("Copy data".equals(command)){
				graphController.copySubgraphDataToClipboard();
			} else if ("Exit".equals(command)){
				Ni3.callOnClosing();
				System.exit(0);
				// Runtime.getRuntime().exit(0);
			} else if ("NodeCreate".equals(command)){
				nodeCreate(-1, -1, Double.NaN, Double.NaN);
			} else if ("NodeEdit".equals(command)){
				nodeEdit(null);
			} else if ("NodeDelete".equals(command)){
				nodeDelete(null);
			} else if ("Documentation".equals(command)){
				showDocumentation();
			} else if ("About".equals(command)){
				About();
			} else if ("Copy map".equals(command)){
				copyMap();
			} else if ("Copy graph".equals(command)){
				copyGraph();
			} else if ("Change Password".equals(command)){
				new LoginController().changePassword();
			} else if ("ConnectionCreate".equals(item.getActionCommand())){
				edgeCreate();
			} else if ("Metaphor".equals(command)){
				Doc.clearCurrentFavorite();
				Doc.setMetaphorSet(item.getText());
			} else if ("Maps off".equals(item.getActionCommand())){
				showMaps(false);
			} else if (item instanceof LegendMenuItem){
				invertLegend(item);
			} else if ("Maps".equals(item.getActionCommand())){
				Doc.clearThematicData();
				Doc.setGeoLegendData(null, null, null);
				Doc.removeOverlays();
				setMap(Integer.valueOf(item.getName()));
			} else if ("ThematicLegend".equals(item.getActionCommand())){
				mapView.toggleLegend();
			} else if ("ThematicMap".equals(item.getActionCommand())){
				loadThematicDataSet(Integer.valueOf(item.getName()));
			} else if ("Chart".equals(item.getActionCommand()) || "DynamicChart".equals(item.getActionCommand())
					|| "SNABasic".equals(item.getActionCommand())){
				final Integer chartId = Integer.valueOf(item.getName());
				ChartController cc = ChartController.getInstance();
				boolean result = cc.setChart(chartId);
				if (result){
					setChartSelected(chartId);
				} else{
					JMenuItem currentItem = getChartMenuItem(Doc.getCurrentChartId());
					if (currentItem != null){
						currentItem.setSelected(true);
					}
				}
			} else if (OfflineSynchronizer.SYNCHRONIZE_OUT.equals(item.getActionCommand())){
				new OfflineSynchronizer().callSync(true, false);
				Doc.refreshObjects();
				Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			} else if (OfflineSynchronizer.SYNCHRONIZE_IN.equals(item.getActionCommand())){
				new OfflineSynchronizer().callSync(false, true);
				Doc.refreshObjects();
				Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
				Doc.updateThematicMaps();
			} else if (OfflineSynchronizer.SYNCHRONIZE_IMAGES.equals(item.getActionCommand())){
				new OfflineSynchronizer().callSyncImages();
				Doc.refreshObjects();
				Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			} else if (OfflineSynchronizer.SYNCHRONIZE_BOTH.equals(item.getActionCommand())){
				new OfflineSynchronizer().callSync(true, true);
				Doc.refreshObjects();
				Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
				Doc.updateThematicMaps();
			} else if (command.charAt(0) == '#'){
				final int SchemaID = Integer.valueOf(command.substring(1));
				Doc.changeSchema(SchemaID, false);
			} else if ("MapOverlay".equals(item.getActionCommand())){

				final JCheckBoxMenuItem citem = (JCheckBoxMenuItem) e.getSource();

				if (citem.isSelected()){
					Doc.addOverlay(Integer.valueOf(item.getName()));
				} else{
					Doc.removeOverlay(Integer.valueOf(item.getName()));
				}
			} else if ("Reports".equals(command)){
				launchReport();
			}
		} catch (final java.lang.OutOfMemoryError error){
			Runtime.getRuntime().gc();

			Ni3OptionPane.showMessageDialog(Ni3.mainF, "Java VM is out of memory. Ni3 will terminate.");
			System.out
					.println("***********************\nJava VM is out of memory. Ni3 will terminate.***********************\n");
			Runtime.getRuntime().exit(1);
		}
	}

	private void launchReport(){
		final List<DBObjectList> matrix = itemsPanel.getMatrixLists();
		reportManager.showDialog(matrix, graphPanel, mapView);
	}

	private void setMap(final int MapID){
		if (!mapsShown()){
			GGSplit.removeAll();
			GGSplit.setOneTouchExpandable(true);
			GGSplit.setDividerSize(10);
			GGSplit.setDividerLocation(450);
			GGSplit.add(graphPanel);
			GGSplit.add(mapView);
		}

		if (menuMaps != null){
			JMenuItem item;
			final int l = menuMaps.getItemCount();
			for (int n = 0; n < l; n++){
				item = menuMaps.getItem(n);
				if (Integer.valueOf(item.getName()) == MapID){
					item.setSelected(true);
					break;
				}
			}
		}
	}

	public JMenuItem getChartMenuItem(final int ChartID){
		JMenuItem result = null;

		final String s = Integer.toString(ChartID);

		final int l = menuCharts.getItemCount();
		for (int n = 0; n < l; n++){
			JMenuItem item = menuCharts.getItem(n);
			if (item != null && item.getName() != null && item.getName().equals(s)){
				result = item;
				break;
			}
		}

		if (result == null){
			result = showChartOffItem;
		}
		return result;
	}

	private void setChartSelected(final int chartId){
		if (chartId == 0 && showChartOffItem != null){
			showChartOffItem.setSelected(true);
		} else if (menuCharts != null){
			if (chartGroup != null && chartGroup.getElements() != null){
				for (final Enumeration e = chartGroup.getElements(); e.hasMoreElements();){
					try{
						final JRadioButtonMenuItem b = (JRadioButtonMenuItem) e.nextElement();
						if (Integer.valueOf(b.getName()) == chartId){
							b.setSelected(true);
							break;
						}
					} catch (final Exception a){
						log.error(a.getMessage(), a);
					}
				}
			}
		}

		updateLegendMenu(chartId);
	}

	private void updateLegendMenu(int chartId){
		if (menuLegend == null){
			return;
		}
		Set<Integer> entityIds = Doc.getChartParams().keySet();
		menuLegend.removeAll();
		if (chartId == SNA.SNA_CHART_ID){
			setLegendMenuVisible(false);
			menuItemLegend.setSelected(true);
		} else{
			for (Integer entityId : entityIds){
				Entity entity = Doc.DB.schema.getEntity(entityId);
				JMenuItem item = new LegendMenuItem(entity, entity.Name);
				item.addActionListener(this);
				item.setName("Legend");
				item.setSelected(true);
				menuLegend.add(item);
			}
			setLegendMenuVisible(true);
		}
	}

	private void invertLegend(JMenuItem item){
		if (item instanceof LegendMenuItem && Doc.getChartParams() != null && !Doc.getChartParams().isEmpty()){
			LegendMenuItem legendItem = (LegendMenuItem) item;
			int entityId = legendItem.getEntity() != null ? legendItem.getEntity().ID : Entity.COMMON_ENTITY_ID;
			ChartParams cp = Doc.getChartParams(entityId);
			if (cp != null){
				Doc.setChartLegendVisible(entityId, legendItem.isSelected());
			}
		}
	}

	private void updateLegendMenuState(){
		if (Doc.getCurrentChartId() == 0){
			return;
		}
		if (Doc.getCurrentChartId() == SNA.SNA_CHART_ID){
			menuItemLegend.setSelected(Doc.getChartParams(Entity.COMMON_ENTITY_ID).isLegendVisible());
		} else{
			final int count = menuLegend.getItemCount();
			for (int n = 0; n < count; n++){
				LegendMenuItem item = (LegendMenuItem) menuLegend.getItem(n);
				final int entityId = item.getEntity().ID;
				boolean visible = Doc.getChartParams(entityId).isLegendVisible();
				if (item.isSelected() != visible){
					item.setSelected(visible);
				}
			}
			setLegendMenuVisible(true);
		}
	}

	public void showMaps(final boolean show){
		if (!show){
			GGSplit.setOneTouchExpandable(false);
			GGSplit.setDividerSize(0);
			GGSplit.remove(gisSplit);
			menuMaps.setEnabled(false);
		} else{
			GGSplit.setOneTouchExpandable(true);
			GGSplit.setDividerSize(10);
			GGSplit.setDividerLocation(UserSettings.getIntAppletProperty("graph_panel_split_location", -1));
			GGSplit.setRightComponent(gisSplit);
			menuMaps.setEnabled(true);
		}
	}

	public boolean mapsShown(){
		return mapsEnabled;
	}

	@Override
	public void onSchemaChanged(final int SchemaID){
		combineSearch = null;
		dlgNodeCreate = null;

		for (final Enumeration<AbstractButton> e = buttonSchemaGroup.getElements(); e.hasMoreElements();){
			final JRadioButtonMenuItem bitem = (JRadioButtonMenuItem) e.nextElement();
			if (Integer.valueOf(bitem.getName()) == SchemaID){
				bitem.setSelected(true);
				break;
			}
		}

		if (running){
			suspendRelaxation();
		}

		Doc.setMatrixPointedNode(null);
		itemsPanel.clear();

		menuSchema(SchemaID);

		Doc.clearThematicData();
		Doc.setGeoLegendData(null, null, null);

		if (!running){
			start();
		}

		resumeRelaxation();
		Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, this, null);
	}

	public void loadThematicDataSet(final int thematicMapId){
		Doc.clearThematicData();
		final GeoAnalyticsGateway geoAnalyticsGateway = new HttpGeoAnalyticsGatewayImpl();

		if (thematicMapId > 0){
			// load clusters
			final ThematicMap thematicMap = geoAnalyticsGateway.getThematicMapWithClusters(thematicMapId);
			if (thematicMap == null){
				showLoadThematicErrorMessage();
				Doc.setGeoLegendData(null, null, null);
				return;
			}

			final List<Integer> gisIds = geoAnalyticsGateway.getGeometryIdsByThematicMap(thematicMapId);
			final Set<Integer> listToLoad = new HashSet<Integer>(gisIds);
			Doc.setThematicMapID(thematicMapId);
			Doc.setTerritoryTotalCount(listToLoad.size());

			new Thread(){
				@Override
				public void run(){
					int version = 0;
					final int territoryId = thematicMap.getLayerId();
					// get version
					final GISGateway gisGateway = new HttpGISGatewayImpl();
					final List<GisTerritory> territories = gisGateway.getTerritories();
					for (GisTerritory territory : territories){
						if (territory.getId() == territoryId){
							version = territory.getVersion();
							break;
						}
					}
					log.debug("Version: " + version);
					boolean loaded = true;
					for (final Integer gisId : listToLoad){
						if (log.isDebugEnabled()){
							log.debug("Loading GA geometry " + gisId);
						}
						final GeoAnalyticsCache cache = GeoAnalyticsCache.getInstance();
						List<GISPolygon> thematicData = cache.getPolygons(territoryId, gisId, version);
						if (thematicData == null){
							log.debug("GA geometry " + gisId + " not found in cache, downloading");
							final List<Integer> gisIDs = Arrays.asList(gisId);
							thematicData = geoAnalyticsGateway.getThematicData(gisIDs, territoryId);
							if (thematicData != null && !thematicData.isEmpty()){
								cache.savePolygons(territoryId, gisId, version, thematicData);
							}
						}

						if (thematicData != null && !thematicData.isEmpty()){
							GisThematicGeometry geometry = new GisThematicGeometry(gisId, thematicData);
							geometry.setColor(getColor(gisId, thematicMap));
							if (Doc.getThematicMapID() != thematicMap.getId()){
								log.debug("Loading stopped for thematic map " + thematicMap.getName());
								loaded = false;
								break; // stop the loading
							}
							Doc.addThematicData(geometry);
						}
					}
					if (loaded){
						Doc.finishTerritoryLoad();
					}
				}
			}.start();

			List<ThematicCluster> clusters = thematicMap.getClusters();
			if (clusters != null && !clusters.isEmpty()){
				Doc.setGeoLegendData(clusters, thematicMap.getAttribute());
			}
		}
	}

	private Color getColor(Integer geometryId, ThematicMap tm){
		Color color = null;
		for (ThematicCluster cluster : tm.getClusters()){
			List<Integer> gIds = Utility.stringToIntegerList(cluster.getGisIds(), ",");
			if (gIds.contains(geometryId)){
				color = Utility.createColor(cluster.getColor());
				break;
			}
		}
		if (color == null){
			log.warn("Gis polygon with gisId = " + geometryId + " is not in any cluster");
			color = Color.BLACK;
		}
		return color;
	}

	private void showLoadThematicErrorMessage(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				JOptionPane.showMessageDialog(MainPanel.this, UserSettings.getWord("error_load_thematic_dataset"));
			}
		});
	}

	private void setMenuCheck(final JMenu menu, final String name){
		JMenuItem item;
		final int l = menu.getItemCount();
		for (int n = 0; n < l; n++){
			item = menu.getItem(n);

			if (item != null){
				if (item instanceof JMenu){
					setMenuCheck((JMenu) item, name);
				} else if (item.getName() != null && item.getName().equalsIgnoreCase(name)){
					item.setSelected(true);
					break;
				}
			}
		}
	}

	private void setMenuCheck(final List<JMenuItem> items, final String name){
		for (JMenuItem item : items){
			if (item != null){
				if (item.getName() != null && item.getName().equalsIgnoreCase(name)){
					item.setSelected(true);
					break;
				}
			}
		}
	}

	void copyGraph(){
		graphPanel.copyToClipboard();
	}

	void copyMap(){
		final Image image = mapView.getMapImage();
		if (image != null){
			TransferableImage tImage = new TransferableImage(image);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(tImage, null);
		}
	}

	SearchThread searchThrd = null;

	public void stopBackgroundOperations(){
		if (metaphorLegend != null){
			metaphorLegend.setVisible(false);
			metaphorLegend.dispose();
			metaphorLegend = null;
		}

		if (searchThrd != null){
			searchThrd.stop();
		}

		searchInProgress = false;
		stopAnimation(-1);
	}

	// =========================================================
	// Panels synchronisation methods
	//
	public void performSearch(final String searchWord){

		Doc.setStatus("");
		if (searchWord == null || searchWord.trim().length() < Doc.DB.getMinKeywordSearchLen()){
			showNoResultWindow(MainPanel.KEYWORD_TOO_SHORT);
			return;
		}

		if (!searchInProgress){
			searchInProgress = true;

			searchThrd = new SearchThread(searchWord);
			if (SystemGlobals.isMarathonTesting()){
				searchThrd.run(); // Search synchronously for testing mode
			} else{
				searchThrd.start();
			}
		}
	}

	public void performCombineSearch(){
		Doc.setStatus("");

		final Query query = combineSearch.getQuery();
		Doc.filter.setConnectedOnly(false);
		Doc.dispatchEvent(Ni3ItemListener.MSG_FilterNew, Ni3ItemListener.SRC_Doc, null, Doc.filter);
		SearchController searchController = new SearchController(Doc);
		searchController.combineSearch(query, Doc.isSearchNew(), true);
	}

	public void releasePath(){
		Doc.resetInPathEdges();
		for (final Node n : Doc.Subgraph.getNodes()){
			n.selected = false;
		}

		Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, this, null);
	}

	public void isolateSelected(){
		if (Doc.Subgraph.countSelected() == 0){
			return;
		}

		startAnimation();

		if (running){
			suspendRelaxation();
		}
		// NAV-931
		if (commandPanel != null && commandPanel.filtersPanel != null){
			commandPanel.filtersPanel.untickConnectedOnly();
		}

		Doc.isolateSelected();

		// if (!graphPanel.Running)
		resumeRelaxation();

		Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, this, null);
		stopAnimation(31);
	}

	public void findPath(){
		JInputValuesDialog dlg = new JInputValuesDialog(UserSettings.getWord("FindPathParameters"), new String[] {
				UserSettings.getWord("MaxPathLength"), UserSettings.getWord("PathLenthOverrun") }, new Object[] {
				Doc.DB.getMaxPathLength(), Doc.DB.getPathLengthOverrun() }, new String[] { "####", "####" });
		int maxPathLength, pathLengthOverrun;
		while (true){
			final Point pt = new Point(20, 20);
			dlg.setLocation(pt);
			dlg.setVisible(true);

			if (dlg.getReturnStatus() != JInputValuesDialog.RET_OK)
				return;

			maxPathLength = (Integer) dlg.getValue(0);
			pathLengthOverrun = (Integer) dlg.getValue(1);

			if (pathLengthOverrun > 2)
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("ValueMustBeBetween0and2"), UserSettings
						.getWord("Data validation"), JOptionPane.INFORMATION_MESSAGE);
			else
				break;
		}

		final int status = Doc.findPath(Doc.Subgraph.getSelected(), maxPathLength, pathLengthOverrun);

		switch (status){
			case 1:
				showNoResultWindow(PATH_NOT_FOUND);
				break;

			case 2:
				showNoResultWindow(PATH_FILTERED_OUT);
				break;
		}
	}

	public void resumeRelaxation(){
		suspended = false;
		graphPanel.setFreezeRedraw(false);
	}

	public void suspendRelaxation(){
		graphPanel.setFreezeRedraw(true);
		suspended = true;
		while (inProcessing){
			Utility.sleep(1);
		}
	}

	public void reload(){
		final ArrayList<Integer> dbRoots = new ArrayList<Integer>();
		final ArrayList<Point> points = new ArrayList<Point>();

		Point pt;
		for (final Node r : Doc.Subgraph.getNodes()){
			if (r.isLeading()){
				pt = new Point((int) r.getX(), (int) r.getY());
				points.add(pt);
				dbRoots.add(r.Obj.getId());
			}
		}

		if (dbRoots.size() > 0){
			Doc.clearGraph(false, false);
			final List<Node> nodes = new HttpGraphGatewayImpl().getNodes(dbRoots, Doc.SchemaID, Doc.DB.getDataFilter());
			if (nodes == null){
				Doc.undoredoManager.back();
			} else{
				for (int i = 0; i < nodes.size(); i++){
					if (points.size() > i){
						nodes.get(i).setX(points.get(i).x);
						nodes.get(i).setY(points.get(i).y);
					}
				}
				Doc.showSubgraph(nodes, true);
			}
		}
	}

	public void zoomIn(){
		graphPanel.ZoomChange(1.1, false);
	}

	public void zoomOut(){
		graphPanel.ZoomChange(0.9, false);
	}

	public void edgeDelete(final Edge edge, final boolean DontConfirm){
		int ret;

		if (!edge.Obj.getEntity().CanDelete){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
			return;
		}

		if (!DontConfirm && !Doc.checkUserRights(edge, "ObjectDeleteRights")){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
			return;
		}

		if (!DontConfirm){
			ret = Ni3OptionPane.showConfirmDialog(Ni3.mainF, UserSettings.getWord("Do you want to delete connection"),
					UserSettings.getWord("Delete connection confirmation"), JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
		} else{
			ret = 0;
		}

		if (ret != 0)
			return;

		Doc.deleteEdge(edge);
		if (!DontConfirm){
			Doc.setFilter(Doc.filter, false);
			Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
		}
	}

	public void showCombineSearchDlg(){
		boolean Active;
		boolean clearInputs = UserSettings.getBooleanAppletProperty("Search_clearInputs", false);
		if (combineSearch == null){
			combineSearch = new DlgCombineSearch(Doc, false);

			combineSearch.setLocation(getWidth() / 2 - combineSearch.getHeight() / 2, getHeight() / 2
					- combineSearch.getHeight() / 2); // new
			// Point(20,20)

			Active = true;
			if (clearInputs){
				combineSearch.clearInputs();
			}
			combineSearch.setVisible(true);
		} else{
			if (clearInputs) {
				combineSearch.clearInputs();
			}
			Active = !combineSearch.isVisible();
			combineSearch.setVisible(!combineSearch.isVisible());
		}

		if (Active){
			if (combineSearch.getReturnStatus() == DlgNodeProperties.RET_OK){
				performCombineSearch();
			}
		}
	}

	public void reloadSchema(){
		Doc.changeSchema(Doc.SchemaID, false);
	}

	public void resetHalos(){
		commandPanel.filtersPanel.resetHalos();
	}

	public GraphController getGraphController(){
		return graphController;
	}

	class SearchThread extends Thread{
		private String searchWord;

		public SearchThread(String searchWord){
			this.searchWord = searchWord;
		}

		@Override
		public void run(){
			searchInProgress = true;
			startAnimation();

			if (running){
				suspendRelaxation();
			}

			Doc.setStatus("");

			commandPanel.filtersPanel.untickConnectedOnly();
			searchResult = Doc.DB.search(searchWord, Doc);

			if (Doc.isSearchNew()){
				Doc.dispatchEvent(Ni3ItemListener.MSG_DynamicAttributesCleared, Ni3ItemListener.SRC_Unknown, null, null);

				Doc.clearGraph(true, true);
				Doc.clearSearchResult(false);
				Doc.clearQueryStack();
			}
			resetDisplayFilter();

			final Query query = new Query("", Doc.DB.schema);
			query.setType(QueryType.SIMPLE);
			query.setTextQuery(searchWord);
			Doc.addToQueryStack(query);

			if (searchResult.size() > 0){
				synchronized (searchResult){
					itemsPanel.setSearchResults(searchResult);
				}
			} else{
				showNoResultWindow(NO_SEARCH_RESULT);
			}

			stopAnimation(5);
			resumeRelaxation();
			searchInProgress = false;
		}
	}


	// ========================================================================================================================================================
	private AnimationPanel animationPanel;

	class AnimationPanel extends JPanel{
		static final long serialVersionUID = 0;

		// TODO should it be chaged to just getImage ?

		private final Image activeAnimationImage = IconCache.getImage(IconCache.PROGRESS_ACTIVE);

		private final Image inactiveAnimationImage = IconCache.getImage(IconCache.PROGRESS_INACTIVE);

		private Image mainImage = inactiveAnimationImage;

		public AnimationPanel(){
		}

		public void setActive(){
			mainImage = activeAnimationImage;
			repaint();
		}

		public void setInactive(){
			mainImage = inactiveAnimationImage;
			repaint();
		}

		@Override
		public Dimension getPreferredSize(){
			return new Dimension(40, 40);
		}

		Image offScreenImage;
		Dimension panelDimension;

		@Override
		public void paint(final Graphics g){
			if (offScreenImage == null || panelDimension == null || !panelDimension.equals(getSize())){
				panelDimension = getSize();
				offScreenImage = createImage(panelDimension.width, panelDimension.height);
			}
			final Graphics offScreenGraphics = offScreenImage.getGraphics();
			offScreenGraphics.setColor(UIManager.getColor("Panel.background"));
			offScreenGraphics.fillRect(0, 0, panelDimension.width, panelDimension.height);
			offScreenGraphics.drawImage(mainImage, panelDimension.width / 2 - mainImage.getWidth(null) / 2,
					panelDimension.height / 2 - mainImage.getHeight(null) / 2, this);
			g.drawImage(offScreenImage, 0, 0, this);
		}
	}

	int waitcounter = 0;

	public void startAnimation(){
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		waitcounter++;
		animationPanel.setActive();
	}

	public void stopAnimation(final int fromWhere){
		if (fromWhere == -1){
			waitcounter = 0;
		} else{
			waitcounter--;
		}
		if (waitcounter <= 0){
			if (animationPanel != null){
				animationPanel.setInactive();
			}
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			waitcounter = 0;
		}
	}

	public static final int NO_SEARCH_RESULT = 0;
	public static final int TOO_MANY_SEARCH_RESULT = 2;
	public static final int KEYWORD_TOO_SHORT = 3;
	public static final int PATH_NOT_FOUND = 4;
	public static final int PATH_FILTERED_OUT = 5;

	public void showNoResultWindow(final int type){
		switch (type){
			case TOO_MANY_SEARCH_RESULT: {
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Too many search result") + "\n"
						+ UserSettings.getWord("Too many search result2"), UserSettings
						.getWord("Too many search result title"), JOptionPane.INFORMATION_MESSAGE);

				Doc.setStatus(UserSettings.getWord("Too many search result"));
				break;
			}

			case NO_SEARCH_RESULT: {
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("No search results") + "\n"
						+ UserSettings.getWord("No search results2"), UserSettings.getWord("No search results"),
						JOptionPane.INFORMATION_MESSAGE);
				Doc.setStatus(UserSettings.getWord("No search results"));
				break;
			}

			case KEYWORD_TOO_SHORT: {
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Keyword too short") + "\n"
						+ UserSettings.getWord("Keyword too short2"), UserSettings.getWord("Keyword too short title"),
						JOptionPane.INFORMATION_MESSAGE);
				Doc.setStatus(UserSettings.getWord("No search results"));
				break;
			}

			case PATH_NOT_FOUND: {
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Path not found") + "\n"
						+ UserSettings.getWord("Path not found2"), UserSettings.getWord("Path not found"),
						JOptionPane.INFORMATION_MESSAGE);
				Doc.setStatus(UserSettings.getWord("Path not found"));
				break;
			}

			case PATH_FILTERED_OUT: {
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Path filtered out") + "\n"
						+ UserSettings.getWord("Path filtered out2"), UserSettings.getWord("Path filtered out title"),
						JOptionPane.INFORMATION_MESSAGE);
				Doc.setStatus(UserSettings.getWord("Path filtered out"));
			}
				break;

			default: {
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Error") + "\n"
						+ UserSettings.getWord("Error"), UserSettings.getWord("Error"), JOptionPane.INFORMATION_MESSAGE);

				Doc.setStatus(UserSettings.getWord("Error"));
				break;
			}
		}
	}

	// Get property
	// ========================================================================================================================================================

	public void setGraphDontRelax(final boolean DontRelax){
		dontRelax = DontRelax;

		graphPanel.setGraphDontRelax(DontRelax);
		while (inProcessing){

		}
	}

	public void setInitialFocusNode(final String focusNodeID){
		if (!"".equals(focusNodeID) && !(focusNodeID == null) && !focusNodeID.isEmpty()){
			this.focusNodeID = focusNodeID;
		} else{
			this.focusNodeID = null;
		}

	}

	void performInitialFocusNode(){
		log.info("Received focus node id = " + focusNodeID);
		final int convertedToId = getIdBySrcId(focusNodeID);
		log.info("Received focus node id converted to internal id = " + convertedToId);

		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run(){
					graphController.refocusNode(convertedToId);
				}
			});
		} catch (final InterruptedException e){
			log.warn(e.getMessage(), e);
		} catch (final InvocationTargetException e){
			log.error(e.getMessage(), e);
		}

		if (UserSettings.getBooleanAppletProperty("ShowToolbarPanel", true))
			toolBarController.invalidate();

		focusNodeID = null;
	}

	@Override
	public void run(){
		final Thread me = Thread.currentThread();

		running = true;

		while (relaxer == me && !staniBre){
			try{
				if (!suspended){
					if (!graphPanel.isGraphDirty()){
						synchronized (Doc.Subgraph){
							inProcessing = true;

							if (!dontRelax && Doc.Subgraph.getNodes().size() <= Doc.DB.getMaximumNodeCount()
									&& !Doc.getGraphVisualSettings().isZeroSize()){
								Doc.Subgraph.doGraphLayout();
							}

							graphPanel.setGraphDirty(true);

							if (Doc.Subgraph.getNodes().size() <= Doc.DB.getMaximumNodeCount()){
								graphPanel.inactive = false;
								if (!Doc.getInPathEdges().isEmpty())
									graphPanel.nextPaintState();
								graphPanel.repaint();
							} else{
								graphPanel.inactive = true;
							}

							if (Doc.Subgraph.layoutManagerChanged){
								graphPanel.ZoomToGraphExtents();
								Doc.Subgraph.layoutManagerChanged = false;
							}
							inProcessing = false;
						}
					}
				}

				if (focusNodeID != null){
					performInitialFocusNode();
				}

				if (!staniBre){
					Thread.sleep(180);
				}
			} catch (final InterruptedException e1){
				// break;
				e1.printStackTrace();
			}
		}

		running = false;
	}

	public void start(){
		staniBre = false;
		relaxer = new Thread(this);
		relaxer.start();
	}

	public void stop(){
		relaxer = null;
	}

	@Override
	public void windowActivated(final WindowEvent arg0){
	}

	@Override
	public void windowClosed(final WindowEvent arg0){
	}

	@Override
	public void windowClosing(final WindowEvent arg0){
		Ni3.destroyLockFile();
		stop();
		staniBre = true;
		while (running){
			try{
				Thread.sleep(10);
			} catch (final InterruptedException ex){
				log.warn("error while sleeping", ex);
			}
		}
		stopBackgroundOperations();

		new LoginController().logout();
	}

	@Override
	public void windowDeactivated(final WindowEvent arg0){
	}

	@Override
	public void windowDeiconified(final WindowEvent arg0){
	}

	@Override
	public void windowIconified(final WindowEvent arg0){
	}

	@Override
	public void windowOpened(final WindowEvent arg0){
	}

	public void edgeCreate(){
		edgeCreate(Doc.Subgraph.getFromNodes(), Doc.Subgraph.getToNodes());
	}

	public void edgeCreate(Node[] fromNodes, Node[] toNodes){
		ConnectionFrame cf = new ConnectionFrame(this);

		cf.fromID = fromNodes;
		cf.toID = toNodes;

		if (cf.fromID.length == 1 && cf.toID.length == 1 && cf.fromID[0] == cf.toID[0]){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings
					.getWord("It is not possible to create a connection from a node to itself"), UserSettings
					.getWord("Create connection"), JOptionPane.WARNING_MESSAGE);
		} else{
			if (cf.initComponents(false)){

				cf.restoreLast();

				cf.setLocation(getWidth() / 2 - cf.getHeight() / 2, getHeight() / 2 - cf.getHeight() / 2);
				cf.setVisible(true);
				if (cf.isSuccess()){
					Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
					Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
					Doc
							.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this,
									Doc.Subgraph);
				}
			} else{
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings
						.getWord("It is not possible to create a connection between these two nodes"), UserSettings
						.getWord("Create connection"), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void edgeEdit(final Edge edge){
		if (!Doc.checkUserRights(edge, "ObjectUpdateRights")){
			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
			return;
		}

		Doc.DB.reloadObject(edge.Obj);
		Doc.DB.getFavoritesContextData(Doc.getTopicID(), edge);

		itemsPanel.invalidate();
		itemsPanel.repaint();

		final ConnectionFrame cf = new ConnectionFrame(this, edge);
		cf.setLocation(getWidth() / 2 - cf.getHeight() / 2, getHeight() / 2 - cf.getHeight() / 2);
		cf.setVisible(true);
		if (cf.isSuccess()){
			Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
		}
	}

	public void edgeDetails(final Edge edge){
//		if (!Doc.checkUserRights(edge, "ObjectUpdateRights")){
//			Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
//			return;
//		}

		Doc.DB.reloadObject(edge.Obj);
		Doc.DB.getFavoritesContextData(Doc.getTopicID(), edge);

		itemsPanel.invalidate();
		itemsPanel.repaint();

		final ConnectionFrame cf = new ConnectionFrame(this, edge, true);
		cf.setLocation(getWidth() / 2 - cf.getHeight() / 2, getHeight() / 2 - cf.getHeight() / 2);
		cf.setVisible(true);
//		if (cf.isSuccess()){
//			Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
//			Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
//			Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
//		}
	}

	// TODO remember last created node and restore dialog state using it's data instead of making dialog static itself
	static DlgNodeProperties dlgNodeCreate = null;

	public int nodeCreate(final int GX, final int GY, double lon, double lat){
		try{
			if (dlgNodeCreate == null){
				dlgNodeCreate = new DlgNodeProperties(this, null, false);
			}

			dlgNodeCreate.setVisible(true);

			if (dlgNodeCreate.getReturnStatus() != DlgNodeProperties.RET_OK)
				return -1;
			startAnimation();

			DBObject newObject = dlgNodeCreate.getObject();
			// TODO is this assignment realy useful?
			newObject.setId(-1);

			ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
			objGateway.insertNode(newObject);

			doGeocode(lon, lat, newObject, objGateway);

			if (Doc.getTopicID() != 0){
				final Context c = newObject.getEntity().getContext("Favorites");
				if (c != null && newObject.hasContextValues(c)){
					final Object value = c.pk.getDataType().getValue(Integer.toString(Doc.getFavoritesID()));
					newObject.setValue(c.pk.ID, value);
					objGateway.setContext(newObject, c, Doc.getFavoritesID(), false);
				}
			}

			Point pt = null;

			if (GX != -1 && GY != -1){
				pt = new Point(GX, GY);
			}

			graphController.addNodeToGraph(newObject, pt);

			if (Doc.getCurrentChartId() != 0){
				final ChartParams chartParams = Doc.getChartParams(newObject.getEntity().ID);
				ChartController.getInstance().setChartMinMaxValues(chartParams);
			}

			stopAnimation(101);

			Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			return newObject.getId();
		} catch (final Exception e){
			log.error("Error create node", e);
			return -1;
		}
	}

	private void doGeocode(double lon, double lat, DBObject newObject, ObjectManagementGateway objGateway) {
		if (Double.isNaN(lon) && Double.isNaN(lat)){
            String addressParams = UserSettings.getStringAppletProperty("Geocoding_AddressParams", "address, postal_code, city, country");
            String address = createAddress(addressParams, newObject);
			if (!("".equals(address)) && address != null && !("null".equals(address))) {
				GeocodingResult geocodingResult = geocoder.geocode(address);
				if (geocodingResult != null) {
					lon = geocodingResult.getLon();
					lat = geocodingResult.getLat();
				}
			}
        }

		if (!Double.isNaN(lon) && !Double.isNaN(lat)){
            objGateway.updateNodeCoords(newObject.getId(), lon, lat);
        }
	}

	private String createAddress(String addressParams, DBObject node) {
		String address = "";
		String[] attributes = addressParams.split(",");
		for (String attribute : attributes){
			//get rid of potential
			attribute = attribute.trim();
			Object attributeValue = node.getValueByAttributeName(attribute);
			if (!"null".equals(attributeValue)) {
				address += attributeValue;
				address += ", ";
			}
		}
		if (address.endsWith(", ")) {
			address = address.substring(0, address.length() - 2);
		}
		return address;
	}

	public void nodeEdit(final Node toEdit){
		final List<DBObject> selection = Doc.Subgraph.getSelected();

		if (selection.size() != 1 && toEdit == null){
			return;
		}
		try{
			Node node;

			if (toEdit != null){
				node = toEdit;
			} else{
				node = Doc.Subgraph.findNode(selection.get(0).getId());
			}

			if (!node.Obj.getEntity().CanUpdate)
				return;

			if (!Doc.checkUserRights(node, "ObjectUpdateRights")){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
				return;
			}

			Doc.DB.reloadObject(node.Obj);
			Doc.DB.getFavoritesContextData(Doc.getTopicID(), node);
			itemsPanel.invalidate();
			itemsPanel.repaint();

			boolean updateContext = false;

			final DlgNodeProperties dlg = new DlgNodeProperties(this, node.Obj, node.status != 0);

			DBObject objectToUpdate = dlg.getObject();

			final Context c = objectToUpdate.getEntity().getContext("Favorites");
			if (c != null && node.Obj.hasContextValues(c)){
				updateContext = true;
			}

			dlg.setLocation(getWidth() / 2 - dlg.getHeight() / 2, getHeight() / 2 - dlg.getHeight() / 2); // new
			// Point(20,20)
			dlg.setVisible(true);

			if (dlg.getReturnStatus() != DlgNodeProperties.RET_OK)
				return;
			ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
			objGateway.updateNode(objectToUpdate, node.status != 0);

			doGeocode(Double.NaN, Double.NaN, objectToUpdate, objGateway);


			if (Doc.getTopicID() != 0){
				if (updateContext || (c != null && objectToUpdate.hasContextValues(c))){
					final Object value = c.pk.getDataType().getValue(Integer.toString(Doc.getFavoritesID()));
					objectToUpdate.setValue(c.pk.ID, value);
					objGateway.setContext(objectToUpdate, c, Doc.getFavoritesID(), node.status != 0);
				}
			}

			Doc.DB.reloadNode(node, Doc.Subgraph);
			Doc.DB.reloadObject(objectToUpdate);
			Doc.DB.getFavoritesContextData(Doc.getTopicID(), node);

			node.refreshLabel();

			for (final Edge e : node.inEdges){
				Doc.DB.reloadObject(e.Obj);
				e.refreshLabel();
			}

			for (final Edge e : node.outEdges){
				Doc.DB.reloadObject(e.Obj);
				e.refreshLabel();
			}

			Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc, null, this);
			if (Doc.getCurrentChartId() != 0){
				final ChartParams chartParams = Doc.getChartParams(node.Obj.getEntity().ID);
				ChartController.getInstance().setChartMinMaxValues(chartParams);
			}

			Doc.dispatchEvent(MSG_DBChanged, SRC_MainPanel, this, Doc.Subgraph);
			Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, this, null);
			Doc.fireRedrawGraphs();

			Doc.setFilter(Doc.filter, false);
			Doc.setDataSet(Doc.Subgraph.getDataSet());
		} catch (final Exception e){
			e.printStackTrace();
		}
	}

	public void nodeReplicate(final Node currentNode){
		final List<DBObject> selection = Doc.Subgraph.getSelected();

		if (selection.size() != 1 && currentNode == null){
			return;
		}
		try{
			Node node;

			if (currentNode != null){
				node = currentNode;
			} else{
				node = Doc.Subgraph.findNode(selection.get(0).getId());
			}

			if (!node.Obj.getEntity().CanUpdate)
				return;

			if (!Doc.checkUserRights(node, "ObjectUpdateRights")){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
				return;
			}

			Doc.DB.reloadObject(node.Obj);
			Doc.DB.getFavoritesContextData(Doc.getTopicID(), node);
			itemsPanel.invalidate();
			itemsPanel.repaint();

			boolean updateContext = false;

			final DlgNodeProperties dlg = new DlgNodeProperties(this, node.Obj, node.status != 0, DlgNodePropertiesAction.REPLICATE);

//			final Context c = objectToUpdate.getEntity().getContext("Favorites");
//			if (c != null && node.Obj.hasContextValues(c)){
//				updateContext = true;
//			}

			dlg.setLocation(getWidth() / 2 - dlg.getHeight() / 2, getHeight() / 2 - dlg.getHeight() / 2); // new
			// Point(20,20)
			dlg.setVisible(true);

			if (dlg.getReturnStatus() != DlgNodeProperties.RET_OK)
				return;

			DBObject newReplicatedNode = dlg.getObject();
			newReplicatedNode.setId(-1);
			ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
			objGateway.insertNode(newReplicatedNode);

			doGeocode(Double.NaN, Double.NaN, newReplicatedNode, objGateway);


//			if (Doc.getTopicID() != 0){
//				if (updateContext || (c != null && objectToUpdate.hasContextValues(c))){
//					final Object value = c.pk.getDataType().getValue(Integer.toString(Doc.getFavoritesID()));
//					objectToUpdate.setValue(c.pk.ID, value);
//					objGateway.setContext(objectToUpdate, c, Doc.getFavoritesID(), node.status != 0);
//				}
//			}

//			Doc.DB.reloadNode(node, Doc.Subgraph);
//			Doc.DB.reloadObject(newReplicatedNode);
//			Doc.DB.getFavoritesContextData(Doc.getTopicID(), node);

			Point pt = new Point(20 + (int)node.getX(), (int)node.getY() + 20);

			graphController.addNodeToGraph(newReplicatedNode, pt);

			if (Doc.getCurrentChartId() != 0){
				final ChartParams chartParams = Doc.getChartParams(newReplicatedNode.getEntity().ID);
				ChartController.getInstance().setChartMinMaxValues(chartParams);
			}

			stopAnimation(101);

			Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_SubgraphChanged, Ni3ItemListener.SRC_MainPanel, this, Doc.Subgraph);

//			node.refreshLabel();

			Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
			Doc.dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc, null, this);
			if (Doc.getCurrentChartId() != 0){
				final ChartParams chartParams = Doc.getChartParams(node.Obj.getEntity().ID);
				ChartController.getInstance().setChartMinMaxValues(chartParams);
			}

			Doc.dispatchEvent(MSG_DBChanged, SRC_MainPanel, this, Doc.Subgraph);
			Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, this, null);
			Doc.fireRedrawGraphs();

			Doc.setFilter(Doc.filter, false);
			Doc.setDataSet(Doc.Subgraph.getDataSet());
		} catch (final Exception e){
			e.printStackTrace();
		}
	}

	public void nodeDetails(final Node toEdit){
		final List<DBObject> selection = Doc.Subgraph.getSelected();

		if (selection.size() != 1 && toEdit == null){
			return;
		}
		try{
			Node node;

			if (toEdit != null){
				node = toEdit;
			} else{
				node = Doc.Subgraph.findNode(selection.get(0).getId());
			}

//			if (!node.Obj.getEntity().CanUpdate)
//				return;

//			if (!Doc.checkUserRights(node, "ObjectUpdateRights")){
//				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
//				return;
//			}

			Doc.DB.reloadObject(node.Obj);
			Doc.DB.getFavoritesContextData(Doc.getTopicID(), node);
			itemsPanel.invalidate();
			itemsPanel.repaint();

			boolean updateContext = false;

			final DlgNodeProperties dlg = new DlgNodeProperties(this, node.Obj, node.status != 0, DlgNodePropertiesAction.DETAILS);

//			DBObject objectToUpdate = dlg.getObject();
//			final Context c = objectToUpdate.getEntity().getContext("Favorites");
//			if (c != null && node.Obj.hasContextValues(c)){
//				updateContext = true;
//			}

			dlg.setLocation(getWidth() / 2 - dlg.getHeight() / 2, getHeight() / 2 - dlg.getHeight() / 2); // new
			// Point(20,20)
			dlg.setVisible(true);

//			if (dlg.getReturnStatus() != DlgNodeProperties.RET_OK)
//				return;
//			ObjectManagementGateway objGateway = new HttpObjectManagementGatewayImpl();
//			objGateway.updateNode(objectToUpdate, node.status != 0);
//			if (Doc.getTopicID() != 0){
//				if (updateContext || (c != null && objectToUpdate.hasContextValues(c))){
//					final Object value = c.pk.getDataType().getValue(Integer.toString(Doc.getFavoritesID()));
//					objectToUpdate.setValue(c.pk.ID, value);
//					objGateway.setContext(objectToUpdate, c, Doc.getFavoritesID(), node.status != 0);
//				}
//			}
//
//			Doc.DB.reloadNode(node, Doc.Subgraph);
//			Doc.DB.reloadObject(objectToUpdate);
//			Doc.DB.getFavoritesContextData(Doc.getTopicID(), node);
//
//			node.refreshLabel();

//			for (final Edge e : node.inEdges){
//				Doc.DB.reloadObject(e.Obj);
//				e.refreshLabel();
//			}

//			for (final Edge e : node.outEdges){
//				Doc.DB.reloadObject(e.Obj);
//				e.refreshLabel();
//			}

//			Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
//			Doc.dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc, null, this);
//			if (Doc.getCurrentChartId() != 0){
//				final ChartParams chartParams = Doc.getChartParams(node.Obj.getEntity().ID);
//				ChartController.getInstance().setChartMinMaxValues(chartParams);
//			}

//			Doc.dispatchEvent(MSG_DBChanged, SRC_MainPanel, this, Doc.Subgraph);
//			Doc.dispatchEvent(MSG_GraphDirty, SRC_Graph, this, null);
//			Doc.fireRedrawGraphs();

//			Doc.setFilter(Doc.filter, false);
//			Doc.setDataSet(Doc.Subgraph.getDataSet());
		} catch (final Exception e){
			e.printStackTrace();
		}
	}


	public void nodeDelete(final Node toDelete){
		final List<DBObject> selection = Doc.Subgraph.getSelected();

		if (selection.size() == 1 || toDelete != null){
			try{
				Node node;

				if (toDelete != null){
					node = toDelete;
				} else{
					node = Doc.Subgraph.findNode(selection.get(0).getId());
				}

				if (!Doc.checkUserRights(node, "ObjectDeleteRights")){
					Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Access denied"));
					return;
				}

				if (node.Obj.getEntity().CanDelete && node.status == 0){
					suspendRelaxation();
					final int ret = Ni3OptionPane.showConfirmDialog(Ni3.mainF, UserSettings
							.getWord("Do you want to delete node"), UserSettings.getWord("Delete node confirmation"),
							JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

					if (ret == 0){
						final boolean deleted = Doc.deleteNode(node);
						if (!deleted){
							String text = UserSettings
									.getWord("This node has more edges than your user can see and can not be deleted. Please contact system administrator to delete the edge");
							text += " (id=" + node.ID + ")";
							Ni3OptionPane.showMessageDialog(Ni3.mainF, text, UserSettings.getWord("Access Denied"),
									JOptionPane.ERROR_MESSAGE);
						} else{
							Doc.setFilter(Doc.filter, true);
							Doc.setDataSet(Doc.Subgraph.getDataSet());
							if (Doc.getCurrentChartId() != 0){
								final ChartParams chartParams = Doc.getChartParams(node.Obj.getEntity().ID);
								ChartController.getInstance().setChartMinMaxValues(chartParams);
							}
							Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
							Doc.dispatchEvent(Ni3ItemListener.MSG_DBChanged, Ni3ItemListener.SRC_MainPanel, this,
									Doc.Subgraph);
						}
					}
					resumeRelaxation();
				}
			} catch (final Exception e){
				log.error("Error delete node", e);
			}
		}
	}

	public void exportDataAsXLS(){
		final Ni3FileChooser jfc = new Ni3FileChooser(UserSettings.getWord("Export data"));
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(final File f){
				return f.isDirectory() || f.getName().endsWith(".xls");
			}

			@Override
			public String getDescription(){
				return UserSettings.getWord("MicrosoftExcelFiles");
			}
		});
		final int returnVal = jfc.showSaveDialog(this);
		String fileName;
		if (returnVal == JFileChooser.APPROVE_OPTION){

			final File f = jfc.getSelectedFile();
			fileName = f.getName();

			if (f.exists()){
				final int ret = Ni3OptionPane.showConfirmDialog(Ni3.mainF, UserSettings
						.getWord("Do you want to replace existing file")
						+ " " + fileName, UserSettings.getWord("Replace file confirmation"), JOptionPane.YES_NO_OPTION,

				JOptionPane.INFORMATION_MESSAGE);
				if (ret != 0){
					return;
				}
			}

			final String error = Doc.Subgraph.exportDataAsXLS(f, Doc.DB.schema.ID);

			if (error != null){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, error, UserSettings.getWord("Export data"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void onFilterChanged(final DataFilter filter){
		Doc.setFilter(filter, false);
	}

	@Override
	public void lostOwnership(final Clipboard arg0, final Transferable arg1){
	}

	@Override
	public void onBeforeSubgraphChange(){
		startAnimation();
		if (running){
			suspendRelaxation();
		}
	}

	@Override
	public void onSubgraphChanged(){
		resumeRelaxation();

		stopAnimation(31);
	}

	@Override
	public void onClearSubgraph(){
		repaint();
	}

	@Override
	public void onSubgraphObjectsRemoved(){
		Doc.setFilter(Doc.filter, false);
	}

	public void loadDefaultFavorite(){
		final String s = UserSettings.getProperty("Applet", "DefaultFavorite", null);
		try{
			if (s != null && !"null".equals(s) && Integer.valueOf(s) > 0){
				final int defaultFavId = Integer.valueOf(s);
				try{
					SwingUtilities.invokeAndWait(new Runnable(){
						@Override
						public void run(){
							log.debug("try load default favorite: " + defaultFavId);
							new FavoritesController(Doc).loadDocument(defaultFavId, Doc.SchemaID);
						}
					});
				} catch (final InterruptedException e){
					log.warn(e.getMessage(), e);
				} catch (final InvocationTargetException e){
					log.error(e.getMessage(), e);
				}
			}
		} catch (final NumberFormatException e){
			log.error("Default favorite id is invalid " + s);
		}

	}

	public void resetDisplayFilter(){
		commandPanel.filtersPanel.resetDisplayFilter();
		Doc.setFilter(Doc.filter, true);
	}

	public static boolean showInBrowser(String url){
		final String lowerUrl = url.toLowerCase();
		if (!lowerUrl.startsWith("http:") && !lowerUrl.startsWith("https:") && !lowerUrl.startsWith("ftp:")
				&& !lowerUrl.startsWith("file:")){
			url = "http://" + url;
		}
		if (Ni3.AppletMode && !lowerUrl.startsWith("file:")){
			try{
				SystemGlobals.theApp.getAppletContext().showDocument(new URL(url), "_blank");
			} catch (final MalformedURLException e1){
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else{
			final String osName = System.getProperty("os.name").toLowerCase();
			final Runtime rt = Runtime.getRuntime();
			try{
				if (osName.contains("win")){
					rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
				} else if (osName.contains("mac")){
					rt.exec("open " + url);
				} else{ // assume Unix or Linux
					final String[] browsers = { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror",
							"midori", "kazehakase", "mozilla" };
					String browser = null;
					for (final String b : browsers){
						if (browser == null && rt.exec(new String[] { "which", b }).getInputStream().read() != -1){
							rt.exec(new String[] { browser = b, url });
						}
					}
					if (browser == null){
						throw new Exception(Arrays.toString(browsers));
					}
				}
			} catch (final Exception e){
				log.error("Cannot launch url in browser", e);
				return false;
			}
		}
		return true;
	}

	public void createDynamicAttribute(){
		final DlgDynamicAggregation dlg = new DlgDynamicAggregation(this);

		dlg.setVisible(true);

		Doc.DB.refreshDynamicAttributes(Doc.Subgraph);
	}

	public void onRemoveDynamicAttributes(){
		for (Entity ent : Doc.DB.schema.definitions)
			if (ent.hasDynamicAttributes())
				ent.removeDynamicAttributes();
	}

	public void checkActivityStreamStartup(){
		boolean showActivityStream = UserSettings.getBooleanAppletProperty("ActivityStream_InUse", false)
				&& UserSettings.getBooleanAppletProperty("ActivityStream_ShowOnStartup", false);
		if (showActivityStream){
			showActivityStream();
		}
	}

	public void showActivityStream(){
		if (activityStreamManager == null){
			activityStreamManager = new ActivityStreamManager(this);
		}
		activityStreamManager.showActivityStream();
	}

	private void updateOverlayMenuState(Set<GISOverlay> selectedOverlays){
		if (menuOverlayMaps != null){
			for (int i = 0; i < menuOverlayMaps.getItemCount(); i++){
				JMenuItem item = menuOverlayMaps.getItem(i);
				if (item != null){
					item.setSelected(false);
				}
			}
			for (GISOverlay overlay : selectedOverlays){
				setMenuCheck(menuOverlayMaps, "" + overlay.getId());
			}
		}
	}

	private void updateThematicMenuState(){
		if (defaultMapMenuItem != null){
			defaultMapMenuItem.setSelected(true);
		}
		if (Doc.getThematicMapID() > 0){
			setMenuCheck(thematicMenuItems, "" + Doc.getThematicMapID());
		}
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		super.event(eventCode, sourceID, source, param);
		switch (eventCode){
			case MSG_Reload:
				reload();
				break;
			case MSG_GraphLayoutManagerChanged:
				for (int i = 0; i < layoutMenu.getItemCount(); i++){
					JMenuItem item = layoutMenu.getItem(i);
					item.setSelected(item.getActionCommand().equals(param));
				}
				break;
			case MSG_FavoriteLoaded:
				paintImmediately(getBounds());
				break;
			case MSG_MetaphorSetChanged:
				setMenuCheck(menuMetaphors, (String) param);
				break;
			case MSG_MapChanged:
				setMap((Integer) param);
				break;
			case Ni3ItemListener.MSG_NodeExpressCreateChanged:
				setGraphDontRelax((Boolean) param);
				break;
			case Ni3ItemListener.MSG_EdgeExpressCreateChanged:
				setGraphDontRelax((Boolean) param);
				break;
			case Ni3ItemListener.MSG_CreateNode: {
				Double[] coords = (Double[]) param;
				nodeCreate(-1, -1, coords[0], coords[1]);
			}
				break;
			case Ni3ItemListener.MSG_CreateEdge: {
				edgeCreate();
			}
				break;
			case Ni3ItemListener.MSG_OverlaysChanged:
				if (mapsEnabled){
					updateOverlayMenuState((Set<GISOverlay>) param);
				}
				break;
			case Ni3ItemListener.MSG_ThematicMapsChanged:
				if (mapsEnabled){
					menuMaps();
					menuOverlay();
					updateOverlayMenuState(Doc.getSelectedOverlays());
				}
				break;
			case MSG_ThematicMapChanged:
				if (mapsEnabled && thematicMenuItems != null){
					updateThematicMenuState();
				}
				break;
			case MSG_ChartChanged:
				setChartSelected((Integer) param);
				break;
			case MSG_ChartLegendVisibilityChanged:
				updateLegendMenuState();
				break;
			case MSG_FilterTreeChanged:
				Doc.resetInPathEdges();
				break;
			case MSG_RecalculateStatistics: {
				ValueUsageStatistics statistics = GraphController.calculateStatistics(Doc.Subgraph.getObjects(), false);
				Doc.getStatistics().update(statistics);
			}
				break;
		}
	}

	public ToolBarPanel getToolbarPanel(){
		return toolBarController.getToolbarPanel();
	}
}