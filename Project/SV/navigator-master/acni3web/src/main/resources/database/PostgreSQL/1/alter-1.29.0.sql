-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.28.3';
	_newVersion varchar = '1.29.0';
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

CREATE TABLE sys_schema_group
(
  schemaid integer NOT NULL,
  groupid integer NOT NULL,
  canread integer,
  CONSTRAINT pk_sys_schema_group PRIMARY KEY (schemaid, groupid),
  CONSTRAINT fk_sys_schema_group_sys_group FOREIGN KEY (groupid)
      REFERENCES sys_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_schema_group_sys_schema FOREIGN KEY (schemaid)
      REFERENCES sys_schema (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- fill sys_schema_group with existing accesses (if constraint was dropped in sys_object_user_group table)
insert into sys_schema_group (schemaid, groupid, canread) select objectid, groupid, canread from sys_object_user_group where objectid in (select id from sys_schema);
-- remove accesses to schemas (if constraint was dropped in sys_object_user_group table
delete from sys_object_user_group where objectid in (select id from sys_schema);
-- fill sys_schema_group with missing combinations of groups and schemas
insert into sys_schema_group (schemaid, groupid, canread) (select s.id as schemaid, g.id as groupid, 1 as canread from sys_schema s, sys_group g where not exists (select * from sys_schema_group sg where sg.groupid = g.id and sg.schemaid = s.id));

alter table sys_object_user_group rename to sys_object_group;


CREATE OR REPLACE VIEW sys_object_user_group AS 
        SELECT sg.schemaid as objectid, sg.groupid, sg.canread, null as cancreate, null as canupdate, null as candelete, null as allowed, null as denied
           FROM sys_schema_group sg
UNION 
	SELECT og.objectid, og.groupid, og.canread, og.cancreate, og.canupdate, og.candelete, og.allowed, og.denied
           FROM sys_object_group og;



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
