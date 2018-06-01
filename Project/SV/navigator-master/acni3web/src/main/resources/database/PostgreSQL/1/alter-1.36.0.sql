-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.35.9';
	_newVersion varchar = '1.36.0';
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
   alter table cis_objects add column lastmodified timestamp default now();
   alter table cis_edges drop column locked;
   alter table cis_objects rename column locked to status;
   update cis_objects set status = 0 where status is null;

   --delete locked attribute from sys_object_attributes and related tables (for edges only)
   delete from sys_attribute_group      where attributeid in ( select id from sys_object_attributes where objectdefinitionid in ( select id from sys_object where objecttypeid in (4, 6) ) and upper(name) ilike 'LOCKED');	
   delete from cht_predefinedattributes where attributeid in ( select id from sys_object_attributes where objectdefinitionid in ( select id from sys_object where objecttypeid in (4, 6) ) and upper(name) ilike 'LOCKED');
   delete from sys_context_attributes   where attributeid in ( select id from sys_object_attributes where objectdefinitionid in ( select id from sys_object where objecttypeid in (4, 6) ) and upper(name) ilike 'LOCKED');  
   delete from sys_object_attributes where objectdefinitionid in ( select id from sys_object where objecttypeid in (4, 6) ) and upper(name) ilike 'LOCKED';
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

