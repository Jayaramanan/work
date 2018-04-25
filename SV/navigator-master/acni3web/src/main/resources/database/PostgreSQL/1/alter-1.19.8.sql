-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.19.7';
	_newVersion varchar = '1.19.8';
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

ALTER TABLE gis_geoanalytics DROP CONSTRAINT fk_gis_geoanalytics_sys_schema;
ALTER TABLE gis_geoanalytics DROP CONSTRAINT fk_gis_geoanalytics_sys_group;
ALTER TABLE gis_geoanalytics DROP CONSTRAINT fk_gis_geoanalytics_sys_user;

ALTER TABLE gis_geoanalytics ADD CONSTRAINT fk_gis_geoanalytics_sys_schema FOREIGN KEY (schemaid) REFERENCES sys_schema (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE gis_geoanalytics ADD CONSTRAINT fk_gis_geoanalytics_sys_group FOREIGN KEY (groupid) REFERENCES sys_group (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE gis_geoanalytics ADD CONSTRAINT fk_gis_geoanalytics_sys_user FOREIGN KEY (userid) REFERENCES sys_user (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE gis_territory_color DROP CONSTRAINT fk_gis_territory_color_gis_thematicdataset;

ALTER TABLE cis_territory DROP CONSTRAINT fk_cis_territory_gis_territory;
ALTER TABLE cis_territory DROP CONSTRAINT fk_cis_territory_cis_nodes;

ALTER TABLE cis_territory ADD CONSTRAINT fk_cis_territory_cis_nodes FOREIGN KEY (objectid) REFERENCES cis_nodes (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE cis_territory ADD CONSTRAINT fk_cis_territory_gis_territory FOREIGN KEY (territoryid) REFERENCES gis_territory (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE cis_territory_mapping DROP CONSTRAINT fk_cis_territory_mapping_gis_territory_base;
ALTER TABLE cis_territory_mapping DROP CONSTRAINT fk_cis_territory_mapping_gis_territory_aggr;

ALTER TABLE cis_territory_mapping ADD CONSTRAINT fk_cis_territory_mapping_gis_territory_base 
FOREIGN KEY (base_territoryid) REFERENCES gis_territory (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE cis_territory_mapping ADD CONSTRAINT fk_cis_territory_mapping_gis_territory_aggr 
FOREIGN KEY (aggregation_territoryid) REFERENCES gis_territory (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

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