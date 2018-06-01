-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.15.1';
	_newVersion varchar = '1.15.2';
	_version varchar;
	_cc integer;
	_rows varchar[][] = array[
['Applet','UseAdvancedSearch','FALSE'],
['Applet','Connection_InUse','FALSE'],
['Applet','UseSimpleSearch','FALSE']
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
	delete from sys_settings_application where prop ilike 'T],ltipHTMLWraplen';
	delete from sys_settings_application where prop ilike 'UseSimpleSearch';
	
	for i in array_lower(_rows,1) .. array_upper(_rows,1) loop
    	--raise info 'Check property % in sys_settings_application...', _rows[i][2];
      	select count(*) into _cc from sys_settings_application where prop = _rows[i][2];
      	if(_cc <> 0) then
      		delete from sys_settings_application where prop = _rows[i][2];
      	end if;
      	insert into sys_settings_application (section, prop, value) 
      		values (_rows[i][1], _rows[i][2], _rows[i][3]);
	end loop;

	update sys_settings_application set value = 'FALSE' where prop ilike 'GeoAnalytics_InUse';
	update sys_settings_application set value = 'FALSE' where prop ilike 'Node_InUse';
	update sys_settings_application set value = '50' where prop ilike 'TooltipHTMLWraplen';
	
------------------------------------------------------------------
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

