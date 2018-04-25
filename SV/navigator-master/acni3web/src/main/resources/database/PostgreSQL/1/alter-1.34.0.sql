-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.33.9';
	_newVersion varchar = '1.34.0';
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
	select count(*) into _cc from sys_delta where status = 1;
	if (_cc > 0) then
		raise exception 'sys_delta table contains unprocessed deltas, cannot proceed script';
	end if;
	
	select count(*) into _cc from sys_delta_out where status = 1;
	if (_cc > 0) then
		raise exception 'sys_delta_out table contains unprocessed deltas, cannot proceed update script';
	end if;
	
	drop table if exists sys_delta;
	drop table if exists sys_delta_out;
	
	create table sys_delta_header
	(
		id bigserial primary key,
		deltatype integer not null,
		timestamp timestamp with time zone default now(),
		status integer not null,
		creatorid integer not null,
		issync integer not null default 0,
		CONSTRAINT sys_delta_creatorid_fkey FOREIGN KEY (creatorid) REFERENCES sys_user (id)		
	);  
	
	create table sys_delta_params
	(
		id bigserial primary key,
		deltaid bigint not null,
		name text not null,
		value text,
		CONSTRAINT sys_delta_deltaid_fkey FOREIGN KEY (deltaid) REFERENCES sys_delta_header (id)
	);
	
	DROP TABLE if exists  sys_delta_user;

	CREATE TABLE sys_delta_user (
	 id    bigserial      PRIMARY KEY,
	 created   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
	 delta_header_id BIGINT      NOT NULL REFERENCES sys_delta_header(id),
	 target_user_id INTEGER      NOT NULL REFERENCES sys_user(id),
	 processed  integer      DEFAULT 1
	);
	
------------------------------------------------------------
-- update dbversion in cis_favorites (comment this line if script impacts favorites)
update cis_favorites set dbversion = _newVersion where dbversion = _expectedVersion;
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
