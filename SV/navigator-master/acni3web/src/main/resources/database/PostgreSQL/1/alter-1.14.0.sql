-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.13.1';
	_newVersion varchar = '1.14.0';
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
CREATE TABLE sys_delta_out
(
  id serial NOT NULL,
  objecttypeid integer NOT NULL,
  objectid integer,
  service integer,
  parameters text,
  "timestamp" timestamp without time zone,
  "action" integer,
  status integer,
  creatorid integer,
  targetuser integer,
  CONSTRAINT sys_delta_out_pkey PRIMARY KEY (id),
  CONSTRAINT sys_delta_out_creatorid_fkey FOREIGN KEY (creatorid)
      REFERENCES sys_user (id),
  CONSTRAINT sys_delta_out_targetuser_fkey FOREIGN KEY (targetuser)
      REFERENCES sys_user (id) 
);
------------------------------------------------------------------
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

