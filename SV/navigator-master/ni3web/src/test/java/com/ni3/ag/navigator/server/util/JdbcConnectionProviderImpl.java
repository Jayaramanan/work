package com.ni3.ag.navigator.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class JdbcConnectionProviderImpl implements ConnectionProvider{
	private static final Logger log = Logger.getLogger(JdbcConnectionProviderImpl.class);

	private String url;
	private String username;
	private String password;

	// init driver
	static{
		try{
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException cnfe){
			log.error("Couldn't find the driver!", cnfe);
		}

	}

	@Override
	public Connection getConnection(){
		Connection c = null;

		try{
			c = DriverManager.getConnection(url, username, password);
		} catch (SQLException se){
			log.error("Couldn't connect. ", se);
		}
		return c;
	}

	public String getUrl(){
		return url;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUsername(){
		return username;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	@Override
	public void releaseConnection(Connection connection){

		if (connection == null)
			return;

		try{
			if (!connection.getAutoCommit())
				connection.commit();
		} catch (SQLException e){
			log.error(e);
		} finally{
			try{
				connection.close();
			} catch (SQLException e){
				log.error("Error closing connection", e);
			}
		}
	}

	@Override
	public Connection getPGConnection(){
		return getConnection();
	}
}
