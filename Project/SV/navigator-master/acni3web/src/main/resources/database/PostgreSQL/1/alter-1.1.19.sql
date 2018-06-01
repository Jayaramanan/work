-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.18';
	_newVersion varchar = '1.1.19';
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
	select count(*) into _cc from sys_user_language where prop = 'New_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'New_AS', 'New');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Open_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Open_AS', 'Open');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Save As_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Save As_AS', 'Save As');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Delete_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Delete_AS', 'Delete');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Search_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Search_AS', 'Search');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Object_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Object_AS', 'Object');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Attribute_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Attribute_AS', 'Attribute');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Operator_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Operator_AS', 'Operator');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Parameter_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Parameter_AS', 'Parameter');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Logical_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Logical_AS', 'Logical');
	end if;

	select count(*) into _cc from sys_user_language where prop = 'Group_AS';
	if(_cc = 0) then
		insert into sys_user_language(languageid, prop, value) values (1, 'Group_AS', 'Group');
	end if;
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
