-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.9.4';
	_newVersion varchar = '1.9.5';
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
alter table sys_user_language alter column value type text;  
update sys_user_language set value='<html><body>Ni3 Navigator © version @VER Copyright © 2006-2011 <a href="http://www.ni3.net" target="_blank">Ni3 AG</a>. All rights reserved. <br><br>Map data © <a href="http://www.openstreetmap.org/" target="_blank">OpenStreetMap</a> and contributors <a href="http://creativecommons.org/licenses/by-sa/2.0/" target="_blank">CC-BY-SA</a><br><br>Open Source and Third-Party components utilized in Ni3 Products may include:<br><br>Java Libraries, JVM, JRE (Copyright © <a href="http://www.java.com/en/" target="_blank">Sun Microsystems</a>)<br><br>Jetty, Jelly, Log4J, VFS, Xalan, Xerces, Castor, AXIS, Slide (Copyright © <a href="http://apache.org/licenses/LICENSE-2.0" target="_blank">Apache</a>)<br><br>Jasper, JFree Chart, JXL (Copyright © <a href="http://www.gnu.org/licenses/lgpl.html" target="_blank">SourceForge - LGPL license</a>)<br></body></html>' where prop like 'About text';
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