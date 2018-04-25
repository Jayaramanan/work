-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.9.11';
	_newVersion varchar = '1.9.12';
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
CREATE OR REPLACE FUNCTION appendnodemetaphor(_schemaid bigint, _deletecolumns boolean)
  RETURNS integer AS
$BODY$
    declare _timeStart timestamp;
	    _timeEnd timestamp;
	    _timeTmp timestamp;
	    _Parent int;
	    _SQL text;
	    _SQLExecuted text;
	    _errormsg text;

	AddTableColumns cursor is 
        select distinct 'ALTER TABLE SYS_NodeMetaphor ADD '||t.name||' integer NULL;'
        from SYS_OBJECT_Attributes t inner join SYS_OBJECT_Definition d on t.ObjectDefinitionID=d.id
        where not exists(select a.column_name
                FROM information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
                where o.table_type='BASE TABLE'
                	and o.table_schema ilike current_schema()
                    and lower(o.table_name)='sys_nodemetaphor'
                    and lower(a.column_name)=lower(t.name))
            and InMetaphor=1
            and d.ParentObjectID=_SchemaID;
            
	DropTableColumns cursor is 
                select 'ALTER TABLE SYS_NodeMetaphor DROP COLUMN '||a.column_name
                from information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
                where lower(a.column_name) not in('id','nodeid','iconid','iconname','iconpath','schemaid','priority','metaphorset','description')
                    and o.table_schema ilike current_schema()  
                	and lower(o.table_name)='sys_nodemetaphor'
                    and not exists(select * from SYS_OBJECT_Attributes t where lower(t.Name) = lower(a.column_name) and t.InMetaphor=1);

begin
    _timeStart:=now();
    _timeTmp:=_timeStart;

    _Parent:=nextval('seq_sys_log');
    insert into sys_log(id,Name,TimeStart,Message)
    values(_Parent,'appendNodeMetaphor',_timeStart,'START');

    begin 
        open AddTableColumns;

        fetch next from AddTableColumns
        into _SQL;
        _SQLExecuted:='ADD: ';

        while FOUND loop
            execute _SQL;
            _SQLExecuted:=_SQLExecuted||_SQL||'; ';
            fetch next from AddTableColumns
            into _SQL;
        end loop;
        close AddTableColumns;

        _timeEnd:=now();
        insert into sys_log(id,Name,Command,TimeStart,TimeEnd,ExcTime,Message,Parent)
        values(nextval('seq_sys_log'),'appendNodeMetaphor',_SQLExecuted,_timeStart,_timeEnd,_timeEnd-_timeStart,'OK',_Parent);

        if _DeleteColumns=true then
            open DropTableColumns;

            fetch next from DropTableColumns
            into _SQL;
            _SQLExecuted:='Drop: ';

            while FOUND loop
                execute _SQL;
                fetch next from DropTableColumns
                into _SQL;
                _SQLExecuted:=_SQLExecuted || _SQL || '; ';
            end loop;
            close DropTableColumns;
            
            _timeEnd:=now();
            insert into sys_log(id,Name,Command,TimeStart,TimeEnd,ExcTime,Message,Parent)
            values(nextval('seq_sys_log'),'appendNodeMetaphor',_SQLExecuted,_timeStart,_timeEnd,_timeEnd-_timeStart,'OK',_Parent);
        end if;
    exception
	    when OTHERS then
		    _timeEnd:=now();
		    insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Parent,command,message)
		    values(nextval('seq_sys_log'),'appendNodeMetaphor',_timeStart,_timeEnd,_timeEnd-_timeStart,_Parent,_SQL,'error');
    end;
    return 0;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION appendnodemetaphor(bigint, boolean) OWNER TO sa;
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