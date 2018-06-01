-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.27.1';
	_newVersion varchar = '1.27.2';
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
insert into sys_user_language values (1, 'SyncImages', 'Synchronize images');
insert into sys_user_language values (1, 'ImageSynchronization', 'Image synchronization');
insert into sys_user_language values (1, 'ImgSyncStatusStart', 'Starting...');
insert into sys_user_language values (1, 'ImgSyncStatusGotMyList', 'Making list');
insert into sys_user_language values (1, 'ImgSyncStatusGotMasterList', 'Got master images');
insert into sys_user_language values (1, 'ImgSyncStatusDownloading', 'Downloading...');
insert into sys_user_language values (1, 'ImgSyncStatusDone', 'Done');
insert into sys_user_language values (1, 'ImgSyncStatusErrorCount', 'Errors');
insert into sys_user_language values (1, 'ImgSyncStatusError', 'Error');
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
