-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.43.7';
	_newVersion varchar = '1.44.0';
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
ALTER TABLE gis_territory DROP COLUMN base;

drop table gis_file;
drop table gis_country;
drop table gis_geoanalytics;
drop table gis_territory_color;
drop table gis_layerparams;
drop table gis_layer;
drop table gis_thematiccoloringrange;
drop table gis_thematicdataset;
drop table gis_thematicmap;

drop table cis_territory;
drop table cis_territory_mapping;
drop table cis_territory_structure;

drop table if exists gis_zip;
drop table if exists gis_territory_rule;
drop table addresslonlat;
  
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