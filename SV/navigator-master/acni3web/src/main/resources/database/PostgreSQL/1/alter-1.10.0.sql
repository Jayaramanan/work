-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.9.15';
	_newVersion varchar = '1.10.0';
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
drop table if exists sys_delta;
drop table if exists sys_delta_in;
drop table if exists sys_delta_user;

create table sys_delta
(
	id integer not null primary key,
	objecttypeid integer not null,
	objectid integer,
	service integer,
	parameters text,
	timestamp timestamp,
	action integer,
	status integer,
	creatorId integer,
	CONSTRAINT sys_delta_objectid_fkey FOREIGN KEY (objectid)
		REFERENCES cis_objects (id),
	CONSTRAINT sys_delta_creatorid_fkey FOREIGN KEY (creatorid)
		REFERENCES sys_user (id)
);

create table sys_delta_in
(
	id integer not null primary key,
	objecttypeid integer not null,
	objectid integer,
	service integer,
	parameters text,
	timestamp timestamp,
	action integer,
	status integer,
	creatorId integer,
	CONSTRAINT sys_delta_in_creatorid_fkey FOREIGN KEY (creatorid)
		REFERENCES sys_user (id)
);

create table sys_delta_user
(
	userid integer,
	lastsync timestamp,
	highestsyncid integer,
	CONSTRAINT sys_delta_user_user_fkey FOREIGN KEY (userid)
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