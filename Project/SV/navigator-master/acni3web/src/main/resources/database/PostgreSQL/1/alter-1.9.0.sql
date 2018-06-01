-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.8.11';
	_newVersion varchar = '1.9.0';
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
CREATE OR REPLACE FUNCTION getchart(_objectid integer, _chartid integer, _nodes text, _filteredoutattributes text)
  RETURNS text AS
$BODY$
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
			and not sp_HaveIt(a.predefinedID,_mSet)
		order by a.id;
BEGIN
	if _FilteredOutAttributes='' then
		mFilteredOutAttributes:='0';
	else
		mFilteredOutAttributes:=_FilteredOutAttributes;
	end if;

	_inSQL:='select COALESCE(sum(value),0) from CIS_CHART c where c.NodeID=n.id and c.ChartAttributeID in(select id from CIS_CHART_Attributes tt where not sp_HaveIt(tt.predefinedID,'''||mFilteredOutAttributes||''') and tt.ChartID='||_ChartID||')';
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
$BODY$
  LANGUAGE 'plpgsql';
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