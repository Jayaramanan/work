-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.4.28';
	_newVersion varchar = '1.4.29';
	_version varchar;
	_names varchar[][];
	_name varchar;
	_cc integer;
	i integer;
	sql text;
	
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
	_names = array [['x', 'numeric(15,5)'], 
			['y', 'numeric(15,5)'],
			['nodetype', 'integer']];
	for i in array_lower(_names,1) .. array_upper(_names,1) loop
		raise info '% %', _names[i][1], _names[i][2];
		SELECT
		    count(a.attname) into _cc
		FROM
		    pg_catalog.pg_attribute a
		WHERE
		    a.attnum > 0
		    AND NOT a.attisdropped
		    AND a.attrelid = (
			SELECT c.oid
			FROM pg_catalog.pg_class c
			    LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
			WHERE c.relname ~ '^(cis_nodes)$'
			    AND pg_catalog.pg_table_is_visible(c.oid)
		    )
		    and a.attname = _names[i][1];

		raise info '%', _cc;
		if(_cc = 0) then
			raise info '% column is missing in cis_nodes', _names[i][1];
			sql = 'alter table cis_nodes add column ' || _names[i][1] || ' ' || _names[i][2];			
			raise info '%', sql;
			execute sql;
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