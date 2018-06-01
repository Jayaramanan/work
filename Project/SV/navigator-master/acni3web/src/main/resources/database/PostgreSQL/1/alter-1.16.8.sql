-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.16.7';
	_newVersion varchar = '1.16.8';
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
insert into sys_user_language values (1, 'MsgObjectWithGivenNameNotFound', 'Object with name `{1}` not found');
insert into sys_user_language values (1, 'MsgMandatoryFromToId', '`FromId` and `ToId` for edge objects are mandatory');
insert into sys_user_language values (1, 'CSVDataImport', 'Import data from CSV');
insert into sys_user_language values (1, 'MsgMandatoryColumnNotFilled', 'Mandatory column `{1}` not filled');
insert into sys_user_language values (1, 'ColumnSeparator', 'Column separator');
insert into sys_user_language values (1, 'LineSeparator', 'Line separator');
insert into sys_user_language values (1, 'MsgColumnIsNumericType', 'Column `{1}` is of numeric type. Value cannot be parsed: {2}');
insert into sys_user_language values (1, 'MsgCannotParseFile', 'Cannot parse file');
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
