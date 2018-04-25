-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.5.0';
	_newVersion varchar = '1.5.1';
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

CREATE TABLE gis_layerparams_tmp
(
  id integer NOT NULL DEFAULT nextval('seq_gis_layerparams'::regclass),
  mapid integer NOT NULL,
  layerid integer NOT NULL,
  layerorder integer NOT NULL,
  description text,
  filled character(1),
  minzoom numeric(12,4),
  maxzoom numeric(12,4),
  textminzoom numeric(12,4),
  textmaxzoom numeric(12,4),
  penwidth numeric(7,2),
  color character varying(15),
  font character varying(50),
  textcolor character varying(15),
  textheight integer,
  symbology character varying(2048),
  labels character varying(2048),
  dispclassmin integer,
  dispclassmax integer,
  CONSTRAINT pk_gis_layerparams_tmp_one PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_layerparams_tmp OWNER TO sa;

INSERT INTO gis_layerparams_tmp (id, mapid, layerid, layerorder, description, filled, minzoom,
		maxzoom, textminzoom, textmaxzoom, penwidth, color, font, textcolor, textheight,
		symbology, labels, dispclassmin, dispclassmax) 
		select id, mapid, layerid, layerorder, description, filled, minzoom,
		maxzoom, textminzoom, textmaxzoom, penwidth, color, font, textcolor, textheight,
		symbology, labels, dispclassmin, dispclassmax from gis_layerparams;
	
drop table gis_layerparams;
	
CREATE TABLE gis_layerparams
(
  id integer NOT NULL DEFAULT nextval('seq_gis_layerparams'::regclass),
  mapid integer NOT NULL,
  layerid integer NOT NULL,
  layerorder integer NOT NULL,
  description text,
  filled character(1),
  minzoom numeric(12,4),
  maxzoom numeric(12,4),
  textminzoom numeric(12,4),
  textmaxzoom numeric(12,4),
  penwidth numeric(7,2),
  color character varying(15),
  font character varying(50),
  textcolor character varying(15),
  textheight integer,
  symbology character varying(2048),
  labels character varying(2048),
  dispclassmin integer,
  dispclassmax integer,
  CONSTRAINT pk_gis_layerparams_one PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_layerparams OWNER TO sa;

INSERT INTO gis_layerparams (id, mapid, layerid, layerorder, description, filled, minzoom,
		maxzoom, textminzoom, textmaxzoom, penwidth, color, font, textcolor, textheight,
		symbology, labels, dispclassmin, dispclassmax) 
		select id, mapid, layerid, layerorder, description, filled, minzoom,
		maxzoom, textminzoom, textmaxzoom, penwidth, color, font, textcolor, textheight,
		symbology, labels, dispclassmin, dispclassmax from gis_layerparams_tmp;
		
drop table gis_layerparams_tmp;

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