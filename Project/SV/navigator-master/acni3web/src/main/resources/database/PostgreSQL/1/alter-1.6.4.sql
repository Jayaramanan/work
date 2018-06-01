-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.6.3';
	_newVersion varchar = '1.6.4';
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
	alter table sys_user
		add column hasOfflineClient integer;
	update sys_user set hasOfflineClient = 0;

	create table sys_datasource
	(
		id integer not null,
		datasource_name varchar(255),
		CONSTRAINT pk_sys_datasource PRIMARY KEY (id)
	);

	CREATE TABLE sys_user_datasource
	(
   		id integer NOT NULL, 
   		"userId" integer NOT NULL, 
   		"dataSourceId" integer NOT NULL, 
   		CONSTRAINT pk_sys_user_datasource PRIMARY KEY (id), 
    		FOREIGN KEY ("userId") REFERENCES sys_user (id) , 
    		FOREIGN KEY ("dataSourceId") REFERENCES sys_datasource (id)
	);

	create table sys_delta
	(
		id integer not null,
		objectid integer not null,
		attrid integer not null,
		timestamp timestamp not null,
		newvalue varchar(255),
		constraint pk_sys_delta primary key(id),
			foreign key (attrid) references sys_object_attributes (id),
			foreign key (objectid) references cis_objects(id)           --<----------------------------------------
	);

	create table sys_offline_job
	(
		id integer not null,
		userid integer not null,
		jobtype integer not null,
		status integer not null,
		perfomed_on timestamp not null,                    --<----------------------------------------
		constraint pk_sys_offline_job primary key (id),
			foreign key (userid) references sys_user(id)
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