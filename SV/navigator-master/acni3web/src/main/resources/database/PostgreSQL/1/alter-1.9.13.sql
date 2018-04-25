-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.9.12';
	_newVersion varchar = '1.9.13';
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
insert into sys_user_language values (1, 'ZipAttribute', 'Zip attribute');
insert into sys_user_language values (1, 'AddressAttribute', 'Address attribute');
insert into sys_user_language values (1, 'Country', 'Country');
insert into sys_user_language values (1, 'GeoCoding', 'Geo-coding');

CREATE TABLE sys_geocoding_job
(
  id serial NOT NULL,
  objectid integer NOT NULL,
  zipattributeid integer,
  addressattributeid integer,
  country character varying(200),
  triggeredby integer NOT NULL,
  status integer NOT NULL,
  timestart timestamp without time zone,
  timeend timestamp without time zone,
  CONSTRAINT pk_sys_geocoding_job PRIMARY KEY (id),
  CONSTRAINT sys_geocoding_job_addressattributeid_fkey FOREIGN KEY (addressattributeid)
      REFERENCES sys_object_attributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_geocoding_job_objectid_fkey FOREIGN KEY (objectid)
      REFERENCES sys_object (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_geocoding_job_triggeredby_fkey FOREIGN KEY (triggeredby)
      REFERENCES sys_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sys_geocoding_job_zipattributeid_fkey FOREIGN KEY (zipattributeid)
      REFERENCES sys_object_attributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sys_geocoding_job OWNER TO postgres;
------------------------------------------------------------------
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