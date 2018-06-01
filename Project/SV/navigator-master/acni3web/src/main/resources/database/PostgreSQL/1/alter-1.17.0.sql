-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.16.8';
	_newVersion varchar = '1.17.0';
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

CREATE TABLE sys_licenses_tmp
(
  product character varying(50) NOT NULL,
  license text
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys_licenses_tmp OWNER TO sa;

insert into sys_licenses_tmp (product, license) select product, license from sys_licenses;

DROP TABLE sys_licenses;

CREATE TABLE sys_licenses
(
  id serial NOT NULL,
  product character varying(50) NOT NULL,
  license text,
  CONSTRAINT pk_sys_licenses PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys_licenses OWNER TO sa;

insert into sys_licenses (product, license) select product, license from sys_licenses_tmp;

DROP TABLE sys_licenses_tmp;

insert into sys_user_language values (1, 'MsgEnterProductName', 'Enter product name');
insert into sys_user_language values (1, 'NewProduct', 'New product');
insert into sys_user_language values (1, 'MsgDuplicateLicense', 'Duplicate license contents: `{1}`');

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
