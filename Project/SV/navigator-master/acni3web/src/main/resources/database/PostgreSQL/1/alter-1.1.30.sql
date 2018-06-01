-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script

create or replace function correctSettings()
  returns integer as
$BODY$
    declare 
       props varchar(255)[];
    begin
       props = ARRAY[
           ['File_PrintMap_InUse'],
           ['File_Exit_InUse'],
           ['File_InUse'],
           ['File_PrintGraph_InUse'],
           ['File_ExportData_InUse'],
	       ['Node_NodeEdit_InUse'],
           ['Node_NodeDelete_InUse'],
           ['Node_NodeCreate_InUse'],
           ['Node_InUse'],
           ['Node_NodeHistory_InUse'],
           ['Help_InUse'],
           ['Help_About_InUse'],
           ['Connection_ConnectionHistory_InUse'],
           ['Connection_ConnectionEdit_InUse'],
           ['Connection_ConnectionCreate_InUse'],
           ['Connection_InUse'],
           ['Maps_InUse'],
           ['Charts_InUse']
       ];

       for i in array_lower(props,1) .. array_upper(props,1) loop
          insert into sys_settings_application(section, prop, value) 
              select 'Applet', props[i][1], 'false' where not exists (
                  select section, prop, value from sys_settings_application where prop ilike props[i][1]);
       end loop;
   return 0;
   end;
$BODY$
language 'plpgsql' volatile cost 100;
alter function correctSettings() owner to sa;


CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.29';
	_newVersion varchar = '1.1.30';
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
insert into sys_user_language values (1, 'MsgCantDeleteSystemProperty', 'Can not delete system property');


perform correctSettings();
drop function correctSettings();

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
