#{{{ Marathon Fixture
require 'default'
#}}} Marathon Fixture
require 'constants'
require 'schemaAdminView'
require 'schemaAdminCommon'
require 'acToolBar'

def test
	#datasource names
	escapedDataSource = $escapedDatabases[$activeInstance]
	dataSource = $databases[$activeInstance] 
	connectSource($activeInstance)
	
	#save current object count in tree under selected datasource	
	treeContentSize = 0
	with_window($mainWindowName) {
		treeContentSize = get_p($schemaAdminTree, 'Content[0].size', nil).to_i
		puts 'Current object count in tree ' + treeContentSize.to_s 
		treeContentSize+=1
		puts('Expected new count ' + treeContentSize.to_s)
	}

	#add new schema
	with_window($mainWindowName) {
		select($schemaAdminTree, '[' + escapedDataSource + ']')
		click($schemaAdminAddSchema)
	}
	
	with_window('New schema name') {
		select('field', 'TestSchema')
		click('Ok')
	}

	#assert object count = object count + 1 (so schema added)
	with_window($mainWindowName) {
		assert_p($schemaAdminError, 'Text', '')
		assert_p($schemaAdminTree, 'Content[0].size', treeContentSize.to_s)
		#and try to add an object and some attrs
		click($schemaAdminAddObject)
		select($schemaAdminObjectName, 'TestObjectOne')
		click($schemaAdminUpdateAttributes)
		click($schemaAdminAddAttribute)
		click($schemaAdminAddAttribute)
		select($schemaAdminAttributeTable, 'TestAttr1', '{0, Name}')
		select($schemaAdminAttributeTable, 'TestAttr1', '{0, Label}')
		select($schemaAdminAttributeTable, 'TestAttr2', '{1, Name}')
		select($schemaAdminAttributeTable, 'TestAttr2', '{1, Label}')
		click($acUpdateButton1)
		select_menu($acItemSimpleUpdate)
		

		#expected count should be 6 - (4 - default attrs: lat, lon, srcid, iconname, 2 - just created attrs)
		assert_p($schemaAdminAttributeTable, 'RowCount', '6')
		assert_p($schemaAdminError, 'Text', '')
	}
	
	#cleanup - delete created schema
	with_window($mainWindowName) {
		select($schemaAdminTree, '[TestSchema]')
		click($schemaAdminDelete)
	}
	
	disconnectSource($activeInstance)

end
