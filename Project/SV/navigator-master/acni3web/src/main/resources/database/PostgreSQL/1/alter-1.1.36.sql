-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.35';
	_newVersion varchar = '1.1.36';
	_version varchar;
	_cc integer;
	_rows varchar[][] = array[
['Applet','_InheritsGroupSettings','false'],
['Applet','AC_TAB_SWITCH_ACTION','AllwaysAsk'],
['Applet','Charts_InUse','TRUE'],
['Applet','command_panel_split_location','180'],
['Applet','Connection_ConnectionCreate_InUse','true'],
['Applet','Connection_ConnectionEdit_InUse','false'],
['Applet','Connection_ConnectionHistory_InUse','false'],
['Applet','Connection_InUse','TRUE'],
['Applet','ContextMenu_Connection_Edit_InUse','true'],
['Applet','Default chart','0'],
['Applet','Default graph','197'],
['Applet','File_Exit_InUse','true'],
['Applet','File_ExportData_InUse','true'],
['Applet','File_InUse','TRUE'],
['Applet','File_PrintGraph_InUse','true'],
['Applet','File_PrintMap_InUse','true'],
['Applet','GISChartScale','1'],
['Applet','GISEdgeScale','0'],
['Applet','GISNodeLabelFont','Arial'],
['Applet','GISNodeLabelHeight','9'],
['Applet','GISNodeScale','3'],
['Applet','graph_panel_split_location','500'],
['Applet','GraphEdgeLabelFont','Arial'],
['Applet','GraphEdgeLabelHeight','7'],
['Applet','GraphNodeLabelFont','Arial'],
['Applet','GraphNodeLabelHeight','9'],
['Applet','GraphNodeScale','1'],
['Applet','Help_About_InUse','true'],
['Applet','Help_InUse','true'],
['Applet','HideGisPanel','FALSE'],
['Applet','HideTopList','FALSE'],
['Applet','Language','1'],
['Applet','list_panel_split_location','900'],
['Applet','Maps_InUse','TRUE'],
['Applet','MaxPathLength','5'],
['Applet','Metaphors_InUse','FALSE'],
['Applet','MetaphorZoom','7501,0.2,2,0.2,0,1#10001,0.2,2,0.30,0,1#25001,0.2,2,0.6,0,1#50001,0.2,2,1,0,1#75001,0.2,2,1.2,0,1#100001,0.2,2,1.6,0,1#200001,0.2,2,2.4,0,1#300001,0.2,2,3.6,0,1#400001,0.2,2,6,0,1#500001,0.2,2,6,0,0.66#625001,0.2,2,7.2,0,1#750001,0.2,2,7.2,0,1#825001,0.2,2,7.6,0,1#1000001,0.2,2,8,0,1#1250001,0.2,2,8,0,1#1500001,0.2,2,8,0,0.66#2000001,0.2,2,8,0,0.66#3000001,0.2,2,8,0,0.66#4000001,0.2,2,8,0,0.66#5000001,0.2,2,8,0,0.66'],
['Applet','MinSearchKeywordLength','3'],
['Applet','Node_InUse','TRUE'],
['Applet','Node_NodeCreate_InUse','true'],
['Applet','Node_NodeDelete_InUse','true'],
['Applet','Node_NodeEdit_InUse','true'],
['Applet','Node_NodeHistory_InUse','false'],
['Applet','NoOrphans_True','TRUE'],
['Applet','NoOrphans_Visible','TRUE'],
['Applet','Schema_InUse','FALSE'],
['Applet','Scheme','1'],
['Applet','SearchResultLimit','500'],
['Applet','ShowDirectedGraph_InUse','TRUE'],
['Applet','ShowEdgeLabel_InUse','TRUE'],
['Applet','ShowEdgeThickness_InUse','TRUE'],
['Applet','ShowNodeExpandCounter','TRUE'],
['Applet','ShowRootNodeMenu','FALSE'],
['Applet','Strength_InUse','TRUE'],
['Applet','T],ltipHTMLWrapLen','1000'],
['Applet','top_list_name','account'],
['Applet','TopListTooltip','FALSE'],
['Applet','UnusedMenuItems','Hide'],
['FontColor','GRAPH_CONTROL_COUNTER_FONT','Dialog,0,12'],
['FontColor','GRAPH_CONTROL_ITEM_FONT','Dialog,0,12'],
['FontColor','GRAPH_CONTROL_LABEL_FONT','Dialog,1,12'],
['FontColor','NODE_SELECTED_COLOR','192,192,192'],
['FontColor','PREFILTER_BACKGROUND','197,255,197'],
['FontColor','SEARCH_FIELD_FONT','Dialog,0,12'],
['FontColor','SEARCH_RESULT_HEADER_LABEL_FONT','Dialog,1,12'],
['GIS','ArrowScale','5'],
['GIS','BufferSize','40'],
['GIS','Compression','7'],
['GIS','FastGisReponse','TRUE'],
['GIS','Predefined zooms','50000,75000,150000,300000,500000,1000000,2000000,3000000,5000000,10000000,25000000,50000000,75000000,110000000'],
['GIS','UseGISSearch','TRUE'],
['graph','directed_edge','y'],
['graph','RelaxType','2'],
['graph','show_labels','n'],
['Icons','11','fGermany.png'],
['Icons','14','fEU.png'],
['Icons','18','fNetherlands.png'],
['Icons','22','fNetherlands.png'],
['Icons','25','fEU.png'],
['Icons','29','fSwitzerland.png'],
['Icons','32','ManAtWork.png'],
['Icons','35','sss'],
['Icons','BuildQuery','puzz24.png'],
['Icons','ClearGraph','ClearGraph24.png'],
['Icons','ClearHighlights','ClearHighlights24.png'],
['Icons','ContractGraph','list-remove.png'],
['Icons','CreateEdge','CreateEdge.png'],
['Icons','CreateNode','CreateNode.png'],
['Icons','DocumentNew','document-new.png'],
['Icons','DocumentOpen','document-open.png'],
['Icons','DocumentSaveAs','document-save-as.png'],
['Icons','ExpandGraph','list-add.png'],
['Icons','FindPath','FindPath24.png'],
['Icons','Isolate','Isolate24a.png'],
['Icons','Left','left.png'],
['Icons','ListRemove','list-remove.png'],
['Icons','Logo','ni3_32.png'],
['Icons','PowerOff','PowerOff.png'],
['Icons','ReloadGraph','ReloadGraph24.png'],
['Icons','Remove','remove.png'],
['Icons','Right','right.png'],
['Icons','SaveAs','save24.png'],
['Icons','Search','Search24.png'],
['Icons','SimpleSearch','bullseye24.png'],
['Icons','SystemSearch','system-search.png'],
['Icons','ZoomIn','SearchPlus24.png'],
['Icons','ZoomOut','SearchMinus24.png']
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
	--delete from sys_settings_application;
	for i in array_lower(_rows,1) .. array_upper(_rows,1) loop
    	--raise info 'Check property % in sys_settings_application...', _rows[i][2];
      	select count(*) into _cc from sys_settings_application where prop = _rows[i][2];
      	if(_cc <> 0) then
      		delete from sys_settings_application where prop = _rows[i][2];
      	end if;
      	raise info 'Not exists % - create', _rows[i][2];
      	insert into sys_settings_application (section, prop, value) 
      		values (_rows[i][1], _rows[i][2], _rows[i][3]);
	end loop;
	
------------------------------------------------------------
	raise info 'Database update script is completed';
--	-- update database version to _newVersion
	update sys_iam set version=_newVersion where name = 'PostgreSQL';
	raise info 'Database version updated: % -> %', _version, _newVersion;
END;
$$ LANGUAGE plpgsql;


-- launch function
select alterDatabase();
-- drop function
drop function alterDatabase();
