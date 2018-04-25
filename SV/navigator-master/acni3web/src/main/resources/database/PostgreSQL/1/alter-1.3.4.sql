-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.3.3';
	_newVersion varchar = '1.3.4';
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
insert into sys_user_language(languageid, prop, value) values (1, 'MsgEMailPasswordResetText', 'Dear {1} {2}, \n\n New password for your account is created:\n login : {3} \n password : {4}\n\n With best regards.');
insert into sys_user_language(languageid, prop, value) values (1, 'MsgEmailPasswordResetSubject', 'Ni3 Navitagor: reset password');
insert into sys_user_language(languageid, prop, value) values (1, 'MsgUserNotFound', 'User not found');
insert into sys_user_language(languageid, prop, value) values (1, 'MsgCannotSendMail', 'Cannot send email');
insert into sys_user_language(languageid, prop, value) values (1, 'MsgUserMailEmpty', 'Email is empty for user');
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