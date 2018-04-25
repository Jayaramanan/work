/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.server.dao.DatabaseVersionDAO;

public class DatabaseVersionDAOImpl extends HibernateDaoSupport implements DatabaseVersionDAO{

	@Override
	public String getVersion(final String databaseName){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = session.createSQLQuery("select version from sys_iam where name = :name");
				query.setString("name", databaseName);
				return query.uniqueResult();
			}
		};
		return (String) getHibernateTemplate().execute(callback);
	}

}
