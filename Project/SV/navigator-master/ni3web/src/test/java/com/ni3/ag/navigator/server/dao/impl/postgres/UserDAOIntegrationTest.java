package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.util.ConnectionProvider;
import com.ni3.ag.navigator.shared.domain.User;

public class UserDAOIntegrationTest extends TestCase{
	private static final Logger log = Logger.getLogger(UserDAOIntegrationTest.class);
	private final UserDAO userDao = NSpringFactory.getInstance().getUserDao();
	private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");

	public void testGet(){
		final User user = userDao.get(1);
		assertNotNull(user);
		assertEquals("def", user.getUserName());
	}

	public void testGetBySID(){
		User bySID = userDao.getBySID("usr");
		assertNotNull(bySID);
		assertEquals("usr", bySID.getUserName());
	}

	public void testIsSidUnique(){
		int recordCountFromSysUser = getRecordCountFromSysUser();
		int uniqueSidCountFromSysUser = getUniqueSidCountFromSysUser();
		assertEquals(uniqueSidCountFromSysUser, recordCountFromSysUser);
	}

	private int getUniqueSidCountFromSysUser(){
		int res = -1;
		ConnectionProvider connectionProvider = (ConnectionProvider) context.getBean("connectionProvider");
		Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = null;
		try{
			statement = connection.prepareStatement("select count(distinct sid) as sid_count from sys_user");
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			res = resultSet.getInt("sid_count");
		} catch (SQLException e){
			log.error(e);
			fail(e.getMessage());
		} finally{
			connectionProvider.releaseConnection(connection);
		}
		return res;

	}

	private int getRecordCountFromSysUser(){
		int res = -1;
		ConnectionProvider connectionProvider = (ConnectionProvider) context.getBean("connectionProvider");
		Connection connection = connectionProvider.getConnection();
		PreparedStatement statement = null;
		try{
			statement = connection.prepareStatement("select count(*) as record_count from sys_user");
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			res = resultSet.getInt("record_count");
		} catch (SQLException e){
			log.error(e);
			fail(e.getMessage());
		} finally{
			connectionProvider.releaseConnection(connection);
		}
		return res;
	}
}