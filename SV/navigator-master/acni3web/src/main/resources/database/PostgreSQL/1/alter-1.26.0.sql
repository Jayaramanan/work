-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.25.8';
	_newVersion varchar = '1.26.0';
	_version varchar;
	_index integer;
	_id integer;
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
CREATE TABLE cis_favorites_folder
(
  id integer DEFAULT nextval('seq_user_favorites_folder'::regclass),
  foldername character varying(255),
  schemaid integer,
  creatorid integer,
  parentid integer,
  groupfolder integer,
  sort integer,
  CONSTRAINT fk_cis_favorites_folder_03 FOREIGN KEY (parentid)
      REFERENCES cis_favorites_folder (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_object_definition_01 FOREIGN KEY (schemaid)
      REFERENCES sys_schema (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_user_02 FOREIGN KEY (creatorid)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT pk_cis_favorites_folder primary key (id)
);

insert into cis_favorites_folder select id, foldername, schemaid, userid, parentid, groupfolder from cis_user_favorites_folder;

alter table cis_favorites add column folderid integer;
alter table cis_favorites add column groupfavorites integer;
alter table cis_favorites add column "mode" integer DEFAULT 1;

update cis_favorites set creatorid = (select min(userid) from cis_user_favorites t where t.favoritesid = cis_favorites.id);
update cis_favorites set folderid = (select uf.folderid from cis_group_favorites gf, cis_favorites f, cis_user_favorites uf
	where gf.favoritesid=f.id and f.id=uf.favoritesid and (group_rules is null or group_rules =1)
	and uf.favoritesid = cis_favorites.id);
    
update cis_favorites set groupfavorites = 1 where exists(select * from cis_group_favorites t where t.favoritesid = cis_favorites.id);

update cis_favorites set groupfavorites = 0 where groupfavorites is null;

update cis_favorites set 
	mode = (case when position('Mode=' in data)>0 then cast(substr(data,position('Mode=' in data) + 6, 1) as int) else 1 end)
	where mode != case when position('Mode=' in data)>0 then cast(substr(data,position('Mode=' in data) + 6, 1) as int) else 1 
	end;

drop table cis_group_favorites cascade;
drop table cis_user_favorites cascade;
drop table cis_user_favorites_folder cascade;

_index = 1;
for _id in select id from cis_favorites_folder order by foldername
loop
	update cis_favorites_folder set sort = _index where id = _id;
	_index = _index + 1;
end loop;

ALTER TABLE cis_favorites
  ADD CONSTRAINT fk_cis_favorites_cis_favorites_folder FOREIGN KEY (folderid)
      REFERENCES cis_favorites_folder (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
-------------------------------
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
