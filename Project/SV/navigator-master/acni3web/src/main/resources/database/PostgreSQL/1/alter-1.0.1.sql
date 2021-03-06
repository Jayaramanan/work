-- alter function template
-- change _expectedVersion and _newVersion when you create new script
-- _expectedVersion: version of database on which this script should be launched
-- _newVersion: new version of database, that will be applied to sys_iam table after successfull run of script
CREATE OR REPLACE FUNCTION alterDatabase()
 RETURNS void AS $$
DECLARE 
	_expectedVersion varchar = '1.0.0';
	_newVersion varchar = '1.0.1';
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
	-- database update script should be inserted here
	drop table if exists cht_pi_type cascade;
	drop table if exists cht_xml_attribute cascade;
	drop table if exists cht_xml_doc cascade;
	drop table if exists cht_xml_element cascade;
	drop table if exists cis_changes cascade;
	drop table if exists cis_chronology cascade;
	drop table if exists cis_function cascade;
	drop table if exists cis_pointofinterest cascade;
	drop table if exists cis_sub cascade;
	drop table if exists hst_cis_edges cascade;
	drop table if exists sys_labels cascade;
	drop table if exists sys_labelscheme cascade;
	drop table if exists sys_parameters cascade;
	drop table if exists sys_xml_attribute cascade;
	drop table if exists sys_xml_attribute_struct cascade;
	drop table if exists sys_xml_doc cascade;
	drop table if exists sys_xml_element cascade;
	drop table if exists sys_xml_element_struct cascade;
	drop table if exists sys_xml_table cascade;
	
	alter table cht_color drop column picture;
	alter table cht_line_style drop column picture;
	alter table cht_line_weight	 drop column picture;
	alter table cis_chart_attributes drop column colorid;
	alter table cis_edges drop column fromsrcid;
	alter table cis_edges drop column tosrcid;
	alter table cis_edges drop column add3;
	alter table cis_edges drop column add2;
	alter table cis_edges drop column add1;
	alter table cis_edges drop column add0;
	alter table cis_edges drop column add4;
	alter table cis_edges drop column add5;
	alter table cis_edges drop column add6;
	alter table sys_attribute_group	 drop column cancreate;
	alter table sys_attribute_group	 drop column haveaccess;
	alter table sys_nodemetaphor drop column col1;
	alter table sys_nodemetaphor drop column "type";
	alter table sys_nodemetaphor drop column col2;
	alter table sys_nodemetaphor drop column col3;
	alter table sys_nodemetaphor drop column col4;
	alter table sys_nodemetaphor drop column vip;
	alter table sys_object_attributes drop column ingraphlabel;
	
	delete from sys_settings_group where id <> 1;
	delete from sys_settings_user where id <> 1;
	
	ALTER TABLE cht_chart 					ADD FOREIGN KEY (schemaid) 		REFERENCES sys_object_definition (id);
	ALTER TABLE cht_connection_type 		ADD FOREIGN KEY (id) 			REFERENCES cht_predefinedattributes (id);
	ALTER TABLE cht_predefinedattributes  	ADD FOREIGN KEY (attributeid) 	REFERENCES sys_object_attributes (id);
	ALTER TABLE cis_chart_attributes 		ADD FOREIGN KEY (chartid) 		REFERENCES cht_chart (id);
	ALTER TABLE sys_attribute_structure 	ADD FOREIGN KEY (objectdefinitionid) REFERENCES sys_object_definition (id);
	ALTER TABLE sys_group_prefilter 		ADD FOREIGN KEY (predefid) 		REFERENCES cht_predefinedattributes (id);
	ALTER TABLE sys_group_prefilter 		ADD FOREIGN KEY (groupid) 		REFERENCES sys_group (id);
	ALTER TABLE sys_group_scope 			ADD FOREIGN KEY (groupid) 		REFERENCES sys_group (id);
	ALTER TABLE sys_map_group 				ADD FOREIGN KEY (mapid) 		REFERENCES gis_map (id);
	ALTER TABLE sys_object_attributes 		ADD FOREIGN KEY (managing) 		REFERENCES cht_attribute_visibility (id);
	ALTER TABLE sys_object_attributes 		ADD FOREIGN KEY (createdby) 	REFERENCES sys_user (id);
	ALTER TABLE sys_settings_group 			ADD FOREIGN KEY (id)  			REFERENCES sys_group (id);
	ALTER TABLE sys_settings_user 			ADD FOREIGN KEY (id) 			REFERENCES sys_user (id);
	ALTER TABLE sys_user_group 				ADD FOREIGN KEY (userid) 		REFERENCES sys_user (id);
	ALTER TABLE sys_user_language 			ADD FOREIGN KEY (languageid) 	REFERENCES cht_language (id);

	insert into sys_settings_group(id, prop, section, value) values (1,'Connection_ConnectionCreate_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Connection_ConnectionEdit_InUse','Applet','false');
	insert into sys_settings_group(id, prop, section, value) values (1,'Connection_ConnectionHistory_InUse','Applet','false');
	insert into sys_settings_group(id, prop, section, value) values (1,'ContextMenu_Connection_Edit_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'File_Exit_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'File_ExportData_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Help_About_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Help_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Node_NodeCreate_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Node_NodeDelete_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Node_NodeEdit_InUse','Applet','true');
	insert into sys_settings_group(id, prop, section, value) values (1,'Node_NodeHistory_InUse','Applet','false');
	insert into sys_settings_group(id, prop, section, value) values (1,'ShowDirectedGraph_InUse','Applet','TRUE');

	delete from sys_settings_user where section = 'words';
	
	insert into sys_user_language(languageid, prop, value) values (1, 'SchemaAdministration', 'Schema administration');
	insert into sys_user_language(languageid, prop, value) values (1, 'Update', 'Update');
	insert into sys_user_language(languageid, prop, value) values (1, 'Refresh', 'Refresh');
	insert into sys_user_language(languageid, prop, value) values (1, 'ApplicationConfiguration', 'Application configuration');
	insert into sys_user_language(languageid, prop, value) values (1, 'AttributeConfiguration', 'Attribute configuration');
	insert into sys_user_language(languageid, prop, value) values (1, 'Label', 'Label');
	insert into sys_user_language(languageid, prop, value) values (1, 'InMetaphor', 'In metaphor');
	insert into sys_user_language(languageid, prop, value) values (1, 'Sort', 'Sort');
	insert into sys_user_language(languageid, prop, value) values (1, 'Predefined', 'Value List');
	insert into sys_user_language(languageid, prop, value) values (1, 'InFilter', 'Display Filter');
	insert into sys_user_language(languageid, prop, value) values (1, 'InLabel', 'In label');
	insert into sys_user_language(languageid, prop, value) values (1, 'InToolTip', 'In ToolTip');
	insert into sys_user_language(languageid, prop, value) values (1, 'InSearch', 'In search');
	insert into sys_user_language(languageid, prop, value) values (1, 'InAdvancedSearch', 'In advanced search');
	insert into sys_user_language(languageid, prop, value) values (1, 'InGraphLabel', 'In graph label');
	insert into sys_user_language(languageid, prop, value) values (1, 'LabelBold', 'Label bold');
	insert into sys_user_language(languageid, prop, value) values (1, 'LabelItalic', 'Label italic');
	insert into sys_user_language(languageid, prop, value) values (1, 'LabelUnderline', 'Label underline');
	insert into sys_user_language(languageid, prop, value) values (1, 'ContentBold', 'Content bold');
	insert into sys_user_language(languageid, prop, value) values (1, 'ContentItalic', 'Content italic');
	insert into sys_user_language(languageid, prop, value) values (1, 'ContentUnderline', 'Content underline');
	insert into sys_user_language(languageid, prop, value) values (1, 'Managing', 'Managing');
	insert into sys_user_language(languageid, prop, value) values (1, 'InExport', 'In export');
	insert into sys_user_language(languageid, prop, value) values (1, 'InSimpleSearch', 'In simple search');
	insert into sys_user_language(languageid, prop, value) values (1, 'InPrefilter', 'Data Filter');
	insert into sys_user_language(languageid, prop, value) values (1, 'Format', 'Format');
	insert into sys_user_language(languageid, prop, value) values (1, 'RegExpression', 'Reg expression');
	insert into sys_user_language(languageid, prop, value) values (1, 'ValueDescription', 'Value description');
	insert into sys_user_language(languageid, prop, value) values (1, 'InStructure', 'In structure');
	insert into sys_user_language(languageid, prop, value) values (1, 'Label_sort', 'Label sort');
	insert into sys_user_language(languageid, prop, value) values (1, 'Filter_sort', 'Filter sort');
	insert into sys_user_language(languageid, prop, value) values (1, 'Search_sort', 'Search sort');
	insert into sys_user_language(languageid, prop, value) values (1, 'Activate', 'Activate');
	insert into sys_user_language(languageid, prop, value) values (1, 'DeleteMetaphorColumns', 'Delete metaphor columns');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddObject', 'Add object');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddSchema', 'Add schema');
	insert into sys_user_language(languageid, prop, value) values (1, 'Delete', 'Delete');
	insert into sys_user_language(languageid, prop, value) values (1, 'Copy', 'Copy');
	insert into sys_user_language(languageid, prop, value) values (1, 'ObjectName', 'Object name');
	insert into sys_user_language(languageid, prop, value) values (1, 'CreationDate', 'Creation date');
	insert into sys_user_language(languageid, prop, value) values (1, 'CreatedBy', 'Created by');
	insert into sys_user_language(languageid, prop, value) values (1, 'ObjectType', 'Object type');
	insert into sys_user_language(languageid, prop, value) values (1, 'TableName', 'Table name');
	insert into sys_user_language(languageid, prop, value) values (1, 'TabDisplayGroup', 'Tab display group');
	insert into sys_user_language(languageid, prop, value) values (1, 'Attribute', 'Attribute');
	insert into sys_user_language(languageid, prop, value) values (1, 'CascadeDelete', 'Cascade delete');
	insert into sys_user_language(languageid, prop, value) values (1, 'Add', 'Add');
	insert into sys_user_language(languageid, prop, value) values (1, 'Datatype', 'Datatype');
	insert into sys_user_language(languageid, prop, value) values (1, 'Name', 'Name');
	insert into sys_user_language(languageid, prop, value) values (1, 'InTable', 'In table');
	insert into sys_user_language(languageid, prop, value) values (1, 'PredefinedAttributes', 'Predefined attributes');
	insert into sys_user_language(languageid, prop, value) values (1, 'Value', 'Value');
	insert into sys_user_language(languageid, prop, value) values (1, 'Translation', 'Translation');
	insert into sys_user_language(languageid, prop, value) values (1, 'InUse', 'In use');
	insert into sys_user_language(languageid, prop, value) values (1, 'ToUse', 'To use');
	insert into sys_user_language(languageid, prop, value) values (1, 'Parent', 'Parent');
	insert into sys_user_language(languageid, prop, value) values (1, 'SrcID', 'SrcID');
	insert into sys_user_language(languageid, prop, value) values (1, 'Settings', 'Settings');
	insert into sys_user_language(languageid, prop, value) values (1, 'Section', 'Section');
	insert into sys_user_language(languageid, prop, value) values (1, 'Property', 'Property');
	insert into sys_user_language(languageid, prop, value) values (1, 'MetaphorAdministration', 'Metaphor administration');
	insert into sys_user_language(languageid, prop, value) values (1, 'IconName', 'Icon name');
	insert into sys_user_language(languageid, prop, value) values (1, 'IconPath', 'Icon path');
	insert into sys_user_language(languageid, prop, value) values (1, 'Priority', 'Priority');
	insert into sys_user_language(languageid, prop, value) values (1, 'MetaphorSet', 'Metaphor set');
	insert into sys_user_language(languageid, prop, value) values (1, 'UserAdministration', 'User administration');
	insert into sys_user_language(languageid, prop, value) values (1, 'DeleteGroup', 'Delete group');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddGroup', 'Add group');
	insert into sys_user_language(languageid, prop, value) values (1, 'Allowed', 'Allowed');
	insert into sys_user_language(languageid, prop, value) values (1, 'Denied', 'Denied');
	insert into sys_user_language(languageid, prop, value) values (1, 'Users', 'Users');
	insert into sys_user_language(languageid, prop, value) values (1, 'Objects', 'Objects');
	insert into sys_user_language(languageid, prop, value) values (1, 'Connections', 'Connections');
	insert into sys_user_language(languageid, prop, value) values (1, 'FirstName', 'First name');
	insert into sys_user_language(languageid, prop, value) values (1, 'LastName', 'Last name');
	insert into sys_user_language(languageid, prop, value) values (1, 'UserName', 'User name');
	insert into sys_user_language(languageid, prop, value) values (1, 'Password', 'Password');
	insert into sys_user_language(languageid, prop, value) values (1, 'Object', 'Object');
	insert into sys_user_language(languageid, prop, value) values (1, 'CanRead', 'Can read');
	insert into sys_user_language(languageid, prop, value) values (1, 'CanCreate', 'Can create');
	insert into sys_user_language(languageid, prop, value) values (1, 'CanUpdate', 'Can update');
	insert into sys_user_language(languageid, prop, value) values (1, 'CanDelete', 'Can delete');
	insert into sys_user_language(languageid, prop, value) values (1, 'PredefinedAttribute', 'Predefined attribute');
	insert into sys_user_language(languageid, prop, value) values (1, 'Selected', 'Selected');
	insert into sys_user_language(languageid, prop, value) values (1, 'ConnectionType', 'Connection type');
	insert into sys_user_language(languageid, prop, value) values (1, 'FromObject', 'From object');
	insert into sys_user_language(languageid, prop, value) values (1, 'ToObject', 'To object');
	insert into sys_user_language(languageid, prop, value) values (1, 'LineStyle', 'Line style');
	insert into sys_user_language(languageid, prop, value) values (1, 'LineColor', 'Line color');
	insert into sys_user_language(languageid, prop, value) values (1, 'LineWeight', 'Line weight');
	insert into sys_user_language(languageid, prop, value) values (1, 'FromToScore', 'From to score');
	insert into sys_user_language(languageid, prop, value) values (1, 'ToFromScore', 'To from score');
	insert into sys_user_language(languageid, prop, value) values (1, 'LanguageAdministration', 'Language administration');
	insert into sys_user_language(languageid, prop, value) values (1, 'DeleteLanguage', 'Delete language');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddLanguage', 'Add language');
	insert into sys_user_language(languageid, prop, value) values (1, 'SchemaName', 'Schema name');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgTabDisplayGroupEmpty', 'Please enter value in Tab Display Group field');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgSortEmpty', 'Please enter value in Sort field');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgObjectNameEmpty', 'Please enter value in Object Name field');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgTableNameEmpty', 'Please enter value in Table Name field');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgAttributeFieldsEmpty', 'Please fill Name, Label, Datatype and In Table for all object attributes');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateObjects', 'Object with name {1} already exists');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateAttributes', 'Attribute with name {1} already exists');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicatePropertyName', 'Duplicate property name: {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgSectionNameEmpty', 'Section name cannot be empty');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgPropertyNameEmpty', 'Property name cannot be empty');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgValueEmpty', 'Value cannot be empty');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgConnectionFieldsEmpty', 'Could not update connections. Please check that all data is entered.');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgUserFieldsEmpty', 'Could not update users. Please check that all data is entered.');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgSelectSchema', 'Please select schema');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateNodeMetaphors', 'Duplicate item found with values {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateUsers', 'User with username {1} already exists');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgAttributeCannotBeDeleted', 'The attribute {1} can not be deleted, it''s referenced in sys_attribute_group table. (groups: {2})');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgConnectionCouldNotBeDeleted', 'Connection {1} ({2} -> {3}) could not be deleted. It is referenced in group {4}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgNodeMetaphorDoNotExsist', 'Requested columns do not exist in table sys_nodemetaphor: {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEmptyUserName', 'Username field is empty');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgWrongUserName', 'Wrong password for user {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgSchemaNotSelected', 'Cannot add new object, schema not selected');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgSchemaWithIdDoesNotExists', 'Cannot add new object, schema with id = {1} doesn''t exist');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgNoObjectType', 'Cannot add new object, undefined object type');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgObjectWithIdNotFound', 'No object with id {1} found');
	insert into sys_user_language(languageid, prop, value) values (1, 'GroupMembers', 'Group Members');
	insert into sys_user_language(languageid, prop, value) values (1, 'GroupPrivileges', 'Group Privileges');
	insert into sys_user_language(languageid, prop, value) values (1, 'Ok', 'Ok');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgCannotDeleteSystemAttribute', 'Cannot delete system attribute');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgLanguageNotExist', 'Language with ID = {1} cannot be found. Default system language is taken.');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgNoLanguageSettingForUser', 'Language setting cannot be found for user {1}. Default system language is taken.');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfSchemaCopy', 'Enter name for schema copy');
	insert into sys_user_language(languageid, prop, value) values (1, 'CopySchemaTitle', 'New schema name');
	insert into sys_user_language(languageid, prop, value) values (1, 'CopyOf', 'Copy of');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateLanguage', 'Duplicate language name `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDatabaseWrongVersion', 'Wrong database version: expected {1}, but was {2}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgPredefinedAttributeFieldsEmpty', 'Please fill Value and Label for all predefined attributes');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfNewSchema', 'Enter name of new schema');
	insert into sys_user_language(languageid, prop, value) values (1, 'NewSchemaTitle', 'New schema name');
	insert into sys_user_language(languageid, prop, value) values (1, 'NewSchema', 'New Schema');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddMetaphorSet', 'Add metaphor set');
	insert into sys_user_language(languageid, prop, value) values (1, 'CopyMetaphorSet', 'Copy metaphor set');
	insert into sys_user_language(languageid, prop, value) values (1, 'DeleteMetaphorSet', 'Delete metaphor set');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfNewMetaphorSet', 'Enter name of the new metaphor set');
	insert into sys_user_language(languageid, prop, value) values (1, 'ID', 'ID');
	insert into sys_user_language(languageid, prop, value) values (1, 'Manageable', 'Manageable');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateGroup', 'Duplicate group `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateSchema', 'Duplicate schema `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'UseScopeForNodes', 'Use scope for nodes');
	insert into sys_user_language(languageid, prop, value) values (1, 'Query', 'Query');
	insert into sys_user_language(languageid, prop, value) values (1, 'UseScopeForEdges', 'Use scope for edges');
	insert into sys_user_language(languageid, prop, value) values (1, 'GroupScope', 'Group Scope');
	insert into sys_user_language(languageid, prop, value) values (1, 'HaloColor', 'Halo color');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateConnections', 'Duplicate connections: {1} ({2} -> {3})');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgNotValidHexColor', '{1} is not a valid hexadecimal color');
	insert into sys_user_language(languageid, prop, value) values (1, 'Icon', 'Icon');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddIcon', 'Add icon');
	insert into sys_user_language(languageid, prop, value) values (1, 'OnlyImages', 'Only images');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgCannotAddNewIcon', 'Cannot add icon {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgServiceError', 'Service error: {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgImportError', 'Import error: {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'DeleteIcon', 'Delete Icon');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgMetaphorIconsAreInUse', 'Icons are used in node metaphors: {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'DeleteChart', 'Delete Chart');
	insert into sys_user_language(languageid, prop, value) values (1, 'AddChart', 'Add Chart');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfNewChart', 'Enter name of the new chart');
	insert into sys_user_language(languageid, prop, value) values (1, 'NewChart', 'New Chart');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfNewLanguage', 'Enter name of the new language');
	insert into sys_user_language(languageid, prop, value) values (1, 'NewLanguage', 'New Language');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEnterNameOfNewGroup', 'Enter name of the new group');
	insert into sys_user_language(languageid, prop, value) values (1, 'NewGroup', 'New Group');
	insert into sys_user_language(languageid, prop, value) values (1, 'NewObject', 'New Object');
	insert into sys_user_language(languageid, prop, value) values (1, 'MaxValue', 'Max value');
	insert into sys_user_language(languageid, prop, value) values (1, 'MinScale', 'Min scale');
	insert into sys_user_language(languageid, prop, value) values (1, 'MaxScale', 'Max scale');
	insert into sys_user_language(languageid, prop, value) values (1, 'LabelInUse', 'Label in use');
	insert into sys_user_language(languageid, prop, value) values (1, 'LabelFontSize', 'Label font size');
	insert into sys_user_language(languageid, prop, value) values (1, 'NumberFormat', 'Number format');
	insert into sys_user_language(languageid, prop, value) values (1, 'DisplayOperation', 'Display operation');
	insert into sys_user_language(languageid, prop, value) values (1, 'DisplayAttribute', 'Display attribute');
	insert into sys_user_language(languageid, prop, value) values (1, 'ChartType', 'Chart type');
	insert into sys_user_language(languageid, prop, value) values (1, 'IsValueDisplayed', 'Is value displayed');
	insert into sys_user_language(languageid, prop, value) values (1, 'RGB', 'RGB');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgObjectShouldNotBeNull', 'Object should not be empty value');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateChartName', 'Duplicate name for chart');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateObjectChart', 'More then one ObjectChart defined for Object `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgNotAllRequeredFieldsHasValues', 'Not all requered fields have values');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgLoginTo', 'Login to {1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgDuplicateChartAttributeName', 'Duplicate chart attribute name `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'Connect', 'Connect');
	insert into sys_user_language(languageid, prop, value) values (1, 'Disconnect', 'Disconnect');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgErrorDeleteTable', 'Error drop table: `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgEmpty', '{1}');
	insert into sys_user_language(languageid, prop, value) values (1, 'DatabaseInstance', 'Database instance');
	insert into sys_user_language(languageid, prop, value) values (1, 'Host', 'Host');
	insert into sys_user_language(languageid, prop, value) values (1, 'Port', 'Port');
	insert into sys_user_language(languageid, prop, value) values (1, 'DatabaseName', 'Database name');
	insert into sys_user_language(languageid, prop, value) values (1, 'Default', 'default');
	insert into sys_user_language(languageid, prop, value) values (1, 'MsgInvalidObjectAttribute', 'Invalid object attribute: `{1}`');
	insert into sys_user_language(languageid, prop, value) values (1, 'ChooseColor', 'Choose color');
	insert into sys_user_language(languageid, prop, value) values (1, 'HaloEmpty', '<empty>');
	insert into sys_user_language(languageid, prop, value) values (1, 'HaloChooseColor', 'Choose color');
	insert into sys_user_language(languageid, prop, value) values (1, 'HaloAutomatic', 'Automatic');
	insert into sys_user_language(languageid, prop, value) values (1, 'HaloRandom', 'Random');
	insert into sys_user_language(languageid, prop, value) values (1, 'Language', 'Language');
	
	delete from sys_settings_user;
	insert into sys_settings_user (id, prop, section, value) values (1, '_InheritsGroupSettings', 'Applet', 'true');
	delete from sys_settings_group;
	insert into sys_settings_group (id, prop, section, value) values (1, '_InheritsGroupSettings', 'Applet', 'true');

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