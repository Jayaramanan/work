-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.3.20';
	_newVersion varchar = '1.3.21';
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
	-- database update script should be inserted here

CREATE SEQUENCE gis_map_id_seq;
ALTER TABLE gis_map_id_seq OWNER TO sa;
PERFORM setval('gis_map_id_seq', COALESCE(max(ID)+1,1)) FROM gis_map;
ALTER TABLE gis_map ALTER COLUMN ID SET DEFAULT nextval('gis_map_id_seq');

insert into sys_user_language(languageid, prop, value) values (1, 'Layer', 'Layer');
insert into sys_user_language(languageid, prop, value) values (1, 'LayerOrder', 'Layer order');
insert into sys_user_language(languageid, prop, value) values (1, 'Filled', 'Filled');
insert into sys_user_language(languageid, prop, value) values (1, 'MinZoom', 'Min zoom');
insert into sys_user_language(languageid, prop, value) values (1, 'MaxZoom', 'Max zoom');
insert into sys_user_language(languageid, prop, value) values (1, 'TextMinZoom', 'Text min zoom');
insert into sys_user_language(languageid, prop, value) values (1, 'TextMaxZoom', 'Text max zoom');
insert into sys_user_language(languageid, prop, value) values (1, 'PenWidth', 'Pen width');
insert into sys_user_language(languageid, prop, value) values (1, 'Color', 'Color');
insert into sys_user_language(languageid, prop, value) values (1, 'TextHeight', 'Text height');
insert into sys_user_language(languageid, prop, value) values (1, 'Font', 'Font');
insert into sys_user_language(languageid, prop, value) values (1, 'TextColor', 'Text color');
insert into sys_user_language(languageid, prop, value) values (1, 'Symbology', 'Symbology');
insert into sys_user_language(languageid, prop, value) values (1, 'Labels', 'Labels');
insert into sys_user_language(languageid, prop, value) values (1, 'DispClassMin', 'Disp class min');
insert into sys_user_language(languageid, prop, value) values (1, 'DispClassMax', 'Disp class max');
insert into sys_user_language(languageid, prop, value) values (1, 'BackgroundColor', 'Background color');
insert into sys_user_language(languageid, prop, value) values (1, 'EdgeColor', 'Edge color');
insert into sys_user_language(languageid, prop, value) values (1, 'ArrowColor', 'Arrow color');
insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfNewMap', 'Enter name of the new map');
insert into sys_user_language(languageid, prop, value) values (1, 'NewMap', 'New map');

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