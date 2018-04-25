-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.2';
	_newVersion varchar = '1.1.3';
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

CREATE TABLE sys_object_chart_tmp
(
  objectid integer NOT NULL,
  chartid integer NOT NULL,
  "minvalue" integer,
  "maxvalue" integer,
  minscale numeric(12,4),
  maxscale numeric(12,4),
  labelinuse integer,
  labelfontsize character varying(25),
  numberformat character varying(25),
  displayoperation integer,
  displayattribute integer,
  charttype integer,
  isvaluedisplayed integer,
  rgb text
)
WITH (
  OIDS=FALSE
);


INSERT INTO sys_object_chart_tmp (objectid, chartid, minvalue, maxvalue, minscale, maxscale, labelinuse, labelfontsize, numberformat, displayoperation, displayattribute, charttype, isvaluedisplayed, rgb) select objectid, chartid, minvalue, maxvalue, minscale, maxscale, labelinuse, labelfontsize, numberformat, displayoperation, displayattribute, charttype, isvaluedisplayed, rgb from sys_object_chart;

drop table sys_object_chart;

CREATE TABLE sys_object_chart
(
  id serial NOT NULL,
  objectid integer,
  chartid integer NOT NULL,
  "minvalue" integer,
  "maxvalue" integer,
  minscale numeric(12,4),
  maxscale numeric(12,4),
  labelinuse integer,
  labelfontsize character varying(25),
  numberformat character varying(25),
  displayoperation integer,
  displayattribute integer,
  charttype integer,
  isvaluedisplayed integer,
  rgb text,
  CONSTRAINT pk_sys_object_chart PRIMARY KEY (id),
  CONSTRAINT fk_sys_object_chart_sys_object_definition FOREIGN KEY (objectid)
      REFERENCES sys_object_definition (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys_object_chart OWNER TO sa;

INSERT INTO sys_object_chart (objectid, chartid, minvalue, maxvalue, minscale, maxscale, labelinuse, labelfontsize, numberformat, displayoperation, displayattribute, charttype, isvaluedisplayed, rgb) select objectid, chartid, minvalue, maxvalue, minscale, maxscale, labelinuse, labelfontsize, numberformat, displayoperation, displayattribute, charttype, isvaluedisplayed, rgb from sys_object_chart_tmp;

DROP TABLE sys_object_chart_tmp;

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