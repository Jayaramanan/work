-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.43.6';
	_newVersion varchar = '1.43.7';
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
-- function for getting geometry by geo coordinates
CREATE OR REPLACE FUNCTION getgeometrybypoint(lon numeric, lat numeric, tablename character varying)
  RETURNS integer AS
$BODY$
declare
	_gid 	int;
	_count	int;
	_result int = 0;
	_contains bool;
	territory_cursor refcursor;
BEGIN

open territory_cursor for execute 'SELECT gis.gid as gid FROM ' || tablename || ' gis where ST_transform(gis.the_geom, 4326) && ST_GeomFromText(''POINT(' || lon ||' '|| lat || ')'', 4326)';
fetch next from territory_cursor
into _gid;

while FOUND loop
	execute 'SELECT _st_contains(ST_transform(gis.the_geom, 4326), ST_GeomFromText(''POINT(' || lon ||' '|| lat || ')'', 4326)) FROM ' || tablename || ' gis where gis.gid = ' || _gid into _contains;
	
	if (_contains) then
		_result = _gid;
		EXIT;
	end if;
	fetch next from territory_cursor
	into _gid;
end loop;

close territory_cursor;

return _result;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
  
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