#{{{ Marathon Fixture
require 'default'
#}}} Marathon Fixture
require 'constants'
require 'schemaAdminView'
require 'schemaAdminCommon'

def test

	#datasource names
	escapedDataSource = $escapedDatabases[$activeInstance]
	puts escapedDataSource
	dataSource = $databases[$activeInstance] 
	puts dataSource
	
	connectSource($activeInstance)

	puts $mainWindowName
	#add new schema
	with_window($mainWindowName) {
		select($schemaAdminTree, '[' + escapedDataSource + ']')
		click($schemaAdminAddSchema)
	}
	
	with_window('New schema name') {
		select('field', 'TestSchema')
		click('Ok')
	}

	#add new schema with same name
	with_window($mainWindowName) {
		assert_p($schemaAdminError, 'Text', '')
		select($schemaAdminTree, '[' + escapedDataSource + ']')
		click($schemaAdminAddSchema)
	}
	
	with_window('New schema name') {
		select('field', 'TestSchema')
		click('Ok')
	}

	#assert error message shown
	with_window($mainWindowName) {
		assert_p($schemaAdminError, 'Text', '<html><body>Duplicate schema `TestSchema`<br></body></html>')
	}
	
	#cleanup - delete created schema
	with_window($mainWindowName) {
		select($schemaAdminTree, '[TestSchema]')
		click($schemaAdminDelete)
	}
	
	disconnectSource($activeInstance)	
end
