-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.21';
	_newVersion varchar = '1.1.22';
	_version varchar;
	_cc integer;
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
	select count(*) into _cc from sys_user_language where prop = 'SEARCH_TOOLTIP_TEXT';
	if(_cc = 0) then
		INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SEARCH_TOOLTIP_TEXT', 'Text Search');
	end if;
	
	select count(*) into _cc from sys_user_language where prop = 'SHOW_EDGE_LABELS_CHECKBOX_TOOLTIP_TEXT';
	if(_cc = 0) then
	INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SHOW_EDGE_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Edge labels');
	end if;
	
	select count(*) into _cc from sys_user_language where prop = 'SHOW_EDGE_THICKNESS_CHECKBOX_TOOLTIP_TEXT';
	if(_cc = 0) then
	INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SHOW_EDGE_THICKNESS_CHECKBOX_TOOLTIP_TEXT', 'Show edge thickness');
	end if;
	
	select count(*) into _cc from sys_user_language where prop = 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT';
	if(_cc = 0) then
	INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Node labels');
	end if;
	
	select count(*) into _cc from sys_user_language where prop = 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT';
	if(_cc = 0) then
	INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
	end if;
	
	select count(*) into _cc from sys_user_language where prop = 'ExpandGraph';
	if(_cc = 0) then
	INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ExpandGraph', 'Expand');
	end if;
	
	select count(*) into _cc from sys_user_language where prop = 'ContractGraph';
	if(_cc = 0) then
	INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ContractGraph', 'Contract');
	end if;
	
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
