-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.6.14';
	_newVersion varchar = '1.6.15';
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
CREATE TABLE sys_chart_job
(
  id serial NOT NULL,
  chartid integer NOT NULL,
  triggeredby integer NOT NULL,
  status integer NOT NULL,
  timestart timestamp without time zone,
  timeend timestamp without time zone,
  CONSTRAINT pk_sys_chart_job PRIMARY KEY (id),
  CONSTRAINT sys_chart_job_chartid_fkey FOREIGN KEY (chartid)
      REFERENCES cht_chart (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_chart_job_triggeredby_fkey FOREIGN KEY (triggeredby)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

select count(*) into _cc from sys_user_language where prop = 'Chart';
if(_cc = 0) then
	insert into sys_user_language values (1, 'Chart', 'Chart');
end if;

select count(*) into _cc from sys_user_language where prop = 'Charts';
if(_cc = 0) then
	insert into sys_user_language values (1, 'Charts', 'Charts');
end if;

insert into sys_user_language values (1, 'ScheduledTasks', 'Scheduled tasks');

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