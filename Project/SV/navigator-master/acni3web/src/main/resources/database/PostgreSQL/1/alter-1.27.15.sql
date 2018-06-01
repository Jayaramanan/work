-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.27.14';
	_newVersion varchar = '1.27.15';
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
--fix cis_edges
insert into cis_objects (id, objecttype, userid, locked)
	select id, edgetype, userid, locked from cis_edges where not exists 
		(select id from cis_objects t where t.id=cis_edges.id);
delete from cis_edges where fromid in 
	(select e.FromId from cis_edges e where not exists 
		( select id from cis_nodes where id = e.fromid));
delete from cis_edges where toid in 
	(select e.toid from cis_edges e where not exists 
		( select id from cis_nodes where id = e.toid ));
--fix cis_nodes
insert into cis_objects (id, objecttype, userid, locked) 
	select id, nodetype, 1, 0 from cis_nodes where not exists 
		(select id from cis_objects t where t.id=cis_nodes.id);
--fix cis_objects
delete from cis_objects where id not in(select id from cis_nodes t 
	where t.id=cis_objects.id union select id from cis_edges t where t.id=cis_objects.id);
--add constraints
alter table cis_edges add constraint cis_edges_cis_nodes_fromid_fk foreign key (fromid) references
	cis_nodes (id) match simple on update no action on delete no action;
alter table cis_edges add constraint cis_edges_cis_nodes_toid_fk foreign key (toid) references
	cis_nodes (id) match simple on update no action on delete no action;
alter table cis_objects add constraint cis_objects_sys_object_fk foreign key (objecttype) references
	sys_object (id) match simple on update no action on delete no action;

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