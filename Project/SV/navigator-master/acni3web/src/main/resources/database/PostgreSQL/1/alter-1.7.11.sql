-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.7.10';
	_newVersion varchar = '1.7.11';
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
CREATE TABLE sys_map_job
(
  id serial NOT NULL,
  userid integer NOT NULL,
  triggeredby integer NOT NULL,
  jobtype integer NOT NULL,
  status integer NOT NULL,
  timestart timestamp without time zone,
  timeend timestamp without time zone,
  x1 numeric (18,8),
  x2 numeric (18,8),
  y1 numeric (18,8),
  y2 numeric (18,8),
  scale varchar (255),
  CONSTRAINT pk_sys_map_job PRIMARY KEY (id),
  CONSTRAINT sys_map_job_triggeredby_fkey FOREIGN KEY (triggeredby)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_map_job_userid_fkey FOREIGN KEY (userid)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

select count(*) into _cc from sys_user_language where prop = 'Scale';
if(_cc = 0) then
	insert into sys_user_language values (1, 'Scale', 'Scale');
end if;

select count(*) into _cc from sys_user_language where prop = 'X1';
if(_cc = 0) then
	insert into sys_user_language values (1, 'X1', 'X1');
end if;
select count(*) into _cc from sys_user_language where prop = 'Y1';
if(_cc = 0) then
	insert into sys_user_language values (1, 'Y1', 'Y1');
end if;
select count(*) into _cc from sys_user_language where prop = 'X2';
if(_cc = 0) then
	insert into sys_user_language values (1, 'X2', 'X2');
end if;
select count(*) into _cc from sys_user_language where prop = 'Y2';
if(_cc = 0) then
	insert into sys_user_language values (1, 'Y2', 'Y2');
end if;

insert into sys_user_language values (1, 'DataExtraction', 'Data extraction');
insert into sys_user_language values (1, 'MapExtraction', 'Map extraction');

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