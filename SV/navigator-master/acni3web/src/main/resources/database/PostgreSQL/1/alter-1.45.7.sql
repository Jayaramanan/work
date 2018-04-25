-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.45.6';
	_newVersion varchar = '1.45.7';
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
DROP TABLE cht_line_style cascade;

ALTER TABLE cht_predefinedattributes DROP COLUMN languageid;
ALTER TABLE cht_predefinedattributes DROP COLUMN inuse;

select count(*) into _cc from sys_user_language where prop = 'Transparent';
if(_cc = 0) then
	insert into sys_user_language(languageid, prop, value) values (1, 'Transparent', 'Transparent');
end if;
select count(*) into _cc from sys_user_language where prop = 'Dotted';
if(_cc = 0) then
	insert into sys_user_language(languageid, prop, value) values (1, 'Dotted', 'Dotted');
end if;
select count(*) into _cc from sys_user_language where prop = 'Dashed';
if(_cc = 0) then
	insert into sys_user_language(languageid, prop, value) values (1, 'Dashed', 'Dashed');
end if;
select count(*) into _cc from sys_user_language where prop = 'DashDot';
if(_cc = 0) then
	insert into sys_user_language(languageid, prop, value) values (1, 'DashDot', 'Dash-Dot');
end if;
select count(*) into _cc from sys_user_language where prop = 'Full';
if(_cc = 0) then
	insert into sys_user_language(languageid, prop, value) values (1, 'Full', 'Full');
end if;

------------------------------------------------------------
-- update dbversion in cis_favorites (comment this line if script impacts favorites)
update cis_favorites set dbversion = _newVersion where dbversion = _expectedVersion;
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