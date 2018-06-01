-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.33';
	_newVersion varchar = '1.1.34';
	_version varchar;
	_cc integer;
	maxID integer;
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

	
PERFORM setval('cis_chart_attributes_id_seq', COALESCE(max(ID)+1,1)) FROM cis_chart_attributes;
ALTER TABLE cis_chart_attributes ALTER COLUMN ID SET DEFAULT nextval('cis_chart_attributes_id_seq');

PERFORM setval('sys_object_chart_id_seq', COALESCE(max(ID)+1,1)) FROM sys_object_chart;
ALTER TABLE sys_object_chart ALTER COLUMN ID SET DEFAULT nextval('sys_object_chart_id_seq');

PERFORM setval('cht_predefinedattributes_id_seq', COALESCE(max(ID)+1,1)) FROM cht_predefinedattributes;
ALTER TABLE cht_predefinedattributes ALTER COLUMN ID SET DEFAULT nextval('cht_predefinedattributes_id_seq');

PERFORM setval('sys_object_attributes_id_seq', COALESCE(max(ID)+1,1)) FROM sys_object_attributes;
ALTER TABLE sys_object_attributes ALTER COLUMN ID SET DEFAULT nextval('sys_object_attributes_id_seq');

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