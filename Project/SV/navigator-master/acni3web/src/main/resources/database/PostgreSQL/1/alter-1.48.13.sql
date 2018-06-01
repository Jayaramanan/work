CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE
	_expectedVersion varchar = '1.48.12';
	_newVersion varchar = '1.48.13';
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
		update sys_iam set version=_newVersion where name = 'PostgreSQL';
	raise info 'Database version updated: % -> %', _version, _newVersion;
END;
$$ LANGUAGE plpgsql;

-- launch function
select alterDatabase();
-- drop function
drop function alterDatabase();


CREATE OR REPLACE FUNCTION deleteUserObject(tablename text)
  RETURNS void AS
$$
DECLARE
	_usr_ids text;
	_cis_objects text;
	_cis_nodes text;
	_cis_edges text;
	_usr text;
	_cis_node_from text;
	_cis_node_to text;
BEGIN
	_usr_ids := 'select id from ' || $1;


	_cis_objects := 'delete from cis_objects where id in (' || _usr_ids || ')';
	execute _cis_objects;
	raise info 'Deleted from cis_objects';

	_cis_edges := 'delete from cis_edges where id in (' || _usr_ids || ')';
	execute _cis_edges;
	raise info 'Deleted from cis_edges';

	--need to delete all edges to/from this node first
  _cis_node_from := 'delete from cis_edges where fromid in (' || _usr_ids || ')';
  execute _cis_node_from;
  _cis_node_to := 'delete from cis_edges where toid in (' || _usr_ids || ')';
  execute _cis_node_to;
  raise info 'Deleted referenced edges';

	_cis_nodes := 'delete from cis_nodes where id in (' || _usr_ids || ')';
	execute _cis_nodes;
	raise info 'Deleted from cis_nodes';

  -- we leave the actual data
	_usr := 'alter table ' || $1 || ' rename to ' || 'deleted_' || $1;
	execute _usr;
	raise info 'Deleted usr table';


END
$$
  LANGUAGE plpgsql VOLATILE
  COST 100;