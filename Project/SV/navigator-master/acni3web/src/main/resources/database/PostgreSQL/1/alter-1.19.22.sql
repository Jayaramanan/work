-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.19.21';
	_newVersion varchar = '1.19.22';
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
insert into sys_user_language values (1, 'ReadonlyNotValueListAttribute', 'Please make the attribute a `Value List` to make this column editable');
insert into sys_user_language values (1, 'ReadonlyFixedAttribute', 'The value is not editable because the attribute is fixed');
insert into sys_user_language values (1, 'ReadonlyFilledAutomatically', 'The value is not editable because it is filled automatically');
insert into sys_user_language values (1, 'ReadonlyConfigurableOnOtherScreen', 'This column is editable on `{1}` tab');
insert into sys_user_language values (1, 'ReadonlyLicenseRestrictions', '`Data capture` module should be enabled to make this column editable');
insert into sys_user_language values (1, 'ReadonlyObjectNotAccessible', 'Read privileges should be granted to object `{1}` to make this column editable');
insert into sys_user_language values (1, 'ReadonlyAttributeNotAccessible', 'Read privileges should be granted to attribute `{1}` to make this column editable');
insert into sys_user_language values (1, 'ReadonlyForExistingRecords', 'Column cannot be changed for existing records. Please delete the record and create a new one if necessary');
insert into sys_user_language values (1, 'ReadonlyFixedSetting', 'This setting is configurable in the upper part of the screen');
insert into sys_user_language values (1, 'ReadonlyBaseModuleDisabled', '`Base` module should be enabled to make this column editable');
insert into sys_user_language values (1, 'ReadonlyMapsModuleDisabled', '`Maps` module should be enabled to make this column editable');
insert into sys_user_language values (1, 'ReadonlyProcessedJob', 'Not editable, processed jobs can not be changed');
insert into sys_user_language values (1, 'ReadonlyConnectionTypeAlwaysMandatory', 'Not editable, `ConnectionType` is always `mandatory`');
insert into sys_user_language values (1, 'ReadonlyDataTypeNotText', 'Not editable, this column is used only for `text` datatype');
insert into sys_user_language values (1, 'ReadonlyMultivalueAttribute', 'Please uncheck `Multivalue` checkbox to make this column editable');
insert into sys_user_language values (1, 'ReadonlyValueListAttribute', 'Please change Value List value to other than `Value List` to make this column editable');
insert into sys_user_language values (1, 'ReadonlySelectObjectToEdit', 'Please select the object in the tree to edit this field');

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
