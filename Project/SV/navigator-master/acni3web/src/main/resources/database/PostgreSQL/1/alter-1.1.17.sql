-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.1.16';
	_newVersion varchar = '1.1.17';
	_version varchar;
	_cc integer;
	_genID integer;
	_genSort integer;
	_obj record;
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

	FOR _obj IN select id from sys_object_definition where objecttypeid = 4 LOOP
		select count(*) into _cc from sys_object_attributes where objectdefinitionid = _obj.id and name = 'FromID';
		if(_cc = 0) then
			--select max(id) + 1 into _genID from sys_object_attributes;
			select max(sort) + 1 into _genSort from sys_object_attributes where objectdefinitionid = _obj.id;
			INSERT INTO sys_object_attributes (objectdefinitionid, 
						sort, name, label, predefined, 
						description, datatypeid, infilter, 
						inadvancedsearch, inlabel, intooltip, 
						insearch, inmetaphor, labelbold, 
						labelitalic, labelunderline, contentbold, 
						contentitalic, contentunderline, created, 
						createdby, managing, intable, inexport, 
						insimplesearch, inprefilter, format, 
						regexpression, valuedescription, instructure, 
						label_sort, filter_sort, search_sort) 
						VALUES 
						(_obj.id, 
						_genSort, 'FromID', 'FromID', 0, 
						NULL, 2, 0, 
						0, 0, 0, 
						0, 0, 0, 
						0, 0, 0, 
						0, 0, null, 
						NULL, 0, 'CIS_EDGES', 0, 
						0, 0, NULL, 
						NULL, NULL, NULL, 
						_genSort, _genSort, _genSort);
		end if;
		select count(*) into _cc from sys_object_attributes where objectdefinitionid = _obj.id and name = 'ToID';
		if(_cc = 0) then
			--select max(id) + 1 into _genID from sys_object_attributes;
			select max(sort) + 1 into _genSort from sys_object_attributes where objectdefinitionid = _obj.id;
			INSERT INTO sys_object_attributes (objectdefinitionid, 
						sort, name, label, predefined, 
						description, datatypeid, infilter, 
						inadvancedsearch, inlabel, intooltip, 
						insearch, inmetaphor, labelbold, 
						labelitalic, labelunderline, contentbold, 
						contentitalic, contentunderline, created, 
						createdby, managing, intable, inexport, 
						insimplesearch, inprefilter, format, 
						regexpression, valuedescription, instructure, 
						label_sort, filter_sort, search_sort) 
						VALUES 
						(_obj.id, 
						_genSort, 'ToID', 'ToID', 0, 
						NULL, 2, 0, 
						0, 0, 0, 
						0, 0, 0, 
						0, 0, 0, 
						0, 0, null, 
						NULL, 0, 'CIS_EDGES', 0, 
						0, 0, NULL, 
						NULL, NULL, NULL, 
						_genSort, _genSort, _genSort);
		end if;
		
	END LOOP;
------------------------------------------------------------
	raise info 'Database update script is completed';
--	-- update database version to _newVersion
	update sys_iam set version=_newVersion where name = 'PostgreSQL';
	raise info 'Database version updated: % -> %', _version, _newVersion;
END;
$$ LANGUAGE plpgsql;


-- launch function
select alterDatabase();
-- drop function
drop function alterDatabase();