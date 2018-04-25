-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.27.15';
	_newVersion varchar = '1.28.0';
	_version varchar;
	_cc integer;
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
alter table cis_favorites add column dbversion character varying(50);
select count(*) into _cc from sys_user_language where prop = 'Warning';
if(_cc = 0) then
	INSERT INTO sys_user_language(languageid, prop, value) VALUES (1, 'Warning', 'Warning');
end if;
select count(*) into _cc from sys_user_language where prop = 'MsgFavoriteOutOfDate';
if(_cc = 0) then
	INSERT INTO sys_user_language(languageid, prop, value) VALUES (1, 'MsgFavoriteOutOfDate', 'This favorite was created with a previous version of the application and might not be valid.');
end if;
select count(*) into _cc from sys_user_language where prop = 'MsgFavoritePleaseContactSysAdmin';
if(_cc = 0) then
	INSERT INTO sys_user_language(languageid, prop, value) VALUES (1, 'MsgFavoritePleaseContactSysAdmin', 'Please contact your system administrator.');
end if;
select count(*) into _cc from sys_user_language where prop = 'MsgCouldNotLoadFavorite';
if(_cc = 0) then
	INSERT INTO sys_user_language(languageid, prop, value) VALUES (1, 'MsgCouldNotLoadFavorite', 'Could not load favorite.');
end if;


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
