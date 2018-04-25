-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.46.1';
	_newVersion varchar = '1.47.0';
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
DROP TABLE cis_chart;
ALTER TABLE cis_chart_attributes DROP CONSTRAINT cis_chart_attributes_chartid_fkey;
ALTER TABLE cis_chart_attributes RENAME TO cis_chart_attributes_tmp;

ALTER TABLE cht_chart RENAME TO sys_chart;

ALTER TABLE sys_object_chart DROP COLUMN displayattribute;
ALTER TABLE sys_object_chart DROP COLUMN rgb;

delete from sys_chart_group where chartid = -2;
delete from sys_chart where id = -2;

CREATE TABLE sys_chart_attribute
(
  id serial NOT NULL,
  rgb character varying(7),
  objectchartid integer,
  attributeid integer,
  CONSTRAINT pk_sys_chart_attribute PRIMARY KEY (id),
  CONSTRAINT fk_sys_chart_attribute_sys_object_attributes FOREIGN KEY (attributeid)
      REFERENCES sys_object_attributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_sys_chart_attribute_sys_object_chart FOREIGN KEY (objectchartid)
      REFERENCES sys_object_chart (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);

raise warning 'cis_charts_attributes table is renamed to sys_chart_attribute.';
raise warning 'Please add records to the table sys_chart_attribute manually, if needed. Old chart attributes are stored in cis_chart_attributes_tmp';
raise warning 'cis_chart_attributes_tmp table could be removed afterwards.';

------------------------------------------------------------
-- update dbversion in cis_favorites (comment this line if script impacts favorites)
update cis_favorites set dbversion = _newVersion where dbversion = _expectedVersion or dbversion = '1.46.0';
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