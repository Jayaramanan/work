-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.48.11';
	_newVersion varchar = '1.48.12';
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
DROP VIEW sys_user_settings;

ALTER TABLE sys_settings_application ALTER COLUMN value TYPE text;
ALTER TABLE sys_settings_group ALTER COLUMN value TYPE text;
ALTER TABLE sys_settings_user ALTER COLUMN value TYPE text;

CREATE OR REPLACE VIEW sys_user_settings AS 
        (         SELECT sys_settings_user.id AS userid, sys_settings_user.section, sys_settings_user.prop, sys_settings_user.value
                   FROM sys_settings_user
        UNION 
                 SELECT u.id AS userid, g.section, g.prop, g.value
                   FROM sys_user u
              JOIN sys_user_group ug ON u.id = ug.userid
         JOIN sys_settings_group g ON g.id = ug.groupid
        WHERE NOT (EXISTS ( SELECT t.id, t.section, t.prop, t.value
                 FROM sys_settings_user t
                WHERE t.id = u.id AND t.section::text = g.section::text AND t.prop::text = g.prop::text)))
UNION 
         SELECT u.id AS userid, g.section, g.prop, g.value
           FROM sys_user u, sys_settings_application g
          WHERE NOT (EXISTS ( SELECT t.id, t.section, t.prop, t.value
                   FROM sys_settings_user t
                  WHERE t.id = u.id AND t.section::text = g.section::text AND t.prop::text = g.prop::text)) AND NOT (EXISTS ( SELECT t.id, t.section, t.prop, t.value, ug.groupid, ug.userid
                   FROM sys_settings_group t
              JOIN sys_user_group ug ON t.id = ug.groupid
             WHERE u.id = ug.userid AND t.section::text = g.section::text AND t.prop::text = g.prop::text));
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