-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.29.5';
	_newVersion varchar = '1.29.6';
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
insert into sys_user_language values (1, 'ActivitySaveTopic', 'Save topic');
insert into sys_user_language values (1, 'ActivityUpdateFavorite', 'Update favorite');
insert into sys_user_language values (1, 'ActivityUpdateFavoriteName', 'Update favorite`s name');
insert into sys_user_language values (1, 'ActivityCopyFavorite', 'Copy favorite');
insert into sys_user_language values (1, 'ActivityDeleteFavoritesByFolder', 'Delete favorites by folder');
insert into sys_user_language values (1, 'ActivityUpdateFavoriteFolder', 'Update favorite`s folder');
insert into sys_user_language values (1, 'ActivityMoveToGroup', 'Move favorite to group favorites');
insert into sys_user_language values (1, 'ActivityCreateFolder', 'Create folder');
insert into sys_user_language values (1, 'ActivityUpdateFolder', 'Update folder');
insert into sys_user_language values (1, 'ActivityDeleteFolder', 'Delete folder');

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
