-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.7.25';
	_newVersion varchar = '1.7.26';
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
drop table sys_module_list cascade;
create table sys_module_list
(
	id serial not null primary key,
	name varchar(30) not null,
	path varchar(255) not null,
	hash varchar(40),
	version varchar(20),
	archive_pass varchar(100)
);

insert into sys_user_language values (1, 'Versions', 'Versions');
insert into sys_user_language values (1, 'Path', 'Path');
insert into sys_user_language values (1, 'Version', 'Version');
insert into sys_user_language values (1, 'Hash', 'Hash');
insert into sys_user_language values (1, 'ArchivePassword', 'Archive password');
insert into sys_user_language values (1, 'ModuleNameAlreadyExists', 'Module with name `{1}` and version `{2}` already exists');
insert into sys_user_language values (1, 'MsgFillAllMandatoryFields', 'Not all requered fields has values');
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