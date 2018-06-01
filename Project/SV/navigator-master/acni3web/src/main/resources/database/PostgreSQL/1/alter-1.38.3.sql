-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.38.2';
	_newVersion varchar = '1.38.3';
	_version varchar;
	_cc integer;
	_newText text;
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
  _newText = 'Hello {1} {2},\n' ||
'\n' ||
'\nAs per Administrator request we have generated a new password for your account.' ||
'\n' ||
'\nHere is the information we now have on file for this account:' ||
'\n' ||
'\nInstance: {3}' ||
'\nUser name: {4}' ||
'\nPassword: {5}';
  select count(*) into _cc from sys_user_language where prop = 'MsgEMailPasswordResetText' and languageid = 1;
  if (_cc > 0) then
    update sys_user_language set value = _newText where languageid = 1 and prop = 'MsgEMailPasswordResetText';
  else
    insert into sys_user_language values (1, 'MsgEMailPasswordResetText', _newText);
  end if;
------------------------------------------------------------
-- update dbversion in cis_favorites (comment this line if script impacts favorites)
update cis_favorites set dbversion = _newVersion where dbversion = _expectedVersion;
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