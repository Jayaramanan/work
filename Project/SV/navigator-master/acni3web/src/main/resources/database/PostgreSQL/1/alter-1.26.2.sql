

-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.26.1';
	_newVersion varchar = '1.26.2';
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
insert into sys_user_language (languageid, prop, value) values (1, 'DateTime', 'Timestamp');
insert into sys_user_language (languageid, prop, value) values (1, 'DateFrom', 'From');
insert into sys_user_language (languageid, prop, value) values (1, 'DateTo', 'To');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivitySIDLogin', 'Login with SID');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityPasswordLogin', 'Login with username/password');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityLogout', 'Logout');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityCreateNode', 'Create node');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityCreateEdge', 'Create edge');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityUpdateNode', 'Update node');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityUpdateEdge', 'Update edge');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityDeleteNode', 'Delete node');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityDeleteEdge', 'Delete edge');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityDeleteFavorite', 'Delete favorite');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityInvokeFavorite', 'Invoke favorite');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivitySimpleSearch', 'Simple search');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityAdvancedSearch', 'Advanced search');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityGeoSearch', 'Geo search');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityInvokeChart', 'Invoke chart');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityExportData', 'Export data');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivityCreateFavorite', 'Create favorite');
insert into sys_user_language (languageid, prop, value) values (1, 'ActivitySaveQuery', 'Save query');
insert into sys_user_language (languageid, prop, value) values (1, 'Monitoring', 'Monitoring');
insert into sys_user_language (languageid, prop, value) values (1, 'SearchActivities', 'Search activities');

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
