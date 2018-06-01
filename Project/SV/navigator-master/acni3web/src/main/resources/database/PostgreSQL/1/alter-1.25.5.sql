-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.25.4';
	_newVersion varchar = '1.25.5';
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
alter table sys_group_prefilter rename to sys_group_prefilter_bak;
alter table sys_group_prefilter_bak drop constraint sys_group_prefilter_groupid_fkey;
alter table sys_group_prefilter_bak drop constraint sys_group_prefilter_predefid_fkey;
CREATE TABLE sys_group_prefilter
(
  id serial NOT NULL,
  groupid integer NOT NULL,
  predefid integer NOT NULL,
  CONSTRAINT sys_group_prefilter_pkey PRIMARY KEY (id),
  CONSTRAINT sys_group_prefilter_groupid_fkey FOREIGN KEY (groupid)
      REFERENCES sys_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_group_prefilter_predefid_fkey FOREIGN KEY (predefid)
      REFERENCES cht_predefinedattributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

insert into sys_group_prefilter (groupid, predefid) select groupid, predefid from sys_group_prefilter_bak;
drop table sys_group_prefilter_bak;
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
