-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.8.5';
	_newVersion varchar = '1.8.6';
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
select count(*) into _cc from sys_settings_application where prop = 'GeoAnalytics_InUse';
if (_cc = 0) then
INSERT INTO sys_settings_application(section, prop, value)
    VALUES('Applet', 'GeoAnalytics_InUse', 'TRUE');
end if;

delete from sys_sequence where name = 'MapCount';

INSERT INTO sys_sequence(name, seqno)
VALUES('MapCount', 0);

drop sequence if exists seq_MapCount;

create sequence seq_MapCount;

perform setval('seq_MapCount', (select max(id) from (select id from gis_map union select id from gis_thematicmap)t)); 

drop table if exists gis_territory cascade;

CREATE TABLE gis_territory
(
  id integer NOT NULL,
  territory character varying(50),
  parent integer,
  label character varying(50),
  sort integer,
  base integer,
  CONSTRAINT gis_territory_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);


INSERT INTO gis_territory(id, territory, parent, label, sort)
  VALUES(1, 'Continent', NULL, 'Continent', 1);

INSERT INTO gis_territory(id, territory, parent, label, sort)
  VALUES(2, 'Country', 1, 'Country', 2);

INSERT INTO gis_territory(id, territory, parent, label, sort)
  VALUES(3, 'Region', 2, 'Region', 3);

INSERT INTO gis_territory(id, territory, parent, label, sort)
  VALUES(4, 'ZIP', 3, 'ZIP', 4);


drop table if exists gis_geoanalytics;

drop sequence if exists seq_gis_geoanalytics;

create sequence seq_gis_geoanalytics
	INCREMENT 1
	START 1;
	
CREATE TABLE gis_geoanalytics
(
  id integer NOT NULL DEFAULT nextval('seq_gis_geoanalytics'::regclass),
  description character varying(255),
  schemaid integer,
  groupid integer,
  userid integer,
  settings text,
  CONSTRAINT gis_geoanalytics_pkey PRIMARY KEY (id),
  CONSTRAINT fk_gis_geoanalytics_sys_schema FOREIGN KEY (schemaid)
      REFERENCES sys_schema (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_gis_geoanalytics_sys_group FOREIGN KEY (groupid)
      REFERENCES sys_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_gis_geoanalytics_sys_user FOREIGN KEY (userid)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

drop table if exists gis_territory_color;

CREATE TABLE gis_territory_color
(
  gis_territoryid character varying(50) NOT NULL,
  thematicdatasetid integer NOT NULL,
  color character varying(25),
  CONSTRAINT gis_territory_color_pkey PRIMARY KEY (gis_territoryid, thematicdatasetid),
  CONSTRAINT fk_gis_territory_color_gis_thematicdataset FOREIGN KEY (thematicdatasetid)
      REFERENCES gis_thematicdataset (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX pk_gis_territory_color
    ON gis_territory_color(gis_territoryid, thematicdatasetid);
    
CREATE INDEX idx_gis_territory_color
    ON gis_territory_color(thematicdatasetid);

drop table if exists cis_territory;

CREATE TABLE cis_territory
(
  objectid integer NOT NULL,
  territoryid integer NOT NULL,
  gisid character varying(100),
  CONSTRAINT cis_territory_pkey PRIMARY KEY (objectid, territoryid, gisid),
  CONSTRAINT fk_cis_territory_cis_nodes FOREIGN KEY (objectid)
      REFERENCES cis_nodes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cis_territory_gis_territory FOREIGN KEY (territoryid)
      REFERENCES gis_territory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX pk_cis_territory
    ON cis_territory(objectID, territoryID, gisid);

drop table if exists cis_territory_mapping;

CREATE TABLE cis_territory_mapping
(
  base_territoryid integer,
  base_gisid character varying(255),
  aggregation_territoryid integer,
  aggregation_gisid character varying(255),
  CONSTRAINT fk_cis_territory_mapping_gis_territory_base FOREIGN KEY (base_territoryid)
      REFERENCES gis_territory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cis_territory_mapping_gis_territory_aggr FOREIGN KEY (aggregation_territoryid)
      REFERENCES gis_territory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX idx_cis_territory_mapping
    ON cis_territory_mapping(base_gisid,aggregation_gisid);

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