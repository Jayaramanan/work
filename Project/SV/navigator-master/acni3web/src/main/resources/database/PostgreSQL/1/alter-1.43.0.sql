-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.42.2';
	_newVersion varchar = '1.43.0';
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
CREATE TABLE geo_thematicfolder
(
  id serial NOT NULL,
  "name" text,
  schemaid integer,
  CONSTRAINT geo_thematicfolder_pkey PRIMARY KEY (id),
  CONSTRAINT fk_geo_thematicfolder_sys_schema FOREIGN KEY (schemaid)
      REFERENCES sys_schema (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);

CREATE TABLE geo_thematicmap
(
  id serial NOT NULL,
  folderid integer,
  "name" text NOT NULL,
  groupid integer NOT NULL,
  layerid integer NOT NULL,
  attribute text,
  CONSTRAINT geo_thematicmap_pkey PRIMARY KEY (id),
  CONSTRAINT fk_geo_thematicmap_geo_thematicfolder FOREIGN KEY (folderid)
      REFERENCES geo_thematicfolder (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL,
  CONSTRAINT fk_geo_thematicmap_gis_territory FOREIGN KEY (layerid)
      REFERENCES gis_territory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_geo_thematicmap_sys_group FOREIGN KEY (groupid)
      REFERENCES sys_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);

CREATE TABLE geo_thematiccluster
(
  id serial NOT NULL,
  thematicmapid integer NOT NULL,
  fromvalue numeric,
  tovalue numeric,
  color character varying(7),
  gids text,
  CONSTRAINT geo_thematiccluster_pkey PRIMARY KEY (id),
  CONSTRAINT fk_geo_thematiccluster_geo_thematicmap FOREIGN KEY (thematicmapid)
      REFERENCES geo_thematicmap (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);

INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'OverwriteThematicMap?', 'Do you want to overwrite existing thematic map?');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'EnterThematicMapName', 'Please enter the name of the thematic map');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SaveThematicMap', 'Save thematic map');

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