-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.19.29';
	_newVersion varchar = '1.19.30';
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

delete from sys_user_language where prop = 'BaseTerritory';
insert into sys_user_language values (1, 'BaseLayer', 'Base');

update gis_territory gt set parent = null where parent is not null and not exists (select id from gis_territory gt1 where gt1.id = gt.parent);
update gis_territory gt set base = null where base is not null and not exists (select id from gis_layer gl where gl.id = gt.base);

ALTER TABLE gis_territory
  ADD CONSTRAINT fk_gis_territory_parent_gis_territory FOREIGN KEY (parent)
      REFERENCES gis_territory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE gis_territory
  ADD CONSTRAINT fk_gis_territory_base_gis_layer FOREIGN KEY (base)
      REFERENCES gis_layer (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

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
