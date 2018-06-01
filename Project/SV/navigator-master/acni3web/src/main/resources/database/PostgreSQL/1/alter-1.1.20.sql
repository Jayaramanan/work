-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script

create or replace function correctTranslations()
  returns integer as
$BODY$
    declare 
       props varchar(255)[][];
    begin
       props = ARRAY[
           ['FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward'],
           ['SaveAs', 'Save Graph'],
           ['BACK_BUTTON_TOOLTIP_TEXT', 'Back'],
           ['Reload', 'Reload'],
           ['Isolate', 'Isolate'],
	       ['Find path', 'Find path'],
           ['Clear highlights', 'Clear highlights'],
           ['Clear', 'Clear'],
           ['Search', 'Search'],
           ['Stop', 'Stop'],
           ['File', 'File'],
           ['Print graph', 'Print graph'],
           ['Print map', 'Print map'],
           ['Exit', 'Exit'],
           ['Node', 'Node'],
           ['NodeCreate', 'Create node'],
           ['NodeEdit', 'Edit node'],
           ['Connection', 'Connection'],
           ['ConnectionCreate', 'Create connection'],
           ['ConnectionEdit', 'Edit connection'],
           ['ConnectionHistory', 'Connection history'],
           ['Metaphors', 'Metaphors'],
           ['Maps', 'Maps'],
           ['ThematicLegend', 'ThematicLegend'],
           ['Help', 'Help'],
           ['Off', 'Off'],
           ['Legend', 'Legend']
       ];

       for i in array_lower(props,1) .. array_upper(props,1) loop
          insert into sys_user_language(languageid, prop, value) 
              select 1, props[i][1], props[i][2] where not exists (
                  select languageid, prop, value from sys_user_language where prop ilike props[i][1]);
       end loop;
   return 0;
   end;
$BODY$
language 'plpgsql' volatile cost 100;
alter function correctTranslations() owner to sa;


CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.19';
	_newVersion varchar = '1.1.20';
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
insert into sys_user_language(languageid, prop, value) values (1, 'Change Password', 'Change password');
insert into sys_user_language(languageid, prop, value) values (1, 'Copy graph', 'Copy graph');
insert into sys_user_language(languageid, prop, value) values (1, 'Copy map', 'Copy map');
insert into sys_user_language(languageid, prop, value) values (1, 'Export data', 'Export data');
insert into sys_user_language(languageid, prop, value) values (1, 'Copy data', 'Copy data');
insert into sys_user_language(languageid, prop, value) values (1, 'NodeDelete', 'Node delete');
insert into sys_user_language(languageid, prop, value) values (1, 'NodeSecurity', 'Node security');
insert into sys_user_language(languageid, prop, value) values (1, 'ApplyToSelection', 'Apply to selection');
insert into sys_user_language(languageid, prop, value) values (1, 'ApplyToGraph', 'Apply to graph');
insert into sys_user_language(languageid, prop, value) values (1, 'ApplyToSearchResult', 'Apply to search result');
insert into sys_user_language(languageid, prop, value) values (1, 'MetaphorsLegend', 'Legend');
insert into sys_user_language(languageid, prop, value) values (1, 'Pie charts', 'Pie charts');
insert into sys_user_language(languageid, prop, value) values (1, 'Stacked charts', 'Stacked charts');
insert into sys_user_language(languageid, prop, value) values (1, 'Bar charts', 'Bar charts');
insert into sys_user_language(languageid, prop, value) values (1, 'Remove selected', 'Remove selected');
insert into sys_user_language(languageid, prop, value) values (1, 'Simple search', 'Simple search');
insert into sys_user_language(languageid, prop, value) values (1, 'Create node', 'Create node');
insert into sys_user_language(languageid, prop, value) values (1, 'Create edge', 'Create edge');
insert into sys_user_language(languageid, prop, value) values (1, 'PowerOff', 'Power Off');

perform correctTranslations();
drop function correctTranslations();

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
