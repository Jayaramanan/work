-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.42.1';
	_newVersion varchar = '1.42.2';
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
CREATE OR REPLACE FUNCTION getGeometryByPoint(lon numeric, lat numeric, tablename character varying)
  RETURNS integer AS
$BODY$
declare
	_gid 	int;
	_count	int;
	_result int = 0;
	_contains bool;
	territory_cursor refcursor;
BEGIN

open territory_cursor for execute 'SELECT gis.gid as gid FROM ' || tablename || ' gis where gis.the_geom && ST_GeomFromText(''POINT(' || lon ||' '|| lat || ')'', 900913)';
fetch next from territory_cursor
into _gid;

while FOUND loop
	execute 'SELECT _st_contains(gis.the_geom, ST_GeomFromText(''POINT(' || lon ||' '|| lat || ')'', 900913)) FROM ' || tablename || ' gis where gis.gid = ' || _gid into _contains;
	
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
  
  
-- procedure adds all mappings for one layer
CREATE OR REPLACE FUNCTION fillGeometryCache(tablename character varying)
  RETURNS void AS
$BODY$
declare
	_gid 	int;
	_cachetable character varying;
	_sql character varying;
BEGIN
	_cachetable = tablename||'_mapping';
	_sql:= 'INSERT INTO ' || _cachetable || ' (nodeid, gid)'||
	'SELECT g.nodeid, g.gid ' ||
	'FROM (SELECT n.id as nodeid, getGeometryByPoint(n.lon, n.lat, '''|| tablename ||''') as gid FROM cis_nodes n WHERE (n.lon != 0 OR n.lat != 0)'
	'AND NOT EXISTS (select nodeid from '|| _cachetable ||' where nodeid = n.id)) g WHERE g.gid > 0';
	execute _sql;
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