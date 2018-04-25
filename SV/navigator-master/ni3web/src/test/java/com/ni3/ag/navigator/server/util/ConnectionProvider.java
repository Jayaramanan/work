package com.ni3.ag.navigator.server.util;

import java.sql.Connection;

public interface ConnectionProvider{
	public Connection getConnection();

	public void releaseConnection(Connection connection);

	Connection getPGConnection();
}
