-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.12';
	_newVersion varchar = '1.1.13';
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
update sys_object_attributes set predefined = 0 where predefined is null;
update sys_object_attributes set infilter = 0 where infilter is null;
update sys_object_attributes set inadvancedsearch = 0 where inadvancedsearch is null;
update sys_object_attributes set inlabel = 0 where inlabel is null;
update sys_object_attributes set intooltip = 0 where intooltip is null;
update sys_object_attributes set insearch = 0 where insearch is null;
update sys_object_attributes set inmetaphor = 0 where inmetaphor is null;
update sys_object_attributes set labelbold = 0 where labelbold is null;
update sys_object_attributes set labelitalic = 0 where labelitalic is null;
update sys_object_attributes set labelunderline = 0 where labelunderline is null;
update sys_object_attributes set contentbold = 0 where contentbold is null;
update sys_object_attributes set contentitalic = 0 where contentitalic is null;
update sys_object_attributes set contentunderline = 0 where contentunderline is null;
update sys_object_attributes set inexport = 0 where inexport is null;
update sys_object_attributes set insimplesearch = 0 where insimplesearch is null;
update sys_object_attributes set inprefilter = 0 where inprefilter is null;
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