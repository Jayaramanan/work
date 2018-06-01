CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE
	_expectedVersion varchar = '1.48.13';
	_newVersion varchar = '1.48.14';
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
		update sys_iam set version=_newVersion where name = 'PostgreSQL';
	raise info 'Database version updated: % -> %', _version, _newVersion;
END;
$$ LANGUAGE plpgsql;

-- launch function
select alterDatabase();
-- drop function
drop function alterDatabase();


CREATE OR REPLACE FUNCTION deleteUserSchema(schema_name text)
  RETURNS void AS
$$
DECLARE
	_row record;
	_schema_id int;
BEGIN
	execute 'select id from sys_schema where name = ''' || schema_name || '''' into _schema_id;
	raise info 'schema id %', _schema_id;

	for _row in select tablename as tablename from sys_object where schemaid = _schema_id
	loop
		raise info 'found user table %', _row.tablename;
		execute deleteUserObject(_row.tablename);
	end loop;

END
$$
  LANGUAGE plpgsql VOLATILE
  COST 100;
