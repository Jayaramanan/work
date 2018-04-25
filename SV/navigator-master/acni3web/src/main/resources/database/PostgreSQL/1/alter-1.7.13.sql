-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.7.12';
	_newVersion varchar = '1.7.13';
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
	create table sys_delta_user
	(
		id integer not null,
		objectid integer not null,
		attrid integer not null,
		timestamp timestamp not null,
		olddvalue varchar(255),
		newvalue varchar(255),
		action integer not null,
		status integer not null,
		userid integer not null,
		constraint pk_sys_delta_user primary key(id),
			foreign key (attrid) references sys_object_attributes (id),
			foreign key (objectid) references cis_objects(id),
			foreign key (userid) references sys_user (id)
	);
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