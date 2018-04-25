require 'constants'

require 'loginView'

def connectSource(index)
	escapedDataSource = $escapedDatabases[index]
	dataSource = $databases[index] 
	
	#select datasource
	with_window($mainWindowName) {
		select($schemaAdminTree, '[' + escapedDataSource + ']')
		click($schemaAdminConnect)
	}

	with_window('Login to ' + dataSource) {
		assert_p($loginLogin, 'Enabled', 'true')
		assert_p($loginError, 'Text', '')
		select($loginUserName, $login[index])
		select($loginPassowrd, $password[index])
		click($loginLogin)
	}
end

def disconnectSource(index)
	escapedDataSource = $escapedDatabases[index]
	with_window($mainWindowName) {
		select($schemaAdminTree, '[' + escapedDataSource + ']')
		click($schemaAdminDisconnect)
	}	
end
