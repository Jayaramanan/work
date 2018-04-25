-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.19.15';
	_newVersion varchar = '1.19.16';
	_version varchar;
	_id integer;
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
update sys_object_attributes set dbdatatypeid = 105 where predefined = 1 and (multivalue is null or multivalue = 0);
for _id in select id from sys_schema loop
	perform appendSchemaTables(_id, 0);
end loop;
ALTER TABLE cis_edges ALTER COLUMN Directed TYPE integer USING Directed::integer;
ALTER TABLE cis_edges ALTER COLUMN InPath TYPE integer USING InPath::integer;
ALTER TABLE cis_edges ALTER COLUMN ConnectionType TYPE integer USING ConnectionType::integer;

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
