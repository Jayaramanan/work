-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.19.8';
	_newVersion varchar = '1.19.9';
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
	    _tablename text;
	    _columnname text;
	    _datatype text;
	    _olddatatype text;
	    _errorid int;

	NewTables cursor is 
        select distinct 'CREATE TABLE '||replace(COALESCE(oa.inTable,od.TableName),'dbo.','')||'(ID bigint NOT NULL, CONSTRAINT PK_'||replace(COALESCE(oa.inTable,od.TableName),'dbo.','')||' PRIMARY KEY(ID))'
        from SYS_OBJECT_Definition od inner join SYS_OBJECT_Attributes oa on (od.ID=oa.ObjectDefinitionID)
        where not exists(select * 
            from information_schema.tables tbl
            where tbl.table_name iLike replace(COALESCE(oa.inTable,od.TableName),'dbo.','')
            	and tbl.table_schema ilike current_schema()
                  and tbl.table_type='BASE TABLE')
            and upper(replace(COALESCE(COALESCE(oa.inTable,od.TableName),''),'dbo.',''))!='CIS_EDGES'
            and upper(replace(COALESCE(COALESCE(oa.inTable,od.TableName),''),'dbo.',''))!='CIS_NODES'
            and TableName is not null
            and od.ParentObjectID=_SchemaID and (select case when _objectid = 0 then true else od.id = _objectid end);

	NewTableColumns cursor is 
	select distinct 'ALTER TABLE '||replace(COALESCE(oa.inTable,od.TableName),'dbo.','')||' ADD '||oa.name||
	' '||(select dt.name from cht_datatype dt where dt.id = oa.dbdatatypeid)||
	case when phys_dt.name ilike 'varchar' and oa.length is null then '(255)'
		when phys_dt.name ilike 'varchar' and oa.length is not null then '('||oa.length||')'
			else '' end||
	 ' NULL'  as sql
	from cht_datatype phys_dt inner join SYS_OBJECT_Attributes oa on phys_dt.id = oa.dbdatatypeId 
			inner join SYS_OBJECT_Definition od on oa.ObjectDefinitionID=od.id
	where not exists(select *
		from information_schema.tables o inner join information_schema.columns a on
			(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
		where o.table_name iLike replace(COALESCE(oa.inTable,od.TableName),'dbo.','') 
		    and a.column_name iLike oa.name
		    and o.table_schema ilike current_schema()
		    and o.table_type='BASE TABLE')
	    and upper(replace(COALESCE(COALESCE(oa.inTable,od.TableName),''),'dbo.',''))!='CIS_EDGES'
	    and upper(replace(COALESCE(COALESCE(oa.inTable,od.TableName),''),'dbo.',''))!='CIS_NODES'
	    and od.ParentObjectID=_SchemaID and (select case when _objectid = 0 then true else od.id = _objectid end);


	DropTableColumns cursor is 
        select 'ALTER TABLE '||tbl.table_name||' DROP COLUMN '||col.column_name
        FROM information_schema.tables tbl 
        inner join information_schema.columns col on(tbl.table_catalog=col.table_catalog and tbl.table_schema=col.table_schema and tbl.table_name=col.table_name)
        where not exists(select *
                from cht_datatype phys_dt inner join SYS_OBJECT_Attributes oa on phys_dt.id = oa.dbdatatypeId
				inner join SYS_OBJECT_Definition od on oa.ObjectDefinitionID=od.id
                where tbl.table_name  iLike  replace(COALESCE(oa.inTable,od.TableName),'dbo.','') 
                        and col.column_name  iLike oa.name 
                        and replace(COALESCE(oa.inTable,od.TableName),'dbo.','')!='CIS_EDGES'
                        and od.ParentObjectID=_SchemaID and (select case when _objectid = 0 then true else od.id = _objectid end))
              and lower(col.column_name) not in ('id','lon','lat','precision') 
              and lower(tbl.table_name) in (select lower(replace(COALESCE(aa.inTable,tt.TableName),'dbo.',''))
                    from SYS_OBJECT_Definition tt inner join SYS_OBJECT_Attributes aa on aa.ObjectDefinitionID=tt.id
                    where tt.ParentObjectID=_SchemaID and (select case when _objectid = 0 then true else tt.id = _objectid end)
                    and replace(tt.TableName,'dbo.','')!='CIS_EDGES') 
              and upper(tbl.table_name)!='CIS_EDGES'
                            and lower(tbl.table_name) in (select lower(replace(COALESCE(aa.inTable,tt.TableName),'dbo.',''))
                    from SYS_OBJECT_Definition tt inner join SYS_OBJECT_Attributes aa on aa.ObjectDefinitionID=tt.id
                    where tt.ParentObjectID=_SchemaID and (select case when _objectid = 0 then true else tt.id = _objectid end)
                    and replace(tt.TableName,'dbo.','')!='CIS_NODES') 
              and upper(tbl.table_name)!='CIS_NODES';


    AlterColumnTypes cursor is 
	select distinct COALESCE(oa.inTable,od.TableName), oa.name, (select dt.name from cht_datatype dt where dt.id = oa.dbdatatypeid)||
		case when phys_dt.name ilike 'varchar' and oa.length is null then '(255)'
			when phys_dt.name ilike 'varchar' and oa.length is not null then '('||oa.length||')'
				else '' end  as datatype, col.udt_name
	from cht_datatype phys_dt inner join SYS_OBJECT_Attributes oa on phys_dt.id = oa.dbdatatypeId 
	inner join SYS_OBJECT_Definition od on oa.ObjectDefinitionID=od.id
	inner join information_schema.tables tbl on (tbl.table_name iLike replace(COALESCE(oa.inTable,od.TableName),'dbo.','') 
		    and tbl.table_schema ilike current_schema() and tbl.table_type='BASE TABLE')
	inner join information_schema.columns col on (tbl.table_catalog=col.table_catalog and tbl.table_schema=col.table_schema and tbl.table_name=col.table_name)
	where col.column_name iLike oa.name and lower(col.column_name) != 'id'
	    and not(
			(col.udt_name like 'varchar' and phys_dt.name like 'varchar' and 
				((oa.length = col.character_maximum_length) or (oa.length is null)))
			or
			(col.udt_name like 'int4' and phys_dt.name like 'integer')
			or
			(col.udt_name like 'float8' and phys_dt.name like 'float')
			or 
			(col.udt_name ilike phys_dt.name)
		)
	    and upper(replace(COALESCE(COALESCE(oa.inTable,od.TableName),''),'dbo.',''))!='CIS_EDGES'
	    and upper(replace(COALESCE(COALESCE(oa.inTable,od.TableName),''),'dbo.',''))!='CIS_NODES'
	    and od.ParentObjectID=_SchemaID and (select case when _objectid = 0 then true else od.id = _objectid end);
       
BEGIN
     raise info 'Start process...';
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
            raise info '%', _SQL;
            execute _SQL;
            fetch next from NewTables
            into _SQL;
        end loop;
        close NewTables;

        open DropTableColumns;

        fetch next from DropTableColumns
        into _SQL;

        while FOUND loop
	    raise info '%', _SQL;
            execute _SQL;
            fetch next from DropTableColumns
            into _SQL;
        end loop;
        close DropTableColumns;

        open NewTableColumns;

        fetch next from NewTableColumns
        into _SQL;

        while FOUND loop
	    raise info '%', _SQL;
            execute _SQL;
            fetch next from NewTableColumns
            into _SQL;
        end loop;
        close NewTableColumns;

	raise info 'Start alter columns...';
        open AlterColumnTypes;	
        fetch next from AlterColumnTypes
        into _tablename, _columnname, _datatype, _olddatatype;

        while FOUND loop
		begin
			_SQL := 'ALTER TABLE '||_tablename||' ALTER COLUMN '||_columnname||' TYPE '||_datatype||' USING '||_columnname||'::'||_datatype;
			raise info '%', _SQL;
			execute _SQL;
		exception
		when OTHERS then
			SELECT COALESCE(MAX(errorid),0)+1 INTO _errorid FROM SYS_USER_DATA_ERROR;
			_SQL := 'INSERT INTO SYS_USER_DATA_ERROR (errorid, tablename, columnname, olddatatype, newdatatype, objectid, value, error, errtime)
				SELECT '||_errorid||', '''||_tablename||''', '''||_columnname||''', '''||_olddatatype||''', '''||_datatype||''',
				id, '||_columnname||'::text, '''||SQLERRM||''', now() FROM '||_tablename;
			raise info '%',_SQL;
			execute _SQL;

			_SQL := 'ALTER TABLE '||_tablename||' DROP COLUMN '||_columnname;
			raise info '%',_SQL;
			execute _SQL;

			_SQL := 'ALTER TABLE '||_tablename||' ADD COLUMN '||_columnname||' '||_datatype||' NULL';
			raise info '%',_SQL;
			execute _SQL;
		end;
            fetch next from AlterColumnTypes
            into _tablename, _columnname, _datatype;
        end loop;
        close AlterColumnTypes;

        _timeEnd=now();
	insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Message,Parent)
	values(nextval('seq_sys_log'),'appendSchemaTables',_timeStart,_timeEnd,_timeEnd-_timeStart,'OK',_Parent);
    exception
	    when OTHERS then
		_timeEnd:=now();
		insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Message,Parent)
		values(nextval('seq_sys_log'),'appendSchemaTables',_timeStart,_timeEnd,_timeEnd-_timeStart,'ERROR: '||SQLERRM,_Parent);
    end;
    return 0;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;

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
