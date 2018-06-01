-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.7.6';
	_newVersion varchar = '1.7.7';
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
insert into sys_user_language values (1, 'UpdateRestriction', 'User update restriction');
insert into sys_user_language values (1, 'DeleteRestriction', 'User delete restriction');

insert into sys_user_language values (1, 'UpdateAccessNone', 'Cannot update objects');
insert into sys_user_language values (1, 'UpdateAccessUser', 'Can update own created objects');
insert into sys_user_language values (1, 'UpdateAccessGroup', 'Can update group created objects');
insert into sys_user_language values (1, 'UpdateAccessAll', 'Can update any object');

insert into sys_user_language values (1, 'DeleteAccessNone', 'Cannot delete objects');
insert into sys_user_language values (1, 'DeleteAccessUser', 'Can delete own created objects');
insert into sys_user_language values (1, 'DeleteAccessGroup', 'Can delete group created objects');
insert into sys_user_language values (1, 'DeleteAccessAll', 'Can delete any object');

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