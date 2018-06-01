package com.ni3.ag.navigator.client.domain.cache;

import com.ni3.ag.navigator.client.gateway.IconGateway;
import com.ni3.ag.navigator.client.gateway.ServiceFactory;
import com.ni3.ag.navigator.client.gui.Ni3;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class IconCache {
	private static final String PREFIX = "/images/";

	public static final IconDescriptor MENU_FOLDER = 						new IconDescriptor(PREFIX + "menu_folder.png");
	public static final IconDescriptor MENU_FAVORITE = 						new IconDescriptor(PREFIX + "menu_favorite.png");
	public static final IconDescriptor MENU_QUERY = 						new IconDescriptor(PREFIX + "menu_query.png");
	public static final IconDescriptor MENU_TOPIC = 						new IconDescriptor(PREFIX + "menu_topic.png");

	public static final IconDescriptor REPORT_XLS = 						new IconDescriptor(PREFIX + "report_xls.png");
	public static final IconDescriptor REPORT_PDF = 						new IconDescriptor(PREFIX + "report_pdf.png");
	public static final IconDescriptor REPORT_NI3_LOGO =					new IconDescriptor(PREFIX + "report_ni3_logo.png");

	public static final IconDescriptor GRAPH_ANCHOR_MARK = 					new IconDescriptor(PREFIX + "graph_anchor_mark.png");
	public static final IconDescriptor ZOOM_IN = 							new IconDescriptor(PREFIX + "zoom_in.png");
	public static final IconDescriptor ZOOM_OUT = 							new IconDescriptor(PREFIX + "zoom_out.png");
	public static final IconDescriptor REFRESH_STOPPED = 					new IconDescriptor(PREFIX + "refresh_stopped.png");
	public static final IconDescriptor REFRESH_ACTIVE = 					new IconDescriptor(PREFIX + "refresh_active.png");

	public static final IconDescriptor TOOLBAR_LEFT = 						new IconDescriptor(PREFIX + "toolbar_left.png");
	public static final IconDescriptor TOOLBAR_RIGHT = 						new IconDescriptor(PREFIX + "toolbar_right.png");
	public static final IconDescriptor TOOLBAR_FINDPATH = 					new IconDescriptor(PREFIX + "toolbar_findpath.png");
	public static final IconDescriptor TOOLBAR_CLEAR_HIGHLIGHTS = 			new IconDescriptor(PREFIX + "toolbar_clear_highlights.png");
	public static final IconDescriptor TOOLBAR_REMOVE = 					new IconDescriptor(PREFIX + "toolbar_remove.png");
	public static final IconDescriptor TOOLBAR_ISOLATE = 					new IconDescriptor(PREFIX + "toolbar_isolate.png");
	public static final IconDescriptor TOOLBAR_SIMPLE_SEARCH = 				new IconDescriptor(PREFIX + "toolbar_simple_search.png");
	public static final IconDescriptor TOOLBAR_ADVANCED_SEARCH = 			new IconDescriptor(PREFIX + "toolbar_advanced_search.png");
	public static final IconDescriptor TOOLBAR_GEO_ANALYTICS = 				new IconDescriptor(PREFIX + "toolbar_geo_analytics.png");
	public static final IconDescriptor TOOLBAR_RELOAD = 					new IconDescriptor(PREFIX + "toolbar_reload.png");
	public static final IconDescriptor TOOLBAR_CLEAR = 						new IconDescriptor(PREFIX + "toolbar_clear.png");
	public static final IconDescriptor TOOLBAR_GRAPH_EDIT_TOGGLE = 			new IconDescriptor(PREFIX + "toolbar_graph_edit_toggle.png");
	public static final IconDescriptor TOOLBAR_CREATE_DYNAMIC_ATTRIBUTE = 	new IconDescriptor(PREFIX + "toolbar_create_dynamic_attribute.png");
	public static final IconDescriptor TOOLBAR_SAVE = 						new IconDescriptor(PREFIX + "toolbar_save.png");
	public static final IconDescriptor TOOLBAR_ACTIVITY_STREAM = 			new IconDescriptor(PREFIX + "toolbar_activity_stream.png");
	public static final IconDescriptor TOOLBAR_NAVIGATOR_LOGO = 			new IconDescriptor(PREFIX + "toolbar_navigator_logo.png");

	public static final IconDescriptor FILTER_NODE = 						new IconDescriptor(PREFIX + "filter_node.png");
	public static final IconDescriptor FILTER_EDGE = 						new IconDescriptor(PREFIX + "filter_edge.png");
	public static final IconDescriptor FILTER_EXPAND = 						new IconDescriptor(PREFIX + "filter_expand.png");
	public static final IconDescriptor FILTER_COLLAPSE = 					new IconDescriptor(PREFIX + "filter_collapse.png");
	public static final IconDescriptor FILTER_NI3_LOGO = 					new IconDescriptor(PREFIX + "filter_ni3_logo.png");

	public static final IconDescriptor PROGRESS_ACTIVE = 					new IconDescriptor(PREFIX + "progress_active.gif");
	public static final IconDescriptor PROGRESS_INACTIVE = 					new IconDescriptor(PREFIX + "progress_inactive.gif");

	public static final IconDescriptor MAP_NODE_TOGGLE = 					new IconDescriptor(PREFIX + "map_node_toggle.png");
	public static final IconDescriptor MAP_PIN_TOGGLE = 					new IconDescriptor(PREFIX + "map_pin_toggle.png");

	public static final IconDescriptor SEARCH_SINGLE_NODE = 				new IconDescriptor(PREFIX + "search_single_node.png");
	public static final IconDescriptor SEARCH_LINKED_NODES = 				new IconDescriptor(PREFIX + "search_linked_nodes.png");
	public static final IconDescriptor SEARCH_NODES_WITH_CONNECTIONS = 		new IconDescriptor(PREFIX + "search_nodes_with_connections.png");


	public static ImageIcon getImageIcon(IconDescriptor name) {
		URL imageURL = Ni3.class.getResource(name.getName());
		return new ImageIcon(imageURL);
	}

	public static Image getImage(IconDescriptor name){
		Image res = null;
		try {
			res = ImageIO.read( Ni3.class.getResource(name.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	private static class IconDescriptor {
		private String name;
		public IconDescriptor(String name){
			this.name = name;
		}
		public String getName(){
			return name;
		}
	}
}
