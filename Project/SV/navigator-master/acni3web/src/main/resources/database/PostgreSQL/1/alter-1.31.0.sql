-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.30.6';
	_newVersion varchar = '1.31.0';
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
INSERT INTO SYS_SETTINGS_APPLICATION (SECTION, PROP, VALUE) VALUES ('Applet','ConfigLockedObjectPrivileges','true');

update sys_attribute_group set editingunlock = case 
when canRead = 1 and canUpdate = 1 and managing >= 1 and editingunlock = 1 then 1
when canRead = 1 and canUpdate = 1 and managing = 1 and editingunlock = 2 then 2
when canRead = 1 and canUpdate = 1 and managing = 2 and editingunlock = 2 then 3
else 0 end, editinglock = case
when canRead = 1 and canUpdate = 1 and managing >= 1 and editinglock = 1 then 1
when canRead = 1 and canUpdate = 1 and managing = 1 and editinglock = 2 then 2
when canRead = 1 and canUpdate = 1 and managing = 2 and editinglock = 2 then 3
else 0 end;

ALTER TABLE sys_attribute_group DROP COLUMN managing;
ALTER TABLE sys_attribute_group DROP COLUMN canUpdate;

insert into sys_user_language values (1, 'EditingOptions', 'Editing options');
insert into sys_user_language values (1, 'EditingOptionsLocked', 'Editing options (locked)');
insert into sys_user_language values (1, 'Mandatory', 'Mandatory');
insert into sys_user_language values (1, 'NotVisible', 'Not visible');
insert into sys_user_language values (1, 'ConfigureLockedObjects', 'Configure locked objects separately');

select count(*) into _cc from sys_user_language where prop='ReadOnly';
if(_cc = 0) then
	insert into sys_user_language values (1, 'ReadOnly','Readonly');
else
	update sys_user_language set value = 'Readonly' where prop = 'ReadOnly' and languageid = 1; 
end if;

update cis_favorites set dbversion = '1.31.0' where dbversion ilike '1.30.%';
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
