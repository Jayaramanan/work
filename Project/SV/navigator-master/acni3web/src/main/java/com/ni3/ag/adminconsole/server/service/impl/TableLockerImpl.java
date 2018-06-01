/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.server.service.TableLocker;

public class TableLockerImpl extends HibernateDaoSupport implements TableLocker{
	private static final Logger log = Logger.getLogger(TableLockerImpl.class);

	@Override
	public boolean lockTables(final String[] names){
		log.info("Lock tables request: " + names);
		if (names == null)
			return true;
		if (names.length == 0)
			return true;
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback(){

			@Override
			public Object doInHibernate(Session s) throws HibernateException, SQLException{
				StringBuffer sb = new StringBuffer();
				sb.append("lock table ");
				for (int i = 0; i < names.length; i++){
					if (i != 0)
						sb.append(", ");
					sb.append(names[i]);
				}
				sb.append(" IN EXCLUSIVE MODE NOWAIT");
				log.debug("locking query: " + sb.toString());
				SQLQuery q = s.createSQLQuery(sb.toString());
				try{
					int result = q.executeUpdate();
					log.debug("query result: " + result);
					return true;
				} catch (HibernateException e){
					log.error("Error executing: " + sb.toString(), e);
					return false;
				}
			}
		});
	}

}
