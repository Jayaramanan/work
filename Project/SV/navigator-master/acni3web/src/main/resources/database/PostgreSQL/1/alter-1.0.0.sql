--
-- PostgreSQL database dump
--

-- Started on 2010-02-02 14:43:54

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 549 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

--drop procedural language if exists 'plpgsql';

--CREATE PROCEDURAL LANGUAGE plpgsql;

--ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

--SET search_path = "$user", pg_catalog;

--
-- TOC entry 19 (class 1255 OID 85487)
-- Dependencies: 6 549
-- Name: appendnodemetaphor(bigint, boolean); Type: FUNCTION; Owner: sa
--

CREATE FUNCTION appendnodemetaphor(_schemaid bigint, _deletecolumns boolean) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
                where  o.table_type='BASE TABLE'
                	and o.table_schema ilike current_schema()
                    and lower(o.table_name)='sys_nodemetaphor'
                    and lower(a.column_name)=lower(t.name))
            and InMetaphor=1
            and d.ParentObjectID=_SchemaID;
            
	DropTableColumns cursor is 
                select 'ALTER TABLE SYS_NodeMetaphor DROP COLUMN '||a.column_name
                from information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
                where lower(a.column_name) not in('id','nodeid','iconid','iconname','iconpath','schemaid','priority','metaphorset','description')
                      and lower(o.table_name)='sys_nodemetaphor' 
                      and o.table_schema ilike current_schema()
                      and not exists(select * from SYS_OBJECT_Attributes t where t.Name~*a.column_name and t.InMetaphor=1);

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
$$;


ALTER FUNCTION appendnodemetaphor(_schemaid bigint, _deletecolumns boolean) OWNER TO sa;

--
-- TOC entry 20 (class 1255 OID 85488)
-- Dependencies: 549 6
-- Name: appendschematables(bigint, bigint); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION appendschematables(_schemaid bigint, _objectid bigint) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
            and TableName is not null
            and a.ParentObjectID=_SchemaID;

	NewTableColumns cursor is 
	select distinct 'ALTER TABLE '||replace(COALESCE(t.inTable,d.TableName),'dbo.','')||' ADD '||t.name||case when t.predefined=1 or t.datatypeid=2 then ' numeric NULL' else  ' varchar(255) NULL' end
	from SYS_OBJECT_Attributes t inner join SYS_OBJECT_Definition d on t.ObjectDefinitionID=d.id
        where not exists(select *
                from information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
                where o.table_name iLike replace(COALESCE(t.inTable,d.TableName),'dbo.','') 
                    and a.column_name iLike t.name
                    and o.table_schema ilike current_schema()
                    and o.table_type='BASE TABLE')
            and upper(replace(COALESCE(COALESCE(t.inTable,d.TableName),''),'dbo.',''))!='CIS_EDGES'
            and d.ParentObjectID=_SchemaID;

	DropTableColumns cursor is 
        select 'ALTER TABLE '||o.table_name||' DROP COLUMN '||a.column_name
        FROM information_schema.tables o inner join information_schema.columns a on(o.table_catalog=a.table_catalog and o.table_schema=a.table_schema and o.table_name=a.table_name)
        where not exists(select *
                from SYS_OBJECT_Attributes t inner join SYS_OBJECT_Definition d on t.ObjectDefinitionID=d.id
                where o.table_name  iLike  replace(COALESCE(t.inTable,d.TableName),'dbo.','') 
                        and a.column_name  iLike t.name
                        and replace(COALESCE(t.inTable,d.TableName),'dbo.','')!='CIS_EDGES'
                        and d.ParentObjectID=_SchemaID)
              and lower(a.column_name) not in ('id','lon','lat','precision')
              and lower(o.table_name) in(select lower(replace(COALESCE(aa.inTable,tt.TableName),'dbo.',''))
                    from SYS_OBJECT_Definition tt inner join SYS_OBJECT_Attributes aa on aa.ObjectDefinitionID=tt.id
                    where tt.ParentObjectID=_SchemaID and  replace(tt.TableName,'dbo.','')!='CIS_EDGES')
                    and upper(o.table_name)!='CIS_EDGES';
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

        open NewTableColumns;

        fetch next from NewTableColumns
        into _SQL;

        while FOUND loop
            execute _SQL;
            fetch next from NewTableColumns
            into _SQL;
        end loop;
        close NewTableColumns;

        open DropTableColumns;

        fetch next from DropTableColumns
        into _SQL;

        while FOUND loop
            execute _SQL;
            fetch next from DropTableColumns
            into _SQL;
        end loop;
        close DropTableColumns;

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
$$;


ALTER FUNCTION appendschematables(_schemaid bigint, _objectid bigint) OWNER TO sa;

--
-- TOC entry 22 (class 1255 OID 85489)
-- Dependencies: 549 6
-- Name: createuser(integer, character varying, character varying, character varying, character varying, character varying, integer); Type: FUNCTION;  Owner: postgres
--

CREATE FUNCTION createuser(iid integer, ifirstname character varying, ilastname character varying, iuser character varying, ipassword character varying, isid character varying, iuserastemplate integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
    DECLARE controlID integer;
        itimeStart timestamp;
        itimeEnd timestamp;
        iSQL text;
        ierrormsg character varying(4000);
        iParent integer;
begin
    itimeStart:=now();
    --select nextval('seq_sys_log') into iParent;
    iParent:=nextval('seq_sys_log');
    insert into sys_log(id,Name,TimeStart,Message)
    values(iParent,'createUser',itimeStart,'START');
    begin
	    iSQL:='insert into sys_user(ID,FirstName,LastName,UserName,Password,SID) values ('||iID||','''||iFirstName||''','''||iLastName||''','''||iuser||''','''||iPassword||''','''||iSID||''')';
	    execute iSQL;
	    iSQL:='insert into SYS_USER_SETTINGS(UserID,Section,Prop,Value)
		       select '||iID||',Section,Prop,Value 
		       from SYS_USER_SETTINGS where UserID='||iuserAsTemplate;
	    execute iSQL;
	    iSQL:='update SYS_USER_SETTINGS set value=0 where prop=''Default graph'' and UserID='||iID||
		  '; update SYS_USER_SETTINGS set value='''' where prop=''initial_search'' and UserID='||iID;
	    execute iSQL;
	    iSQL:='insert into SYS_USER_GROUP(UserID,GroupID)
		       select '||iID||',GroupID 
		       from SYS_USER_GROUP where UserID='||iuserAsTemplate;
	    execute iSQL;
	    itimeEnd:=now();
	    insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Parent,message)
	    values(nextval('seq_sys_log'),'createUser',itimeStart,itimeEnd,itimeEnd-itimeStart,iParent,'OK');
    exception
	    when OTHERS then
		    itimeEnd:=now();
		    insert into sys_log(id,Name,TimeStart,TimeEnd,ExcTime,Parent,command,message)
		    values(nextval('seq_sys_log'),'createUser',itimeStart,itimeEnd,itimeEnd-itimeStart,iParent,iSQL,'error');
    end;
	
end;
$$;


ALTER FUNCTION createuser(iid integer, ifirstname character varying, ilastname character varying, iuser character varying, ipassword character varying, isid character varying, iuserastemplate integer) OWNER TO postgres;

--
-- TOC entry 23 (class 1255 OID 85490)
-- Dependencies: 6 549
-- Name: createuser(integer, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION;  Owner: postgres
--

CREATE FUNCTION createuser(iid integer, ifirstname character varying, ilastname character varying, iusername character varying, ipassword character varying, isid character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
   execute createUser(iID,iFirstName,iLastName,iUserName,iPassword,iSID,1);
end;
$$;


ALTER FUNCTION createuser(iid integer, ifirstname character varying, ilastname character varying, iusername character varying, ipassword character varying, isid character varying) OWNER TO postgres;

--
-- TOC entry 24 (class 1255 OID 85491)
-- Dependencies: 549 6
-- Name: deleteobject(integer, integer); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION deleteobject(iid integer, iuserid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
declare itStamp timestamp;
BEGIN
    itStamp:=now();

    INSERT INTO HST_CIS_OBJECTS(tStamp,ID,ObjectType,UserID)
    select itStamp,ID,ObjectType,UserID
    from CIS_OBJECTS
    where ID=iID;

	/*
	    INSERT INTO HST_CIS_OBJECTS(tStamp,ID,ObjectType,UserID)
	    values(itStamp,iID,0,iUserID);
	*/

    delete from CIS_OBJECTS
    where ID=iID;

    return 0;
    
END;
$$;


ALTER FUNCTION deleteobject(iid integer, iuserid integer) OWNER TO sa;

--
-- TOC entry 25 (class 1255 OID 85492)
-- Dependencies: 6 549
-- Name: ftrg_objects(); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION ftrg_objects() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN

        -- Work out the increment/decrement amount(s).
        IF (TG_OP = 'DELETE') THEN
		delete from cis_objects where id=OLD.id;
	/*
        ELSIF (TG_OP = 'INSERT') THEN
	    if (TG_TABLE_NAME='cis_nodes') then
		insert into CIS_OBJECTS(id,ObjectType,userid)
		values(NEW.id,NEW.NodeType,0);
	    else
		insert into CIS_OBJECTS(id,ObjectType,userid)
		values(NEW.id,NEW.EdgeType,NEW.User);
	    end if;
	*/
        END IF;
        RETURN NULL;
    END;
$$;


ALTER FUNCTION ftrg_objects() OWNER TO sa;

--
-- TOC entry 26 (class 1255 OID 85493)
-- Dependencies: 6 549
-- Name: getchart(integer, integer, text, text); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION getchart(_objectid integer, _chartid integer, _nodes text, _filteredoutattributes text) RETURNS text
    LANGUAGE plpgsql
    AS $$
declare _attributeID integer;
	_SQL text;
	_mSelect text;
	_mFrom text;
	_mWhere text;
	_inSQL text;
	mFilteredOutAttributes text;
	attributeSet cursor(mChartID integer,_mSet text) is
		select a.id
		from cis_chart_Attributes a
		where a.chartid=mChartID
			and position(','||a.predefinedID||',' IN ','||_mSet||',')=0;
BEGIN
	if _FilteredOutAttributes='' then
		mFilteredOutAttributes:='0';
	else
		mFilteredOutAttributes:=_FilteredOutAttributes;
	end if;

	_inSQL:='select COALESCE(sum(value),0) from CIS_CHART c where c.NodeID=n.id and c.ChartAttributeID in(select id from CIS_CHART_Attributes tt where cast(tt.predefinedID as integer) not in('||mFilteredOutAttributes||') and tt.ChartID='||_ChartID||')';
	_mSelect:='select n.id, COALESCE(cast(((case when('||_inSQL||')<ch.minvalue then ch.minvalue when('||_inSQL||')>ch.maxvalue then ch.maxvalue else('||_inSQL||') end)-ch.minvalue)/(ch.maxvalue-ch.minvalue)*(ch.maxscale-ch.minscale)+ch.minscale AS integer),ch.minvalue), ch.rgb,ch.LabelInUse,ch.LabelFontSize,ch.NumberFormat ';
	_mFrom :=' from CIS_NODES n,SYS_OBJECT_Chart ch';
	_mWhere:=' where n.ID in ('||_Nodes||') and n.NodeType=ch.ObjectID and ch.ChartID='||_ChartID;
	_mWhere:=_mWhere||' and exists(select nodeid from cis_chart cc, CIS_CHART_Attributes an where cc.NodeID=n.id and an.id=cc.ChartAttributeID and an.ChartID='||_ChartID||')';
			  
	if _ObjectID>0 then
		_mWhere:=_mWhere||' and n.NodeType='||_ObjectID;
	end if;

	open attributeSet(_ChartID,mFilteredOutAttributes);

	fetch attributeSet
	into _attributeID;

	while FOUND loop
		_mSelect := _mSelect||',COALESCE((select c.Value from CIS_CHART c where c.NodeID=n.id and c.ChartAttributeID='||_attributeID||'),''0''),
		       COALESCE((select a.RGB from CIS_CHART c, CIS_CHART_Attributes a where a.ID='||_attributeID||' and c.NodeID=n.id and c.ChartAttributeID=a.id),''0'')';
		fetch attributeSet
		into _attributeID;
	end loop;

	_SQL:=_mSelect||_mFrom||_mWhere;

	close attributeSet;
	--insert into ZZZ_TEST(vc1) values (_SQL)

	return _SQL;
END;
$$;


ALTER FUNCTION getchart(_objectid integer, _chartid integer, _nodes text, _filteredoutattributes text) OWNER TO sa;

--
-- TOC entry 21 (class 1255 OID 85494)
-- Dependencies: 549 6
-- Name: getmetaphor(character varying, integer); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION getmetaphor(_metaphorset character varying, _nodeid integer) RETURNS text
    LANGUAGE plpgsql
    AS $$
declare _SQL text;
	_mSelect text;
	_mFrom text;
	_mWhere text;
	_mCase text;
	_mnSelect text;
	_mnFrom text;
	_mnWhere text;
	--_mnCase text;
	_currObjectID integer;
	_objectID integer;
	_columnName varchar(100);
	_tableName varchar(100);
	_firstRun smallint;
	attributeSet cursor(_mSet varchar(100)) is  
	select a.ObjectDefinitionID,a.name,o.TableName
	from SYS_OBJECT_Attributes a,SYS_OBJECT_Definition o
	where a.ObjectDefinitionID in(select distinct t.NodeID
					  from SYS_NodeMetaphor t
					  where t.MetaphorSet=_mSet)
		  and a.InMetaphor=1
		  and o.id=a.ObjectDefinitionID
	order by ObjectDefinitionID,a.name;
BEGIN

	_currObjectID:=-1;
	_firstRun:=1;
	open attributeSet(_metaphorSet);

	fetch attributeSet
	into _objectID,_columnName,_tableName;

	WHILE FOUND loop
		if _currObjectID!=_ObjectID then
			if _currObjectID>0 then
				if _firstRun = 1 then
					if _nodeID=0 then 
					   _SQL:=_SQL||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||')'; --||_mnCase
					else
					   _SQL:=_SQL||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||') and n.id='||_nodeID;--||_mnCase 
					end if;           
					_firstRun:=0;
				else
					if _nodeID=0 then
					   _SQL:=_SQL||' union '||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||')';--||_mnCase
					else
					   _SQL:=_SQL||' union '||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||') and n.id='||_nodeID;--||_mnCase
					end if;
				end if;
			else
				_SQL:='';
			end if;	
			_mSelect:='select c.id, COALESCE(n.IconName,COALESCE(m.IconName,''all.png'')),COALESCE(m.Priority,1) ';
			_mFrom:='from SYS_NodeMetaphor m,CIS_NODES n,'||_tableName || ' c ';
			_mWhere:= 'where m.NodeID='||_objectID||' and MetaphorSet='''||_metaphorSet||''' and n.NodeType=m.NodeID and n.ID=c.ID ' ||
					' and COALESCE(c.'||_columnName||',0)=case when m.'||_columnName||' is null then COALESCE(c.'||_columnName||',0) else cast(m.'||_columnName||' as integer) end ';
			_mCase:='and case when m.'||_columnName||' is null then 1 else 0 end ';
			_mnSelect:='select min(case when mn.'||_columnName||' is null then 1 else 0 end ';
			_mnFrom:='from SYS_NodeMetaphor mn,CIS_NODES nn,'||_tableName || ' cn ';
			_mnWhere:='where mn.NodeID='||_objectID||' and MetaphorSet='''||_metaphorSet||''' and nn.NodeType=mn.NodeID and nn.ID=cn.ID and c.id=cn.id ' ||
					' and COALESCE(cn.'||_columnName||',0)=case when mn.'||_columnName||' is null then COALESCE(cn.'||_columnName||',0) else cast(mn.'||_columnName||' as integer) end ';
			--_mnCase:='case when mn.'||_columnName||' is null then 1 else 0 end ';
			_currObjectID:=_ObjectID;
		else 
			_mWhere:= _mWhere||'and COALESCE(c.'||_columnName||',0)=case when m.'||_columnName||' is null then COALESCE(c.'||_columnName||',0) else cast(m.'||_columnName||' as integer) end ';
			_mCase:=_mCase||'+ case when m.'||_columnName||' is null then 1 else 0 end ';
			_mnSelect:=_mnSelect || '+ case when mn.'||_columnName||' is null then 1 else 0 end ';
			_mnWhere:=_mnWhere||'and COALESCE(cn.'||_columnName||',0)=case when mn.'||_columnName||' is null then COALESCE(cn.'||_columnName||',0) else cast(mn.'||_columnName||' as integer) end  ';
			--_mnCase:=_mnCase||'case when mn.'||_columnName||' is null then 1 else 0 end '
		end if;
		fetch attributeSet
		into _objectID,_columnName,_tableName;
	END loop;
	if _firstRun = 1 then 
		 if _nodeID=0 then 
		    _SQL:=_SQL||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||')';--||_mnCase
		 else
		    _SQL:=_SQL||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||') and n.id='||_nodeID;--||_mnCase            
		 end if;
	else
		 if _nodeID=0 then
		    _SQL:=_SQL||' union '||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||')';--||_mnCase
		 else
		    _SQL:=_SQL||' union '||_mSelect||_mFrom||_mWhere||_mCase||' = ('||_mnSelect||')'||_mnFrom||_mnWhere||') and n.id='||_nodeID;--||_mnCase
		 end if;
	end if;
	close attributeSet;

	--insert into ZZZ_TEST(Usage,tempAdate,maxA,maxB)
	--values('getMetaphor',getdate(),_SQL);
	--values('getMetaphor',getdate(),_SQL,convert(varchar(20),_nodeID));

	return _SQL;
END;
$$;


ALTER FUNCTION getmetaphor(_metaphorset character varying, _nodeid integer) OWNER TO sa;

--
-- TOC entry 27 (class 1255 OID 85495)
-- Dependencies: 549 6
-- Name: getmetaphor(character varying); Type: FUNCTION;  Owner: postgres
--

CREATE FUNCTION getmetaphor(_metaphorset character varying) RETURNS text
    LANGUAGE plpgsql
    AS $$
declare _SQL text;
BEGIN
	select getMetaphor(_metaphorSet,0) into _SQL;
	return _SQL;
END;
$$;


ALTER FUNCTION getmetaphor(_metaphorset character varying) OWNER TO postgres;

--
-- TOC entry 28 (class 1255 OID 85496)
-- Dependencies: 549 6
-- Name: sp_deleteobject(integer, integer); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION sp_deleteobject(iid integer, iuserid integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare itStamp timestamp;
BEGIN
    itStamp:=now();

    INSERT INTO HST_CIS_OBJECTS(tStamp,ID,ObjectType,UserID)
    select itStamp,ID,ObjectType,UserID
    from CIS_OBJECTS
    where ID=iID;

	/*
	    INSERT INTO HST_CIS_OBJECTS(tStamp,ID,ObjectType,UserID)
	    values(itStamp,iID,0,iUserID);
	*/

    delete from CIS_OBJECTS
    where ID=iID;

    return;
    
END;
$$;


ALTER FUNCTION sp_deleteobject(iid integer, iuserid integer) OWNER TO sa;

--
-- TOC entry 29 (class 1255 OID 85497)
-- Dependencies: 549 6
-- Name: sp_getseq(character varying); Type: FUNCTION;  Owner: sa
--

CREATE FUNCTION sp_getseq(_seqname character varying) RETURNS bigint
    LANGUAGE plpgsql
    AS $$
declare seq bigint;
begin

	update sys_sequence
	set seqno=(select greatest(nextval('seq_ObjectCount'),seqno+1) from sys_sequence where name='ObjectCount')
	where name='ObjectCount';
    
	select into seq seqno
	from sys_sequence
	where name='ObjectCount';

	return seq;
	
end;
$$;


ALTER FUNCTION sp_getseq(_seqname character varying) OWNER TO sa;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1736 (class 1259 OID 85537)
-- Dependencies: 6
-- Name: cht_attribute_type; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_attribute_type (
    id integer NOT NULL,
    typename character varying(50),
    system integer
);


ALTER TABLE cht_attribute_type OWNER TO sa;

--
-- TOC entry 1737 (class 1259 OID 85540)
-- Dependencies: 6
-- Name: cht_attribute_usage; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_attribute_usage (
    id integer NOT NULL,
    usagename character varying(50),
    usage character varying(200)
);


ALTER TABLE cht_attribute_usage OWNER TO sa;

--
-- TOC entry 1738 (class 1259 OID 85543)
-- Dependencies: 6
-- Name: cht_attribute_visibility; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_attribute_visibility (
    id integer NOT NULL,
    label character varying(30)
);


ALTER TABLE cht_attribute_visibility OWNER TO sa;

--
-- TOC entry 1739 (class 1259 OID 85546)
-- Dependencies: 6
-- Name: seq_cht_chart; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_cht_chart
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_cht_chart OWNER TO sa;

--
-- TOC entry 2473 (class 0 OID 0)
-- Dependencies: 1739
-- Name: seq_cht_chart; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_cht_chart', 36, true);


--
-- TOC entry 1740 (class 1259 OID 85548)
-- Dependencies: 2123 6
-- Name: cht_chart; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_chart (
    id integer DEFAULT nextval('seq_cht_chart'::regclass) NOT NULL,
    name character varying(50),
    comment character varying(255),
    schemaid integer
);


ALTER TABLE cht_chart OWNER TO sa;

--
-- TOC entry 1741 (class 1259 OID 85552)
-- Dependencies: 6
-- Name: cht_chart_type; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_chart_type (
    id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE cht_chart_type OWNER TO sa;

--
-- TOC entry 1742 (class 1259 OID 85555)
-- Dependencies: 6
-- Name: cht_color; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_color (
    id integer NOT NULL,
    rgb character varying(7),
    picture text,
    filepath character varying(500),
    color character varying(50)
);


ALTER TABLE cht_color OWNER TO sa;

--
-- TOC entry 1743 (class 1259 OID 85561)
-- Dependencies: 6
-- Name: cht_connection_type; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_connection_type (
    id integer NOT NULL,
    label character varying(50),
    oldid integer
);


ALTER TABLE cht_connection_type OWNER TO sa;

--
-- TOC entry 1744 (class 1259 OID 85564)
-- Dependencies: 6
-- Name: cht_datatype; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_datatype (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE cht_datatype OWNER TO sa;

--
-- TOC entry 1745 (class 1259 OID 85567)
-- Dependencies: 6
-- Name: cht_display_operation; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_display_operation (
    id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE cht_display_operation OWNER TO sa;

--
-- TOC entry 1746 (class 1259 OID 85570)
-- Dependencies: 6
-- Name: cht_icons_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE cht_icons_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE cht_icons_id_seq OWNER TO sa;

--
-- TOC entry 2474 (class 0 OID 0)
-- Dependencies: 1746
-- Name: cht_icons_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('cht_icons_id_seq', 1, true);


--
-- TOC entry 1747 (class 1259 OID 85572)
-- Dependencies: 2124 6
-- Name: cht_icons; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_icons (
    id integer DEFAULT nextval('cht_icons_id_seq'::regclass) NOT NULL,
    iconname character varying(100) NOT NULL,
    icon bytea
);


ALTER TABLE cht_icons OWNER TO sa;

--
-- TOC entry 1748 (class 1259 OID 85579)
-- Dependencies: 6
-- Name: seq_cht_language; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_cht_language
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_cht_language OWNER TO sa;

--
-- TOC entry 2475 (class 0 OID 0)
-- Dependencies: 1748
-- Name: seq_cht_language; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_cht_language', 5, true);


--
-- TOC entry 1749 (class 1259 OID 85581)
-- Dependencies: 2125 6
-- Name: cht_language; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_language (
    id integer DEFAULT nextval('seq_cht_language'::regclass) NOT NULL,
    language character varying(50)
);


ALTER TABLE cht_language OWNER TO sa;

--
-- TOC entry 1750 (class 1259 OID 85585)
-- Dependencies: 6
-- Name: cht_line_style; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_line_style (
    id integer NOT NULL,
    style character varying(50),
    picture text,
    filepath character varying(500)
);


ALTER TABLE cht_line_style OWNER TO sa;

--
-- TOC entry 1751 (class 1259 OID 85591)
-- Dependencies: 6
-- Name: cht_line_weight; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_line_weight (
    id integer NOT NULL,
    label character varying(50),
    picture text,
    filepath character varying(500),
    width numeric(5,2) NOT NULL
);


ALTER TABLE cht_line_weight OWNER TO sa;

--
-- TOC entry 1752 (class 1259 OID 85597)
-- Dependencies: 6
-- Name: cht_metaphorimage; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_metaphorimage (
    metaphor character varying(255),
    description character varying(255),
    tooltip character varying(255)
);


ALTER TABLE cht_metaphorimage OWNER TO sa;

--
-- TOC entry 1753 (class 1259 OID 85603)
-- Dependencies: 6
-- Name: cht_object_type; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_object_type (
    id integer NOT NULL,
    name character varying(50),
    description text
);


ALTER TABLE cht_object_type OWNER TO sa;

--
-- TOC entry 1754 (class 1259 OID 85609)
-- Dependencies: 6
-- Name: cht_pi_type; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_pi_type (
    id character varying(255),
    type character varying(255)
);


ALTER TABLE cht_pi_type OWNER TO sa;

--
-- TOC entry 1755 (class 1259 OID 85615)
-- Dependencies: 6
-- Name: cht_predefinedattributes_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE cht_predefinedattributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE cht_predefinedattributes_id_seq OWNER TO sa;

--
-- TOC entry 2476 (class 0 OID 0)
-- Dependencies: 1755
-- Name: cht_predefinedattributes_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('cht_predefinedattributes_id_seq', 1352, true);


--
-- TOC entry 1756 (class 1259 OID 85617)
-- Dependencies: 2126 2127 6
-- Name: cht_predefinedattributes; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_predefinedattributes (
    id integer DEFAULT nextval('cht_predefinedattributes_id_seq'::regclass) NOT NULL,
    languageid integer DEFAULT 1 NOT NULL,
    attributeid integer,
    value character varying(255),
    label character varying(255),
    inuse integer,
    touse integer,
    sort integer,
    parent integer,
    srcid character varying(255),
    halocolor character varying(15)
);


ALTER TABLE cht_predefinedattributes OWNER TO sa;

--
-- TOC entry 1757 (class 1259 OID 85631)
-- Dependencies: 6
-- Name: seq_cht_role; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_cht_role
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_cht_role OWNER TO sa;

--
-- TOC entry 2477 (class 0 OID 0)
-- Dependencies: 1757
-- Name: seq_cht_role; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_cht_role', 7, true);


--
-- TOC entry 1758 (class 1259 OID 85633)
-- Dependencies: 2128 6
-- Name: cht_role; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_role (
    id integer DEFAULT nextval('seq_cht_role'::regclass) NOT NULL,
    name character varying(255) NOT NULL,
    weight numeric(10,4)
);


ALTER TABLE cht_role OWNER TO sa;

--
-- TOC entry 1759 (class 1259 OID 85637)
-- Dependencies: 6
-- Name: cht_symbol; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_symbol (
    id integer NOT NULL,
    label character varying(50),
    picture text,
    filepath character varying(500)
);


ALTER TABLE cht_symbol OWNER TO sa;

--
-- TOC entry 1760 (class 1259 OID 85643)
-- Dependencies: 6
-- Name: cht_territory_type; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_territory_type (
    id integer NOT NULL,
    name character varying(50),
    territorylevel integer
);


ALTER TABLE cht_territory_type OWNER TO sa;

--
-- TOC entry 1761 (class 1259 OID 85646)
-- Dependencies: 6
-- Name: cht_xml_attribute; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_xml_attribute (
    id integer NOT NULL,
    attributename character varying(50)
);


ALTER TABLE cht_xml_attribute OWNER TO sa;

--
-- TOC entry 1762 (class 1259 OID 85649)
-- Dependencies: 6
-- Name: cht_xml_doc; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_xml_doc (
    id integer NOT NULL,
    docname character varying(50)
);


ALTER TABLE cht_xml_doc OWNER TO sa;

--
-- TOC entry 1763 (class 1259 OID 85652)
-- Dependencies: 6
-- Name: cht_xml_element; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cht_xml_element (
    id integer NOT NULL,
    elementname character varying(50)
);


ALTER TABLE cht_xml_element OWNER TO sa;

--
-- TOC entry 1764 (class 1259 OID 85655)
-- Dependencies: 6
-- Name: cis_changes; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_changes (
    chronology integer,
    nodefrom integer,
    nodeto integer,
    connectionchange text,
    stengthchange text
);


ALTER TABLE cis_changes OWNER TO sa;

--
-- TOC entry 1765 (class 1259 OID 85661)
-- Dependencies: 6
-- Name: cis_chart; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_chart (
    nodeid integer NOT NULL,
    chartattributeid integer NOT NULL,
    value numeric(12,4)
);


ALTER TABLE cis_chart OWNER TO sa;

--
-- TOC entry 1766 (class 1259 OID 85664)
-- Dependencies: 6
-- Name: cis_chart_attributes; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_chart_attributes (
    id integer NOT NULL,
    chartid integer,
    colorid integer,
    rgb character varying(7),
    predefinedid character varying(255),
    name character varying(100),
    description character varying(255)
);


ALTER TABLE cis_chart_attributes OWNER TO sa;

--
-- TOC entry 1767 (class 1259 OID 85670)
-- Dependencies: 6
-- Name: cis_chronology; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_chronology (
    id integer NOT NULL,
    chronologydate timestamp without time zone,
    note text,
    sqlprocedure text,
    processed integer,
    createdby integer,
    authorisedby integer,
    dateauthorised timestamp without time zone
);


ALTER TABLE cis_chronology OWNER TO sa;

--
-- TOC entry 1768 (class 1259 OID 85676)
-- Dependencies: 6
-- Name: cis_edges; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_edges (
    id integer NOT NULL,
    edgetype integer,
    fromid integer NOT NULL,
    toid integer NOT NULL,
    userid integer,
    inscoring integer,
    fromsrcid character varying(255),
    tosrcid character varying(255),
    directed integer,
    add3 integer,
    comment integer,
    add2 integer,
    add1 integer,
    add0 integer,
    strength numeric,
    add4 integer,
    add5 integer,
    locked numeric,
    inpath numeric,
    connectiontype integer,
    add6 integer
);


ALTER TABLE cis_edges OWNER TO sa;

--
-- TOC entry 1769 (class 1259 OID 85682)
-- Dependencies: 6
-- Name: cis_edges_scope; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_edges_scope (
    edgeid integer NOT NULL,
    groupid integer NOT NULL,
    flag character(1)
);


ALTER TABLE cis_edges_scope OWNER TO sa;

--
-- TOC entry 1770 (class 1259 OID 85685)
-- Dependencies: 6
-- Name: seq_cis_favorites; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_cis_favorites
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_cis_favorites OWNER TO sa;

--
-- TOC entry 2478 (class 0 OID 0)
-- Dependencies: 1770
-- Name: seq_cis_favorites; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_cis_favorites', 201, true);


--
-- TOC entry 1771 (class 1259 OID 85687)
-- Dependencies: 2129 6
-- Name: cis_favorites; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_favorites (
    id integer DEFAULT nextval('seq_cis_favorites'::regclass) NOT NULL,
    description character varying(255),
    schemaid integer,
    data text,
    layout text
);


ALTER TABLE cis_favorites OWNER TO sa;

--
-- TOC entry 1772 (class 1259 OID 85694)
-- Dependencies: 6
-- Name: seq_cis_function; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_cis_function
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_cis_function OWNER TO sa;

--
-- TOC entry 2479 (class 0 OID 0)
-- Dependencies: 1772
-- Name: seq_cis_function; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_cis_function', 1, true);


--
-- TOC entry 1773 (class 1259 OID 85696)
-- Dependencies: 2130 6
-- Name: cis_function; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_function (
    id integer DEFAULT nextval('seq_cis_function'::regclass) NOT NULL,
    col0 character varying(100),
    col1 character varying(100),
    col2 character varying(100),
    col3 character varying(100),
    col4 character varying(100),
    col5 character varying(100),
    col6 character varying(100),
    col7 character varying(100),
    col8 character varying(100),
    col9 character varying(100),
    col10 character varying(100),
    col11 character varying(100),
    col12 character varying(100),
    col13 character varying(100),
    col14 character varying(100),
    col15 character varying(100),
    col16 character varying(100),
    col17 character varying(100),
    col18 character varying(100),
    col19 character varying(100),
    col20 character varying(100),
    col21 character varying(100),
    col22 character varying(100),
    col23 character varying(100),
    col24 character varying(100),
    col25 character varying(100),
    col26 character varying(100),
    col27 character varying(100)
);


ALTER TABLE cis_function OWNER TO sa;

--
-- TOC entry 1774 (class 1259 OID 85703)
-- Dependencies: 6
-- Name: cis_node_role; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_node_role (
    nodeid integer NOT NULL,
    roleid integer NOT NULL,
    score numeric(15,5)
);


ALTER TABLE cis_node_role OWNER TO sa;

--
-- TOC entry 1775 (class 1259 OID 85706)
-- Dependencies: 6
-- Name: cis_nodes; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_nodes (
    id integer NOT NULL,
    nodetype integer NOT NULL,
    lon numeric(18,8),
    lat numeric(18,8),
    x numeric(15,5),
    y numeric(15,5),
    iconname character varying(255)
);


ALTER TABLE cis_nodes OWNER TO sa;

--
-- TOC entry 1776 (class 1259 OID 85709)
-- Dependencies: 6
-- Name: cis_nodes_scope; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_nodes_scope (
    nodeid integer NOT NULL,
    groupid integer NOT NULL,
    flag character(1)
);


ALTER TABLE cis_nodes_scope OWNER TO sa;

--
-- TOC entry 1777 (class 1259 OID 85712)
-- Dependencies: 6
-- Name: cis_objects; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_objects (
    id integer NOT NULL,
    objecttype integer NOT NULL,
    userid integer,
    locked integer
);


ALTER TABLE cis_objects OWNER TO sa;

--
-- TOC entry 1778 (class 1259 OID 85715)
-- Dependencies: 6
-- Name: cis_pointofinterest; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_pointofinterest (
    id integer NOT NULL,
    col1 integer,
    col2 integer,
    name character varying(255),
    street character varying(255),
    zip character varying(255),
    city character varying(255),
    lon real,
    lat real
);


ALTER TABLE cis_pointofinterest OWNER TO sa;

--
-- TOC entry 1779 (class 1259 OID 85721)
-- Dependencies: 6
-- Name: seq_cis_sub; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_cis_sub
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_cis_sub OWNER TO sa;

--
-- TOC entry 2480 (class 0 OID 0)
-- Dependencies: 1779
-- Name: seq_cis_sub; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_cis_sub', 1, true);


--
-- TOC entry 1780 (class 1259 OID 85723)
-- Dependencies: 2131 6
-- Name: cis_sub; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_sub (
    id integer DEFAULT nextval('seq_cis_sub'::regclass) NOT NULL,
    col0 character varying(100),
    col1 character varying(100),
    col2 character varying(100),
    col3 character varying(100),
    col4 character varying(100),
    col5 character varying(100),
    col6 character varying(100),
    col7 character varying(100),
    col8 character varying(100),
    col9 character varying(100),
    col10 character varying(100),
    col11 character varying(100),
    col12 character varying(100),
    col13 character varying(100),
    col14 character varying(100),
    col15 character varying(100),
    col16 character varying(100),
    col17 character varying(100),
    col18 character varying(100),
    col19 character varying(100),
    col20 character varying(100),
    col21 character varying(100),
    col22 character varying(100),
    col23 character varying(100),
    col24 character varying(100),
    col25 character varying(100),
    col26 character varying(100),
    col27 character varying(100)
);


ALTER TABLE cis_sub OWNER TO sa;

--
-- TOC entry 1781 (class 1259 OID 85730)
-- Dependencies: 6
-- Name: cis_territory; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_territory (
    id integer NOT NULL,
    parentterritoryid integer,
    field1 character varying(100),
    field2 character varying(100),
    field3 character varying(100),
    territorytypeid integer
);


ALTER TABLE cis_territory OWNER TO sa;

--
-- TOC entry 1782 (class 1259 OID 85733)
-- Dependencies: 6
-- Name: cis_user_favorites; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE cis_user_favorites (
    userid integer NOT NULL,
    favoritesid integer NOT NULL,
    layout text
);


ALTER TABLE cis_user_favorites OWNER TO sa;

--
-- TOC entry 1783 (class 1259 OID 85739)
-- Dependencies: 6
-- Name: gis_country; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_country (
    id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE gis_country OWNER TO sa;

--
-- TOC entry 1784 (class 1259 OID 85742)
-- Dependencies: 6
-- Name: gis_file; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_file (
    id integer NOT NULL,
    filename character varying(255),
    layerid integer,
    countryid integer
);


ALTER TABLE gis_file OWNER TO sa;

--
-- TOC entry 1785 (class 1259 OID 85745)
-- Dependencies: 6
-- Name: gis_layer; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_layer (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE gis_layer OWNER TO sa;

--
-- TOC entry 1786 (class 1259 OID 85748)
-- Dependencies: 6
-- Name: gis_layerparams; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_layerparams (
    mapid integer NOT NULL,
    layerid integer NOT NULL,
    layerorder integer NOT NULL,
    description text,
    filled character(1),
    minzoom numeric(12,4),
    maxzoom numeric(12,4),
    textminzoom numeric(12,4),
    textmaxzoom numeric(12,4),
    penwidth numeric(7,2),
    color character varying(15),
    font character varying(50),
    textcolor character varying(15),
    textheight integer,
    symbology character varying(2048),
    labels character varying(2048),
    dispclassmin integer,
    dispclassmax integer
);


ALTER TABLE gis_layerparams OWNER TO sa;

--
-- TOC entry 1787 (class 1259 OID 85754)
-- Dependencies: 6
-- Name: gis_map; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_map (
    id integer NOT NULL,
    name character varying(50),
    background character varying(15),
    edgecolor character varying(15),
    arrowcolor character varying(15)
);


ALTER TABLE gis_map OWNER TO sa;

--
-- TOC entry 1788 (class 1259 OID 85757)
-- Dependencies: 6
-- Name: gis_thematiccoloring; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_thematiccoloring (
    id integer NOT NULL,
    description character varying(255),
    type character varying(1),
    color1 character varying(10),
    color2 character varying(50),
    maxvalue numeric(12,4),
    minvalue numeric(12,4)
);


ALTER TABLE gis_thematiccoloring OWNER TO sa;

--
-- TOC entry 1789 (class 1259 OID 85760)
-- Dependencies: 6
-- Name: gis_thematiccoloringrange; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_thematiccoloringrange (
    id integer NOT NULL,
    thematiccoloringid integer,
    fromvalue numeric(12,4),
    tovalue numeric(12,4),
    color character varying(10)
);


ALTER TABLE gis_thematiccoloringrange OWNER TO sa;

--
-- TOC entry 1790 (class 1259 OID 85763)
-- Dependencies: 6
-- Name: gis_thematicdataset; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_thematicdataset (
    id integer NOT NULL,
    thematicmapid integer,
    description character varying(255),
    sqlquery text,
    thematiccoloringid integer
);


ALTER TABLE gis_thematicdataset OWNER TO sa;

--
-- TOC entry 1791 (class 1259 OID 85769)
-- Dependencies: 6
-- Name: gis_thematicmap; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE gis_thematicmap (
    id integer NOT NULL,
    name character varying(255),
    mapid integer,
    schemaid integer
);


ALTER TABLE gis_thematicmap OWNER TO sa;

--
-- TOC entry 1792 (class 1259 OID 85772)
-- Dependencies: 6
-- Name: hst_cis_edges; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE hst_cis_edges (
    tstamp timestamp without time zone NOT NULL,
    id integer NOT NULL,
    edgetype integer,
    fromid integer NOT NULL,
    toid integer NOT NULL,
    connectiontype integer,
    strength integer,
    directed integer,
    inpath integer,
    locked integer,
    comment text,
    userid integer,
    inscoring integer
);


ALTER TABLE hst_cis_edges OWNER TO sa;

--
-- TOC entry 1793 (class 1259 OID 85778)
-- Dependencies: 6
-- Name: hst_cis_objects; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE hst_cis_objects (
    tstamp timestamp without time zone NOT NULL,
    id integer NOT NULL,
    objecttype integer NOT NULL,
    userid integer
);


ALTER TABLE hst_cis_objects OWNER TO sa;

--
-- TOC entry 1794 (class 1259 OID 85824)
-- Dependencies: 6
-- Name: seq_objectcount; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_objectcount
    START WITH 4
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_objectcount OWNER TO sa;

--
-- TOC entry 2481 (class 0 OID 0)
-- Dependencies: 1794
-- Name: seq_objectcount; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_objectcount', 20011, true);


--
-- TOC entry 1795 (class 1259 OID 85826)
-- Dependencies: 6
-- Name: seq_sys_log; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_sys_log
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_sys_log OWNER TO sa;

--
-- TOC entry 2482 (class 0 OID 0)
-- Dependencies: 1795
-- Name: seq_sys_log; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_sys_log', 88, true);


--
-- TOC entry 1796 (class 1259 OID 85828)
-- Dependencies: 6
-- Name: seq_sys_nodemetaphor; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_sys_nodemetaphor
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_sys_nodemetaphor OWNER TO sa;

--
-- TOC entry 2483 (class 0 OID 0)
-- Dependencies: 1796
-- Name: seq_sys_nodemetaphor; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_sys_nodemetaphor', 1320, true);


--
-- TOC entry 1797 (class 1259 OID 85830)
-- Dependencies: 6
-- Name: seq_sys_object_connection; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_sys_object_connection
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_sys_object_connection OWNER TO sa;

--
-- TOC entry 2484 (class 0 OID 0)
-- Dependencies: 1797
-- Name: seq_sys_object_connection; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_sys_object_connection', 348, true);


--
-- TOC entry 1798 (class 1259 OID 85832)
-- Dependencies: 6
-- Name: seq_sys_user_activity; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_sys_user_activity
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_sys_user_activity OWNER TO sa;

--
-- TOC entry 2485 (class 0 OID 0)
-- Dependencies: 1798
-- Name: seq_sys_user_activity; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_sys_user_activity', 22423, true);


--
-- TOC entry 1799 (class 1259 OID 85834)
-- Dependencies: 6
-- Name: seq_sysdiagrams; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_sysdiagrams
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_sysdiagrams OWNER TO sa;

--
-- TOC entry 2486 (class 0 OID 0)
-- Dependencies: 1799
-- Name: seq_sysdiagrams; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_sysdiagrams', 1, false);


--
-- TOC entry 1800 (class 1259 OID 85836)
-- Dependencies: 6
-- Name: seq_user_activity; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE seq_user_activity
    START WITH 397
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE seq_user_activity OWNER TO sa;

--
-- TOC entry 2487 (class 0 OID 0)
-- Dependencies: 1800
-- Name: seq_user_activity; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('seq_user_activity', 25475, true);


--
-- TOC entry 1801 (class 1259 OID 85838)
-- Dependencies: 6
-- Name: sys_attribute_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_attribute_group (
    attributeid integer NOT NULL,
    groupid integer NOT NULL,
    cancreate integer,
    canread integer,
    canupdate integer,
    haveaccess integer
);


ALTER TABLE sys_attribute_group OWNER TO sa;

--
-- TOC entry 1802 (class 1259 OID 85841)
-- Dependencies: 6
-- Name: sys_attribute_structure; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_attribute_structure (
    id bigint NOT NULL,
    indexid bigint,
    name character varying(255),
    label character varying(255),
    tablename character varying(255),
    objectdefinitionid bigint
);


ALTER TABLE sys_attribute_structure OWNER TO sa;

--
-- TOC entry 1803 (class 1259 OID 85847)
-- Dependencies: 6
-- Name: sys_chart_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_chart_group (
    chartid integer NOT NULL,
    groupid integer NOT NULL
);


ALTER TABLE sys_chart_group OWNER TO sa;

--
-- TOC entry 1804 (class 1259 OID 85850)
-- Dependencies: 6
-- Name: sys_connection_user_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_connection_user_group (
    connectionid integer NOT NULL,
    groupid integer NOT NULL,
    allowed text,
    denied text
);


ALTER TABLE sys_connection_user_group OWNER TO sa;

--
-- TOC entry 1805 (class 1259 OID 85856)
-- Dependencies: 6
-- Name: sys_group_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE sys_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE sys_group_id_seq OWNER TO sa;

--
-- TOC entry 2488 (class 0 OID 0)
-- Dependencies: 1805
-- Name: sys_group_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('sys_group_id_seq', 5, true);


--
-- TOC entry 1806 (class 1259 OID 85858)
-- Dependencies: 2132 6
-- Name: sys_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_group (
    id integer DEFAULT nextval('sys_group_id_seq'::regclass) NOT NULL,
    name character varying(50),
    nodescope character(1),
    edgescope character(1)
);


ALTER TABLE sys_group OWNER TO sa;

--
-- TOC entry 1807 (class 1259 OID 85862)
-- Dependencies: 6
-- Name: sys_group_prefilter; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_group_prefilter (
    groupid integer NOT NULL,
    predefid integer NOT NULL
);


ALTER TABLE sys_group_prefilter OWNER TO sa;

--
-- TOC entry 1808 (class 1259 OID 85865)
-- Dependencies: 6
-- Name: sys_group_scope; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_group_scope (
    objectid integer NOT NULL,
    groupid integer NOT NULL,
    nodescope text,
    edgescope text
);


ALTER TABLE sys_group_scope OWNER TO sa;

--
-- TOC entry 1809 (class 1259 OID 85871)
-- Dependencies: 6
-- Name: sys_iam; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_iam (
    name character varying(255) NOT NULL,
    id integer NOT NULL,
    version character varying(50)
);


ALTER TABLE sys_iam OWNER TO sa;

--
-- TOC entry 1810 (class 1259 OID 85874)
-- Dependencies: 6
-- Name: sys_labels; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_labels (
    tablename character varying(50) NOT NULL,
    labelschemeid integer NOT NULL,
    field character varying(50) NOT NULL,
    label character varying(50),
    xmllabels character varying(50),
    system integer,
    xmlorder integer
);


ALTER TABLE sys_labels OWNER TO sa;

--
-- TOC entry 1811 (class 1259 OID 85877)
-- Dependencies: 6
-- Name: sys_labelscheme; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_labelscheme (
    id integer NOT NULL,
    name character varying(50)
);


ALTER TABLE sys_labelscheme OWNER TO sa;

--
-- TOC entry 1812 (class 1259 OID 85880)
-- Dependencies: 2133 6
-- Name: sys_log; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_log (
    id integer DEFAULT nextval('seq_sys_log'::regclass) NOT NULL,
    name text,
    command text,
    timestart timestamp without time zone,
    timeend timestamp without time zone,
    exctime character varying(25),
    message text,
    parent integer
);


ALTER TABLE sys_log OWNER TO sa;

--
-- TOC entry 1813 (class 1259 OID 85887)
-- Dependencies: 6
-- Name: sys_map_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_map_group (
    mapid integer NOT NULL,
    groupid integer NOT NULL
);


ALTER TABLE sys_map_group OWNER TO sa;

--
-- TOC entry 1814 (class 1259 OID 85890)
-- Dependencies: 2134 2135 6
-- Name: sys_nodemetaphor; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_nodemetaphor (
    id integer DEFAULT nextval('seq_sys_nodemetaphor'::regclass) NOT NULL,
    nodeid integer,
    iconname character varying(100),
    iconpath character varying(255),
    schemaid integer,
    priority integer,
    metaphorset character varying(100) DEFAULT 'Default'::character varying,
    col1 integer,
    type integer,
    col2 integer,
    col3 integer,
    col4 integer,
    description character varying(255),
    vip integer,
    iconid integer
);


ALTER TABLE sys_nodemetaphor OWNER TO sa;

--
-- TOC entry 1815 (class 1259 OID 85898)
-- Dependencies: 6
-- Name: sys_object_attr_log; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_attr_log (
    attributeid integer NOT NULL,
    modification timestamp without time zone NOT NULL,
    userid integer
);


ALTER TABLE sys_object_attr_log OWNER TO sa;

--
-- TOC entry 1816 (class 1259 OID 85901)
-- Dependencies: 6
-- Name: sys_object_attributes_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE sys_object_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE sys_object_attributes_id_seq OWNER TO sa;

--
-- TOC entry 2489 (class 0 OID 0)
-- Dependencies: 1816
-- Name: sys_object_attributes_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('sys_object_attributes_id_seq', 755, true);


--
-- TOC entry 1817 (class 1259 OID 85903)
-- Dependencies: 2136 2137 2138 2139 2140 2141 2142 6
-- Name: sys_object_attributes; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_attributes (
    id integer DEFAULT nextval('sys_object_attributes_id_seq'::regclass) NOT NULL,
    objectdefinitionid integer NOT NULL,
    sort integer,
    name character varying(50),
    label character varying(50),
    predefined integer,
    description text,
    datatypeid integer,
    infilter integer,
    inlabel integer,
    intooltip integer,
    insearch integer,
    inadvancedsearch integer,
    inmetaphor integer,
    ingraphlabel integer,
    labelbold integer DEFAULT 0,
    labelitalic integer DEFAULT 0,
    labelunderline integer DEFAULT 0,
    contentbold integer DEFAULT 0,
    contentitalic integer DEFAULT 0,
    contentunderline integer DEFAULT 0,
    created timestamp without time zone,
    createdby integer,
    managing integer,
    inexport integer,
    intable character varying(255),
    insimplesearch integer,
    inprefilter integer,
    format character varying(50),
    regexpression character varying(255),
    valuedescription character varying(255),
    instructure bigint,
    label_sort integer,
    filter_sort integer,
    search_sort integer
);


ALTER TABLE sys_object_attributes OWNER TO sa;

--
-- TOC entry 1818 (class 1259 OID 85916)
-- Dependencies: 6
-- Name: sys_object_chart_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE sys_object_chart_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE sys_object_chart_id_seq OWNER TO sa;

--
-- TOC entry 2490 (class 0 OID 0)
-- Dependencies: 1818
-- Name: sys_object_chart_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('sys_object_chart_id_seq', 6, true);


--
-- TOC entry 1819 (class 1259 OID 85918)
-- Dependencies: 2143 6
-- Name: sys_object_chart; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_chart (
    objectid integer NOT NULL,
    chartid integer NOT NULL,
    minvalue integer,
    maxvalue integer,
    minscale numeric(12,4),
    maxscale numeric(12,4),
    labelinuse integer,
    labelfontsize character varying(25),
    numberformat character varying(25),
    displayoperation integer,
    displayattribute integer,
    charttype integer,
    isvaluedisplayed integer,
    id integer DEFAULT nextval('sys_object_chart_id_seq'::regclass) NOT NULL,
    rgb text
);


ALTER TABLE sys_object_chart OWNER TO sa;

--
-- TOC entry 1820 (class 1259 OID 85925)
-- Dependencies: 2144 6
-- Name: sys_object_connection; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_connection (
    id integer DEFAULT nextval('seq_sys_object_connection'::regclass) NOT NULL,
    fromobject integer NOT NULL,
    toobject integer NOT NULL,
    connectiontypeid integer NOT NULL,
    linestyleid integer,
    lineweightid integer,
    rgb character varying(25),
    linestartsymbolid integer,
    lineendsymbolid integer,
    fromtoscore numeric(15,5),
    tofromscore numeric(15,5),
    objectid integer,
    precheckprocedure text
);


ALTER TABLE sys_object_connection OWNER TO sa;

--
-- TOC entry 1821 (class 1259 OID 85932)
-- Dependencies: 6
-- Name: sys_object_definition_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE sys_object_definition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE sys_object_definition_id_seq OWNER TO sa;

--
-- TOC entry 2491 (class 0 OID 0)
-- Dependencies: 1821
-- Name: sys_object_definition_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('sys_object_definition_id_seq', 80, true);


--
-- TOC entry 1822 (class 1259 OID 85934)
-- Dependencies: 2145 6
-- Name: sys_object_definition; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_definition (
    id integer DEFAULT nextval('sys_object_definition_id_seq'::regclass) NOT NULL,
    parentobjectid integer,
    objecttypeid integer,
    name character varying(50),
    description text,
    creation timestamp without time zone,
    createdby integer,
    tabdisplay integer,
    tablename character varying(50),
    sort integer,
    managable integer
);


ALTER TABLE sys_object_definition OWNER TO sa;

--
-- TOC entry 1823 (class 1259 OID 85941)
-- Dependencies: 6
-- Name: sys_object_log; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_log (
    objectid integer NOT NULL,
    modification timestamp without time zone NOT NULL,
    userid integer
);


ALTER TABLE sys_object_log OWNER TO sa;

--
-- TOC entry 1824 (class 1259 OID 85944)
-- Dependencies: 6
-- Name: sys_object_user_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_object_user_group (
    objectid integer NOT NULL,
    groupid integer NOT NULL,
    allowed text,
    denied text,
    canread integer,
    cancreate integer,
    canupdate integer,
    candelete integer
);


ALTER TABLE sys_object_user_group OWNER TO sa;

--
-- TOC entry 1825 (class 1259 OID 85950)
-- Dependencies: 6
-- Name: sys_palette; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_palette (
    paletteid integer NOT NULL,
    sequence integer NOT NULL,
    colororder integer NOT NULL,
    color character varying(50)
);


ALTER TABLE sys_palette OWNER TO sa;

--
-- TOC entry 1826 (class 1259 OID 85953)
-- Dependencies: 6
-- Name: sys_parameters; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_parameters (
    id integer NOT NULL,
    section character varying(100),
    property character varying(100),
    value character varying(510)
);


ALTER TABLE sys_parameters OWNER TO sa;

--
-- TOC entry 1827 (class 1259 OID 85959)
-- Dependencies: 6
-- Name: sys_sequence; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_sequence (
    name character varying(100) NOT NULL,
    seqno integer
);


ALTER TABLE sys_sequence OWNER TO sa;

--
-- TOC entry 1828 (class 1259 OID 85965)
-- Dependencies: 6
-- Name: sys_settings_application; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_settings_application (
    section character varying(10) NOT NULL,
    prop character varying(50) NOT NULL,
    value character varying(500)
);


ALTER TABLE sys_settings_application OWNER TO sa;

--
-- TOC entry 1829 (class 1259 OID 85971)
-- Dependencies: 6
-- Name: sys_settings_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_settings_group (
    id integer NOT NULL,
    section character varying(10) NOT NULL,
    prop character varying(50) NOT NULL,
    value character varying(500)
);


ALTER TABLE sys_settings_group OWNER TO sa;

--
-- TOC entry 1830 (class 1259 OID 85977)
-- Dependencies: 6
-- Name: sys_settings_user; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_settings_user (
    id integer NOT NULL,
    section character varying(10) NOT NULL,
    prop character varying(50) NOT NULL,
    value character varying(500)
);


ALTER TABLE sys_settings_user OWNER TO sa;

--
-- TOC entry 1831 (class 1259 OID 85983)
-- Dependencies: 6
-- Name: sys_url; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_url (
    id bigint NOT NULL,
    objectid integer,
    url character varying(255),
    label character varying(100),
    sort character varying(25)
);


ALTER TABLE sys_url OWNER TO sa;

--
-- TOC entry 1832 (class 1259 OID 85986)
-- Dependencies: 6
-- Name: sys_url_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_url_group (
    urlid bigint NOT NULL,
    groupid integer NOT NULL
);


ALTER TABLE sys_url_group OWNER TO sa;

--
-- TOC entry 1833 (class 1259 OID 85989)
-- Dependencies: 6
-- Name: sys_user_id_seq; Type: SEQUENCE;  Owner: sa
--

CREATE SEQUENCE sys_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE sys_user_id_seq OWNER TO sa;

--
-- TOC entry 2492 (class 0 OID 0)
-- Dependencies: 1833
-- Name: sys_user_id_seq; Type: SEQUENCE SET;  Owner: sa
--

SELECT pg_catalog.setval('sys_user_id_seq', 7, true);


--
-- TOC entry 1834 (class 1259 OID 85991)
-- Dependencies: 2146 6
-- Name: sys_user; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_user (
    id integer DEFAULT nextval('sys_user_id_seq'::regclass) NOT NULL,
    firstname character varying(50),
    lastname character varying(50),
    username character varying(50),
    password character varying(100),
    sid character varying(255)
);


ALTER TABLE sys_user OWNER TO sa;

--
-- TOC entry 1835 (class 1259 OID 85998)
-- Dependencies: 2147 2148 6
-- Name: sys_user_activity; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_user_activity (
    id integer DEFAULT nextval('seq_user_activity'::regclass) NOT NULL,
    userid integer,
    httpheader text,
    request text,
    ipaddress text,
    activitytype character varying(255),
    loglevel character varying(255),
    datetime timestamp without time zone DEFAULT now()
);


ALTER TABLE sys_user_activity OWNER TO sa;

--
-- TOC entry 1836 (class 1259 OID 86006)
-- Dependencies: 6
-- Name: sys_user_group; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_user_group (
    groupid integer NOT NULL,
    userid integer NOT NULL
);


ALTER TABLE sys_user_group OWNER TO sa;

--
-- TOC entry 1837 (class 1259 OID 86009)
-- Dependencies: 6
-- Name: sys_user_language; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_user_language (
    languageid integer NOT NULL,
    prop character varying(50) NOT NULL,
    value character varying(2000)
);


ALTER TABLE sys_user_language OWNER TO sa;

--
-- TOC entry 1838 (class 1259 OID 86012)
-- Dependencies: 1931 6
-- Name: sys_user_settings; Type: VIEW;  Owner: postgres
--

CREATE VIEW sys_user_settings AS
    (SELECT sys_settings_user.id AS userid, sys_settings_user.section, sys_settings_user.prop, sys_settings_user.value FROM sys_settings_user UNION SELECT u.id AS userid, g.section, g.prop, g.value FROM ((sys_user u JOIN sys_user_group ug ON ((u.id = ug.userid))) JOIN sys_settings_group g ON ((g.id = ug.groupid))) WHERE (NOT (EXISTS (SELECT t.id, t.section, t.prop, t.value FROM sys_settings_user t WHERE (((t.id = u.id) AND ((t.section)::text = (g.section)::text)) AND ((t.prop)::text = (g.prop)::text)))))) UNION SELECT u.id AS userid, g.section, g.prop, g.value FROM sys_user u, sys_settings_application g WHERE ((NOT (EXISTS (SELECT t.id, t.section, t.prop, t.value FROM sys_settings_user t WHERE (((t.id = u.id) AND ((t.section)::text = (g.section)::text)) AND ((t.prop)::text = (g.prop)::text))))) AND (NOT (EXISTS (SELECT t.id, t.section, t.prop, t.value, ug.groupid, ug.userid FROM (sys_settings_group t JOIN sys_user_group ug ON ((t.id = ug.groupid))) WHERE (((u.id = ug.userid) AND ((t.section)::text = (g.section)::text)) AND ((t.prop)::text = (g.prop)::text))))));


ALTER TABLE sys_user_settings OWNER TO postgres;

--
-- TOC entry 1839 (class 1259 OID 86017)
-- Dependencies: 6
-- Name: sys_xml_attribute; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_xml_attribute (
    id integer NOT NULL,
    elementid integer,
    sortorder integer,
    attributenameid integer,
    value character varying(50)
);


ALTER TABLE sys_xml_attribute OWNER TO sa;

--
-- TOC entry 1840 (class 1259 OID 86020)
-- Dependencies: 6
-- Name: sys_xml_attribute_struct; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_xml_attribute_struct (
    xmldocid integer NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    xmlelementid integer NOT NULL,
    xmlattributeid integer NOT NULL
);


ALTER TABLE sys_xml_attribute_struct OWNER TO sa;

--
-- TOC entry 1841 (class 1259 OID 86023)
-- Dependencies: 2149 6
-- Name: sys_xml_doc; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_xml_doc (
    id integer NOT NULL,
    xmldoc integer,
    processingdate timestamp without time zone,
    writedate timestamp without time zone DEFAULT now(),
    crationdate timestamp without time zone,
    xmlcontent text
);


ALTER TABLE sys_xml_doc OWNER TO sa;

--
-- TOC entry 1842 (class 1259 OID 86030)
-- Dependencies: 6
-- Name: sys_xml_element; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_xml_element (
    id integer NOT NULL,
    parentid integer,
    elementnameid integer,
    xmldocid integer,
    sortorder integer
);


ALTER TABLE sys_xml_element OWNER TO sa;

--
-- TOC entry 1843 (class 1259 OID 86033)
-- Dependencies: 6
-- Name: sys_xml_element_struct; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_xml_element_struct (
    xmldocid integer NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    xmlelementid integer NOT NULL
);


ALTER TABLE sys_xml_element_struct OWNER TO sa;

--
-- TOC entry 1844 (class 1259 OID 86036)
-- Dependencies: 6
-- Name: sys_xml_table; Type: TABLE;  Owner: sa; Tablespace: 
--

CREATE TABLE sys_xml_table (
    xmldocid integer NOT NULL,
    xmlelementid integer NOT NULL,
    xmlattributeid integer NOT NULL,
    tablename character varying(50),
    tablefield character varying(50),
    attributetypeid integer,
    attributeusageid integer,
    selectstatement character varying(200)
);


ALTER TABLE sys_xml_table OWNER TO sa;

--
-- TOC entry 2380 (class 0 OID 85537)
-- Dependencies: 1736
-- Data for Name: cht_attribute_type; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2381 (class 0 OID 85540)
-- Dependencies: 1737
-- Data for Name: cht_attribute_usage; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2382 (class 0 OID 85543)
-- Dependencies: 1738
-- Data for Name: cht_attribute_visibility; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_attribute_visibility (id, label) VALUES (0, 'hidden');
INSERT INTO cht_attribute_visibility (id, label) VALUES (2, 'mandatory');
INSERT INTO cht_attribute_visibility (id, label) VALUES (1, 'visible');


--
-- TOC entry 2383 (class 0 OID 85548)
-- Dependencies: 1740
-- Data for Name: cht_chart; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2384 (class 0 OID 85552)
-- Dependencies: 1741
-- Data for Name: cht_chart_type; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_chart_type (id, name) VALUES (1, 'Pie');
INSERT INTO cht_chart_type (id, name) VALUES (2, 'Stacked');
INSERT INTO cht_chart_type (id, name) VALUES (3, 'Bar');


--
-- TOC entry 2385 (class 0 OID 85555)
-- Dependencies: 1742
-- Data for Name: cht_color; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2386 (class 0 OID 85561)
-- Dependencies: 1743
-- Data for Name: cht_connection_type; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2387 (class 0 OID 85564)
-- Dependencies: 1744
-- Data for Name: cht_datatype; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_datatype (id, name) VALUES (1, 'text');
INSERT INTO cht_datatype (id, name) VALUES (2, 'int');
INSERT INTO cht_datatype (id, name) VALUES (3, 'bool');
INSERT INTO cht_datatype (id, name) VALUES (4, 'URL');


--
-- TOC entry 2388 (class 0 OID 85567)
-- Dependencies: 1745
-- Data for Name: cht_display_operation; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_display_operation (id, name) VALUES (1, 'Sum');
INSERT INTO cht_display_operation (id, name) VALUES (2, 'Avg');
INSERT INTO cht_display_operation (id, name) VALUES (3, 'Min');
INSERT INTO cht_display_operation (id, name) VALUES (4, 'Max');
INSERT INTO cht_display_operation (id, name) VALUES (5, 'Count');
INSERT INTO cht_display_operation (id, name) VALUES (6, 'Attribute');


--
-- TOC entry 2389 (class 0 OID 85572)
-- Dependencies: 1747
-- Data for Name: cht_icons; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2390 (class 0 OID 85581)
-- Dependencies: 1749
-- Data for Name: cht_language; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_language (id, language) VALUES (1, 'English (UK)');


--
-- TOC entry 2391 (class 0 OID 85585)
-- Dependencies: 1750
-- Data for Name: cht_line_style; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_line_style (id, style, picture, filepath) VALUES (0, 'Transparent', NULL, '~\\Images\\lineStyle\\transparent.png');
INSERT INTO cht_line_style (id, style, picture, filepath) VALUES (1, 'Dot', NULL, '~\\Images\\lineStyle\\dot.png');
INSERT INTO cht_line_style (id, style, picture, filepath) VALUES (2, 'Dashed', NULL, '~\\Images\\lineStyle\\dashed.png');
INSERT INTO cht_line_style (id, style, picture, filepath) VALUES (3, 'Dash-Dot', NULL, '~\\Images\\lineStyle\\dashdot.png');
INSERT INTO cht_line_style (id, style, picture, filepath) VALUES (4, 'Full', NULL, '~\\Images\\lineStyle\\full.png');


--
-- TOC entry 2392 (class 0 OID 85591)
-- Dependencies: 1751
-- Data for Name: cht_line_weight; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_line_weight (id, label, picture, filepath, width) VALUES (0, '1px', NULL, '~\\Images\\lineWeight\\1px.png', 1.00);
INSERT INTO cht_line_weight (id, label, picture, filepath, width) VALUES (1, '3px', NULL, '~\\Images\\lineWeight\\3px.png', 3.00);
INSERT INTO cht_line_weight (id, label, picture, filepath, width) VALUES (2, '5px', NULL, '~\\Images\\lineWeight\\5px.png', 5.00);
INSERT INTO cht_line_weight (id, label, picture, filepath, width) VALUES (3, '7px', NULL, '~\\Images\\lineWeight\\7px.png', 7.00);
INSERT INTO cht_line_weight (id, label, picture, filepath, width) VALUES (4, '9px', NULL, '~\\Images\\lineWeight\\1px.png', 9.00);


--
-- TOC entry 2393 (class 0 OID 85597)
-- Dependencies: 1752
-- Data for Name: cht_metaphorimage; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2394 (class 0 OID 85603)
-- Dependencies: 1753
-- Data for Name: cht_object_type; Type: TABLE DATA;  Owner: sa
--

INSERT INTO cht_object_type (id, name, description) VALUES (1, 'Schema', NULL);
INSERT INTO cht_object_type (id, name, description) VALUES (2, 'Node - account like', NULL);
INSERT INTO cht_object_type (id, name, description) VALUES (3, 'Node - contact like', NULL);
INSERT INTO cht_object_type (id, name, description) VALUES (4, 'Edge', NULL);
INSERT INTO cht_object_type (id, name, description) VALUES (5, 'Attribute', NULL);


--
-- TOC entry 2395 (class 0 OID 85609)
-- Dependencies: 1754
-- Data for Name: cht_pi_type; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2396 (class 0 OID 85617)
-- Dependencies: 1756
-- Data for Name: cht_predefinedattributes; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2397 (class 0 OID 85633)
-- Dependencies: 1758
-- Data for Name: cht_role; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2398 (class 0 OID 85637)
-- Dependencies: 1759
-- Data for Name: cht_symbol; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2399 (class 0 OID 85643)
-- Dependencies: 1760
-- Data for Name: cht_territory_type; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2400 (class 0 OID 85646)
-- Dependencies: 1761
-- Data for Name: cht_xml_attribute; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2401 (class 0 OID 85649)
-- Dependencies: 1762
-- Data for Name: cht_xml_doc; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2402 (class 0 OID 85652)
-- Dependencies: 1763
-- Data for Name: cht_xml_element; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2403 (class 0 OID 85655)
-- Dependencies: 1764
-- Data for Name: cis_changes; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2404 (class 0 OID 85661)
-- Dependencies: 1765
-- Data for Name: cis_chart; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2405 (class 0 OID 85664)
-- Dependencies: 1766
-- Data for Name: cis_chart_attributes; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2406 (class 0 OID 85670)
-- Dependencies: 1767
-- Data for Name: cis_chronology; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2407 (class 0 OID 85676)
-- Dependencies: 1768
-- Data for Name: cis_edges; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2408 (class 0 OID 85682)
-- Dependencies: 1769
-- Data for Name: cis_edges_scope; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2409 (class 0 OID 85687)
-- Dependencies: 1771
-- Data for Name: cis_favorites; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2410 (class 0 OID 85696)
-- Dependencies: 1773
-- Data for Name: cis_function; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2411 (class 0 OID 85703)
-- Dependencies: 1774
-- Data for Name: cis_node_role; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2412 (class 0 OID 85706)
-- Dependencies: 1775
-- Data for Name: cis_nodes; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2413 (class 0 OID 85709)
-- Dependencies: 1776
-- Data for Name: cis_nodes_scope; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2414 (class 0 OID 85712)
-- Dependencies: 1777
-- Data for Name: cis_objects; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2415 (class 0 OID 85715)
-- Dependencies: 1778
-- Data for Name: cis_pointofinterest; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2416 (class 0 OID 85723)
-- Dependencies: 1780
-- Data for Name: cis_sub; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2417 (class 0 OID 85730)
-- Dependencies: 1781
-- Data for Name: cis_territory; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2418 (class 0 OID 85733)
-- Dependencies: 1782
-- Data for Name: cis_user_favorites; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2419 (class 0 OID 85739)
-- Dependencies: 1783
-- Data for Name: gis_country; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2420 (class 0 OID 85742)
-- Dependencies: 1784
-- Data for Name: gis_file; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2421 (class 0 OID 85745)
-- Dependencies: 1785
-- Data for Name: gis_layer; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2422 (class 0 OID 85748)
-- Dependencies: 1786
-- Data for Name: gis_layerparams; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2423 (class 0 OID 85754)
-- Dependencies: 1787
-- Data for Name: gis_map; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2424 (class 0 OID 85757)
-- Dependencies: 1788
-- Data for Name: gis_thematiccoloring; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2425 (class 0 OID 85760)
-- Dependencies: 1789
-- Data for Name: gis_thematiccoloringrange; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2426 (class 0 OID 85763)
-- Dependencies: 1790
-- Data for Name: gis_thematicdataset; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2427 (class 0 OID 85769)
-- Dependencies: 1791
-- Data for Name: gis_thematicmap; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2428 (class 0 OID 85772)
-- Dependencies: 1792
-- Data for Name: hst_cis_edges; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2429 (class 0 OID 85778)
-- Dependencies: 1793
-- Data for Name: hst_cis_objects; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2430 (class 0 OID 85838)
-- Dependencies: 1801
-- Data for Name: sys_attribute_group; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2431 (class 0 OID 85841)
-- Dependencies: 1802
-- Data for Name: sys_attribute_structure; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2432 (class 0 OID 85847)
-- Dependencies: 1803
-- Data for Name: sys_chart_group; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2433 (class 0 OID 85850)
-- Dependencies: 1804
-- Data for Name: sys_connection_user_group; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2434 (class 0 OID 85858)
-- Dependencies: 1806
-- Data for Name: sys_group; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_group (id, name, nodescope, edgescope) VALUES (1, 'Administrators', 'A', 'A');


--
-- TOC entry 2435 (class 0 OID 85862)
-- Dependencies: 1807
-- Data for Name: sys_group_prefilter; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2436 (class 0 OID 85865)
-- Dependencies: 1808
-- Data for Name: sys_group_scope; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2437 (class 0 OID 85871)
-- Dependencies: 1809
-- Data for Name: sys_iam; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_iam (name, id, version) VALUES ('PostgreSQL', 1, '1.0.0');


--
-- TOC entry 2438 (class 0 OID 85874)
-- Dependencies: 1810
-- Data for Name: sys_labels; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2439 (class 0 OID 85877)
-- Dependencies: 1811
-- Data for Name: sys_labelscheme; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2440 (class 0 OID 85880)
-- Dependencies: 1812
-- Data for Name: sys_log; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2441 (class 0 OID 85887)
-- Dependencies: 1813
-- Data for Name: sys_map_group; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2442 (class 0 OID 85890)
-- Dependencies: 1814
-- Data for Name: sys_nodemetaphor; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2443 (class 0 OID 85898)
-- Dependencies: 1815
-- Data for Name: sys_object_attr_log; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2444 (class 0 OID 85903)
-- Dependencies: 1817
-- Data for Name: sys_object_attributes; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2445 (class 0 OID 85918)
-- Dependencies: 1819
-- Data for Name: sys_object_chart; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2446 (class 0 OID 85925)
-- Dependencies: 1820
-- Data for Name: sys_object_connection; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2447 (class 0 OID 85934)
-- Dependencies: 1822
-- Data for Name: sys_object_definition; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2448 (class 0 OID 85941)
-- Dependencies: 1823
-- Data for Name: sys_object_log; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2449 (class 0 OID 85944)
-- Dependencies: 1824
-- Data for Name: sys_object_user_group; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2450 (class 0 OID 85950)
-- Dependencies: 1825
-- Data for Name: sys_palette; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 1, 1, '#C1F903');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 2, 2, '#A9D3D9');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 3, 3, '#FBCEB1');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 4, 4, '#7FFFD4');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 5, 5, '#F0DC82');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 6, 6, '#E97451');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 7, 7, '#ACE1AF');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 8, 8, '#007BA7');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 9, 9, '#7FFF00');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 10, 10, '#DFFF00');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 11, 11, '#00FFFF');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 12, 12, '#1E90FF');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 13, 13, '#50C878');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 14, 14, '#FF00FF');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 15, 15, '#FFD700');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 16, 16, '#DF73FF');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 17, 17, '#C3B091');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 18, 18, '#CCCCFF');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 19, 19, '#0BDA51');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 20, 20, '#AF4035');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 21, 21, '#CFB53B');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 22, 22, '#FFA500');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 23, 23, '#F0DC82');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 24, 24, '#D1E231');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 25, 25, '#FF69B4');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 26, 26, '#98FF98');
INSERT INTO sys_palette (paletteid, sequence, colororder, color) VALUES (1, 27, 27, '#C41E3A');


--
-- TOC entry 2451 (class 0 OID 85953)
-- Dependencies: 1826
-- Data for Name: sys_parameters; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2452 (class 0 OID 85959)
-- Dependencies: 1827
-- Data for Name: sys_sequence; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2453 (class 0 OID 85965)
-- Dependencies: 1828
-- Data for Name: sys_settings_application; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'command_panel_split_location', '180');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'Default chart', '0');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GISChartScale', '1');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet ', 'GISEdgeScale', '0');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GISNodeLabelFont', 'Arial');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GISNodeLabelHeight', '9');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GISNodeScale', '3');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'graph_panel_split_location', '500');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GraphEdgeLabelFont', 'Arial');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GraphEdgeLabelHeight', '7');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GraphNodeLabelFont', 'Arial');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GraphNodeLabelHeight', '9');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'GraphNodeScale', '1');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'HideGisPanel', 'FALSE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'HideTopList', 'FALSE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'MaxPathLength', '5');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'MetaphorZoom', '7501,0.2,2,0.2,0,1#10001,0.2,2,0.30,0,1#25001,0.2,2,0.6,0,1#50001,0.2,2,1,0,1#75001,0.2,2,1.2,0,1#100001,0.2,2,1.6,0,1#200001,0.2,2,2.4,0,1#300001,0.2,2,3.6,0,1#400001,0.2,2,6,0,1#500001,0.2,2,6,0,0.66#625001,0.2,2,7.2,0,1#750001,0.2,2,7.2,0,1#825001,0.2,2,7.6,0,1#1000001,0.2,2,8,0,1#1250001,0.2,2,8,0,1#1500001,0.2,2,8,0,0.66#2000001,0.2,2,8,0,0.66#3000001,0.2,2,8,0,0.66#4000001,0.2,2,8,0,0.66#5000001,0.2,2,8,0,0.66');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'MinSearchKeywordLength', '3');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'NoOrphans_True', 'TRUE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'NoOrphans_Visible', 'TRUE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'SearchResultLimit', '500');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'ShowNodeExpandCounter', 'TRUE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'ShowRootNodeMenu', 'FALSE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'TooltipHTMLWrapLen', '1000');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'top_list_name', 'account');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'TopListTooltip', 'FALSE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Applet', 'UnusedMenuItems', 'Hide');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'GRAPH_CONTROL_COUNTER_FONT', 'Dialog,0,12');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'GRAPH_CONTROL_ITEM_FONT', 'Dialog,0,12');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'GRAPH_CONTROL_LABEL_FONT', 'Dialog,1,12');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'NODE_SELECTED_COLOR', '192,192,192');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'PREFILTER_BACKGROUND', '197,255,197');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'SEARCH_FIELD_FONT', 'Dialog,0,12');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('FontColor', 'SEARCH_RESULT_HEADER_LABEL_FONT', 'Dialog,1,12');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('GIS', 'ArrowScale', '5');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('GIS', 'BufferSize', '40');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('GIS', 'Compression', '7');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('GIS', 'FastGisReponse', 'TRUE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('GIS', 'Predefined zooms', '50000,75000,150000,300000,500000,1000000,2000000,3000000,5000000,10000000,25000000,50000000,75000000,110000000');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('GIS', 'UseGISSearch', 'TRUE');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('graph', 'directed_edge', 'y');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('graph', 'RelaxType', '2');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('graph', 'show_labels', 'n');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '11', 'fGermany.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '14', 'fEU.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '18', 'fNetherlands.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '22', 'fNetherlands.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '25', 'fEU.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '29', 'fSwitzerland.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '32', 'ManAtWork.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', '35', '');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'BuildQuery', 'puzz24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ClearGraph', 'ClearGraph24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ClearHighlights', 'ClearHighlights24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ContractGraph', 'list-remove.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'CreateEdge', 'CreateEdge.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'CreateNode', 'CreateNode.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'DocumentNew', 'document-new.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'DocumentOpen', 'document-open.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'DocumentSaveAs', 'document-save-as.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ExpandGraph', 'list-add.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'FindPath', 'FindPath24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'Isolate', 'Isolate24a.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'Left', 'left.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ListRemove', 'list-remove.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'Logo', 'ni3_32.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'PowerOff', 'PowerOff.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ReloadGraph', 'ReloadGraph24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'Remove', 'remove.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'Right', 'right.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'SaveAs', 'save24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'Search', 'Search24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'SimpleSearch', 'bullseye24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'SystemSearch', 'system-search.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ZoomIn', 'SearchPlus24.png');
INSERT INTO sys_settings_application (section, prop, value) VALUES ('Icons', 'ZoomOut', 'SearchMinus24.png');


--
-- TOC entry 2454 (class 0 OID 85971)
-- Dependencies: 1829
-- Data for Name: sys_settings_group; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'BarCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'BarCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'BarCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'BarCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Charts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Charts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Charts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Charts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Connection_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Connection_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Connection_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Connection_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'File_ChangePassword_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'File_ChangePassword_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'File_ChangePassword_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'File_ChangePassword_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'File_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'File_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'File_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'File_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'File_PrintGraph_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'File_PrintGraph_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'File_PrintGraph_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'File_PrintGraph_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'File_PrintMap_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'File_PrintMap_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'File_PrintMap_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'File_PrintMap_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Maps_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Maps_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Maps_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Maps_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Metaphors_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Metaphors_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Metaphors_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Metaphors_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Node_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Node_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Node_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Node_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'PieCharts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'PieCharts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'PieCharts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'PieCharts_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Schema_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Schema_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Schema_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Schema_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'ShowEdgeLabel_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'ShowEdgeLabel_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'ShowEdgeLabel_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'ShowEdgeLabel_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'ShowEdgeThickness_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'ShowEdgeThickness_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'ShowEdgeThickness_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'ShowEdgeThickness_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'StackedCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'StackedCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'StackedCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'StackedCharts_InUse', 'FALSE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Toolbar_CreateEdge_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Toolbar_CreateEdge_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Toolbar_CreateEdge_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Toolbar_CreateEdge_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (1, 'Applet', 'Toolbar_CreateNode_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (2, 'Applet', 'Toolbar_CreateNode_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (3, 'Applet', 'Toolbar_CreateNode_InUse', 'TRUE');
INSERT INTO sys_settings_group (id, section, prop, value) VALUES (4, 'Applet', 'Toolbar_CreateNode_InUse', 'TRUE');


--
-- TOC entry 2455 (class 0 OID 85977)
-- Dependencies: 1830
-- Data for Name: sys_settings_user; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', '=>', '=>');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Accounts', 'Accounts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Add new line', 'Add new line');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Advanced', 'Advanced');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Bottom List', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Build Query', 'Build Query');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Cancel', 'Cancel');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Change', 'Change');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Charts', 'Charts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Clear', 'Clear');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Clear highlights', 'Clear highlights');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Connection', 'Connection');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ConnectionCreate', 'Create');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ConnectionEdit', 'Edit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ConnectionHistory', 'History');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Contacts', 'Contacts');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ContractGraph', 'Contract Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'Applet', 'Default graph', '180');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'Applet', 'Default graph', '191');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'Applet', 'Default graph', '191');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'Applet', 'Default graph', '191');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Delete_SE', 'Delete value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Description', 'Description');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Directed graph', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Do you want to replace existing favorite', 'Do you want to replace existing favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Done', 'Done');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Edges', 'Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Error', 'Error');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Exit', 'Exit');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Expand', 'Expand');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ExpandGraph', 'Expand Graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'File', 'File');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Files', 'Application');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Filter', 'Filter');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Filters', 'Filters');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Find path', 'Find path');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Graph control', 'Graph display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Group', 'Group');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'History', 'Favourites');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Isolate', 'Isolate');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'Applet', 'Language', '1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Legend', 'Chart legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Levels', 'Degrees to display');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'Applet', 'list_panel_split_location', '900');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Manage Connections', 'Manage Connections');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Maps', 'Maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'MaxPathLength', 'Maximum Path Length');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Metaphors', 'Metaphors');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'New_SE', 'New value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'No search results', 'No search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Node type:', 'Node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Nodes', 'Nodes');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Off', 'Off');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Options', 'Options');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'PathLenthOverrun', 'Minimum Path Override');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Print', 'Print');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Property:', 'Property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Query Builder', 'Query Builder');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Reload', 'Reload');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Remove', 'Remove');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Reset grouping', 'Reset grouping');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Save_SE ', 'Save');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'SaveAs', 'Save Favorite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Saving favourite', 'Saving favourite');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Schema', 'Schema');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Search', 'Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Search result', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Search Tooltip Text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Search Tooltip Text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Search Tooltip Text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Search Tooltip Text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Search Tooltip Text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Search Tooltip Text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Search tooltip text', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Select all', 'Select All');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Select node type:', 'Select node type:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Select operator:', 'Select operator:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Select property:', 'Select property:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Select value:', 'Select value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Show labels', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Show labels');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Thematic maps', 'Thematic maps');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'ThematicLegend', 'Legend');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Top List', 'Account');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Undirected graph', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Value:', 'Value:');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Wrong value', 'Wrong value');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Zoom in', 'Zoom In');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (2, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (3, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (4, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (5, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (7, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (8, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (9, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (10, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (11, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (12, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (13, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (14, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (15, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (16, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (17, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (18, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (19, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (20, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (21, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (22, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (23, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (24, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (25, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (26, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (27, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (28, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (29, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (30, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (31, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (32, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (33, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (34, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (35, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (36, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (37, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (38, 'words', 'Zoom out', 'Zoom out');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'Applet', 'Default graph', '197');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (1, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (6, 'Applet', 'Scheme', '35');
INSERT INTO sys_settings_user (id, section, prop, value) VALUES (6, 'Applet', 'Language', '1');


--
-- TOC entry 2456 (class 0 OID 85983)
-- Dependencies: 1831
-- Data for Name: sys_url; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2457 (class 0 OID 85986)
-- Dependencies: 1832
-- Data for Name: sys_url_group; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2458 (class 0 OID 85991)
-- Dependencies: 1834
-- Data for Name: sys_user; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_user (id, firstname, lastname, username, password, sid) VALUES (1, 'def', 'def', 'def', 'def', 'def');


--
-- TOC entry 2459 (class 0 OID 85998)
-- Dependencies: 1835
-- Data for Name: sys_user_activity; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2460 (class 0 OID 86006)
-- Dependencies: 1836
-- Data for Name: sys_user_group; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_user_group (groupid, userid) VALUES (1, 1);


--
-- TOC entry 2461 (class 0 OID 86009)
-- Dependencies: 1837
-- Data for Name: sys_user_language; Type: TABLE DATA;  Owner: sa
--

INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, '=>', '=>');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'About', 'About');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'About text', '<html><body>Ni3 Navigator © version @VER Copyright © 2006-2011 <a href="http://www.ni3.net" target="_blank">Ni3 AG</a>. All rights reserved. <br><br>Map data © <a href="http://www.openstreetmap.org/" target="_blank">OpenStreetMap</a> and contributors <a href="http://creativecommons.org/licenses/by-sa/2.0/" target="_blank">CC-BY-SA</a><br><br>Open Source and Third-Party components utilized in Ni3 Products may include:<br><br>Java Libraries, JVM, JRE (Copyright © <a href="http://www.java.com/en/" target="_blank">Sun Microsystems</a>)<br><br>Jetty, Jelly, Log4J, VFS, Xalan, Xerces, Castor, AXIS, Slide (Copyright © <a href="http://apache.org/licenses/LICENSE-2.0" target="_blank">Apache</a>)<br><br>Jasper, JFree Chart, JXL (Copyright © <a href="http://www.gnu.org/licenses/lgpl.html" target="_blank">SourceForge - LGPL license</a>)<br></body></html>');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Accounts', 'Accounts');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ACCOUNTS_HEADER_TOOLTIP_TEXT', 'Accounts tooltip');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Add new line', 'Add new line');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Advanced', 'Advanced');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Advanced Search_AS', 'Advanced Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'AND', 'AND');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ApplyFilter', 'Apply');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ApplyPrefilter', 'Apply');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Attribute_AS', 'Attribute');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'BACK_BUTTON_TOOLTIP_TEXT', 'Back');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Bottom List', 'Contacts');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Build Query', 'Advanced Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Cancel', 'Cancel');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Cardioloty Referral', 'Cardioloty Referral');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Change', 'Change');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Charts', 'Charts');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Clear', 'Clear');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Clear highlights', 'Clear highlights');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ClearBeforeSelectAll', 'Clear Before Select-All');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Connection', 'Connection');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Connection_Type', 'Type');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ConnectionCreate', 'Create');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ConnectionEdit', 'Edit');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ConnectionHistory', 'History');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Contacts', 'Contacts');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'CONTACTS_HEADER_TOOLTIP_TEXT', 'Contacts tooltip');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Data validation', 'Data validation');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Delete_AS', 'Delete');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'DeleteDocument', 'Delete');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Description', 'Description');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Directed graph', 'Directed graph');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'DIRECTED_GRAPH_CHECKBOX_TOOLTIP_TEXT', 'Arrows tooltip');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'DIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Directed graph');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Done', 'Done');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Edges', 'Edges');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Error', 'Error');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Exit', 'Exit');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Fields marked with red are mandatory', 'Fields marked with red are mandatory');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'File', 'File');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Files', 'Application');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Filter', 'Display Filter');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Filters', 'Filters');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Find path', 'Find path');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'FindPathParameters', 'Find Path Parameters');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Formulary', 'Formulary');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'FORWARD_BUTTON_TOOLTIP_TEXT', 'Forward');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'General', 'General');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Geographical search', 'Geo Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Graph control', 'Graph control');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Group', 'Group');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Group_AS', 'Group');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Help', 'Help');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'History', 'Favourites');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'HISTORY_HEADER_TOOLTIP_TEXT', 'History tooltip');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Isolate', 'Isolate');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Kooperation APO', 'Kooperation APO');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Legend', 'Chart legend');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Levels', 'Degrees');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'LINK', 'LINK');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Linked', 'Linked');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Logical_AS', 'Logical');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Manage Connections', 'Manage Connections');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Maps', 'Maps');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Metaphors', 'Metaphors');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Mitgliedschaften', 'Mitgliedschaften');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'New_AS', 'New');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'No search results', 'No search results');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'No search results2', '');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Node', 'Node');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Node type:', 'Node type:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'NodeCreate', 'Node Create');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'NodeEdit', 'Node Edit');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'NodeHistory', 'Node History');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Nodes', 'Nodes');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'NoFocus', 'No Focus');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'NoOrphans', 'No Orphans');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'NoUnrelated', 'No Unrelated');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Object_AS', 'Object');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Off', 'Off');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Open_AS', 'Open');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Operator_AS', 'Operator');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Options', 'Options');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'OR', 'OR');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ORDER', 'ORDER');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Organisatiorisch', 'Organisatiorisch');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Parameter_AS', 'Parameter');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Pneumology Referral', 'Pneumology Referral');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'PreFilter', 'Data Filter');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Primary', 'Primary');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Print', 'Print');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Print graph', 'Print graph');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Print map', 'Print map');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Professional', 'Professional');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Property:', 'Property:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Query Builder', 'Query Builder');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Query Item Dialog', 'Query Item Dialog');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Referral', 'Referral');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Referral Inst.', 'Referral Inst.');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Reload', 'Reload');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Remove', 'Remove');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Replace favorite confirmation', 'Replace favourite confirmation');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Reset Filter', 'Reset Filter');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Reset grouping', 'Reset grouping');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ResetFilter', 'Reset');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ResetPrefilter', 'Reset');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Save As_AS', 'Save As');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SaveAs', 'Save Graph');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Saving favourite', 'Saving favourite');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Schema', 'Schema');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Search', 'Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Search add', 'Add to Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Search new', 'New Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Search result', 'Search results');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Search Tooltip text', 'Text Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Search_AS', 'Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SEARCH_RESULT_HEADER_TOOLTIP_TEXT', 'Search results');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SEARCH_TOOLTIP_TEXT', 'Text Search');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Secondary', 'Secondary');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Select all', 'Select all');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Select node type:', 'Select node type:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Select operator:', 'Select operator:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Select property:', 'Select property:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Select value:', 'Select value:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SELECTION_MODE_CHECKBOX_TOOLTIP_TEXT', 'Selection mode tooltip');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SetAsDefault', 'Set as Default');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Show Edge labels', 'Edge labels');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Show edge thickness', 'Show edge thickness');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Show labels', 'Node labels');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SHOW_EDGE_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Edge labels');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SHOW_EDGE_THICKNESS_CHECKBOX_TOOLTIP_TEXT', 'Show edge thickness');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'SHOW_LABELS_CHECKBOX_TOOLTIP_TEXT', 'Node labels');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Social', 'Social');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Society', 'Society');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Stop', 'Stop');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Thematic datasets', 'Thematic datasets1');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Thematic maps', 'Thematic maps');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'ThematicLegend', 'Legend');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Too many nodes', 'Too many nodes');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Too many search result', 'Too many nodes to display');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Too many search result title', 'Warning');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Too many search result2', 'Please refine your search criteria or use Data Filter');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Top List', 'Displayed Nodes');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Type', 'Type');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Undirected graph', 'Undirected graph');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'UNDIRECTED_GRAPH_RADIO_BUTTON_TOOLTIP_TEXT', 'Undirected graph');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Value:', 'Value:');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Wrong advance search sentence', 'Wrong advance search sentence');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Wrong value', 'Wrong value');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Zoom in', 'Zoom in');
INSERT INTO sys_user_language (languageid, prop, value) VALUES (1, 'Zoom out', 'Zoom out');


--
-- TOC entry 2462 (class 0 OID 86017)
-- Dependencies: 1839
-- Data for Name: sys_xml_attribute; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2463 (class 0 OID 86020)
-- Dependencies: 1840
-- Data for Name: sys_xml_attribute_struct; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2464 (class 0 OID 86023)
-- Dependencies: 1841
-- Data for Name: sys_xml_doc; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2465 (class 0 OID 86030)
-- Dependencies: 1842
-- Data for Name: sys_xml_element; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2466 (class 0 OID 86033)
-- Dependencies: 1843
-- Data for Name: sys_xml_element_struct; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2467 (class 0 OID 86036)
-- Dependencies: 1844
-- Data for Name: sys_xml_table; Type: TABLE DATA;  Owner: sa
--



--
-- TOC entry 2253 (class 2606 OID 86343)
-- Dependencies: 1809 1809
-- Name: PK_SYS_IAM; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_iam
    ADD CONSTRAINT "PK_SYS_IAM" PRIMARY KEY (id);


--
-- TOC entry 2151 (class 2606 OID 86347)
-- Dependencies: 1736 1736
-- Name: pk_cht_attribute_type; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_attribute_type
    ADD CONSTRAINT pk_cht_attribute_type PRIMARY KEY (id);


--
-- TOC entry 2153 (class 2606 OID 86349)
-- Dependencies: 1737 1737
-- Name: pk_cht_attribute_usage; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_attribute_usage
    ADD CONSTRAINT pk_cht_attribute_usage PRIMARY KEY (id);


--
-- TOC entry 2155 (class 2606 OID 86351)
-- Dependencies: 1738 1738
-- Name: pk_cht_attribute_visibility; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_attribute_visibility
    ADD CONSTRAINT pk_cht_attribute_visibility PRIMARY KEY (id);


--
-- TOC entry 2157 (class 2606 OID 86353)
-- Dependencies: 1740 1740
-- Name: pk_cht_chart; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_chart
    ADD CONSTRAINT pk_cht_chart PRIMARY KEY (id);


--
-- TOC entry 2159 (class 2606 OID 86355)
-- Dependencies: 1741 1741
-- Name: pk_cht_chart_type; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_chart_type
    ADD CONSTRAINT pk_cht_chart_type PRIMARY KEY (id);


--
-- TOC entry 2161 (class 2606 OID 86357)
-- Dependencies: 1742 1742
-- Name: pk_cht_color; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_color
    ADD CONSTRAINT pk_cht_color PRIMARY KEY (id);


--
-- TOC entry 2163 (class 2606 OID 86359)
-- Dependencies: 1743 1743
-- Name: pk_cht_connection_type; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_connection_type
    ADD CONSTRAINT pk_cht_connection_type PRIMARY KEY (id);


--
-- TOC entry 2165 (class 2606 OID 86361)
-- Dependencies: 1744 1744
-- Name: pk_cht_datatype; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_datatype
    ADD CONSTRAINT pk_cht_datatype PRIMARY KEY (id);


--
-- TOC entry 2167 (class 2606 OID 86363)
-- Dependencies: 1745 1745
-- Name: pk_cht_display_operation; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_display_operation
    ADD CONSTRAINT pk_cht_display_operation PRIMARY KEY (id);


--
-- TOC entry 2169 (class 2606 OID 86365)
-- Dependencies: 1747 1747
-- Name: pk_cht_icons; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_icons
    ADD CONSTRAINT pk_cht_icons PRIMARY KEY (id);


--
-- TOC entry 2171 (class 2606 OID 86367)
-- Dependencies: 1749 1749
-- Name: pk_cht_language; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_language
    ADD CONSTRAINT pk_cht_language PRIMARY KEY (id);


--
-- TOC entry 2173 (class 2606 OID 86369)
-- Dependencies: 1750 1750
-- Name: pk_cht_line_style; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_line_style
    ADD CONSTRAINT pk_cht_line_style PRIMARY KEY (id);


--
-- TOC entry 2175 (class 2606 OID 86371)
-- Dependencies: 1751 1751
-- Name: pk_cht_line_weight; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_line_weight
    ADD CONSTRAINT pk_cht_line_weight PRIMARY KEY (id);


--
-- TOC entry 2177 (class 2606 OID 86373)
-- Dependencies: 1753 1753
-- Name: pk_cht_object_type; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_object_type
    ADD CONSTRAINT pk_cht_object_type PRIMARY KEY (id);


--
-- TOC entry 2179 (class 2606 OID 86375)
-- Dependencies: 1756 1756
-- Name: pk_cht_predefinedattributes; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_predefinedattributes
    ADD CONSTRAINT pk_cht_predefinedattributes PRIMARY KEY (id);


--
-- TOC entry 2181 (class 2606 OID 86379)
-- Dependencies: 1758 1758
-- Name: pk_cht_role; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_role
    ADD CONSTRAINT pk_cht_role PRIMARY KEY (id);


--
-- TOC entry 2183 (class 2606 OID 86381)
-- Dependencies: 1759 1759
-- Name: pk_cht_symbol; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_symbol
    ADD CONSTRAINT pk_cht_symbol PRIMARY KEY (id);


--
-- TOC entry 2185 (class 2606 OID 86383)
-- Dependencies: 1760 1760
-- Name: pk_cht_territory_type; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_territory_type
    ADD CONSTRAINT pk_cht_territory_type PRIMARY KEY (id);


--
-- TOC entry 2187 (class 2606 OID 86385)
-- Dependencies: 1761 1761
-- Name: pk_cht_xml_attribute; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_xml_attribute
    ADD CONSTRAINT pk_cht_xml_attribute PRIMARY KEY (id);


--
-- TOC entry 2189 (class 2606 OID 86387)
-- Dependencies: 1762 1762
-- Name: pk_cht_xml_doc; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_xml_doc
    ADD CONSTRAINT pk_cht_xml_doc PRIMARY KEY (id);


--
-- TOC entry 2191 (class 2606 OID 86389)
-- Dependencies: 1763 1763
-- Name: pk_cht_xml_element; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cht_xml_element
    ADD CONSTRAINT pk_cht_xml_element PRIMARY KEY (id);


--
-- TOC entry 2193 (class 2606 OID 86391)
-- Dependencies: 1765 1765 1765
-- Name: pk_cis_chart; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_chart
    ADD CONSTRAINT pk_cis_chart PRIMARY KEY (nodeid, chartattributeid);


--
-- TOC entry 2195 (class 2606 OID 86393)
-- Dependencies: 1766 1766
-- Name: pk_cis_chart_attributes; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_chart_attributes
    ADD CONSTRAINT pk_cis_chart_attributes PRIMARY KEY (id);


--
-- TOC entry 2197 (class 2606 OID 86395)
-- Dependencies: 1767 1767
-- Name: pk_cis_chronology; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_chronology
    ADD CONSTRAINT pk_cis_chronology PRIMARY KEY (id);


--
-- TOC entry 2199 (class 2606 OID 86397)
-- Dependencies: 1768 1768
-- Name: pk_cis_edges; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_edges
    ADD CONSTRAINT pk_cis_edges PRIMARY KEY (id);


--
-- TOC entry 2201 (class 2606 OID 86399)
-- Dependencies: 1769 1769 1769
-- Name: pk_cis_edges_scope; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_edges_scope
    ADD CONSTRAINT pk_cis_edges_scope PRIMARY KEY (edgeid, groupid);


--
-- TOC entry 2203 (class 2606 OID 86401)
-- Dependencies: 1771 1771
-- Name: pk_cis_favorites; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_favorites
    ADD CONSTRAINT pk_cis_favorites PRIMARY KEY (id);


--
-- TOC entry 2205 (class 2606 OID 86403)
-- Dependencies: 1773 1773
-- Name: pk_cis_function; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_function
    ADD CONSTRAINT pk_cis_function PRIMARY KEY (id);


--
-- TOC entry 2207 (class 2606 OID 86405)
-- Dependencies: 1774 1774 1774
-- Name: pk_cis_node_role; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_node_role
    ADD CONSTRAINT pk_cis_node_role PRIMARY KEY (nodeid, roleid);


--
-- TOC entry 2209 (class 2606 OID 86407)
-- Dependencies: 1775 1775
-- Name: pk_cis_nodes; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_nodes
    ADD CONSTRAINT pk_cis_nodes PRIMARY KEY (id);


--
-- TOC entry 2211 (class 2606 OID 86409)
-- Dependencies: 1777 1777
-- Name: pk_cis_objects; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_objects
    ADD CONSTRAINT pk_cis_objects PRIMARY KEY (id);


--
-- TOC entry 2213 (class 2606 OID 86411)
-- Dependencies: 1778 1778
-- Name: pk_cis_pointofinterest; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_pointofinterest
    ADD CONSTRAINT pk_cis_pointofinterest PRIMARY KEY (id);


--
-- TOC entry 2215 (class 2606 OID 86424)
-- Dependencies: 1780 1780
-- Name: pk_cis_sub; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_sub
    ADD CONSTRAINT pk_cis_sub PRIMARY KEY (id);


--
-- TOC entry 2217 (class 2606 OID 86426)
-- Dependencies: 1781 1781
-- Name: pk_cis_territory; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_territory
    ADD CONSTRAINT pk_cis_territory PRIMARY KEY (id);


--
-- TOC entry 2219 (class 2606 OID 86428)
-- Dependencies: 1782 1782 1782
-- Name: pk_cis_user_favorites; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY cis_user_favorites
    ADD CONSTRAINT pk_cis_user_favorites PRIMARY KEY (userid, favoritesid);


--
-- TOC entry 2221 (class 2606 OID 86430)
-- Dependencies: 1783 1783
-- Name: pk_gis_country; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_country
    ADD CONSTRAINT pk_gis_country PRIMARY KEY (id);


--
-- TOC entry 2223 (class 2606 OID 86432)
-- Dependencies: 1784 1784
-- Name: pk_gis_file; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_file
    ADD CONSTRAINT pk_gis_file PRIMARY KEY (id);


--
-- TOC entry 2225 (class 2606 OID 86434)
-- Dependencies: 1785 1785
-- Name: pk_gis_layer; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_layer
    ADD CONSTRAINT pk_gis_layer PRIMARY KEY (id);


--
-- TOC entry 2227 (class 2606 OID 86436)
-- Dependencies: 1786 1786 1786 1786
-- Name: pk_gis_layerparams; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_layerparams
    ADD CONSTRAINT pk_gis_layerparams PRIMARY KEY (mapid, layerid, layerorder);


--
-- TOC entry 2229 (class 2606 OID 86438)
-- Dependencies: 1787 1787
-- Name: pk_gis_map; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_map
    ADD CONSTRAINT pk_gis_map PRIMARY KEY (id);


--
-- TOC entry 2231 (class 2606 OID 86440)
-- Dependencies: 1788 1788
-- Name: pk_gis_thematiccoloring; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_thematiccoloring
    ADD CONSTRAINT pk_gis_thematiccoloring PRIMARY KEY (id);


--
-- TOC entry 2233 (class 2606 OID 86442)
-- Dependencies: 1789 1789
-- Name: pk_gis_thematiccoloringrange; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_thematiccoloringrange
    ADD CONSTRAINT pk_gis_thematiccoloringrange PRIMARY KEY (id);


--
-- TOC entry 2235 (class 2606 OID 86444)
-- Dependencies: 1790 1790
-- Name: pk_gis_thematicdataset; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_thematicdataset
    ADD CONSTRAINT pk_gis_thematicdataset PRIMARY KEY (id);


--
-- TOC entry 2237 (class 2606 OID 86446)
-- Dependencies: 1791 1791
-- Name: pk_gis_thematicmap; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY gis_thematicmap
    ADD CONSTRAINT pk_gis_thematicmap PRIMARY KEY (id);


--
-- TOC entry 2239 (class 2606 OID 86458)
-- Dependencies: 1801 1801 1801
-- Name: pk_sys_attribute_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_attribute_group
    ADD CONSTRAINT pk_sys_attribute_group PRIMARY KEY (attributeid, groupid);


--
-- TOC entry 2241 (class 2606 OID 86460)
-- Dependencies: 1802 1802
-- Name: pk_sys_attribute_structure; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_attribute_structure
    ADD CONSTRAINT pk_sys_attribute_structure PRIMARY KEY (id);


--
-- TOC entry 2243 (class 2606 OID 86462)
-- Dependencies: 1803 1803 1803
-- Name: pk_sys_chart_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_chart_group
    ADD CONSTRAINT pk_sys_chart_group PRIMARY KEY (chartid, groupid);


--
-- TOC entry 2245 (class 2606 OID 86464)
-- Dependencies: 1804 1804 1804
-- Name: pk_sys_connection_user_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_connection_user_group
    ADD CONSTRAINT pk_sys_connection_user_group PRIMARY KEY (connectionid, groupid);


--
-- TOC entry 2247 (class 2606 OID 86466)
-- Dependencies: 1806 1806
-- Name: pk_sys_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_group
    ADD CONSTRAINT pk_sys_group PRIMARY KEY (id);


--
-- TOC entry 2249 (class 2606 OID 86468)
-- Dependencies: 1807 1807 1807
-- Name: pk_sys_group_prefilter; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_group_prefilter
    ADD CONSTRAINT pk_sys_group_prefilter PRIMARY KEY (groupid, predefid);


--
-- TOC entry 2251 (class 2606 OID 86470)
-- Dependencies: 1808 1808 1808
-- Name: pk_sys_group_scope; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_group_scope
    ADD CONSTRAINT pk_sys_group_scope PRIMARY KEY (objectid, groupid);


--
-- TOC entry 2255 (class 2606 OID 86472)
-- Dependencies: 1810 1810 1810 1810
-- Name: pk_sys_labels; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_labels
    ADD CONSTRAINT pk_sys_labels PRIMARY KEY (tablename, labelschemeid, field);


--
-- TOC entry 2257 (class 2606 OID 86474)
-- Dependencies: 1811 1811
-- Name: pk_sys_labelscheme; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_labelscheme
    ADD CONSTRAINT pk_sys_labelscheme PRIMARY KEY (id);


--
-- TOC entry 2259 (class 2606 OID 86476)
-- Dependencies: 1812 1812
-- Name: pk_sys_log; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_log
    ADD CONSTRAINT pk_sys_log PRIMARY KEY (id);


--
-- TOC entry 2261 (class 2606 OID 86478)
-- Dependencies: 1813 1813 1813
-- Name: pk_sys_map_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_map_group
    ADD CONSTRAINT pk_sys_map_group PRIMARY KEY (mapid, groupid);


--
-- TOC entry 2263 (class 2606 OID 86480)
-- Dependencies: 1814 1814
-- Name: pk_sys_nodemetaphor; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_nodemetaphor
    ADD CONSTRAINT pk_sys_nodemetaphor PRIMARY KEY (id);


--
-- TOC entry 2265 (class 2606 OID 86482)
-- Dependencies: 1815 1815 1815
-- Name: pk_sys_object_attr_log; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_attr_log
    ADD CONSTRAINT pk_sys_object_attr_log PRIMARY KEY (attributeid, modification);


--
-- TOC entry 2267 (class 2606 OID 86488)
-- Dependencies: 1817 1817
-- Name: pk_sys_object_attributes; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_attributes
    ADD CONSTRAINT pk_sys_object_attributes PRIMARY KEY (id);


--
-- TOC entry 2269 (class 2606 OID 86490)
-- Dependencies: 1819 1819
-- Name: pk_sys_object_chart; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_chart
    ADD CONSTRAINT pk_sys_object_chart PRIMARY KEY (id);


--
-- TOC entry 2271 (class 2606 OID 86492)
-- Dependencies: 1820 1820
-- Name: pk_sys_object_connection; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT pk_sys_object_connection PRIMARY KEY (id);


--
-- TOC entry 2273 (class 2606 OID 86494)
-- Dependencies: 1822 1822
-- Name: pk_sys_object_definition; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_definition
    ADD CONSTRAINT pk_sys_object_definition PRIMARY KEY (id);


--
-- TOC entry 2275 (class 2606 OID 86496)
-- Dependencies: 1823 1823 1823
-- Name: pk_sys_object_log; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_log
    ADD CONSTRAINT pk_sys_object_log PRIMARY KEY (objectid, modification);


--
-- TOC entry 2277 (class 2606 OID 86498)
-- Dependencies: 1824 1824 1824
-- Name: pk_sys_object_user_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_object_user_group
    ADD CONSTRAINT pk_sys_object_user_group PRIMARY KEY (objectid, groupid);


--
-- TOC entry 2279 (class 2606 OID 86500)
-- Dependencies: 1825 1825 1825 1825
-- Name: pk_sys_palette; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_palette
    ADD CONSTRAINT pk_sys_palette PRIMARY KEY (paletteid, sequence, colororder);


--
-- TOC entry 2281 (class 2606 OID 86502)
-- Dependencies: 1826 1826
-- Name: pk_sys_parameters; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_parameters
    ADD CONSTRAINT pk_sys_parameters PRIMARY KEY (id);


--
-- TOC entry 2283 (class 2606 OID 86504)
-- Dependencies: 1827 1827
-- Name: pk_sys_sequence; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_sequence
    ADD CONSTRAINT pk_sys_sequence PRIMARY KEY (name);


--
-- TOC entry 2285 (class 2606 OID 86508)
-- Dependencies: 1828 1828 1828
-- Name: pk_sys_settings_application; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_settings_application
    ADD CONSTRAINT pk_sys_settings_application PRIMARY KEY (section, prop);


--
-- TOC entry 2287 (class 2606 OID 86510)
-- Dependencies: 1829 1829 1829 1829
-- Name: pk_sys_settings_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_settings_group
    ADD CONSTRAINT pk_sys_settings_group PRIMARY KEY (id, section, prop);


--
-- TOC entry 2289 (class 2606 OID 86512)
-- Dependencies: 1830 1830 1830 1830
-- Name: pk_sys_settings_user; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_settings_user
    ADD CONSTRAINT pk_sys_settings_user PRIMARY KEY (id, section, prop);


--
-- TOC entry 2291 (class 2606 OID 86514)
-- Dependencies: 1831 1831
-- Name: pk_sys_url; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_url
    ADD CONSTRAINT pk_sys_url PRIMARY KEY (id);


--
-- TOC entry 2293 (class 2606 OID 86521)
-- Dependencies: 1832 1832 1832
-- Name: pk_sys_url_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_url_group
    ADD CONSTRAINT pk_sys_url_group PRIMARY KEY (urlid, groupid);


--
-- TOC entry 2295 (class 2606 OID 86527)
-- Dependencies: 1834 1834
-- Name: pk_sys_user; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_user
    ADD CONSTRAINT pk_sys_user PRIMARY KEY (id);


--
-- TOC entry 2297 (class 2606 OID 86529)
-- Dependencies: 1835 1835
-- Name: pk_sys_user_activity; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_user_activity
    ADD CONSTRAINT pk_sys_user_activity PRIMARY KEY (id);


--
-- TOC entry 2299 (class 2606 OID 86531)
-- Dependencies: 1836 1836 1836
-- Name: pk_sys_user_group; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_user_group
    ADD CONSTRAINT pk_sys_user_group PRIMARY KEY (groupid, userid);


--
-- TOC entry 2301 (class 2606 OID 86533)
-- Dependencies: 1837 1837 1837
-- Name: pk_sys_user_language; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_user_language
    ADD CONSTRAINT pk_sys_user_language PRIMARY KEY (languageid, prop);


--
-- TOC entry 2303 (class 2606 OID 86535)
-- Dependencies: 1839 1839
-- Name: pk_sys_xml_attribute; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_xml_attribute
    ADD CONSTRAINT pk_sys_xml_attribute PRIMARY KEY (id);


--
-- TOC entry 2305 (class 2606 OID 86537)
-- Dependencies: 1840 1840 1840 1840 1840
-- Name: pk_sys_xml_attribute_struct; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_xml_attribute_struct
    ADD CONSTRAINT pk_sys_xml_attribute_struct PRIMARY KEY (xmldocid, "timestamp", xmlelementid, xmlattributeid);


--
-- TOC entry 2307 (class 2606 OID 86539)
-- Dependencies: 1841 1841
-- Name: pk_sys_xml_doc; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_xml_doc
    ADD CONSTRAINT pk_sys_xml_doc PRIMARY KEY (id);


--
-- TOC entry 2309 (class 2606 OID 86541)
-- Dependencies: 1842 1842
-- Name: pk_sys_xml_element; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_xml_element
    ADD CONSTRAINT pk_sys_xml_element PRIMARY KEY (id);


--
-- TOC entry 2311 (class 2606 OID 86543)
-- Dependencies: 1843 1843 1843 1843
-- Name: pk_sys_xml_element_struct; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_xml_element_struct
    ADD CONSTRAINT pk_sys_xml_element_struct PRIMARY KEY (xmldocid, "timestamp", xmlelementid);


--
-- TOC entry 2313 (class 2606 OID 86545)
-- Dependencies: 1844 1844 1844 1844
-- Name: pk_sys_xml_table; Type: CONSTRAINT;  Owner: sa; Tablespace: 
--

ALTER TABLE ONLY sys_xml_table
    ADD CONSTRAINT pk_sys_xml_table PRIMARY KEY (xmldocid, xmlelementid, xmlattributeid);


--
-- TOC entry 2378 (class 2620 OID 86548)
-- Dependencies: 1775 25
-- Name: trg_edge_objects; Type: TRIGGER;  Owner: sa
--

CREATE TRIGGER trg_edge_objects
    AFTER INSERT OR DELETE ON cis_nodes
    FOR EACH ROW
    EXECUTE PROCEDURE ftrg_objects();


--
-- TOC entry 2377 (class 2620 OID 86549)
-- Dependencies: 1768 25
-- Name: trg_edge_objects; Type: TRIGGER;  Owner: sa
--

CREATE TRIGGER trg_edge_objects
    AFTER INSERT OR DELETE ON cis_edges
    FOR EACH ROW
    EXECUTE PROCEDURE ftrg_objects();


--
-- TOC entry 2379 (class 2620 OID 86550)
-- Dependencies: 1775 25
-- Name: trg_node_objects; Type: TRIGGER;  Owner: sa
--

CREATE TRIGGER trg_node_objects
    AFTER INSERT OR DELETE ON cis_nodes
    FOR EACH ROW
    EXECUTE PROCEDURE ftrg_objects();


--
-- TOC entry 2337 (class 2606 OID 86551)
-- Dependencies: 2246 1803 1806
-- Name: fk2_sys_group; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_chart_group
    ADD CONSTRAINT fk2_sys_group FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2322 (class 2606 OID 86556)
-- Dependencies: 2208 1775 1774
-- Name: fk__cis_node___nodei__61074ec2; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_node_role
    ADD CONSTRAINT fk__cis_node___nodei__61074ec2 FOREIGN KEY (nodeid) REFERENCES cis_nodes(id);


--
-- TOC entry 2323 (class 2606 OID 86561)
-- Dependencies: 1774 2180 1758
-- Name: fk__cis_node___rolei__60132a89; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_node_role
    ADD CONSTRAINT fk__cis_node___rolei__60132a89 FOREIGN KEY (roleid) REFERENCES cht_role(id);


--
-- TOC entry 2335 (class 2606 OID 86591)
-- Dependencies: 1801 1806 2246
-- Name: fk_attribut_group; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_attribute_group
    ADD CONSTRAINT fk_attribut_group FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2314 (class 2606 OID 86596)
-- Dependencies: 1767 2196 1764
-- Name: fk_cis_changes_cis_chronology; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_changes
    ADD CONSTRAINT fk_cis_changes_cis_chronology FOREIGN KEY (chronology) REFERENCES cis_chronology(id);


--
-- TOC entry 2315 (class 2606 OID 86601)
-- Dependencies: 1775 1764 2208
-- Name: fk_cis_changes_cis_nodes; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_changes
    ADD CONSTRAINT fk_cis_changes_cis_nodes FOREIGN KEY (nodefrom) REFERENCES cis_nodes(id);


--
-- TOC entry 2316 (class 2606 OID 86606)
-- Dependencies: 1775 2208 1764
-- Name: fk_cis_changes_cis_nodes1; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_changes
    ADD CONSTRAINT fk_cis_changes_cis_nodes1 FOREIGN KEY (nodeto) REFERENCES cis_nodes(id);


--
-- TOC entry 2317 (class 2606 OID 86611)
-- Dependencies: 1766 1765 2194
-- Name: fk_cis_chart_cis_chart_attribute; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_chart
    ADD CONSTRAINT fk_cis_chart_cis_chart_attribute FOREIGN KEY (chartattributeid) REFERENCES cis_chart_attributes(id);


--
-- TOC entry 2318 (class 2606 OID 86616)
-- Dependencies: 1775 2208 1765
-- Name: fk_cis_chart_cis_nodes; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_chart
    ADD CONSTRAINT fk_cis_chart_cis_nodes FOREIGN KEY (nodeid) REFERENCES cis_nodes(id);


--
-- TOC entry 2319 (class 2606 OID 86621)
-- Dependencies: 1834 1767 2294
-- Name: fk_cis_chronology_sys_user; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_chronology
    ADD CONSTRAINT fk_cis_chronology_sys_user FOREIGN KEY (createdby) REFERENCES sys_user(id);


--
-- TOC entry 2320 (class 2606 OID 86626)
-- Dependencies: 1834 2294 1767
-- Name: fk_cis_chronology_sys_user1; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_chronology
    ADD CONSTRAINT fk_cis_chronology_sys_user1 FOREIGN KEY (authorisedby) REFERENCES sys_user(id);


--
-- TOC entry 2321 (class 2606 OID 86631)
-- Dependencies: 1771 1822 2272
-- Name: fk_cis_favorites_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_favorites
    ADD CONSTRAINT fk_cis_favorites_sys_object_definition FOREIGN KEY (schemaid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2325 (class 2606 OID 86636)
-- Dependencies: 1781 1760 2184
-- Name: fk_cis_territory_cht_territory_type; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_territory
    ADD CONSTRAINT fk_cis_territory_cht_territory_type FOREIGN KEY (territorytypeid) REFERENCES cht_territory_type(id);


--
-- TOC entry 2326 (class 2606 OID 86641)
-- Dependencies: 1781 2216 1781
-- Name: fk_cis_territory_cis_territory; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_territory
    ADD CONSTRAINT fk_cis_territory_cis_territory FOREIGN KEY (parentterritoryid) REFERENCES cis_territory(id);


--
-- TOC entry 2327 (class 2606 OID 86646)
-- Dependencies: 2202 1782 1771
-- Name: fk_cis_user_favorites_cis_favorites; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_user_favorites
    ADD CONSTRAINT fk_cis_user_favorites_cis_favorites FOREIGN KEY (favoritesid) REFERENCES cis_favorites(id);


--
-- TOC entry 2328 (class 2606 OID 86651)
-- Dependencies: 1782 2294 1834
-- Name: fk_cis_user_favorites_sys_user; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_user_favorites
    ADD CONSTRAINT fk_cis_user_favorites_sys_user FOREIGN KEY (userid) REFERENCES sys_user(id);


--
-- TOC entry 2331 (class 2606 OID 86656)
-- Dependencies: 1788 2230 1789
-- Name: fk_gis_thematiccoloringrange_gis_thematiccoloring; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY gis_thematiccoloringrange
    ADD CONSTRAINT fk_gis_thematiccoloringrange_gis_thematiccoloring FOREIGN KEY (thematiccoloringid) REFERENCES gis_thematiccoloring(id);


--
-- TOC entry 2332 (class 2606 OID 86661)
-- Dependencies: 2230 1790 1788
-- Name: fk_gis_thematicdataset_gis_thematiccoloring; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY gis_thematicdataset
    ADD CONSTRAINT fk_gis_thematicdataset_gis_thematiccoloring FOREIGN KEY (thematiccoloringid) REFERENCES gis_thematiccoloring(id);


--
-- TOC entry 2333 (class 2606 OID 86666)
-- Dependencies: 2236 1790 1791
-- Name: fk_gis_thematicdataset_gis_thematicmap; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY gis_thematicdataset
    ADD CONSTRAINT fk_gis_thematicdataset_gis_thematicmap FOREIGN KEY (thematicmapid) REFERENCES gis_thematicmap(id);


--
-- TOC entry 2334 (class 2606 OID 86671)
-- Dependencies: 2228 1787 1791
-- Name: fk_gis_thematicmap_gis_map; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY gis_thematicmap
    ADD CONSTRAINT fk_gis_thematicmap_gis_map FOREIGN KEY (mapid) REFERENCES gis_map(id);


--
-- TOC entry 2336 (class 2606 OID 86676)
-- Dependencies: 2266 1817 1801
-- Name: fk_sys_attribute; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_attribute_group
    ADD CONSTRAINT fk_sys_attribute FOREIGN KEY (attributeid) REFERENCES sys_object_attributes(id);


--
-- TOC entry 2338 (class 2606 OID 86681)
-- Dependencies: 2156 1740 1803
-- Name: fk_sys_chart; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_chart_group
    ADD CONSTRAINT fk_sys_chart FOREIGN KEY (chartid) REFERENCES cht_chart(id);


--
-- TOC entry 2339 (class 2606 OID 86686)
-- Dependencies: 2246 1806 1804
-- Name: fk_sys_connection_user_group_sys_group; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_connection_user_group
    ADD CONSTRAINT fk_sys_connection_user_group_sys_group FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2340 (class 2606 OID 86691)
-- Dependencies: 2270 1804 1820
-- Name: fk_sys_connection_user_group_sys_object_connection; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_connection_user_group
    ADD CONSTRAINT fk_sys_connection_user_group_sys_object_connection FOREIGN KEY (connectionid) REFERENCES sys_object_connection(id);


--
-- TOC entry 2342 (class 2606 OID 86696)
-- Dependencies: 2246 1813 1806
-- Name: fk_sys_group; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_map_group
    ADD CONSTRAINT fk_sys_group FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2341 (class 2606 OID 86701)
-- Dependencies: 1810 1811 2256
-- Name: fk_sys_labels_sys_labelscheme; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_labels
    ADD CONSTRAINT fk_sys_labels_sys_labelscheme FOREIGN KEY (labelschemeid) REFERENCES sys_labelscheme(id);


--
-- TOC entry 2343 (class 2606 OID 86706)
-- Dependencies: 1747 1814 2168
-- Name: fk_sys_nodemetaphor_cht_icons; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_nodemetaphor
    ADD CONSTRAINT fk_sys_nodemetaphor_cht_icons FOREIGN KEY (iconid) REFERENCES cht_icons(id);


--
-- TOC entry 2344 (class 2606 OID 86711)
-- Dependencies: 1815 1817 2266
-- Name: fk_sys_object_attr_log_sys_object_attributes; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_attr_log
    ADD CONSTRAINT fk_sys_object_attr_log_sys_object_attributes FOREIGN KEY (attributeid) REFERENCES sys_object_attributes(id);


--
-- TOC entry 2345 (class 2606 OID 86716)
-- Dependencies: 1815 1834 2294
-- Name: fk_sys_object_attr_log_sys_user; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_attr_log
    ADD CONSTRAINT fk_sys_object_attr_log_sys_user FOREIGN KEY (userid) REFERENCES sys_user(id);


--
-- TOC entry 2346 (class 2606 OID 86721)
-- Dependencies: 1817 1744 2164
-- Name: fk_sys_object_attributes_cht_datatype; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_attributes
    ADD CONSTRAINT fk_sys_object_attributes_cht_datatype FOREIGN KEY (datatypeid) REFERENCES cht_datatype(id);


--
-- TOC entry 2347 (class 2606 OID 86726)
-- Dependencies: 2272 1817 1822
-- Name: fk_sys_object_attributes_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_attributes
    ADD CONSTRAINT fk_sys_object_attributes_sys_object_definition FOREIGN KEY (objectdefinitionid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2348 (class 2606 OID 86731)
-- Dependencies: 1819 1822 2272
-- Name: fk_sys_object_chart_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_chart
    ADD CONSTRAINT fk_sys_object_chart_sys_object_definition FOREIGN KEY (objectid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2349 (class 2606 OID 86736)
-- Dependencies: 1820 1756 2178
-- Name: fk_sys_object_connection_cht_predefinedattributes; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_object_connection_cht_predefinedattributes FOREIGN KEY (connectiontypeid) REFERENCES cht_predefinedattributes(id);


--
-- TOC entry 2364 (class 2606 OID 86741)
-- Dependencies: 1831 1822 2272
-- Name: fk_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_url
    ADD CONSTRAINT fk_sys_object_definition FOREIGN KEY (objectid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2357 (class 2606 OID 86746)
-- Dependencies: 1822 1753 2176
-- Name: fk_sys_object_definition_cht_object_type; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_definition
    ADD CONSTRAINT fk_sys_object_definition_cht_object_type FOREIGN KEY (objecttypeid) REFERENCES cht_object_type(id);


--
-- TOC entry 2358 (class 2606 OID 86751)
-- Dependencies: 1822 2272 1822
-- Name: fk_sys_object_definition_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_definition
    ADD CONSTRAINT fk_sys_object_definition_sys_object_definition FOREIGN KEY (parentobjectid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2359 (class 2606 OID 86756)
-- Dependencies: 1822 1834 2294
-- Name: fk_sys_object_definition_sys_user; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_definition
    ADD CONSTRAINT fk_sys_object_definition_sys_user FOREIGN KEY (createdby) REFERENCES sys_user(id);


--
-- TOC entry 2360 (class 2606 OID 86761)
-- Dependencies: 2272 1822 1823
-- Name: fk_sys_object_modification_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_log
    ADD CONSTRAINT fk_sys_object_modification_sys_object_definition FOREIGN KEY (objectid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2361 (class 2606 OID 86766)
-- Dependencies: 1823 2294 1834
-- Name: fk_sys_object_modification_sys_user; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_log
    ADD CONSTRAINT fk_sys_object_modification_sys_user FOREIGN KEY (userid) REFERENCES sys_user(id);


--
-- TOC entry 2350 (class 2606 OID 86771)
-- Dependencies: 2272 1822 1820
-- Name: fk_sys_object_objectid; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_object_objectid FOREIGN KEY (objectid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2362 (class 2606 OID 86776)
-- Dependencies: 1824 2246 1806
-- Name: fk_sys_object_user_group_sys_group; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_user_group
    ADD CONSTRAINT fk_sys_object_user_group_sys_group FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2363 (class 2606 OID 86781)
-- Dependencies: 2272 1822 1824
-- Name: fk_sys_object_user_group_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_user_group
    ADD CONSTRAINT fk_sys_object_user_group_sys_object_definition FOREIGN KEY (objectid) REFERENCES sys_object_definition(id);


--
-- TOC entry 2351 (class 2606 OID 86786)
-- Dependencies: 2172 1750 1820
-- Name: fk_sys_related_objects_cht_line_style; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_related_objects_cht_line_style FOREIGN KEY (linestyleid) REFERENCES cht_line_style(id);


--
-- TOC entry 2352 (class 2606 OID 86791)
-- Dependencies: 2174 1751 1820
-- Name: fk_sys_related_objects_cht_line_weight; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_related_objects_cht_line_weight FOREIGN KEY (lineweightid) REFERENCES cht_line_weight(id);


--
-- TOC entry 2353 (class 2606 OID 86796)
-- Dependencies: 1820 2182 1759
-- Name: fk_sys_related_objects_cht_symbol; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_related_objects_cht_symbol FOREIGN KEY (linestartsymbolid) REFERENCES cht_symbol(id);


--
-- TOC entry 2354 (class 2606 OID 86801)
-- Dependencies: 2182 1820 1759
-- Name: fk_sys_related_objects_cht_symbol1; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_related_objects_cht_symbol1 FOREIGN KEY (lineendsymbolid) REFERENCES cht_symbol(id);


--
-- TOC entry 2355 (class 2606 OID 86806)
-- Dependencies: 1820 2272 1822
-- Name: fk_sys_related_objects_sys_object_definition; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_related_objects_sys_object_definition FOREIGN KEY (fromobject) REFERENCES sys_object_definition(id);


--
-- TOC entry 2356 (class 2606 OID 86811)
-- Dependencies: 1820 1822 2272
-- Name: fk_sys_related_objects_sys_object_definition1; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_object_connection
    ADD CONSTRAINT fk_sys_related_objects_sys_object_definition1 FOREIGN KEY (toobject) REFERENCES sys_object_definition(id);


--
-- TOC entry 2368 (class 2606 OID 86816)
-- Dependencies: 1839 1761 2186
-- Name: fk_sys_xml_attribute_cht_xml_attribute; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_attribute
    ADD CONSTRAINT fk_sys_xml_attribute_cht_xml_attribute FOREIGN KEY (attributenameid) REFERENCES cht_xml_attribute(id);


--
-- TOC entry 2370 (class 2606 OID 86821)
-- Dependencies: 1840 1761 2186
-- Name: fk_sys_xml_attribute_struct_cht_xml_attribute; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_attribute_struct
    ADD CONSTRAINT fk_sys_xml_attribute_struct_cht_xml_attribute FOREIGN KEY (xmlattributeid) REFERENCES cht_xml_attribute(id);


--
-- TOC entry 2369 (class 2606 OID 86826)
-- Dependencies: 1839 2308 1842
-- Name: fk_sys_xml_attribute_sys_xml_element; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_attribute
    ADD CONSTRAINT fk_sys_xml_attribute_sys_xml_element FOREIGN KEY (elementid) REFERENCES sys_xml_element(id);


--
-- TOC entry 2371 (class 2606 OID 86831)
-- Dependencies: 1841 2188 1762
-- Name: fk_sys_xml_doc_cht_xml_doc; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_doc
    ADD CONSTRAINT fk_sys_xml_doc_cht_xml_doc FOREIGN KEY (xmldoc) REFERENCES cht_xml_doc(id);


--
-- TOC entry 2372 (class 2606 OID 86836)
-- Dependencies: 1842 1763 2190
-- Name: fk_sys_xml_element_cht_xml_element; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_element
    ADD CONSTRAINT fk_sys_xml_element_cht_xml_element FOREIGN KEY (elementnameid) REFERENCES cht_xml_element(id);


--
-- TOC entry 2375 (class 2606 OID 86841)
-- Dependencies: 1843 1762 2188
-- Name: fk_sys_xml_element_struct_cht_xml_doc; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_element_struct
    ADD CONSTRAINT fk_sys_xml_element_struct_cht_xml_doc FOREIGN KEY (xmldocid) REFERENCES cht_xml_doc(id);


--
-- TOC entry 2376 (class 2606 OID 86846)
-- Dependencies: 1843 1763 2190
-- Name: fk_sys_xml_element_struct_cht_xml_element; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_element_struct
    ADD CONSTRAINT fk_sys_xml_element_struct_cht_xml_element FOREIGN KEY (xmlelementid) REFERENCES cht_xml_element(id);


--
-- TOC entry 2373 (class 2606 OID 86851)
-- Dependencies: 1842 1841 2306
-- Name: fk_sys_xml_element_sys_xml_doc; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_element
    ADD CONSTRAINT fk_sys_xml_element_sys_xml_doc FOREIGN KEY (xmldocid) REFERENCES sys_xml_doc(id);


--
-- TOC entry 2374 (class 2606 OID 86856)
-- Dependencies: 1842 1842 2308
-- Name: fk_sys_xml_element_sys_xml_element; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_xml_element
    ADD CONSTRAINT fk_sys_xml_element_sys_xml_element FOREIGN KEY (parentid) REFERENCES sys_xml_element(id);


--
-- TOC entry 2365 (class 2606 OID 86861)
-- Dependencies: 1832 1831 2290
-- Name: fk_url; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_url_group
    ADD CONSTRAINT fk_url FOREIGN KEY (urlid) REFERENCES sys_url(id);


--
-- TOC entry 2366 (class 2606 OID 86866)
-- Dependencies: 1832 1806 2246
-- Name: fk_url_group; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_url_group
    ADD CONSTRAINT fk_url_group FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2329 (class 2606 OID 86871)
-- Dependencies: 1784 1783 2220
-- Name: r_2; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY gis_file
    ADD CONSTRAINT r_2 FOREIGN KEY (countryid) REFERENCES gis_country(id);


--
-- TOC entry 2367 (class 2606 OID 86876)
-- Dependencies: 1806 1836 2246
-- Name: r_6; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY sys_user_group
    ADD CONSTRAINT r_6 FOREIGN KEY (groupid) REFERENCES sys_group(id);


--
-- TOC entry 2330 (class 2606 OID 86881)
-- Dependencies: 1784 1785 2224
-- Name: r_61; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY gis_file
    ADD CONSTRAINT r_61 FOREIGN KEY (layerid) REFERENCES gis_layer(id);


--
-- TOC entry 2324 (class 2606 OID 86886)
-- Dependencies: 1822 2272 1775
-- Name: r_8; Type: FK CONSTRAINT;  Owner: sa
--

ALTER TABLE ONLY cis_nodes
    ADD CONSTRAINT r_8 FOREIGN KEY (nodetype) REFERENCES sys_object_definition(id);



-- Completed on 2010-02-02 14:43:56

--
-- PostgreSQL database dump complete
--

