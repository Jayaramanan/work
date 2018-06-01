-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.18.6';
	_newVersion varchar = '1.19.0';
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
CREATE OR REPLACE FUNCTION sp_haveit(_tobefiltered text, _tofilter text)
  RETURNS boolean AS
$BODY$
declare _start int;
	_end int;
	_number text;
	_toBe text;
	_Filter text;
begin
	/*
		position(substring in string) int Location of specified substring position('om' in 'Thomas') 3 
		substring(string [from int] [for int]) text Extract substring  substring('Thomas' from 2 for 3) hom 
		substring(string from pattern) text Extract substring matching POSIX regular expression. See Section 9.7 for more information on pattern matching.  substring('Thomas' from '...$') mas 
		substring(string from pattern for escape) text Extract substring matching SQL regular expression. See Section 9.7 for more information on pattern matching.  substring('Thomas' from '%#"o_a#"_' for '#') oma 
		trim([leading | trailing | both] [characters] from string)  
		strpos(string, substring) int Location of specified substring (same as position(substring in string), but note the reversed argument order)  strpos('high', 'ig') 2 
		substr(string, from [, count]) text Extract substring (same as substring(string from from for count))  substr('alphabet', 3, 2) ph 
	 
	*/
	if length(_toBeFiltered) = 0 OR _toBeFiltered is null then
	    return false;
	elsif strpos(_toBeFiltered,',')=0 then
	    if strpos(_toFilter,_toBeFiltered)=0 then
		return false;
	    else
		return true;
	    end if;
	else
	    _toBe:=_toBeFiltered||',';
	    _Filter:=','||_toFilter||',';

	    _start:=0;
	    _end:=strpos(_toBeFiltered,',');
	    WHILE length(_toBe)>0 LOOP
		_number:=','||substr(_toBe,_start,_end);
		if strpos(_Filter,_number)>0 then
		   return true;
		end if;
		_toBe:=substr(_toBe,_end+1); 
	    END LOOP;
	end if;
	
	return false;
end;
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