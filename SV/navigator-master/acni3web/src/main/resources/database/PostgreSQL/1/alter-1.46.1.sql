-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script

CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.46.0';
	_newVersion varchar = '1.46.1';
	_version varchar;
	_cc integer;
	_sql varchar;
	_inSql varchar;
	_rows varchar[][] = array[
['Applet', 'ActivityStream_InUse', 'false'],
['Applet', 'ActivityStream_ShowOnStartup', 'false'],
['Applet', 'AC_TAB_SWITCH_ACTION', 'NotSet'],
['Applet', 'BarCharts_InUse', 'true'],
['Applet', 'Charts_InUse', 'true'],
['Applet', 'command_panel_split_location', '-1'],
['Applet', 'ConfigLockedObjectPrivileges', 'false'],
['Applet', 'Connection_ConnectionCreate_InUse', 'false'],
['Applet', 'Connection_ConnectionEdit_InUse', 'false'],
['Applet', 'Connection_InUse', 'false'],
['Applet', 'ContextMenu_Connection_Delete_InUse', 'true'],
['Applet', 'ContextMenu_Connection_Edit_InUse', 'true'],
['Applet', 'ContextMenu_Connection_InUse', 'true'],
['Applet', 'ContextMenu_Connection_JumpToTopic_InUse', 'true'],
['Applet', 'ContextMenu_Node_Asign_Image_InUse', 'true'],
['Applet', 'ContextMenu_Node_Delete_InUse', 'true'],
['Applet', 'ContextMenu_Node_Edit_InUse', 'true'],
['Applet', 'ContextMenu_Node_Expand_InUse', 'true'],
['Applet', 'ContextMenu_Node_InUse', 'true'],
['Applet', 'ContextMenu_Node_RefocusAsIs_InUse', 'true'],
['Applet', 'ContextMenu_Node_Refocus_InUse', 'true'],
['Applet', 'ContextMenu_Node_Remove_InUse', 'true'],
['Applet', 'ContextMenu_Node_SelectiveExpand_InUse', 'true'],
['Applet', 'ContextMenu_Node_ShowPolygon', 'true'],
['Applet', 'ContextMenu_Node_ShowPolyline', 'true'],
['Applet', 'CurrentFavoritesOnly_True', 'true'],
['Applet', 'DataFilterTreeIcons', 'true'],
['Applet', 'DateFormat', 'dd/MM/yyyy'],
['Applet', 'DefaultFavorite', '-1'],
['Applet', 'DisplayFilter_ShowEmptyValues', 'true'],
['Applet', 'DynamicCharts_InUse', 'true'],
['Applet', 'EdgeInFocusColor', '255,0,0'],
['Applet', 'EdgeInFocusWidth', '2.0'],
['Applet', 'Favorites_InUse', 'true'],
['Applet', 'File_ChangePassword_InUse', 'true'],
['Applet', 'File_CopyData_InUse', 'true'],
['Applet', 'File_CopyGraph_InUse', 'true'],
['Applet', 'File_CopyMap_InUse', 'true'],
['Applet', 'File_Exit_InUse', 'true'],
['Applet', 'File_ExportAsCSV_InUse', 'false'],
['Applet', 'File_ExportData_InUse', 'true'],
['Applet', 'File_InUse', 'true'],
['Applet', 'FilterFrom_True', 'true'],
['Applet', 'FilterFrom_Visible', 'true'],
['Applet', 'FilterTo_True', 'true'],
['Applet', 'FilterTo_Visible', 'true'],
['Applet', 'FilterTreeIcons', 'true'],
['Applet', 'GeoAnalytics_InUse', 'false'],
['Applet', 'GEO_ANALYTICS_GRADIENT_END_COLOR', '216,235,200'],
['Applet', 'GEO_ANALYTICS_GRADIENT_START_COLOR', '132,168,2'],
['Applet', 'GraphEdgeLabelFont', 'Arial'],
['Applet', 'GraphEdgeLabelHeight', '7'],
['Applet', 'GraphLabelWrapLength', '0'],
['Applet', 'GraphLayout_InUse', 'true'],
['Applet', 'GraphNodeLabelFont', 'Arial'],
['Applet', 'GraphNodeLabelHeight', '9'],
['Applet', 'GraphNodeScale', '1'],
['Applet', 'graph_panel_split_location', '-1'],
['Applet', 'GridLayout_InUse', 'true'],
['Applet', 'HelpDocumentUrl', 'http://www.ni3.net/resources/documentation/'],
['Applet', 'Help_About_InUse', 'true'],
['Applet', 'Help_Documentation_InUse', 'true'],
['Applet', 'Help_InUse', 'true'],
['Applet', 'HideGisPanel', 'false'],
['Applet', 'Hierarchies', ''],
['Applet', 'HierarchyLayout_InUse', 'true'],
['Applet', 'ImageCacheRefresh', 'false'],
['Applet', 'Language', '1'],
['Applet', 'LayoutSettings_InUse', 'true'],
['Applet', 'list_panel_split_location', '-1'],
['Applet', 'Maps_InUse', 'true'],
['Applet', 'MarkFocusNodes', 'true'],
['Applet', 'Matrix_Merge_InUse', 'true'],
['Applet', 'MaxMetaphorZoom', '1.25'],
['Applet', 'MaximumNodeNumber', '1500'],
['Applet', 'MaxPathLength', '10'],
['Applet', 'Metaphors_InUse', 'true'],
['Applet', 'MinKeywordSearchLen', '3'],
['Applet', 'NodeSpaceSliderMaxValue', '30'],
['Applet', 'NodeSpaceSliderMinValue', '0'],
['Applet', 'NodeSpaceSliderValue', '10'],
['Applet', 'Node_InUse', 'false'],
['Applet', 'Node_NodeCreate_InUse', 'false'],
['Applet', 'Node_NodeDelete_InUse', 'false'],
['Applet', 'Node_NodeEdit_InUse', 'false'],
['Applet', 'NoFocus_True', 'false'],
['Applet', 'NoFocus_Visible', 'true'],
['Applet', 'NoOrphans_True', 'true'],
['Applet', 'NoOrphans_Visible', 'false'],
['Applet', 'NoSingles_True', 'false'],
['Applet', 'NoSingles_Visible', 'true'],
['Applet', 'NoUnrelated_True', 'false'],
['Applet', 'NoUnrelated_Visible', 'true'],
['Applet', 'ObjectDeleteRights', '3'],
['Applet', 'ObjectUpdateRights', '3'],
['Applet', 'PasswordComplexity', ''],
['Applet', 'PathLengthOverrun', '1'],
['Applet', 'PieCharts_InUse', 'true'],
['Applet', 'RadialLayout_InUse', 'true'],
['Applet', 'Schema_InUse', 'true'],
['Applet', 'Scheme', '-1'],
['Applet', 'ShowDirectedGraph_InUse', 'true'],
['Applet', 'ShowEdgeLabel_InUse', 'true'],
['Applet', 'ShowEdgeThickness_InUse', 'true'],
['Applet', 'ShowNodeExpandCounter', 'true'],
['Applet', 'ShowNodeExpandCounter_InUse', 'true'],
['Applet', 'ShowToolbarPanel', 'true'],
['Applet', 'SNABasic_InUse', 'true'],
['Applet', 'SNAWarningMaxEdges', '1000'],
['Applet', 'SNAWarningMaxNodes', '500'],
['Applet', 'SpringLayout_InUse', 'true'],
['Applet', 'StackedCharts_InUse', 'true'],
['Applet', 'SumValueFor_Visible', 'true'],
['Applet', 'ThematicLegend_InUse', 'true'],
['Applet', 'Tile_Server_Max_Zoom', '15'],
['Applet', 'Tile_Server_Url', 'http://eu1.ni3.net/tiles/'],
['Applet', 'Toolbar_CreateEdge_InUse', 'true'],
['Applet', 'Toolbar_CreateNode_InUse', 'true'],
['Applet', 'Toolbar_DynamicAttribute_InUse', 'true'],
['Applet', 'TooltipHTMLFontSize', '3'],
['Applet', 'TooltipHTMLWrapLen', '30'],
['Applet', 'TopicEdgeSelection_Visible', 'false'],
['Applet', 'TopicGUI_InUse', 'false'],
['Applet', 'UnusedMenuItems', '0'],
['Applet', '_InheritsGroupSettings', 'true'],
['FontColor', 'ChartLegendFont', 'Dialog,1,14'],
['FontColor', 'GRAPH_CONTROL_COUNTER_FONT', 'Dialog,0,12'],
['FontColor', 'GRAPH_CONTROL_ITEM_FONT', 'Dialog,0,12'],
['FontColor', 'NODE_SELECTED_COLOR', '255,0,0'],
['FontColor', 'PREFILTER_BACKGROUND', '255,255,255'],
['FontColor', 'SEARCH_FIELD_FONT', 'Dialog,0,12'],
['GIS', 'DefaultMapID', '2'],
['graph', 'directed_edge', 'false'],
['graph', 'RelaxType', '2'],
['graph', 'show_labels', 'false']
];
BEGIN
	-- check version
	select version into _version from sys_iam where name = 'PostgreSQL';

	if (_version != _expectedVersion) then
		raise exception 'Wrong database version: expected - %, but was %', _expectedVersion, _version;
	elsif (_version = _newVersion) then
		raise exception 'New database version should differ from current : %', _version;
	end if;

	raise info 'Version check completed';
------------------------------------------------------------
update sys_settings_user set prop = 'DefaultFavorite' where prop = 'Default graph';
update sys_settings_group set prop = 'DefaultFavorite' where prop = 'Default graph';
update sys_settings_application set prop = 'DefaultFavorite' where prop = 'Default graph';

update sys_settings_user set prop = 'Matrix_Merge_InUse' where prop = 'Matrix_Context_Merge_InUse';
update sys_settings_group set prop = 'Matrix_Merge_InUse' where prop = 'Matrix_Context_Merge_InUse';
update sys_settings_application set prop = 'Matrix_Merge_InUse' where prop = 'Matrix_Context_Merge_InUse';

	_inSql = '';
	for i in array_lower(_rows,1) .. array_upper(_rows,1) loop
		if (i > 1) then
			_inSql = _inSql || ',';
		end if;
		_inSql = _inSql || '''' ||  _rows[i][2] || '''';
	end loop;
	
	_sql := 'delete from sys_settings_application where prop not in ('||_inSql||')';
	raise info 'sql: %', _sql;
	execute _sql;
	_sql := 'delete from sys_settings_group where prop not in ('||_inSql||')';
	execute _sql;
	_sql := 'delete from sys_settings_user where prop not in ('||_inSql||')';
	execute _sql;
	
	for i in array_lower(_rows,1) .. array_upper(_rows,1) loop
	--raise info 'Check property % in sys_settings_application...', _rows[i][2];
      	select count(*) into _cc from sys_settings_application where prop = _rows[i][2];
		if(_cc = 0) then
			raise info 'Not exists % - create', _rows[i][2];
			insert into sys_settings_application (section, prop, value) 
				values (_rows[i][1], _rows[i][2], _rows[i][3]);
		end if;
	end loop;

	------------------------------------------------------------
	-- update dbversion in cis_favorites (comment this line if script impacts favorites)
	update cis_favorites set dbversion = _newVersion where dbversion = _expectedVersion;
------------------------------------------------------------
	raise info 'Database update script is completed';
	-- update database version to _newVersion
	update sys_iam set version=_newVersion where name = 'PostgreSQL';
	raise info 'Database version updated: % -> %', _version, _newVersion;
END;
$$ LANGUAGE plpgsql;


-- launch function
select alterDatabase();
-- drop function
drop function alterDatabase();
