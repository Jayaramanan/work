-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.28.2';
	_newVersion varchar = '1.28.3';
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
delete from cis_chart_attributes where chartid = (select id from cht_chart where name = 'SNA');
delete from cht_chart where name = 'SNA';
delete from sys_chart_group where chartid = -2;

alter table cht_chart alter column schemaid drop not null;

INSERT INTO cht_chart(id, name, comment, schemaid) VALUES(-2, 'SNA', 'SNA', (select min(id) from sys_schema));

INSERT INTO cis_chart_attributes(id, chartid, rgb, predefinedid, name, description, formula)
  VALUES(-3, -2, '#0000FF', '-999999', 'Betweenness', 'Betweenness', NULL);
INSERT INTO cis_chart_attributes(id, chartid, rgb, predefinedid, name, description, formula)
  VALUES(-4, -2, '#00FF00', '-999999', 'Closeness', 'Closeness', NULL);
INSERT INTO cis_chart_attributes(id, chartid, rgb, predefinedid, name, description, formula)
  VALUES(-5, -2, '#FF0000', '-999999', 'Degree', 'Degree', NULL);
INSERT INTO cis_chart_attributes(id, chartid, rgb, predefinedid, name, description, formula)
  VALUES(-2, -2, '#FFFF00', '-999999', 'Eigenvector', 'Eigenvector', NULL);
INSERT INTO cis_chart_attributes(id, chartid, rgb, predefinedid, name, description, formula)
  VALUES(-1, -2, '#FF00FF', '-999999', 'Clustering', 'Clustering', NULL);

INSERT INTO sys_settings_application(section, prop, value) VALUES('Applet','SNA_InUse', 'TRUE');

INSERT INTO sys_chart_group(groupid, chartid) select id, -2 from sys_group;
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
