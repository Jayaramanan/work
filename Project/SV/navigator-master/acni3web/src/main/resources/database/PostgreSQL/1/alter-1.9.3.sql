-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.9.2';
	_newVersion varchar = '1.9.3';
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
select count(*) into _cc from sys_user_language where lower(prop) = 'noorphans';
if _cc > 0 then 
	update sys_user_language set value = 'No Orphans' where lower(prop) = 'noorphans';
else
	insert into sys_user_language values (1, 'NoOrphans', 'No Orphans');
end if;

select count(*) into _cc from sys_user_language where lower(prop) = 'nounrelated';
if _cc > 0 then 
	update sys_user_language set value = 'No Unrelated' where lower(prop) = 'nounrelated';
else
	insert into sys_user_language values (1, 'NoUnrelated', 'No Unrelated');
end if;

select count(*) into _cc from sys_user_language where lower(prop) = 'nofocus';
if _cc > 0 then 
	update sys_user_language set value = 'Keep Focus' where lower(prop) = 'nofocus';
else
	insert into sys_user_language values (1, 'NoFocus', 'Keep Focus');
end if;

select count(*) into _cc from sys_user_language where lower(prop) = 'nosingles';
if _cc > 0 then 
	update sys_user_language set value = 'No Outliers' where lower(prop) = 'nosingles';
else
	insert into sys_user_language values (1, 'NoSingles', 'No Outliers');
end if;

select count(*) into _cc from sys_user_language where lower(prop) = 'filterfrom';
if _cc > 0 then 
	update sys_user_language set value = 'Filter From' where lower(prop) = 'filterfrom';
else
	insert into sys_user_language values (1, 'FilterFrom', 'Filter From');
end if;

select count(*) into _cc from sys_user_language where lower(prop) = 'filterto';
if _cc > 0 then 
	update sys_user_language set value = 'Filter To' where lower(prop) = 'filterto';
else
	insert into sys_user_language values (1, 'FilterTo', 'Filter To');
end if;
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