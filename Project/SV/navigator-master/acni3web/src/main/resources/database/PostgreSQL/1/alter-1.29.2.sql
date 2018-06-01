-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.29.1';
	_newVersion varchar = '1.29.2';
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
insert into sys_user_language values (1, 'MsgAttributeReferencedFromFavorite', 'Cannot delete attribute `{1}`. It is referenced from favorites: {2}');
insert into sys_user_language values (1, 'MsgObjectReferencedFromFavorite', 'Cannot delete object `{1}`. It is referenced from favorites: {2}');
insert into sys_user_language values (1, 'MsgChartReferencedFromFavorite', 'Cannot delete chart `{1}`. It is referenced from favorites: {2}');
insert into sys_user_language values (1, 'ForceDelete', 'Force delete');
insert into sys_user_language values (1, 'ConfirmDeleteChart', 'Do you want to delete chart?');
insert into sys_user_language values (1, 'MsgObjectReferencedFromCharts', 'Cannot delete object `{1}`. It is referenced from charts: {2}');
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
