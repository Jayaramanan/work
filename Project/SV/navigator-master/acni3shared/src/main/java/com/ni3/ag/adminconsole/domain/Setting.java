/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.util.Arrays;
import java.util.List;

public interface Setting{

	public static final String[] SETTINGS_MENU_TREE_NODES = new String[] { "File_", "Node_", "Connection_", "Maps_",
	        "Charts_", "Help_" };

	public final static String INHERITS_GROUP_SETTINGS_PROPERTY = "_InheritsGroupSettings";
	public final static String LIST_PANEL_SPLIT_LOCATION = "list_panel_split_location";
	public static final String APPLET_SECTION = "Applet";
	public static final String GIS_SECTION = "GIS";
	public static final String TAB_SWITCH_ACTION_PROPERTY = "AC_TAB_SWITCH_ACTION";
	public static final String SCHEME_PROPERTY = "Scheme";
	public static final String LANGUAGE_PROPERTY = "Language";
	public static final String OBJECT_UPDATE_RIGHTS_PROPERTY = "ObjectUpdateRights";
	public static final String OBJECT_DELETE_RIGHTS_PROPERTY = "ObjectDeleteRights";
	public static final String DEFAULT_MAP_ID_PROPERTY = "DefaultMapID";
	public static final String DEFAULT_FAVORITE_PROPERTY = "DefaultFavorite";
	public static final String HIDE_GIS_PANEL_PROPERTY = "HideGisPanel";
	public static final String DATE_FORMAT_PROPERTY = "DateFormat";
	public static final String HELP_DOCUMENT_URL_PROPERTY = "HelpDocumentUrl";
	public static final String PASSWORD_COMPLEXITY_SETTING = "PasswordComplexity";
	public static final String NODE_SELECTED_COLOR_PROPERTY = "NODE_SELECTED_COLOR";
	public static final String PREFILTER_BACKGROUND_PROPERTY = "PREFILTER_BACKGROUND";
	public static final String GRADIENT_START_COLOR_PROPERTY = "GEO_ANALYTICS_GRADIENT_START_COLOR";
	public static final String GRADIENT_END_COLOR_PROPERTY = "GEO_ANALYTICS_GRADIENT_END_COLOR";
	public static final String DISPLAY_FILTER_SHOW_EMPTY_VALUES_PROPERTY = "DisplayFilter_ShowEmptyValues";
	public static final String CONFIG_LOCKED_OBJECT_PRIVILEGES_PROPERTY = "ConfigLockedObjectPrivileges";
	public static final String HIERARCHIES_PROPERTY = "Hierarchies";
	public static final String IN_USE_SUFFIX = "_InUse";
	public static final String SHOW_PREFIX = "Show";
	public static final String VISIBLE_SUFFIX = "_Visible";
	public static final String IMAGE_CACHE_REFRESH = "ImageCacheRefresh";

	public static final String[] mandatorySettings = { TAB_SWITCH_ACTION_PROPERTY, INHERITS_GROUP_SETTINGS_PROPERTY,
	        SCHEME_PROPERTY, LANGUAGE_PROPERTY, "ActivityStream_InUse", "BarCharts_InUse", "Charts_InUse",
	        "command_panel_split_location", "Connection_ConnectionCreate_InUse", "Connection_ConnectionEdit_InUse",
	        "Connection_InUse", "ContextMenu_Connection_Delete_InUse", "ContextMenu_Connection_Delete_InUse",
	        "ContextMenu_Connection_Edit_InUse", "ContextMenu_Connection_Edit_InUse", "ContextMenu_Connection_InUse",
	        "ContextMenu_Connection_InUse", "ContextMenu_Node_Delete_InUse", "ContextMenu_Node_Edit_InUse",
	        "ContextMenu_Node_Expand_InUse", "ContextMenu_Node_InUse", "ContextMenu_Node_Refocus_InUse",
	        "ContextMenu_Node_Remove_InUse", "ContextMenu_Node_ShowPolygon", "ContextMenu_Node_ShowPolyline",
	        "Default chart", "Default thematic dataset", DEFAULT_MAP_ID_PROPERTY, "directed_edge", "EdgeInFocusColor",
	        "Favorites_InUse", "File_ChangePassword_InUse", "File_CopyData_InUse", "File_CopyGraph_InUse",
	        "File_CopyMap_InUse", "File_Exit_InUse", "File_ExportData_InUse", "File_InUse", "FILES_LABEL_FONT",
	        "GRAPH_CONTROL_COUNTER_FONT", "GRAPH_CONTROL_ITEM_FONT", "graph_panel_split_location", "Help_About_InUse",
	        "Help_Documentation_InUse", "Help_InUse", HELP_DOCUMENT_URL_PROPERTY, "HideGisPanel", "InitialZoom", "Language",
	        "Maps_InUse", "Metaphors_InUse", "MetaphorZoom", "Node_InUse", "Node_NodeDelete_InUse", "Node_NodeEdit_InUse",
	        "Node_NodeSecurity_InUse", NODE_SELECTED_COLOR_PROPERTY, "NoFocus_True", "NoFocus_Visible", "NoOrphans_True",
	        "NoOrphans_Visible", "NoUnrelated_True", "NoUnrelated_Visible", PASSWORD_COMPLEXITY_SETTING, "PieCharts_InUse",
	        PREFILTER_BACKGROUND_PROPERTY, "RelaxType", "Schema_InUse", "SEARCH_FIELD_FONT", "show_labels",
	        "ShowDirectedGraph_InUse", "ShowEdgeLabel_InUse", "ShowEdgeThickness_InUse", "StackedCharts_InUse",
	        "Toolbar_CreateNode_InUse", "TopicEdgeSelection_Visible", "UnusedMenuItems" };

	public static final List<String> BOOLEAN_SETTINGS = Arrays.asList(INHERITS_GROUP_SETTINGS_PROPERTY, "NoOrphans_True",
	        "directed_edge", "show_labels", HIDE_GIS_PANEL_PROPERTY, "UseAdvancedSearch", "UseSimpleSearch",
	        DISPLAY_FILTER_SHOW_EMPTY_VALUES_PROPERTY, CONFIG_LOCKED_OBJECT_PRIVILEGES_PROPERTY,
	        "ActivityStream_ShowOnStartup", IMAGE_CACHE_REFRESH);

	public String getSection();

	public void setSection(String section);

	public String getProp();

	public void setProp(String prop);

	public String getValue();

	public void setValue(String value);

	public boolean isNew();
}
