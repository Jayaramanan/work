-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.7.2';
	_newVersion varchar = '1.7.3';
	_version varchar;

	deleteGS cursor is 
		select min(objectid), groupid from sys_group_scope group by groupid having(count(groupid) > 1);
	_obj integer;
	_gr integer;
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
	alter table sys_group_scope drop constraint pk_sys_group_scope;
	alter table sys_group_scope alter column objectid drop not null;
	alter table sys_group_scope add primary key (groupid);
	open deleteGS;
	fetch next from deleteGS into _obj, _gr;
	while FOUND loop
		delete from sys_group_scope where objectid = _obj and groupid = _gr;
		fetch next from deleteGS into _obj, _gr;
	end loop;
	close deleteGS;
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