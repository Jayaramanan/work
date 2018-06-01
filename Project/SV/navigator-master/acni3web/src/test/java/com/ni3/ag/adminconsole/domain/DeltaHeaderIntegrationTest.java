package com.ni3.ag.adminconsole.domain;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import junit.framework.TestCase;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import com.ni3.ag.adminconsole.server.dao.DeltaHeaderDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;

public class DeltaHeaderIntegrationTest extends TestCase{

	private final ClassPathXmlApplicationContext context;
	private final UserDAO userDAO;
	private final DeltaHeaderDAO deltaHeaderDAO;

	public DeltaHeaderIntegrationTest() throws NamingException{
		final PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setServerName("localhost");
		dataSource.setDatabaseName("demo_33");
		dataSource.setUser("ni3");
		dataSource.setPassword("ni3");

		final SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		builder.bind("jdbc/demo", dataSource);

		context = new ClassPathXmlApplicationContext("ACNi3Web-beans-integration.xml");

		userDAO = (UserDAO) context.getBean("UserDAO");
		deltaHeaderDAO = (DeltaHeaderDAO) context.getBean("DeltaHeaderDAO");
	}

	public void testGetCountForUser() throws NamingException{
		final User user = userDAO.getById(8);
		final Integer count = deltaHeaderDAO.getCountByUser(user);
		assertEquals(Integer.valueOf(1), count);
	}

	public void testGetUnprocessedCount() throws NamingException{
		final Integer count = deltaHeaderDAO.getUnprocessedCount();
		assertEquals(Integer.valueOf(2), count);
	}

	public void testLoad(){
		final DeltaHeader header = deltaHeaderDAO.load(3L);
		assertEquals(Integer.valueOf(8), header.getCreator());
	}

	public void testSave(){
		/*
		final DeltaHeader header = new DeltaHeader();
		final User user = userDAO.getById(8);
		header.setCreator(user);
		header.setDeltaType(DeltaType.SETTING_UPDATE);
		final Set<DeltaParam> parameters = new HashSet<DeltaParam>(2);
		parameters.add(new DeltaParam(DeltaParamIdentifier.PropertyName, "test name"));
		parameters.add(new DeltaParam(DeltaParamIdentifier.PropertyValue, "test value"));
		header.setDeltaParamList(parameters);
		deltaHeaderDAO.saveOrUpdate(header);
		*/
	}

}
