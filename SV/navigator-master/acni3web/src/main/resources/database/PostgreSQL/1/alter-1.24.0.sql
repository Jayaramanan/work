-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.23.3';
	_newVersion varchar = '1.24.0';
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
	ALTER TABLE sys_object_attributes ADD COLUMN incontext integer;
	ALTER TABLE cis_edges ADD COLUMN favoritesid integer;

	insert into sys_user_language values (1, 'InContext', 'In context');
	
CREATE TABLE sys_context 
( 
  id serial NOT NULL, 
  objectdefinitionid integer, 
  "name" character varying(255), 
  pkattrid integer,
  tablename character varying(255), 
  CONSTRAINT sys_context_pk PRIMARY KEY (id),
  CONSTRAINT fk_sys_context_sys_object FOREIGN KEY (objectdefinitionid)
      REFERENCES sys_object (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_context_sys_object_attributes FOREIGN KEY (pkattrid)
      REFERENCES sys_object_attributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH ( 
  OIDS=FALSE 
); 


CREATE TABLE sys_context_attributes 
( 
  id serial not null,
  contextid integer NOT NULL, 
  attributeid integer NOT NULL, 
  CONSTRAINT sys_context_attribute_pk PRIMARY KEY (id),
  CONSTRAINT fk_sys_context_attributes_sys_context FOREIGN KEY (contextid)
      REFERENCES sys_context (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_sys_context_attributes_sys_object_attributes FOREIGN KEY (attributeid)
      REFERENCES sys_object_attributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH ( 
  OIDS=FALSE 
); 
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
