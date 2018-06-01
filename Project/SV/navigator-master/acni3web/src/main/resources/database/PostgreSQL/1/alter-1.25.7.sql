-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.25.6';
	_newVersion varchar = '1.25.7';
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
alter table sys_chart_group rename to sys_chart_group_bak;
alter table sys_chart_group_bak drop constraint fk2_sys_group;
alter table sys_chart_group_bak drop constraint fk_sys_chart;
CREATE TABLE sys_chart_group
(
  id serial NOT NULL,
  groupid integer NOT NULL,
  chartid integer NOT NULL,
  CONSTRAINT sys_chart_group_pkey PRIMARY KEY (id),
  CONSTRAINT sys_chart_group_sys_group_fkey FOREIGN KEY (groupid)
      REFERENCES sys_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_chart_group_cht_chart_fkey FOREIGN KEY (chartid)
      REFERENCES cht_chart (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

insert into sys_chart_group (groupid, chartid) select groupid, chartid from sys_chart_group_bak;
drop table sys_chart_group_bak;
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
