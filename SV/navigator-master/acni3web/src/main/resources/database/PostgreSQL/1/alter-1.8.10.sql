-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.8.9';
	_newVersion varchar = '1.8.10';
	_version varchar;
	_cc integer;
	_settings_to_insert varchar[][] = array[
	['Applet', 'NodeSpaceSliderMinValue', '0'],
	['Applet', 'NodeSpaceSliderMaxValue', '30'],
	['Applet', 'NodeSpaceSliderValue', '10']
	];
	_translations_to_insert varchar[][] = array[
	['Node space slider', 'Node space slider'],
	['Alpha slider', 'Alpha slider'],
	['Zoom slider', 'Zoom slider'],
	['Metaphor size', 'Metaphor size'],
	['Connection size', 'Connection size'],
	['Freeze graph layout', 'Freeze graph layout'],
	['Path filtered out title', 'Path filtered out title'],
	['Path filtered out', 'Path filtered out'],
	['Path filtered out2', 'Path filtered out2']
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

	for i in array_lower(_settings_to_insert,1) .. array_upper(_settings_to_insert,1) loop
		select count(*) into _cc from sys_settings_application where prop = _settings_to_insert[i][2];
		if (_cc=0) then
			insert into sys_settings_application (section, prop, value) 
				values ( _settings_to_insert[i][1], _settings_to_insert[i][2], _settings_to_insert[i][3]);
		end if;
	end loop;
	
	for i in array_lower(_translations_to_insert,1) .. array_upper(_translations_to_insert,1) loop
		select count(*) into _cc from sys_user_language  where prop = _translations_to_insert[i][1];
		if (_cc=0) then
			insert into sys_user_language (languageid, prop, value) 
				values (1, _translations_to_insert[i][1], _translations_to_insert[i][2]);
		end if;
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