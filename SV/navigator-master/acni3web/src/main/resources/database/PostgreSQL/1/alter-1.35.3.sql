-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.35.2';
	_newVersion varchar = '1.35.3';
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
insert into sys_user_language values (1, 'MsgActivityCreateFavorite', '{user} created a new favorite `{name}`');
insert into sys_user_language values (1, 'MsgActivityDeleteFavorite', '{user} deleted a favorite');
insert into sys_user_language values (1, 'MsgActivityUpdateFavorite', '{user} updated the favorite `{name}`');
insert into sys_user_language values (1, 'MsgActivityCopyFavorite', '{user} copied the favorite `{name}`');
insert into sys_user_language values (1, 'MsgActivityCreateFavoriteFolder', '{user} created a new favorites` folder `{name}`');
insert into sys_user_language values (1, 'MsgActivityDeleteFavoriteFolder', '{user} deleted favorites` folder');
insert into sys_user_language values (1, 'MsgActivityUpdateFavoriteFolder', '{user} updated favorites` folder `{name}`');
insert into sys_user_language values (1, 'MsgActivityCreateNode', '{user} created a new node `{name}`');
insert into sys_user_language values (1, 'MsgActivityUpdateNode', '{user} updated the node `{name}`');
insert into sys_user_language values (1, 'MsgActivityCreateEdge', '{user} created a new edge `{name}` from `{from}` to `{to}`');
insert into sys_user_language values (1, 'MsgActivityUpdateEdge', '{user} updated the edge `{name}` from `{from}` to `{to}`');
insert into sys_user_language values (1, 'MsgActivityDeleteObject', '{user} deleted an object');
insert into sys_user_language values (1, 'MsgActivityCreateEdgeShort', '{user} created a new edge `{name}`');
insert into sys_user_language values (1, 'MsgActivityUpdateEdgeShort', '{user} updated the edge `{name}`');

insert into sys_user_language values (1, 'DeletedLabel', '<deleted>');
insert into sys_user_language values (1, 'ActivityStream', 'Activity stream');
insert into sys_user_language values (1, 'ShowMore', 'Show more');
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
