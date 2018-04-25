-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.20.7';
	_newVersion varchar = '1.20.8';
	_version varchar;
	_cc integer;
	_rows varchar[][] = array[
		['File', 'File'],
		['Node', 'Node'],
		['Maps', 'Maps'],
		['Help', 'Help'],
		['Change Password', 'Change password'],
		['Print map', 'Print map'],
		['Copy data', 'Copy data'],
		['Copy graph', 'Copy graph'],
		['Copy map', 'Copy map'],
		['Exit', 'Exit'],
		['Export data', 'Export data'],
		['Print graph', 'Print graph'],
		['NodeDelete', 'Node delete'],
		['NodeEdit', 'Node Edit'],
		['NodeSecurity', 'Node security'],
		['NodeCreate', '"Node Create"'],
		['NodeHistory', '"Node History"'],
		['ConnectionCreate', 'Create'],
		['ConnectionDelete', 'Delete'],
		['ConnectionEdit', 'Edit'],
		['ConnectionHistory', 'History'],
		['About', 'About']
	];
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
	for i in array_lower(_rows,1) .. array_upper(_rows,1) loop
		select count(*) into _cc from sys_user_language where prop = _rows[i][1];
		if(_cc = 0) then
			insert into sys_user_language (languageid, prop, value) 
				values (1, _rows[i][1], _rows[i][2]);
		end if;
	end loop;
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
