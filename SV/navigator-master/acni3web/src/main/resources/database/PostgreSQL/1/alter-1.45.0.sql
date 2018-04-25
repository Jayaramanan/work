-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.44.0';
	_newVersion varchar = '1.45.0';
	_version varchar;
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
drop table cht_attribute_type;
drop table cht_attribute_usage;
drop table cht_attribute_visibility;
drop table cht_color;
drop table cht_metaphorimage;
drop table cis_node_role;
drop table cht_role;
drop table cht_territory_type;
drop table sys_attribute_structure;
drop table sys_connection_user_group;
drop table sys_datachange;
drop table sys_log;
drop table sys_map_group;
drop table sys_object_attr_log;
drop table sys_object_log;

ALTER TABLE sys_object_connection DROP COLUMN linestartsymbolid;
ALTER TABLE sys_object_connection DROP COLUMN lineendsymbolid;
drop table cht_symbol;

ALTER TABLE sys_object_connection DROP COLUMN fromtoscore;
ALTER TABLE sys_object_connection DROP COLUMN tofromscore;
ALTER TABLE sys_object_connection DROP COLUMN precheckprocedure;


ALTER TABLE sys_object_attributes DROP COLUMN instructure;

ALTER TABLE cht_line_weight DROP COLUMN filepath;

ALTER TABLE cis_edges DROP COLUMN inscoring;

ALTER TABLE cis_nodes DROP COLUMN x;
ALTER TABLE cis_nodes DROP COLUMN y;

ALTER TABLE gis_map DROP COLUMN background;
ALTER TABLE gis_map DROP COLUMN edgecolor;
ALTER TABLE gis_map DROP COLUMN arrowcolor;

ALTER TABLE gis_overlay DROP COLUMN mapid;

drop view sys_object_user_group;

CREATE OR REPLACE VIEW sys_object_user_group AS 
         SELECT sg.schemaid AS objectid, sg.groupid, sg.canread, NULL::unknown AS cancreate, NULL::unknown AS canupdate, NULL::unknown AS candelete
           FROM sys_schema_group sg
UNION 
         SELECT og.objectid, og.groupid, og.canread, og.cancreate, og.canupdate, og.candelete
           FROM sys_object_group og;

ALTER TABLE sys_object_group DROP COLUMN allowed;
ALTER TABLE sys_object_group DROP COLUMN denied;

drop view sys_object_definition;

CREATE OR REPLACE VIEW sys_object_definition AS 
         SELECT sys_schema.id, sys_schema.name, 1 AS objecttypeid, NULL::unknown AS parentobjectid, NULL::unknown AS tablename, sys_schema.description, sys_schema.creation, sys_schema.createdby, 0 AS sort
           FROM sys_schema
UNION 
         SELECT sys_object.id, sys_object.name, sys_object.objecttypeid, sys_object.schemaid AS parentobjectid, sys_object.tablename, sys_object.description, sys_object.creation, sys_object.createdby, sys_object.sort
           FROM sys_object;
ALTER TABLE sys_object DROP COLUMN tabdisplay;
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