-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script

create or replace function correctUserSettings()
  returns integer as
$BODY$
    declare  
     user_id integer;
     rowcount integer;
     _cc integer;
     user_ids cursor is
	select id from sys_user; 
    begin
       select count(id) into rowcount from sys_user;
       open user_ids;
       for i in 1 .. rowcount loop
          fetch next from user_ids into user_id;
          select count(id) into _cc from sys_settings_user where id = user_id and section = 'Applet' and prop = '_InheritsGroupSettings';
          if(_cc = 0) then
          	insert into sys_settings_user (id, section, prop, value) values (user_id, 'Applet', '_InheritsGroupSettings', 'false');
          end if;
       end loop;
       close user_ids;
   return 0;
   end;
$BODY$
language 'plpgsql' volatile cost 100;
alter function correctUserSettings() owner to sa;

CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.26';
	_newVersion varchar = '1.1.27';
	_version varchar;
	_cc integer;
	i integer;
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
	perform correctUserSettings();
	drop function correctUserSettings();
	insert into sys_user_language values (1, 'InheritApplicationGroupLevelProperties', 'Inherit application/group level properties');
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
