-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.22.0';
	_newVersion varchar = '1.23.0';
	_version varchar;
	_joinSql text;
	_SQL text;
	
	DataCursor cursor is
	select 'union '|| 'select m.id, '||id||', '||name|| ' from sys_nodemetaphor n inner join sys_metaphor m on (n.id=m.id) '|| 'where '||name||' is not null and nodeid=' || objectdefinitionid 
	from sys_object_attributes where inmetaphor=1 order by objectdefinitionid, id;
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
CREATE TABLE sys_metaphor 
( 
  id serial NOT NULL, 
  schemaid integer, 
  objectdefinitionid integer, 
  priority integer, 
  iconid integer,
  iconname character varying(255),
  metaphorset character varying(255),
  description character varying(255),
  CONSTRAINT PK_sys_metaphor PRIMARY KEY (id),
  CONSTRAINT fk_sys_metaphor_sys_object FOREIGN KEY (objectdefinitionid)
      REFERENCES sys_object (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_metaphor_sys_schema FOREIGN KEY (schemaid)
      REFERENCES sys_schema (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_metaphor_cht_icons FOREIGN KEY (iconid)
      REFERENCES cht_icons (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE TABLE sys_metaphor_data 
( 
  keyid serial NOT NULL,
  id integer NOT NULL, 
  attributeid integer NOT NULL, 
  data integer, 
  CONSTRAINT sys_metaphor_data_pkey PRIMARY KEY (keyid),
  CONSTRAINT fk_sys_metaphor_data_sys_metaphor FOREIGN KEY (id)
      REFERENCES sys_metaphor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_metaphor_data_sys_object_attributes FOREIGN KEY (attributeid)
      REFERENCES sys_object_attributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sys_metaphor_data_cht_predefinedattributes FOREIGN KEY (data)
      REFERENCES cht_predefinedattributes (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

INSERT INTO sys_metaphor(id, schemaid, objectdefinitionid, priority, iconname, metaphorset, iconid) 
SELECT id, schemaid, nodeid, priority, iconname, metaphorset,iconid FROM sys_nodemetaphor;
   
   	open DataCursor;	
    fetch next from DataCursor
    into _joinSql;

    while FOUND loop
		if (_SQL is null) then
			_SQL := 'INSERT INTO sys_metaphor_data(id, attributeid, data) '|| substr(_joinSql, 7) || ' ';
		else
			_SQL := _SQL || _joinSql || ' ';
		end if;
		fetch next from DataCursor
		into _joinSql;
    end loop;
    close DataCursor;

	raise info '%', _SQL;
	if (_SQL is not null) then
		execute _SQL;
	end if;
	
PERFORM setval('sys_metaphor_id_seq', COALESCE(max(ID)+1,1)) FROM sys_metaphor;
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
