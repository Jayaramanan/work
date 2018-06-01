-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.3.24';
	_newVersion varchar = '1.4.0';
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
	-- database update script should be inserted here
		CREATE SEQUENCE seq_user_favorites_folder;
		
		CREATE TABLE cis_user_favorites_folder ( 
		    id         	int4 NOT NULL DEFAULT nextval('seq_user_favorites_folder'),
		    FolderName	varchar(255) NULL
		);
		alter table cis_user_favorites_folder add primary key (id);
		
		ALTER TABLE cis_user_favorites
			ADD COLUMN folderID int4 NULL;
		
		ALTER TABLE cis_user_favorites
			ADD CONSTRAINT fk_cis_user_favorites_folder
			FOREIGN KEY(folderid)
			REFERENCES cis_user_favorites_folder(id);
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