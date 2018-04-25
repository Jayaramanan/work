-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.25';
	_newVersion varchar = '1.1.26';
	_version varchar;
	_cc integer;
	i integer;
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
	update sys_settings_application set prop = 'AC_TAB_SWITCH_ACTION' where upper(prop) = upper('AC_TAB_SWITCH_ACTION');
	select count(*) into _cc from sys_settings_application where prop = 'AC_TAB_SWITCH_ACTION';
	if (_cc = 0) then
		INSERT INTO sys_settings_application(section, prop, value) VALUES ('Applet', 'AC_TAB_SWITCH_ACTION', null);
	end if;

------------------------------------------------------------
	raise info 'Database update script is completed';
--	-- update database version to _newVersion
	update sys_iam set version=_newVersion where name = 'PostgreSQL';
	raise info 'Database version updated: % -> %', _version, _newVersion;
END;
$$ LANGUAGE plpgsql;


-- launch function
select alterDatabase();
-- drop function
drop function alterDatabase();
