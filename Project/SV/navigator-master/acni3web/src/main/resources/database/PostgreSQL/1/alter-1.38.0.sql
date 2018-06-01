-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.37.1';
	_newVersion varchar = '1.38.0';
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
ALTER TABLE gis_thematicdataset drop column thematiccoloringid;
ALTER TABLE gis_thematicdataset DROP CONSTRAINT pk_gis_thematicdataset;
ALTER TABLE gis_thematicdataset ADD CONSTRAINT pk_gis_thematicdataset PRIMARY KEY(id);

ALTER TABLE gis_thematiccoloringrange drop column thematiccoloringid;
ALTER TABLE gis_thematiccoloringrange add column thematicdatasetid integer not null;
ALTER TABLE gis_thematiccoloringrange ADD CONSTRAINT fk_gis_thematiccoloringrange_gis_thematicdataset FOREIGN KEY (thematicdatasetid)
      REFERENCES gis_thematicdataset (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;
	
DROP TABLE gis_thematiccoloring;

CREATE SEQUENCE gis_thematicdataset_id_seq;
PERFORM setval('gis_thematicdataset_id_seq', COALESCE(max(ID)+1,1)) FROM gis_thematicdataset;
ALTER TABLE gis_thematicdataset ALTER COLUMN ID SET DEFAULT nextval('gis_thematicdataset_id_seq');

CREATE SEQUENCE gis_thematiccoloringrange_id_seq;
PERFORM setval('gis_thematiccoloringrange_id_seq', COALESCE(max(ID)+1,1)) FROM gis_thematiccoloringrange;
ALTER TABLE gis_thematiccoloringrange ALTER COLUMN ID SET DEFAULT nextval('gis_thematiccoloringrange_id_seq');

insert into sys_user_language( languageid, prop, value ) values (1, 'GA', 'GA');

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