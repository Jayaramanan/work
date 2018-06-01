-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.28.1';
	_newVersion varchar = '1.28.2';
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
insert into sys_user_language values (1, 'MsgPasswordFormatNotCorrect', 'Password complexity format is not correct');
insert into sys_user_language values (1, 'MsgPasswordDoesntMatchComplexity', 'Password doesn`t match complexity');
insert into sys_user_language values (1, 'PasswordComplexity', 'Password complexity');
insert into sys_user_language values (1, 'PasswordComplexityDescriptionLabel', 'The password should be at least 8 symbols long and should contain at least 1 lower case letter, 1 upper case letter and 1 digit');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'PasswordComplexity', '[a-z]{1}##[A-Z]{1}##[0-9]{1}##[.]{8}');
insert into sys_user_language values (1, 'PasswordComplexityTooltip', '<html><body>Format: [&lt;expression1&gt;]{&lt;min_count1&gt;}##[&lt;expression2&gt;]{&lt;min_count2&gt;}<br>[&lt;expression&gt;] - expression<br>{&lt;min_count&gt;} - minimum count<br>## - separator<br>Example:[A-Z]{1}##[0-9]{2}##[.]{8}<br>[A-Z]{1} - at least 1 upper case letter<br>[0-9]{2} - at least 2 digits<br>[.]{8} - at least 8 symbols long</body></html>');

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
