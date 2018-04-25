-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.32';
	_newVersion varchar = '1.1.33';
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
	create sequence sys_schema_object_id_seq;

CREATE TABLE sys_schema
(
  id integer NOT NULL DEFAULT nextval('sys_schema_object_id_seq'::regclass),
  "name" character varying(50),
  description text,
  creation timestamp without time zone,
  createdby integer,
  CONSTRAINT sys_schema_pkey PRIMARY KEY (id),
  CONSTRAINT fk_sys_schema_sys_user FOREIGN KEY (createdby)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys_schema OWNER TO sa;



CREATE TABLE sys_object
(
  id integer NOT NULL DEFAULT nextval('sys_schema_object_id_seq'::regclass),
  "name" character varying(50),
  objecttypeid integer,
  schemaid integer,
  tablename character varying(50),
  description text,
  creation timestamp without time zone,
  createdby integer,
  tabdisplay integer,
  sort integer,
  CONSTRAINT sys_object_pkey PRIMARY KEY (id),
  CONSTRAINT fk_sys_object_sys_schema FOREIGN KEY (schemaid)
      REFERENCES sys_schema (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_object_cht_object_type FOREIGN KEY (objecttypeid)
      REFERENCES cht_object_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_object_sys_user FOREIGN KEY (createdby)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys_object OWNER TO sa;

INSERT INTO sys_schema(
            id, "name", description, creation, createdby)
	select id, "name", description, creation, createdby from sys_object_definition where objecttypeid = 1;

INSERT INTO sys_object(
            id, "name", objecttypeid, schemaid, tablename, description, creation, createdby, tabdisplay, sort)
        select id, "name", objecttypeid, parentobjectid, tablename, description, creation, createdby, tabdisplay, sort
        from sys_object_definition where objecttypeid <> 1;


drop table sys_object_definition cascade;

CREATE VIEW sys_object_definition AS
	(select id, "name", 1 as objecttypeid, null as parentobjectid, null as tablename, description, creation, createdby, 0 as tabdisplay, 0 as sort
        from sys_schema)
	union
	(select id, "name", objecttypeid, schemaid as parentobjectid, tablename, description, creation, createdby, tabdisplay, sort
        from sys_object);


ALTER TABLE sys_schema_object_id_seq OWNER TO sa;
PERFORM setval('sys_schema_object_id_seq', COALESCE(max(ID)+1,1)) FROM sys_object_definition;


alter table cht_chart add constraint fk_cht_chart_sys_schema foreign key (schemaid) 
	REFERENCES sys_schema (id);

ALTER TABLE cis_favorites  ADD CONSTRAINT fk_cis_favorites_sys_schema FOREIGN KEY (schemaid)
	REFERENCES sys_schema (id);

ALTER TABLE cis_nodes ADD CONSTRAINT fk_cis_nodes_sys_object FOREIGN KEY (nodetype)
	REFERENCES sys_object (id);

ALTER TABLE sys_attribute_structure ADD CONSTRAINT fk_sys_attribute_structure_sys_object FOREIGN KEY (objectdefinitionid)
	REFERENCES sys_object (id);

ALTER TABLE sys_object_attributes ADD CONSTRAINT fk_sys_object_attributes_sys_object FOREIGN KEY (objectdefinitionid)
	REFERENCES sys_object (id);
      
ALTER TABLE sys_object_chart ADD CONSTRAINT fk_sys_object_chart_sys_object FOREIGN KEY (objectid)
	REFERENCES sys_object (id);

ALTER TABLE sys_object_connection ADD CONSTRAINT fk_sys_object_connection_fromobject_sys_object FOREIGN KEY (fromobject)
	REFERENCES sys_object (id);

ALTER TABLE sys_object_connection ADD CONSTRAINT fk_sys_object_connection_toobject_sys_object FOREIGN KEY (toobject)
	REFERENCES sys_object (id);

ALTER TABLE sys_object_connection ADD CONSTRAINT fk_sys_object_connection_sys_object FOREIGN KEY (objectid)
	REFERENCES sys_object (id);

ALTER TABLE sys_object_log ADD CONSTRAINT fk_sys_object_modification_sys_object FOREIGN KEY (objectid)
      REFERENCES sys_object (id);

ALTER TABLE sys_object_user_group ADD CONSTRAINT fk_sys_object_user_group_sys_object FOREIGN KEY (objectid)
      REFERENCES sys_object (id);

ALTER TABLE sys_url ADD CONSTRAINT fk_sys_url_sys_object FOREIGN KEY (objectid)
      REFERENCES sys_object (id);
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