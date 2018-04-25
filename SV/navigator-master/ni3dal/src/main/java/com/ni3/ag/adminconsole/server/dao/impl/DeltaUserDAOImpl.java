/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.DeltaUser;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.DeltaUserDAO;

public class DeltaUserDAOImpl extends HibernateDaoSupport implements DeltaUserDAO{

	@Override
	public Integer getCountByUser(final User u){
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(DeltaUser.class);
				c.add(Restrictions.eq(DeltaUser.TARGET_USER, u)).setProjection(Projections.count(DeltaUser.ID));
				return c.uniqueResult();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

	@Override
	public Integer getUnprocessedRecordCount(){
		HibernateCallback callback = new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(DeltaUser.class);
				c.add(Restrictions.or(Restrictions.eq(DeltaUser.PROCESSED, 1), Restrictions.isNull(DeltaUser.PROCESSED)));
				c.setProjection(Projections.count(DeltaUser.ID));
				return c.uniqueResult();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

}
