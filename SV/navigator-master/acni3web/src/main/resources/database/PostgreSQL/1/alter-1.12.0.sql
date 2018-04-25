-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.11.3';
	_newVersion varchar = '1.12.0';
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
CREATE TABLE sys_report_template
(
  id serial NOT NULL,
  "name" character varying(100) NOT NULL,
  "type" integer,
  "xml" text,
  CONSTRAINT pk_sys_report_template PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

insert into sys_user_language values (1, 'Ni3ReportViewer', 'Ni3 Report Viewer');
insert into sys_user_language values (1, 'Reports', 'Reports');
insert into sys_user_language values (1, 'NextPage', 'Next page');
insert into sys_user_language values (1, 'PreviousPage', 'Previous page');
insert into sys_user_language values (1, 'FirstPage', 'First page');
insert into sys_user_language values (1, 'LastPage', 'Last page');
insert into sys_user_language values (1, 'FitPage', 'Fit page');
insert into sys_user_language values (1, 'FitWidth', 'Fit width');
insert into sys_user_language values (1, 'ActualSize', 'Actual size');
insert into sys_user_language values (1, 'GoToPage', 'Go to page ');
insert into sys_user_language values (1, 'ZoomRatio', 'Zoom');

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

