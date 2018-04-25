-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.4.38';
	_newVersion varchar = '1.4.39';
	_version varchar;
	_cc integer;
	_settings_to_update varchar[][] = array[
	['Applet', 'ApplyFilter_Visible', 'TRUE'],
	['Applet', 'NoFocus_Visible', 'TRUE'],
	['Applet', 'NoUnrelated_Visible', 'TRUE'],
	['Applet', 'ResetFilter_Visible', 'TRUE']
	];
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

	for i in array_lower(_settings_to_update,1) .. array_upper(_settings_to_update,1) loop
		update sys_settings_application set value=_settings_to_update[i][3] 
			where prop ilike _settings_to_update[i][2] and section ilike _settings_to_update[i][1];
	end loop;
	
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