-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.11.2';
	_newVersion varchar = '1.11.3';
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
CREATE OR REPLACE FUNCTION appendschematables(_schemaid bigint, _objectid bigint)
  RETURNS integer AS
$BODY$
    declare _timeStart timestamp;
	    _timeEnd timestamp; 
	    _timeTmp timestamp;
	    _Parent int;
	    _SQL text;
	    _errormsg text;

	NewTables cursor is
        select distinct 'CREATE TABLE '||replace(COALESCE(b.inTable,a.TableName),'dbo.','')||'(ID bigint NOT NULL, CONSTRAINT PK_'||replace(COALESCE(b.inTable,a.TableName),'dbo.','')||' PRIMARY KEY(ID))'
        from SYS_OBJECT_Definition a inner join SYS_OBJECT_Attributes b on (a.ID=b.ObjectDefinitionID)
        where not exists(select * 
            from information_schema.tables o
            where o.table_name iLike replace(COALESCE(b.inTable,a.TableName),'dbo.','')
            	and o.table_schema ilike current_schema()
                  and o.table_type='BASE TABLE')
            and upper(replace(COALESCE(COALESCE(b.inTable,a.TableName),''),'dbo.',''))!='CIS_EDGES'
            and upper(replace(COALESCE(COALESCE(b.inTable,a.TableName),''),'dbo.',''))!='CIS_NODES'
            and TableName is not null
            and a.ParentObjectID=_SchemaID;

	NewTableColumns cursor is 
	select distinct 'ALTER TABLE '||replace(COALESCE(t.inTable,d.TableName),'dbo.','')||' ADD '||t.name||case when t.predefined=1 or t.datatypeid in (2,4) then ' numeric NULL' else  ' varchar(255) NULL' end
	from SYS_OBJECT_Attributes t inner join SYS_OBJECT_Definition d on t.ObjectDefinitionID=d.id
        where not exists(select *
                from information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
                where o.table_name iLike replace(COALESCE(t.inTable,d.TableName),'dbo.','') 
                    and a.column_name iLike t.name
                    and o.table_schema ilike current_schema()
                    and o.table_type='BASE TABLE')
            and upper(replace(COALESCE(COALESCE(t.inTable,d.TableName),''),'dbo.',''))!='CIS_EDGES'
            and upper(replace(COALESCE(COALESCE(t.inTable,d.TableName),''),'dbo.',''))!='CIS_NODES'
            and d.ParentObjectID=_SchemaID;

	DropTableColumns cursor is 
        select 'ALTER TABLE '||o.table_name||' DROP COLUMN '||a.column_name
        FROM information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
        where not exists(select *
                from SYS_OBJECT_Attributes t inner join SYS_OBJECT_Definition d on t.ObjectDefinitionID=d.id
                where o.table_name  iLike  replace(COALESCE(t.inTable,d.TableName),'dbo.','') 
                        and a.column_name  iLike t.name 
                        and (
                        	(a.udt_name like 'varchar' and t.datatypeId not in (2,4))
                        	or 
                        	(a.udt_name like 'numeric' and t.datatypeId in (2,4))
                        )
                        and replace(COALESCE(t.inTable,d.TableName),'dbo.','')!='CIS_EDGES'
                        and d.ParentObjectID=_SchemaID)
              and lower(a.column_name) not in ('id','lon','lat','precision')
              and lower(o.table_name) in (select lower(replace(COALESCE(aa.inTable,tt.TableName),'dbo.',''))
                    from SYS_OBJECT_Definition tt inner join SYS_OBJECT_Attributes aa on aa.ObjectDefinitionID=tt.id
                    where tt.ParentObjectID=_SchemaID 
                    and replace(tt.TableName,'dbo.','')!='CIS_EDGES') 
              and upper(o.table_name)!='CIS_EDGES'
                            and lower(o.table_name) in (select lower(replace(COALESCE(aa.inTable,tt.TableName),'dbo.',''))
                    from SYS_OBJECT_Definition tt inner join SYS_OBJECT_Attributes aa on aa.ObjectDefinitionID=tt.id
                    where tt.ParentObjectID=_SchemaID 
                    and replace(tt.TableName,'dbo.','')!='CIS_NODES') 
              and upper(o.table_name)!='CIS_NODES';
BEGIN

    _timeStart := now();
    _timeTmp := _timeStart;

    _Parent:=nextval('seq_sys_log');
    insert into sys_log(id,Name,TimeStart,Message)
    values(_Parent,'appendSchemaTables',_timeStart,'START');

    begin
        open NewTables;

        fetch next from NewTables
        into _SQL;

        while FOUND loop
            execute _SQL;
            fetch next from NewTables
            into _SQL;
        end loop;
        close NewTables;

        open DropTableColumns;

        fetch next from DropTableColumns
        into _SQL;

        while FOUND loop
            execute _SQL;
            fetch next from DropTableColumns
            into _SQL;
        end loop;
        close DropTableColumns;

        open NewTableColumns;

        fetch next from NewTableColumns
        into _SQL;

        while FOUND loop
            execute _SQL;
            fetch next from NewTableColumns
            into _SQL;
        end loop;
        close NewTableColumns;

        _timeEnd=now();
	insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Message,Parent)
	values(nextval('seq_sys_log'),'appendSchemaTables',_timeStart,_timeEnd,_timeEnd-_timeStart,'OK',_Parent);
    exception
	    when OTHERS then
		_timeEnd:=now();
		insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Message,Parent)
		values(nextval('seq_sys_log'),'appendSchemaTables',_timeStart,_timeEnd,_timeEnd-_timeStart,'ERROR',_Parent);
    end;
    return 0;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;
ALTER FUNCTION appendschematables(bigint, bigint) OWNER TO sa;


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

