-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.4.5';
	_newVersion varchar = '1.4.6';
	_version varchar;
	_cc integer;
	_settings_to_insert varchar[][] = array[
	['Applet', 'ApplyFilter_Visible', 'FALSE'],
	['Applet', 'NoFocus_Visible', 'FALSE'],
	['Applet', 'UseSimplesearch', 'FALSE'],
	['Applet', 'BarCharts_InUse', 'FALSE'],
	['Applet', 'File_ChangePassword_InUse', 'FALSE'],
	['Applet', 'MaxPathLength', '5'],
	['Applet', 'PieCharts_InUse', 'FALSE'],
	['Applet', 'StackedCharts_InUse', 'FALSE'],
	['Applet', 'Toolbar_CreateEdge_InUse', 'FALSE'],
	['Applet', 'Toolbar_CreateNode_InUse', 'FALSE'],
	['Applet', 'TooltipHTMLWrapLen', '50'],
	['Applet', 'NoUnrelated_Visible', 'FALSE'],
	['Applet', 'PathLengthOverrun', '0'],
	['Applet', 'Connection_ConnectionDelete_InUse', 'FALSE'],
	['Applet', 'MinKeywordSearchLen', '2'],
	['Applet', 'ResetFilter_Visible', 'FALSE'],
	['Applet', 'GISPieScale', '17'],
	['Applet', 'MaxNumberOfSearchResult', '900'],
	['gis', 'DefaultMapID', '2'],
	['GIS', 'NumberOfRetries', '3'],
	['GIS', 'ShowImperialScale', 'TRUE'],
	['GIS', 'ShowMetricScale', 'TRUE'],
	['FontColor', 'GRAPH_CONTROL_ITEM_FONT', 'Dialog,0,12']
	];
	_settings_to_drop varchar[] = array[
	'CHARACTER_NUMBER_FOR_LIST_TOOLTIP',
	'Default thematic dataset',
	'default_level_filter',
	'bottom_list_name',
	'query_builder_frame_height',
	'query_builder_frame_width',
	'query_builder_frame_x_location',
	'query_builder_frame_y_location',
	'query_builder_item_dialog_height',
	'query_builder_item_dialog_width',
	'query_builder_item_dialog_x_location',
	'query_builder_item_dialog_y_location',
	'warning_dialog_height',
	'warning_dialog_width',
	'warning_dialog_x_location',
	'warning_dialog_y_location',
	'BottomListTooltip',
	'MaximumNodeNumber',
	'MaxMetaphorZoom',
	'MinMetaphorZoom',
	'graph_layout',
	'initial_search',
	'HISTORY_BACKGROUND',
	'HISTORY_BORDER',
	'HISTORY_HEADER_BACKGROUND',
	'HISTORY_HEADER_FOREGROUND',
	'HISTORY_HEADER_LABEL_FONT',
	'HISTORY_ITEM_BACKGROUND',
	'HISTORY_ITEM_BORDER',
	'HISTORY_ITEM_FONT',
	'HISTORY_ITEM_FOREGROUND',
	'HISTORY_ITEM_SELECTED_BACKGROUND',
	'INNER_COMMAND_PANEL_BORDERS_COLOR',
	'ITEMS_PANEL_BORDER',
	'LEVELS_LABEL_FONT',
	'LEVELS_LABEL_FOREGROUND',
	'TREE_PANEL_BACKGROUND',
	'TREE_PANEL_ITEMS_BACKGROUND',
	'TREE_PANEL_ITEMS_FOREGROUND',
	'ZOOM_IN_ZOOM_OUT_PANEL_BACKGROUND',
	'QUERY_ITEM_PANEL_BACKGROUND',
	'BOTTOM_LIST_BACKGROUND',
	'BOTTOM_LIST_BORDER',
	'BOTTOM_LIST_HEADER_BACKGROUND',
	'BOTTOM_LIST_HEADER_FOREGROUND',
	'BOTTOM_LIST_HEADER_LABEL_FONT',
	'BOTTOM_LIST_ITEM_BACKGROUND',
	'BOTTOM_LIST_ITEM_BORDER',
	'BOTTOM_LIST_ITEM_FONT',
	'BOTTOM_LIST_ITEM_FOREGROUND',
	'BOTTOM_LIST_ITEM_SELECTED_BACKGROUND',
	'BACK_BUTTON_BACKGROUND',
	'BUILD_QUERY_BUTTON_BORDER',
	'FORWARD_BUTTON_BACKGROUND',
	'RELOAD_BUTTON_BORDER',
	'SEARCH_BUTTON_BORDER',
	'COMMAND_PANEL_BACKGROUND',
	'COMMAND_PANEL_FILTERS_LIST_HEADER_BACKGROUND',
	'COMMAND_PANEL_FILTERS_LIST_HEADER_FOREGROUND',
	'COMMAND_PANEL_FILTERS_LIST_HEADER_LABEL_FONT',
	'COMMAND_PANEL_OPTIONS_LIST_HEADER_BACKGROUND',
	'COMMAND_PANEL_OPTIONS_LIST_HEADER_FOREGROUND',
	'COMMAND_PANEL_OPTIONS_LIST_HEADER_LABEL_FONT',
	'FILES_LABEL_FONT',
	'FILES_LABEL_FOREGROUND',
	'FILTER_HEADER_LABEL_FONT',
	'GisEdges',
	'GisEdgesArrow',
	'GRAPH_CONTROL_LABEL_FOREGROUND',
	'GRAPH_PANEL_BORDER',
	'SEARCH_FIELD_BACKGROUND',
	'SEARCH_FIELD_FOREGROUND',
	'SEARCH_PANEL_BACKGROUND',
	'SEARCH_RESULT_BACKGROUND',
	'SEARCH_RESULT_BORDER',
	'SEARCH_RESULT_HEADER_BACKGROUND',
	'SEARCH_RESULT_HEADER_FOREGROUND',
	'SEARCH_RESULT_ITEM_BACKGROUND',
	'SEARCH_RESULT_ITEM_BORDER',
	'SEARCH_RESULT_ITEM_FONT',
	'SEARCH_RESULT_ITEM_FOREGROUND',
	'SEARCH_RESULT_ITEM_SELECTED_BACKGROUND',
	'TOP_LIST_BACKGROUND',
	'TOP_LIST_BORDER',
	'TOP_LIST_HEADER_BACKGROUND',
	'TOP_LIST_HEADER_FOREGROUND',
	'TOP_LIST_HEADER_LABEL_FONT',
	'TOP_LIST_ITEM_BACKGROUND',
	'TOP_LIST_ITEM_BORDER',
	'TOP_LIST_ITEM_FONT',
	'TOP_LIST_ITEM_FOREGROUND',
	'TOP_LIST_ITEM_SELECTED_BACKGROUND',
	'bunch_angle_1',
	'bunch_angle_10',
	'bunch_angle_11',
	'bunch_angle_2',
	'bunch_angle_3',
	'bunch_angle_4',
	'bunch_angle_5',
	'bunch_angle_6',
	'bunch_angle_7',
	'bunch_angle_8',
	'bunch_angle_9',
	'force_multiplier',
	'graph_sleep_time',
	'link_length_1',
	'link_length_10',
	'link_length_2',
	'link_length_3',
	'link_length_4',
	'link_length_5',
	'link_length_6',
	'link_length_7',
	'link_length_8',
	'link_length_9',
	'link_length_offset',
	'repulsion_range',
	'selection_mode',
	'stretch',
	'total_angle_occupie',
	'total_try_times'
	];
	_group_setting_to_drop varchar = 'Strength_InUse';
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

	for i in array_lower(_settings_to_insert,1) .. array_upper(_settings_to_insert,1) loop
		select count(*) into _cc from sys_settings_application where prop = _settings_to_insert[i][2];
		if (_cc=0) then
			insert into sys_settings_application (section, prop, value) 
				values ( _settings_to_insert[i][1], _settings_to_insert[i][2], _settings_to_insert[i][3]);
		end if;
	end loop;
	
	for i in array_lower(_settings_to_drop,1) .. array_upper(_settings_to_drop,1) loop
		delete from sys_settings_application where prop like _settings_to_drop[i];
	end loop;
	
	delete from sys_settings_group where prop like _group_setting_to_drop;
	
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