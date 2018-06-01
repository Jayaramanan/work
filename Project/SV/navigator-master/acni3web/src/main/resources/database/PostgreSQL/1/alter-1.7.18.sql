-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.7.17';
	_newVersion varchar = '1.7.18';
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
ALTER TABLE cis_user_favorites
ADD COLUMN group_rules integer NULL
; 
CREATE TABLE cis_group_favorites (
    groupid integer NOT NULL,
    favoritesid integer NOT NULL,
    layout text NULL,
    PRIMARY KEY(groupid,favoritesid)
)
; 
ALTER TABLE cis_group_favorites
    ADD CONSTRAINT fk_cis_group_favorites_sys_group
FOREIGN KEY(groupid)
REFERENCES sys_group(id)
; 
ALTER TABLE cis_group_favorites
    ADD CONSTRAINT fk_cis_group_favorites_cis_favorites
FOREIGN KEY(favoritesid)
REFERENCES cis_favorites(id)
; 
ALTER TABLE cis_favorites
ADD COLUMN name varchar(255) NULL
;
ALTER TABLE cis_favorites ALTER COLUMN description TYPE text
;
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