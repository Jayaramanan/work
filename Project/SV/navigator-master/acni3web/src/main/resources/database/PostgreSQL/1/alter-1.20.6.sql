-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.20.5';
	_newVersion varchar = '1.20.6';
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
insert into sys_user_language values (1, 'Send', 'Send');
insert into sys_user_language values (1, 'MsgErrorSendStarterModuleToClient', 'Cannot send starter module to client: {1}');
insert into sys_user_language values (1, 'MsgThickClientMailBody','Dear {1} {2},\nYour Ni3 Navigator Offline Client is ready.\n To get the application please place the attached files to any directory on your hard drive and double-click on ni3.exe. The application will download itself and start automatically.');
insert into sys_user_language values (1, 'MsgThickClientMailSubject', 'Ni3 Navigator offline client');
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
